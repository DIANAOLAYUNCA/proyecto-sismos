package pe.edu.cssp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.service.AfectacionNivelService;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.AfectacionNivelForm;

class AfectacionNivelServiceTest extends BaseDatosTest {
    @Autowired AfectacionNivelService servicio;

    private AfectacionNivelForm form(String codigo, String nombre) {
        AfectacionNivelForm f = new AfectacionNivelForm();
        f.setCodigo(codigo); f.setNombre(nombre);
        return f;
    }

    @Test
    void registraConCodigoEnMayusculasYAuditoria() {
        AfectacionNivel n = servicio.registrar(form("  sev ", "  Severo "));
        assertEquals("SEV", n.getCodigo());
        assertEquals("Severo", n.getNombre());
        assertEquals("sistema", n.getCreacionUsuario());
        assertNotNull(n.getCreacionFecha());
    }

    @Test
    void rechazaCodigoDuplicadoSinImportarMayusculas() {
        servicio.registrar(form("SEV", "Severo"));
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(form("sev", "Otro")));
    }

    @Test
    void noPermiteQuedarseConElCodigoDeOtroNivelAlEditar() {
        servicio.registrar(form("AAA", "Uno"));
        AfectacionNivel b = servicio.registrar(form("BBB", "Dos"));
        assertThrows(ReglaNegocioException.class, () -> servicio.actualizar(b.getId(), form("aaa", "Dos")));
    }

    @Test
    void permiteEditarConservandoSuPropioCodigo() {
        AfectacionNivel n = servicio.registrar(form("CCC", "Tres"));
        AfectacionNivel editado = servicio.actualizar(n.getId(), form("ccc", "Tres modificado"));
        assertEquals("Tres modificado", editado.getNombre());
        assertNotNull(editado.getActualizacionFecha());
    }

    @Test
    void eliminaUnNivelSinUso() {
        AfectacionNivel n = servicio.registrar(form("DDD", "Libre"));
        servicio.eliminar(n.getId());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.buscarPorId(n.getId()));
    }

    @Test
    void noEliminaUnNivelQueUsaUnSismo() {
        AfectacionNivel n = nivel();
        evento("REGISTRADO", n);
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> servicio.eliminar(n.getId()));
        assertTrue(error.getMessage().contains("1 sismo"));
        assertEquals(n.getId(), servicio.buscarPorId(n.getId()).getId());
    }

    @Test
    void eliminarUnNivelNoBorraLosSismosDeOtros() {
        AfectacionNivel usado = nivel();
        var sismo = evento("REGISTRADO", usado);
        AfectacionNivel libre = nivel();
        servicio.eliminar(libre.getId());
        assertTrue(eventos.findById(sismo.getId()).isPresent());
    }
}
