package pe.edu.cssp;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.ReporteDano;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.service.ReporteDanoService;
import pe.edu.cssp.web.form.ReporteDanoForm;

@AutoConfigureMockMvc
class ModulosWebTest extends BaseDatosTest {
    @Autowired MockMvc mvc;
    @Autowired ReporteDanoService reportes;

    private ReporteDano reporte(Evento sismo, TipoDano tipo, int cantidad) {
        ReporteDanoForm f = new ReporteDanoForm();
        f.setEventoId(sismo.getId()); f.setTipoDanoId(tipo.getId()); f.setUbicacionId(sismo.getUbicacion().getId());
        f.setFechaHora(haceParaFormulario(24)); f.setCantidadAfectada(cantidad); f.setDescripcion("Daños de prueba");
        return reportes.registrar(f);
    }

    // ---------- Niveles de afectación ----------

    @Test
    void listaYDetalleDeNiveles() throws Exception {
        AfectacionNivel n = nivel();
        mvc.perform(get("/afectacionniveles")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Lista de niveles de afectación")))
                .andExpect(content().string(containsString(n.getCodigo())));
        mvc.perform(get("/afectacionniveles/" + n.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString(n.getNombre())));
    }

    @Test
    void formulariosDeNiveles() throws Exception {
        AfectacionNivel n = nivel();
        mvc.perform(get("/afectacionniveles/nuevo")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Nuevo nivel de afectación")));
        mvc.perform(get("/afectacionniveles/" + n.getId() + "/editar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar nivel de afectación")))
                .andExpect(content().string(containsString(n.getCodigo())));
        mvc.perform(get("/afectacionniveles/" + n.getId() + "/eliminar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Confirmar eliminación")));
    }

