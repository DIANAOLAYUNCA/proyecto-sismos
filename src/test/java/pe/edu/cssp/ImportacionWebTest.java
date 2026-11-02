package pe.edu.cssp;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.util.StreamUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cssp.web.error.ManejadorImportacion;

@AutoConfigureMockMvc
class ImportacionWebTest extends BaseDatosTest {
    @Autowired MockMvc mvc;

    private MockMultipartFile archivo(String nombre, byte[] contenido) {
        return new MockMultipartFile("archivo", nombre, "text/csv", contenido);
    }

    private byte[] incluido(String nombre) throws Exception {
        return StreamUtils.copyToByteArray(new ClassPathResource("datos/" + nombre).getInputStream());
    }

    @Test
    void laPantallaMuestraLosTresCatalogosYElBotonDeArchivosIncluidos() throws Exception {
        mvc.perform(get("/importacion")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Importar catálogos desde CSV")))
                .andExpect(content().string(containsString("Importar archivos incluidos")))
                .andExpect(content().string(containsString("Ubigeo (ubicaciones)")))
                .andExpect(content().string(containsString("Tipos de alerta")))
                .andExpect(content().string(containsString("Tipos de daño")))
                .andExpect(content().string(containsString("enctype=\"multipart/form-data\"")))
                .andExpect(content().string(containsString("codigo, nombre, color, prioridad, descripcion, estado")));
    }

    @Test
    void importarLosArchivosIncluidosMuestraElResumenDeLosTresCatalogos() throws Exception {
        mvc.perform(post("/importacion/incluidos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Resultado: Ubigeo (ubicaciones) (ubigeo.csv)")))
                .andExpect(content().string(containsString("Importadas: <strong>25</strong>")))
                .andExpect(content().string(containsString("Importadas: <strong>4</strong>")))
                .andExpect(content().string(containsString("Importadas: <strong>7</strong>")))
                .andExpect(content().string(containsString("DAN-07")))
                .andExpect(content().string(containsString("EMER")));
        assertTrue(ubicaciones.findByCodigoIgnoreCase("25").isPresent());
    }

    @Test
    void subirElArchivoDeUnCatalogoImportaSusFilas() throws Exception {
        mvc.perform(multipart("/importacion/tipo-dano").file(archivo("tipo_dano.csv", incluido("tipo_dano.csv"))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Importadas: <strong>7</strong>")));
        assertTrue(tiposDano.findByCodigoIgnoreCase("DAN-03").isPresent());
    }

    @Test
    void subirDosVecesElMismoArchivoLoOmite() throws Exception {
        byte[] datos = incluido("tipo_alerta.csv");
        mvc.perform(multipart("/importacion/tipo-alerta").file(archivo("tipo_alerta.csv", datos))).andExpect(status().isOk());
        mvc.perform(multipart("/importacion/tipo-alerta").file(archivo("tipo_alerta.csv", datos)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Omitidas: <strong>4</strong>")))
                .andExpect(content().string(containsString("Importadas: <strong>0</strong>")));
    }

    @Test
    void rechazaArchivosQueNoSonCsv() throws Exception {
        mvc.perform(multipart("/importacion/ubigeo").file(archivo("datos.txt", "codigo,nombre,tipo,estado\n".getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Solo se aceptan archivos con extensión .csv")));
    }

    @Test
    void pideSeleccionarUnArchivoCuandoVieneVacio() throws Exception {
        mvc.perform(multipart("/importacion/ubigeo").file(archivo("ubigeo.csv", new byte[0])))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Seleccione un archivo CSV")));
    }

    @Test
    void muestraElErrorCuandoFaltanColumnas() throws Exception {
        mvc.perform(multipart("/importacion/ubigeo").file(archivo("ubigeo.csv", "codigo,nombre\n01,Amazonas\n".getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("faltan columnas: tipo, estado")));
    }

    @Test
    void muestraLasFilasRechazadasConSuMotivo() throws Exception {
        String csv = "codigo,nombre,tipo,estado\n01,Amazonas,DEPARTAMENTO,ACTIVO\nXX,Mal tipo,PLANETA,ACTIVO\n";
        mvc.perform(multipart("/importacion/ubigeo").file(archivo("ubigeo.csv", csv.getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Importadas: <strong>1</strong>")))
                .andExpect(content().string(containsString("Rechazadas: <strong>1</strong>")))
                .andExpect(content().string(containsString("RECHAZADA")))
                .andExpect(content().string(containsString("El tipo no es válido")));
    }

    @Test
    void unCatalogoDesconocidoMuestraNoEncontrado() throws Exception {
        mvc.perform(multipart("/importacion/otro").file(archivo("x.csv", "a\n".getBytes(StandardCharsets.UTF_8))))
                .andExpect(content().string(containsString("No existe el catálogo solicitado.")));
    }

    @Test
    void elArchivoDemasiadoGrandeVuelveALaPantallaConUnMensaje() {
        ExtendedModelMap model = new ExtendedModelMap();
        String vista = new ManejadorImportacion().archivoMuyGrande(model);
        assertEquals("importacion/index", vista);
        assertTrue(model.get("error").toString().contains("tamaño máximo"));
        assertNotNull(model.get("catalogos"));
    }
}
