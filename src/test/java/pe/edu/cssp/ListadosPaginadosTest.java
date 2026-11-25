package pe.edu.cssp;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cssp.entity.Estacion;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.service.DashboardService;

/** Paginación, orden y filtros (resueltos en la base de datos) de sismos, estaciones, ubicaciones y tipos; y dashboard. */
@AutoConfigureMockMvc
class ListadosPaginadosTest extends BaseDatosTest {
    @Autowired MockMvc mvc;
    @Autowired DashboardService dashboard;

    private Evento sismo(Ubicacion ubicacion, Estacion estacion, TipoAlerta alerta, double magnitud, LocalDateTime fecha, String estado) {
        Evento ev = new Evento();
        ev.setEstacion(estacion); ev.setUbicacion(ubicacion); ev.setTipoAlerta(alerta);
        ev.setCodigo(siguiente("PAG-")); ev.setFechaHora(fecha); ev.setMagnitud(magnitud); ev.setProfundidad(30);
        ev.setLatitud(-16.4); ev.setLongitud(-71.5); ev.setReferencia("Prueba de paginación"); ev.setEstado(estado);
        ev.setCreacionFecha(fechaHoraHace(0)); ev.setCreacionUsuario("prueba");
        return eventos.save(ev);
    }

    private TipoAlerta alertaDeColor(String color) {
        TipoAlerta a = alerta();
        a.setColor(color);
        return alertas.save(a);
    }

    // ---------- Sismos ----------

    @Test
    void sismosSePaginanConTamanoConfigurable() throws Exception {
        Ubicacion u = ubicacion("ACTIVO");
        Estacion e = estacion(u);
        TipoAlerta a = alerta();
        for (int i = 0; i < 12; i++) sismo(u, e, a, 4.0, fechaHoraHace(10 + i), "REGISTRADO");

        mvc.perform(get("/eventos").param("ubicacionId", "" + u.getId()).param("tamano", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("de <strong>12</strong> registros")))
                .andExpect(content().string(containsString("Siguiente")));
        mvc.perform(get("/eventos").param("ubicacionId", "" + u.getId()).param("tamano", "10").param("pagina", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<strong>11</strong>–<strong>12</strong>")));
        mvc.perform(get("/eventos").param("ubicacionId", "" + u.getId()).param("tamano", "20"))
                .andExpect(content().string(not(containsString("Siguiente"))));
    }

    @Test
    void sismosSeFiltranPorFechaMagnitudYSeOrdenan() throws Exception {
        Ubicacion u = ubicacion("ACTIVO");
        Estacion e = estacion(u);
        TipoAlerta a = alerta();
        LocalDateTime dia = LocalDateTime.of(2026, 3, 15, 10, 0);
        Evento fuerte = sismo(u, e, a, 6.3, dia, "REGISTRADO");
        Evento debil = sismo(u, e, a, 3.1, dia.plusHours(2), "REGISTRADO");
        Evento otroDia = sismo(u, e, a, 5.0, dia.plusDays(1), "REGISTRADO");

        String html = mvc.perform(get("/eventos").param("ubicacionId", "" + u.getId()).param("fecha", "2026-03-15")
                        .param("orden", "magnitud").param("dir", "desc"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(otroDia.getCodigo()))))
                .andReturn().getResponse().getContentAsString();
        assertTrue(html.indexOf(fuerte.getCodigo()) < html.indexOf(debil.getCodigo()), "el más fuerte va primero");

        mvc.perform(get("/eventos").param("ubicacionId", "" + u.getId()).param("magnitudMinima", "5"))
                .andExpect(content().string(containsString(fuerte.getCodigo())))
                .andExpect(content().string(containsString(otroDia.getCodigo())))
                .andExpect(content().string(not(containsString(debil.getCodigo()))));
    }

    @Test
    void laListaDeSismosMuestraLaColumnaDistrito() throws Exception {
        evento("REGISTRADO", nivel());
        mvc.perform(get("/eventos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Distrito")))
                .andExpect(content().string(containsString("paginacion")));
    }

    @Test
    void unaFechaInvalidaEnElFiltroNoRompeLaPantalla() throws Exception {
        mvc.perform(get("/eventos").param("fecha", "no-es-fecha")).andExpect(status().isOk())
                .andExpect(content().string(containsString("La fecha del filtro no es válida.")));
    }

    // ---------- Estaciones, ubicaciones y tipos ----------