    @Test
    void registraUnNivelYRedirigeAlDetalle() throws Exception {
        mvc.perform(post("/afectacionniveles").param("codigo", "web1").param("nombre", "Nivel web"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("exito"));
    }

    @Test
    void muestraLosErroresDeValidacionDelNivel() throws Exception {
        mvc.perform(post("/afectacionniveles").param("codigo", "").param("nombre", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("El código es obligatorio.")))
                .andExpect(content().string(containsString("El nombre es obligatorio.")));
    }

    @Test
    void rechazaUnCodigoDeNivelRepetidoDesdeElFormulario() throws Exception {
        AfectacionNivel n = nivel();
        mvc.perform(post("/afectacionniveles").param("codigo", n.getCodigo().toLowerCase()).param("nombre", "Repetido"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ya está registrado")));
    }

    @Test
    void noEliminaUnNivelEnUsoYAvisaElMotivo() throws Exception {
        AfectacionNivel n = nivel();
        evento("REGISTRADO", n);
        mvc.perform(post("/afectacionniveles/" + n.getId() + "/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", containsString("lo usan 1 sismo(s)")));
    }

    @Test
    void eliminaUnNivelLibre() throws Exception {
        AfectacionNivel n = nivel();
        mvc.perform(post("/afectacionniveles/" + n.getId() + "/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("exito"));
    }

    @Test
    void unNivelInexistenteMuestraLaPaginaNoEncontrado() throws Exception {
        mvc.perform(get("/afectacionniveles/987654"))
                .andExpect(content().string(containsString("No existe el nivel de afectación solicitado.")));
    }

    // ---------- Reportes de daño ----------

    @Test
    void listaDeReportesConTotalYFiltroPorSismo() throws Exception {
        Evento uno = evento("REGISTRADO", null);
        Evento dos = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        reporte(uno, tipo, 40);
        reporte(dos, tipo, 7);
        mvc.perform(get("/reportes-dano").param("eventoId", String.valueOf(uno.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(uno.getCodigo())))
                .andExpect(content().string(not(containsString("<td>" + dos.getCodigo() + "</td>"))))
                .andExpect(content().string(containsString("<strong>40</strong>")));
        mvc.perform(get("/reportes-dano")).andExpect(status().isOk());
    }

    @Test
    void formularioNuevoPrecargaElSismoYSuUbicacion() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        tipoDano("ACTIVO");
        mvc.perform(get("/reportes-dano/nuevo").param("eventoId", String.valueOf(sismo.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nuevo reporte de daño")))
                .andExpect(content().string(containsString("value=\"" + sismo.getId() + "\" selected")));
    }

    @Test
    void detalleEdicionYConfirmacionDeUnReporte() throws Exception {
        Evento sismo = evento("REGISTRADO", nivel());
        ReporteDano r = reporte(sismo, tipoDano("ACTIVO"), 12);
        mvc.perform(get("/reportes-dano/" + r.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString("Daños de prueba")))
                .andExpect(content().string(containsString("Reportes de este sismo")));
        mvc.perform(get("/reportes-dano/" + r.getId() + "/editar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar reporte de daño")))
                .andExpect(content().string(containsString("Da&ntilde;os de prueba")));
        mvc.perform(get("/reportes-dano/" + r.getId() + "/eliminar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("DESCARTADO")));
    }

    @Test
    void registraUnReporteValidoYRedirigeAlDetalle() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        mvc.perform(post("/reportes-dano")
                        .param("eventoId", String.valueOf(sismo.getId()))
                        .param("tipoDanoId", String.valueOf(tipo.getId()))
                        .param("ubicacionId", String.valueOf(sismo.getUbicacion().getId()))
                        .param("fechaHora", haceParaFormulario(24))
                        .param("cantidadAfectada", "30")
                        .param("descripcion", "Colapso parcial de viviendas")
                        .param("estado", "REGISTRADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("exito"));
    }

    @Test
    void muestraTodosLosErroresDeValidacionDelReporte() throws Exception {
        mvc.perform(post("/reportes-dano").param("estado", "REGISTRADO").param("cantidadAfectada", "-5"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Seleccione el sismo asociado.")))
                .andExpect(content().string(containsString("Seleccione un tipo de daño.")))
                .andExpect(content().string(containsString("Seleccione una ubicación.")))
                .andExpect(content().string(containsString("La fecha y hora son obligatorias.")))
                .andExpect(content().string(containsString("La cantidad afectada no puede ser negativa.")))
                .andExpect(content().string(containsString("La descripción es obligatoria.")));
    }

    @Test
    void rechazaUnReporteParaUnSismoEliminadoDesdeElFormulario() throws Exception {
        Evento sismo = evento("INACTIVO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        mvc.perform(post("/reportes-dano")
                        .param("eventoId", String.valueOf(sismo.getId()))
                        .param("tipoDanoId", String.valueOf(tipo.getId()))
                        .param("ubicacionId", String.valueOf(sismo.getUbicacion().getId()))
                        .param("fechaHora", haceParaFormulario(24))
                        .param("cantidadAfectada", "1")
                        .param("descripcion", "x")
                        .param("estado", "REGISTRADO"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Seleccione un sismo vigente")));
    }

    @Test
    void rechazaUnTipoDeDanoInactivoDesdeElFormulario() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        TipoDano inactivo = tipoDano("INACTIVO");
        mvc.perform(post("/reportes-dano")
                        .param("eventoId", String.valueOf(sismo.getId()))
                        .param("tipoDanoId", String.valueOf(inactivo.getId()))
                        .param("ubicacionId", String.valueOf(sismo.getUbicacion().getId()))
                        .param("fechaHora", haceParaFormulario(24))
                        .param("cantidadAfectada", "1")
                        .param("descripcion", "x")
                        .param("estado", "REGISTRADO"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Seleccione un tipo de daño activo.")));
    }

    @Test
    void muestraElErrorDeReglaDeNegocioPorFechaFutura() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        mvc.perform(post("/reportes-dano")
                        .param("eventoId", String.valueOf(sismo.getId()))
                        .param("tipoDanoId", String.valueOf(tipo.getId()))
                        .param("ubicacionId", String.valueOf(sismo.getUbicacion().getId()))
                        .param("fechaHora", haceParaFormulario(-48))
                        .param("cantidadAfectada", "1")
                        .param("descripcion", "x")
                        .param("estado", "REGISTRADO"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("no pueden ser futuras")));
    }

    @Test
    void descartaUnReporteYYaNoOfreceEditarNiEliminar() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDano r = reporte(sismo, tipoDano("ACTIVO"), 9);
        mvc.perform(post("/reportes-dano/" + r.getId() + "/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("exito"));
        mvc.perform(get("/reportes-dano/" + r.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString("DESCARTADO")))
                .andExpect(content().string(not(containsString("/editar"))));
    }

    @Test
    void elDetalleDelSismoEnlazaConSusReportesDeDano() throws Exception {
        Evento sismo = evento("REGISTRADO", null);
        mvc.perform(get("/eventos/" + sismo.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString("Registrar reporte de daño")))
                .andExpect(content().string(containsString("Ver reportes de daño")));
    }
}
