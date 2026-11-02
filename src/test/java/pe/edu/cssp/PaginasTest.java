package pe.edu.cssp;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cssp.entity.Estacion;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.entity.Ubicacion;

@AutoConfigureMockMvc
class PaginasTest extends BaseDatosTest {
    @Autowired MockMvc mvc;

    private void cargaConBarraYScript(String ruta) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().isOk())
                .andExpect(content().string(containsString("navbar-marca")))
                .andExpect(content().string(containsString("/js/app.js")))
                .andExpect(content().string(containsString("/css/estilos.css")));
    }

    @Test
    void todasLasPantallasDeTodosLosModulosCargan() throws Exception {
        Ubicacion ubicacion = ubicacion("ACTIVO");
        Estacion estacion = estacion(ubicacion);
        TipoAlerta alerta = alerta();
        TipoDano tipoDano = tipoDano("ACTIVO");
        Evento evento = evento("REGISTRADO", nivel());

        List<String> rutas = new ArrayList<>(List.of(
                "/eventos", "/eventos/nuevo", "/eventos/" + evento.getId(), "/eventos/" + evento.getId() + "/editar", "/eventos/" + evento.getId() + "/eliminar",
                "/estaciones", "/estaciones/nuevo", "/estaciones/" + estacion.getId(), "/estaciones/" + estacion.getId() + "/editar", "/estaciones/" + estacion.getId() + "/eliminar",
                "/ubicaciones", "/ubicaciones/nuevo", "/ubicaciones/" + ubicacion.getId(), "/ubicaciones/" + ubicacion.getId() + "/editar", "/ubicaciones/" + ubicacion.getId() + "/eliminar",
                "/tipos-alerta", "/tipos-alerta/nuevo", "/tipos-alerta/" + alerta.getId(), "/tipos-alerta/" + alerta.getId() + "/editar", "/tipos-alerta/" + alerta.getId() + "/eliminar",
                "/tipos-dano", "/tipos-dano/nuevo", "/tipos-dano/" + tipoDano.getId(), "/tipos-dano/" + tipoDano.getId() + "/editar", "/tipos-dano/" + tipoDano.getId() + "/eliminar",
                "/afectacionniveles", "/afectacionniveles/nuevo",
                "/reportes-dano", "/reportes-dano/nuevo",
                "/importacion"));
        for (String ruta : rutas) cargaConBarraYScript(ruta);
    }

    @Test
    void laPaginaDeNoEncontradoTambienTieneLaBarra() throws Exception {
        cargaConBarraYScript("/eventos/987654");
    }

    @Test
    void laBarraEnlazaConTodosLosModulos() throws Exception {
        String[] enlaces = {"/eventos", "/reportes-dano", "/estaciones", "/ubicaciones", "/afectacionniveles",
                "/tipos-alerta", "/tipos-dano", "/importacion"};
        for (String enlace : enlaces) {
            mvc.perform(get("/afectacionniveles"))
                    .andExpect(content().string(containsString("data-ruta=\"" + enlace + "\"")));
        }
    }
}