    @Test
    void estacionesSeBuscanYMuestranDistrito() throws Exception {
        Ubicacion u = ubicacion("ACTIVO");
        Estacion buscada = estacion(u);
        Estacion otra = estacion(u);
        mvc.perform(get("/estaciones").param("q", buscada.getCodigo()).param("ubicacionId", "" + u.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Distrito")))
                .andExpect(content().string(containsString(buscada.getNombre())))
                .andExpect(content().string(not(containsString(">" + otra.getNombre() + "<"))));
    }

    @Test
    void ubicacionesYTiposTienenBuscadorYPaginacion() throws Exception {
        Ubicacion u = ubicacion("INACTIVO");
        mvc.perform(get("/ubicaciones").param("q", u.getNombre()).param("estado", "INACTIVO"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(u.getNombre())))
                .andExpect(content().string(containsString("de <strong>1</strong> registros")));

        TipoAlerta a = alerta();
        mvc.perform(get("/tipos-alerta").param("q", a.getCodigo()).param("orden", "nombre"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(a.getNombre())))
                .andExpect(content().string(containsString("paginacion")));

        var t = tipoDano("ACTIVO");
        mvc.perform(get("/tipos-dano").param("q", t.getCodigo()).param("sector", "VIVIENDA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(t.getNombre())));
        mvc.perform(get("/tipos-dano").param("q", t.getCodigo()).param("sector", "SALUD"))
                .andExpect(content().string(not(containsString(t.getNombre()))));
    }

    @Test
    void unOrdenNoPermitidoSeIgnora() throws Exception {
        mvc.perform(get("/estaciones").param("orden", "password").param("dir", "desc")).andExpect(status().isOk());
        mvc.perform(get("/eventos").param("orden", "x; drop table Evento")).andExpect(status().isOk());
    }

    // ---------- Dashboard ----------

    @Test
    void losKpiDelDashboardRespetanElFiltroDeUbicacionYExcluyenLosDadosDeBaja() {
        Ubicacion u = ubicacion("ACTIVO");
        Estacion e = estacion(u);
        sismo(u, e, alertaDeColor("VERDE"), 6.0, fechaHoraHace(30), "CERRADO");      // alta por magnitud
        sismo(u, e, alertaDeColor("ROJO"), 3.0, fechaHoraHace(40), "EN_SEGUIMIENTO"); // alta por color de alerta
        sismo(u, e, alertaDeColor("VERDE"), 4.0, fechaHoraHace(50), "REGISTRADO");    // normal
        sismo(u, e, alertaDeColor("ROJO"), 7.5, fechaHoraHace(60), "INACTIVO");       // dado de baja: no cuenta
        sismo(u, e, alerta(), 4.0, LocalDateTime.now().minusYears(3), "REGISTRADO");  // fuera del periodo

        DashboardService.Datos datos = dashboard.calcular(DashboardService.Filtro.de(u.getId(), 6));
        assertEquals(3, datos.kpis().getTotalSismos());
        assertEquals(new BigDecimal("6.00"), datos.kpis().getMagnitudMaxima());
        assertEquals(2, datos.kpis().getSismosAlertaAlta());
        assertEquals(67, datos.kpis().getPorcentajeAlertaAlta());
        assertEquals(2, datos.kpis().getSismosAbiertos());
        assertEquals(1, datos.kpis().getEstacionesActivas());
        assertEquals(6, datos.sismosPorMes().size());
        assertEquals(3, datos.sismosPorMes().stream().mapToLong(m -> m.getTotal()).sum());
    }

    @Test
    void elPeriodoDelDashboardSoloAceptaValoresPermitidos() {
        assertEquals(6, DashboardService.Filtro.de(null, 999).meses());
        assertEquals(12, DashboardService.Filtro.de(null, 12).meses());
        assertEquals(0, DashboardService.Filtro.de(-4, null).ubicacionId());
    }

    @Test
    void elDashboardMuestraFiltroYFichaDeIndicadores() throws Exception {
        Ubicacion u = ubicacion("ACTIVO");
        mvc.perform(get("/dashboard").param("ubicacionId", "" + u.getId()).param("meses", "12"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Aplicar filtro")))
                .andExpect(content().string(containsString("Ficha técnica de los indicadores")))
                .andExpect(content().string(containsString("Decisión que apoya")))
                .andExpect(content().string(containsString("Afectados reportados")))
                .andExpect(content().string(not(containsString("Importaciones"))));
    }
}
