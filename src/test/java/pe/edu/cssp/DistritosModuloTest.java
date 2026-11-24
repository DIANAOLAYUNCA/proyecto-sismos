package pe.edu.cssp;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cssp.entity.Departamento;
import pe.edu.cssp.entity.Distrito;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.Provincia;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.DepartamentoRepository;
import pe.edu.cssp.repository.DistritoRepository;
import pe.edu.cssp.repository.ProvinciaRepository;
import pe.edu.cssp.service.DistritoService;
import pe.edu.cssp.service.ReporteDanoService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.ReporteDanoForm;

@AutoConfigureMockMvc
class DistritosModuloTest extends BaseDatosTest {
    @Autowired MockMvc mvc;
    @Autowired DepartamentoRepository departamentos;
    @Autowired ProvinciaRepository provincias;
    @Autowired DistritoRepository distritos;
    @Autowired DistritoService distritoService;
    @Autowired ReporteDanoService reportes;

    // ---------- Listado: paginación, orden y filtros ----------

    @Test
    void paginaElListadoYMuestraElTotal() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");
        for (int i = 1; i <= 25; i++) distrito(provincia, String.format("%02d", i), "Distrito " + String.format("%02d", i));

        mvc.perform(get("/distritos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Distritos encontrados: <span>25</span>")))
                .andExpect(content().string(containsString("Distrito 01")))
                .andExpect(content().string(containsString("Distrito 10")))
                .andExpect(content().string(not(containsString("Distrito 11"))));
        mvc.perform(get("/distritos").param("pagina", "3")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Distrito 25")))
                .andExpect(content().string(not(containsString("Distrito 20"))));
    }

    @Test
    void elTamanoDePaginaEsConfigurableYSoloAceptaLosValoresPermitidos() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");
        for (int i = 1; i <= 25; i++) distrito(provincia, String.format("%02d", i), "Distrito " + String.format("%02d", i));

        mvc.perform(get("/distritos").param("tamano", "20")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Distrito 20")))
                .andExpect(content().string(not(containsString("Distrito 21"))));
        mvc.perform(get("/distritos").param("tamano", "7")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Distrito 10")))
                .andExpect(content().string(not(containsString("Distrito 11"))));
    }

    @Test
    void ordenaPorLaColumnaPedidaYIgnoraColumnasQueNoExisten() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");
        distrito(provincia, "01", "Alfa");
        distrito(provincia, "02", "Zeta");

        String descendente = mvc.perform(get("/distritos").param("orden", "nombre").param("dir", "desc"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(descendente.indexOf("Zeta") < descendente.indexOf("Alfa"));

        String ascendente = mvc.perform(get("/distritos").param("orden", "nombre").param("dir", "asc"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(ascendente.indexOf("Alfa") < ascendente.indexOf("Zeta"));

        mvc.perform(get("/distritos").param("orden", "password;drop table").param("dir", "x")).andExpect(status().isOk());
    }

    @Test
    void filtraPorTextoDepartamentoProvinciaYEstado() throws Exception {
        Departamento ancash = departamento("02", "Áncash");
        Departamento cusco = departamento("08", "Cusco");
        Provincia santa = provincia(ancash, "06", "Santa");
        Provincia urubamba = provincia(cusco, "07", "Urubamba");
        distrito(santa, "01", "Chimbote");
        distrito(santa, "02", "Nepeña");
        Distrito machu = distrito(urubamba, "05", "Machupicchu");
        machu.setEstado("INACTIVO");
        distritos.save(machu);

        mvc.perform(get("/distritos").param("q", "chimb")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Chimbote")))
                .andExpect(content().string(not(containsString("Nepeña"))));
        mvc.perform(get("/distritos").param("q", "080705")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Machupicchu")))
                .andExpect(content().string(not(containsString("Chimbote"))));
        mvc.perform(get("/distritos").param("departamentoId", String.valueOf(ancash.getId()))).andExpect(status().isOk())
                .andExpect(content().string(containsString("Nepeña")))
                .andExpect(content().string(not(containsString("Machupicchu"))));
        mvc.perform(get("/distritos").param("provinciaId", String.valueOf(urubamba.getId()))).andExpect(status().isOk())
                .andExpect(content().string(containsString("Machupicchu")));
        mvc.perform(get("/distritos").param("estado", "INACTIVO")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Machupicchu")))
                .andExpect(content().string(not(containsString("Chimbote"))));
    }

    @Test
    void losEnlacesDePaginacionConservanLosFiltros() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");
        for (int i = 1; i <= 25; i++) distrito(provincia, String.format("%02d", i), "Local " + String.format("%02d", i));

        mvc.perform(get("/distritos").param("q", "Local").param("tamano", "10")).andExpect(status().isOk())
                .andExpect(content().string(containsString("q=Local")))
                .andExpect(content().string(containsString("pagina=2")));
    }

    // ---------- CRUD ----------

    @Test
    void registraUnDistritoYLoMuestraEnElDetalle() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");

        mvc.perform(post("/distritos")
                        .param("provinciaId", String.valueOf(provincia.getId()))
                        .param("codigo", "09").param("nombre", "Nuevo Distrito").param("estado", "ACTIVO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("exito"));

        Distrito creado = distritos.findByProvincia_IdAndCodigo(provincia.getId(), "09").orElseThrow();
        assertEquals("020609", creado.getUbigeo());
        mvc.perform(get("/distritos/" + creado.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString("020609")))
                .andExpect(content().string(containsString("Nuevo Distrito")));
    }

    @Test
    void rechazaCodigosInvalidosYRepetidosEnLaMismaProvincia() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");
        distrito(provincia, "01", "Chimbote");

        mvc.perform(post("/distritos").param("provinciaId", String.valueOf(provincia.getId()))
                        .param("codigo", "1").param("nombre", "X").param("estado", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2 dígitos")));
        mvc.perform(post("/distritos").param("provinciaId", String.valueOf(provincia.getId()))
                        .param("codigo", "01").param("nombre", "Repetido").param("estado", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ya existe un distrito con ese código")));
        mvc.perform(post("/distritos").param("codigo", "02").param("nombre", "Sin provincia").param("estado", "ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Seleccione la provincia")));
        assertEquals(1, distritos.count());
    }

    @Test
    void permiteElMismoCodigoEnProvinciasDistintas() throws Exception {
        Departamento ancash = departamento("02", "Áncash");
        Provincia santa = provincia(ancash, "06", "Santa");
        Provincia huaraz = provincia(ancash, "01", "Huaraz");
        distrito(santa, "01", "Chimbote");

        mvc.perform(post("/distritos").param("provinciaId", String.valueOf(huaraz.getId()))
                        .param("codigo", "01").param("nombre", "Huaraz").param("estado", "ACTIVO"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, distritos.count());
    }

    @Test
    void editaUnDistritoSinChocarConsigoMismo() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");
        Distrito d = distrito(provincia, "01", "Chimbote");

        mvc.perform(get("/distritos/" + d.getId() + "/editar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar distrito")));
        mvc.perform(post("/distritos/" + d.getId() + "/editar").param("id", String.valueOf(d.getId()))
                        .param("provinciaId", String.valueOf(provincia.getId()))
                        .param("codigo", "01").param("nombre", "Chimbote Centro").param("estado", "ACTIVO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("exito"));
        assertEquals("Chimbote Centro", distritos.findById(d.getId()).orElseThrow().getNombre());
    }

    @Test
    void eliminarPasaElDistritoAInactivoSinBorrarlo() throws Exception {
        Provincia provincia = provincia(departamento("02", "Áncash"), "06", "Santa");
        Distrito d = distrito(provincia, "01", "Chimbote");

        mvc.perform(get("/distritos/" + d.getId() + "/eliminar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("INACTIVO")));
        mvc.perform(post("/distritos/" + d.getId() + "/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("exito"));
        assertEquals("INACTIVO", distritos.findById(d.getId()).orElseThrow().getEstado());
        assertEquals(1, distritos.count());

        mvc.perform(post("/distritos/" + d.getId() + "/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void unDistritoQueNoExisteMuestraNoEncontrado() throws Exception {
        mvc.perform(get("/distritos/987654")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No existe el distrito solicitado.")));
    }

    // ---------- Distrito dependiente del departamento (sismos y estaciones) ----------

    @Test
    void elDesplegableSoloOfreceLosDistritosActivosDelDepartamentoDeLaUbicacion() throws Exception {
        Departamento ancash = departamento("02", "Áncash");
        Departamento cusco = departamento("08", "Cusco");
        Provincia santa = provincia(ancash, "06", "Santa");
        Provincia urubamba = provincia(cusco, "07", "Urubamba");
        distrito(santa, "01", "Chimbote");
        Distrito inactivo = distrito(santa, "02", "Nepeña");
        inactivo.setEstado("INACTIVO");
        distritos.save(inactivo);
        distrito(urubamba, "05", "Machupicchu");
        Ubicacion ubicacionAncash = ubicacionConCodigo("02");

        mvc.perform(get("/distritos/por-ubicacion/" + ubicacionAncash.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Chimbote"))
                .andExpect(jsonPath("$[0].provincia").value("Santa"));
    }

    @Test
    void sinUbicacionValidaElDesplegableQuedaVacio() throws Exception {
        mvc.perform(get("/distritos/por-ubicacion/987654")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void elDistritoEsOpcionalPeroSiSeEligeDebePertenecerAlDepartamentoYEstarActivo() {
        Departamento ancash = departamento("02", "Áncash");
        Departamento cusco = departamento("08", "Cusco");
        Distrito chimbote = distrito(provincia(ancash, "06", "Santa"), "01", "Chimbote");
        Distrito machu = distrito(provincia(cusco, "07", "Urubamba"), "05", "Machupicchu");
        Ubicacion ubicacionAncash = ubicacionConCodigo("02");

        assertNull(distritoService.elegir(null, ubicacionAncash, null));
        assertEquals(chimbote.getId(), distritoService.elegir(chimbote.getId(), ubicacionAncash, null).getId());
        assertThrows(ReglaNegocioException.class, () -> distritoService.elegir(machu.getId(), ubicacionAncash, null));
        assertThrows(ReglaNegocioException.class, () -> distritoService.elegir(987654, ubicacionAncash, null));

        chimbote.setEstado("INACTIVO");
        distritos.save(chimbote);
        assertThrows(ReglaNegocioException.class, () -> distritoService.elegir(chimbote.getId(), ubicacionAncash, null));
        assertEquals(chimbote.getId(), distritoService.elegir(chimbote.getId(), ubicacionAncash, chimbote).getId(),
                "un registro puede conservar su distrito aunque se haya dado de baja después");
    }

    @Test
    void registraUnaEstacionConDistritoYLoMuestraEnElDetalle() throws Exception {
        Provincia santa = provincia(departamento("02", "Áncash"), "06", "Santa");
        Distrito chimbote = distrito(santa, "01", "Chimbote");
        Ubicacion ubicacion = ubicacionConCodigo("02");

        mvc.perform(get("/estaciones/nuevo")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Distrito (opcional)")));
        mvc.perform(post("/estaciones").param("codigo", "EST-D1").param("nombre", "Estación con distrito")
                        .param("ubicacionId", String.valueOf(ubicacion.getId())).param("distritoId", String.valueOf(chimbote.getId()))
                        .param("latitud", "-9.07").param("longitud", "-78.59")
                        .param("instalacionFecha", "2020-01-01").param("estado", "ACTIVA"))
                .andExpect(status().is3xxRedirection());

        var estacion = estaciones.findByCodigoIgnoreCase("EST-D1").orElseThrow();
        assertEquals(chimbote.getId(), estacion.getDistrito().getId());
        mvc.perform(get("/estaciones/" + estacion.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString("Santa - Chimbote")));
    }

    @Test
    void rechazaUnaEstacionConUnDistritoDeOtroDepartamento() throws Exception {
        Distrito machu = distrito(provincia(departamento("08", "Cusco"), "07", "Urubamba"), "05", "Machupicchu");
        Ubicacion ubicacionAncash = ubicacionConCodigo("02");

        mvc.perform(post("/estaciones").param("codigo", "EST-D2").param("nombre", "Mal ubicada")
                        .param("ubicacionId", String.valueOf(ubicacionAncash.getId())).param("distritoId", String.valueOf(machu.getId()))
                        .param("latitud", "-9.07").param("longitud", "-78.59")
                        .param("instalacionFecha", "2020-01-01").param("estado", "ACTIVA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("no pertenece a la ubicación")));
        assertTrue(estaciones.findByCodigoIgnoreCase("EST-D2").isEmpty());
    }

    @Test
    void elFormularioDeSismosOfreceElDistrito() throws Exception {
        mvc.perform(get("/eventos/nuevo")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Distrito (opcional)")))
                .andExpect(content().string(containsString("distrito-dependiente.js")));
    }

    // ---------- Paginación de reportes de daño ----------

    @Test
    void elTotalDeAfectadosSumaTodosLosReportesDelFiltroNoSoloLaPagina() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        for (int i = 1; i <= 12; i++) registrarReporte(sismo, tipo, i);

        mvc.perform(get("/reportes-dano")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Reportes encontrados: <span>12</span>")))
                .andExpect(content().string(containsString("<strong>78</strong>")));
        mvc.perform(get("/reportes-dano").param("pagina", "2")).andExpect(status().isOk())
                .andExpect(content().string(containsString("<strong>78</strong>")));
    }

    @Test
    void elListadoDeReportesMuestraPrimeroLosMasRecientesYPermiteCambiarElOrden() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        registrarReporte(sismo, tipo, 111);
        registrarReporte(sismo, tipo, 222);

        String reciente = mvc.perform(get("/reportes-dano")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(reciente.indexOf(">222<") < reciente.indexOf(">111<"));

        String porAfectados = mvc.perform(get("/reportes-dano").param("orden", "afectados").param("dir", "asc"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(porAfectados.indexOf(">111<") < porAfectados.indexOf(">222<"));
    }

    @Test
    void elFiltroPorSismoSeConservaAlPaginar() throws Exception {
        Evento uno = evento("REGISTRADO", null);
        Evento otro = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        for (int i = 1; i <= 11; i++) registrarReporte(uno, tipo, 1);
        registrarReporte(otro, tipo, 500);

        mvc.perform(get("/reportes-dano").param("eventoId", String.valueOf(uno.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Reportes encontrados: <span>11</span>")))
                .andExpect(content().string(containsString("eventoId=" + uno.getId())))
                .andExpect(content().string(containsString("<strong>11</strong>")));
    }

    // ---------- Paginación de niveles ----------

    @Test
    void elListadoDeNivelesTambienEstaPaginado() throws Exception {
        for (int i = 0; i < 12; i++) nivel();

        mvc.perform(get("/afectacionniveles")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Niveles encontrados: <span>12</span>")))
                .andExpect(content().string(containsString("pagina=2")));
    }

    // ---------- Apoyo ----------

    private void registrarReporte(Evento sismo, TipoDano tipo, int cantidad) {
        ReporteDanoForm f = new ReporteDanoForm();
        f.setEventoId(sismo.getId()); f.setTipoDanoId(tipo.getId()); f.setUbicacionId(sismo.getUbicacion().getId());
        f.setFechaHora(haceParaFormulario(24)); f.setCantidadAfectada(cantidad); f.setDescripcion("Daños de prueba");
        reportes.registrar(f);
    }

    private Ubicacion ubicacionConCodigo(String codigo) {
        Ubicacion u = ubicacion("ACTIVO");
        u.setCodigo(codigo);
        return ubicaciones.save(u);
    }

    private Departamento departamento(String codigo, String nombre) {
        Departamento d = new Departamento();
        d.setCodigo(codigo); d.setNombre(nombre);
        d.setCreacionFecha(LocalDateTime.now()); d.setCreacionUsuario("prueba");
        return departamentos.save(d);
    }

    private Provincia provincia(Departamento departamento, String codigo, String nombre) {
        Provincia p = new Provincia();
        p.setCodigo(codigo); p.setNombre(nombre); p.setDepartamento(departamento);
        p.setCreacionFecha(LocalDateTime.now()); p.setCreacionUsuario("prueba");
        return provincias.save(p);
    }

    private Distrito distrito(Provincia provincia, String codigo, String nombre) {
        Distrito d = new Distrito();
        d.setCodigo(codigo); d.setNombre(nombre); d.setProvincia(provincia);
        d.setCreacionFecha(LocalDateTime.now()); d.setCreacionUsuario("prueba");
        return distritos.save(d);
    }
}
