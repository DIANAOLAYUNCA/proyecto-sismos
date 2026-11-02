package pe.edu.cssp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import pe.edu.cssp.service.importacion.CsvLector;
import pe.edu.cssp.web.error.ReglaNegocioException;

class CsvLectorTest {

    private CsvLector.Contenido leer(String texto) throws IOException {
        return CsvLector.leer(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void leeFinesDeLineaWindowsYSaltaLineasEnBlancoConservandoLaNumeracion() throws IOException {
        CsvLector.Contenido c = leer("codigo,estado\r\n01,ACTIVO\r\n\r\n02,ACTIVO\r\n");
        assertEquals(2, c.getFilas().size());
        assertEquals("ACTIVO", c.getFilas().get(0).getValores().get(1));
        assertEquals(2, c.getFilas().get(0).getLinea());
        assertEquals(4, c.getFilas().get(1).getLinea());
    }

    @Test
    void conservaLosCerosInicialesYLasTildes() throws IOException {
        CsvLector.Contenido c = leer("codigo,nombre\n01,Áncash\n07,Callao\n");
        assertEquals("01", c.getFilas().get(0).getValores().get(0));
        assertEquals("Áncash", c.getFilas().get(0).getValores().get(1));
    }

    @Test
    void ignoraElBomYNormalizaLosEncabezados() throws IOException {
        CsvLector.Contenido c = leer("﻿ Codigo , NOMBRE\nA,B\n");
        assertEquals("codigo", c.getEncabezados().get(0));
        assertEquals("nombre", c.getEncabezados().get(1));
    }

    @Test
    void aceptaCamposEntreComillasConComasYComillasDobles() throws IOException {
        CsvLector.Contenido c = leer("codigo,descripcion\nA,\"Agua, luz y \"\"gas\"\"\"\n");
        assertEquals("Agua, luz y \"gas\"", c.getFilas().get(0).getValores().get(1));
    }

    @Test
    void rechazaComillasSinCerrar() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class, () -> leer("codigo,nombre\nA,\"abierta\n"));
        assertTrue(error.getMessage().contains("Línea 2"));
    }

    @Test
    void rechazaUnArchivoVacio() {
        assertThrows(ReglaNegocioException.class, () -> leer(""));
        assertThrows(ReglaNegocioException.class, () -> leer("\r\n   \r\n"));
    }

    @Test
    void rechazaUnArchivoQueNoEstaEnUtf8() {
        byte[] latin1 = "codigo,nombre\nA,Áncash\n".getBytes(StandardCharsets.ISO_8859_1);
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> CsvLector.leer(new ByteArrayInputStream(latin1)));
        assertTrue(error.getMessage().contains("UTF-8"));
    }
}
