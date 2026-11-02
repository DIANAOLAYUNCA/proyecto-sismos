package pe.edu.cssp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.ReporteDano;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.service.ReporteDanoService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.ReporteDanoForm;

class ReporteDanoServiceTest extends BaseDatosTest {
    @Autowired ReporteDanoService servicio;

    private ReporteDanoForm form(Evento evento, TipoDano tipo, int cantidad) {
        ReporteDanoForm f = new ReporteDanoForm();
        f.setEventoId(evento.getId());
        f.setTipoDanoId(tipo.getId());
        f.setUbicacionId(evento.getUbicacion().getId());
        f.setFechaHora(haceParaFormulario(24));
        f.setCantidadAfectada(cantidad);
        f.setDescripcion("  Viviendas con grietas  ");
        return f;
    }

    @Test
    void registraReporteAsociadoAlSismoYSiempreComoRegistrado() {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDanoForm f = form(sismo, tipoDano("ACTIVO"), 45);
        f.setEstado("VERIFICADO");
        ReporteDano r = servicio.registrar(f);
        assertEquals("REGISTRADO", r.getEstado());
        assertEquals(sismo.getId(), r.getEvento().getId());
        assertEquals("Viviendas con grietas", r.getDescripcion());
        assertEquals(45, r.getCantidadAfectada());
        assertNull(r.getAfectacionNivel());
        assertEquals("sistema", r.getCreacionUsuario());
    }

    @Test
    void permiteAsociarUnNivelDeAfectacionOpcional() {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDanoForm f = form(sismo, tipoDano("ACTIVO"), 3);
        f.setAfectacionNivelId(nivel().getId());
        assertNotNull(servicio.registrar(f).getAfectacionNivel());
    }

    @Test
    void rechazaUnSismoEliminado() {
        Evento sismo = evento("INACTIVO", null);
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(form(sismo, tipoDano("ACTIVO"), 1)));
    }

    @Test
    void rechazaUnSismoInexistente() {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDanoForm f = form(sismo, tipoDano("ACTIVO"), 1);
        f.setEventoId(999999);
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(f));
    }

    @Test
    void rechazaUnTipoDeDanoInactivo() {
        Evento sismo = evento("REGISTRADO", null);
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(form(sismo, tipoDano("INACTIVO"), 1)));
    }

    @Test
    void rechazaUnaUbicacionInactiva() {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDanoForm f = form(sismo, tipoDano("ACTIVO"), 1);
        f.setUbicacionId(ubicacion("INACTIVO").getId());
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(f));
    }

    @Test
    void rechazaUnaFechaAnteriorAlSismo() {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDanoForm f = form(sismo, tipoDano("ACTIVO"), 1);
        f.setFechaHora(haceParaFormulario(72));
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> servicio.registrar(f));
        assertTrue(error.getMessage().contains("anteriores"));
    }

    @Test
    void rechazaUnaFechaFutura() {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDanoForm f = form(sismo, tipoDano("ACTIVO"), 1);
        f.setFechaHora(haceParaFormulario(-24));
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> servicio.registrar(f));
        assertTrue(error.getMessage().contains("futuras"));
    }

    @Test
    void rechazaUnaFechaConFormatoInvalido() {
        Evento sismo = evento("REGISTRADO", null);
        ReporteDanoForm f = form(sismo, tipoDano("ACTIVO"), 1);
        f.setFechaHora("2026-13-45T99:99");
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(f));
    }

    @Test
    void eliminarDescartaElReporteYYaNoCuentaEnElTotal() {
        Evento sismo = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        ReporteDano a = servicio.registrar(form(sismo, tipo, 40));
        ReporteDano b = servicio.registrar(form(sismo, tipo, 5));
        assertEquals(45, servicio.totalAfectados(servicio.listar(sismo.getId())));

        servicio.eliminar(a.getId());

        assertEquals("DESCARTADO", servicio.buscarPorId(a.getId()).getEstado());
        List<ReporteDano> restantes = servicio.listar(sismo.getId());
        assertEquals(2, restantes.size());
        assertEquals(5, servicio.totalAfectados(restantes));
        assertEquals("REGISTRADO", servicio.buscarPorId(b.getId()).getEstado());
        assertEquals("REGISTRADO", eventos.findById(sismo.getId()).orElseThrow().getEstado());
    }

    @Test
    void noSePuedeDescartarDosVecesNiEditarUnReporteDescartado() {
        Evento sismo = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        ReporteDano r = servicio.registrar(form(sismo, tipo, 10));
        servicio.eliminar(r.getId());
        assertThrows(ReglaNegocioException.class, () -> servicio.eliminar(r.getId()));
        assertThrows(ReglaNegocioException.class, () -> servicio.actualizar(r.getId(), form(sismo, tipo, 11)));
    }

    @Test
    void editarConservaElSismoOriginalAunqueSeEnvieOtro() {
        Evento original = evento("REGISTRADO", null);
        Evento otro = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        ReporteDano r = servicio.registrar(form(original, tipo, 10));
        ReporteDanoForm f = form(original, tipo, 12);
        f.setEventoId(otro.getId());
        f.setEstado("VERIFICADO");
        ReporteDano editado = servicio.actualizar(r.getId(), f);
        assertEquals(original.getId(), editado.getEvento().getId());
        assertEquals(12, editado.getCantidadAfectada());
        assertEquals("VERIFICADO", editado.getEstado());
        assertNotNull(editado.getActualizacionFecha());
    }

    @Test
    void listaSoloLosReportesDelSismoFiltrado() {
        Evento uno = evento("REGISTRADO", null);
        Evento dos = evento("REGISTRADO", null);
        TipoDano tipo = tipoDano("ACTIVO");
        servicio.registrar(form(uno, tipo, 1));
        servicio.registrar(form(dos, tipo, 2));
        servicio.registrar(form(dos, tipo, 3));
        assertEquals(1, servicio.listar(uno.getId()).size());
        assertEquals(2, servicio.listar(dos.getId()).size());
        assertTrue(servicio.listar(null).size() >= 3);
    }
}
