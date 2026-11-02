package pe.edu.cssp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.service.ImportacionCatalogosService;
import pe.edu.cssp.service.importacion.ResultadoImportacion;
import pe.edu.cssp.service.importacion.ResultadoImportacion.Estado;
import pe.edu.cssp.service.importacion.TipoCatalogo;
import pe.edu.cssp.web.error.ReglaNegocioException;

class ImportacionCatalogosServiceTest extends BaseDatosTest {
    @Autowired ImportacionCatalogosService servicio;

    private ResultadoImportacion importar(TipoCatalogo tipo, String csv) {
        return servicio.importar(tipo, "prueba.csv", new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
    }

    private ResultadoImportacion.Linea linea(ResultadoImportacion r, int numero) {
        return r.getLineas().stream().filter(l -> l.getNumero() == numero).findFirst().orElseThrow();
    }

    // ---------- Archivos reales del curso ----------

    @Test
    void importaLos25RegistrosDeUbigeo() {
        long antes = ubicaciones.count();
        ResultadoImportacion r = servicio.importarIncluido(TipoCatalogo.UBIGEO);
        assertEquals(25, r.getTotal());
        assertEquals(25, r.getImportadas());
        assertEquals(0, r.getRechazadas());
        assertEquals(antes + 25, ubicaciones.count());
        Ubicacion amazonas = ubicaciones.findByCodigoIgnoreCase("01").orElseThrow();
        assertEquals("Amazonas", amazonas.getNombre());
        assertEquals("DEPARTAMENTO", amazonas.getTipo());
        assertEquals("ACTIVO", amazonas.getEstado());
        assertEquals("Áncash", ubicaciones.findByCodigoIgnoreCase("02").orElseThrow().getNombre());
        Ubicacion callao = ubicaciones.findByCodigoIgnoreCase("07").orElseThrow();
        assertEquals("Callao", callao.getNombre());
        assertEquals("PROVINCIA_CONSTITUCIONAL", callao.getTipo());
        assertEquals("importacion", callao.getCreacionUsuario());
    }

    @Test
    void importaLos4TiposDeAlerta() {
        ResultadoImportacion r = servicio.importarIncluido(TipoCatalogo.TIPO_ALERTA);
        assertEquals(4, r.getTotal());
        assertEquals(4, r.getImportadas());
        TipoAlerta emergencia = alertas.findByCodigoIgnoreCase("EMER").orElseThrow();
        assertEquals("Emergencia", emergencia.getNombre());
        assertEquals("ROJO", emergencia.getColor());
        assertEquals(4, emergencia.getPrioridad());
        assertTrue(emergencia.getDescripcion().startsWith("Evento con afectación confirmada"));
        assertEquals(1, alertas.findByCodigoIgnoreCase("INFO").orElseThrow().getPrioridad());
    }

    @Test
    void importaLos7TiposDeDano() {
        ResultadoImportacion r = servicio.importarIncluido(TipoCatalogo.TIPO_DANO);
        assertEquals(7, r.getTotal());
        assertEquals(7, r.getImportadas());
        TipoDano infra = tiposDano.findByCodigoIgnoreCase("DAN-04").orElseThrow();
        assertEquals("Infraestructura", infra.getNombre());
        assertEquals("INFRAESTRUCTURA", infra.getSector());
        assertEquals("ACTIVO", infra.getEstado());
        assertEquals("SERVICIOS_BASICOS", tiposDano.findByCodigoIgnoreCase("DAN-05").orElseThrow().getSector());
    }

    @Test
    void importarDosVecesNoDuplicaNiModifica() {
        servicio.importarIncluido(TipoCatalogo.UBIGEO);
        long total = ubicaciones.count();
        ResultadoImportacion segunda = servicio.importarIncluido(TipoCatalogo.UBIGEO);
        assertEquals(0, segunda.getImportadas());
        assertEquals(25, segunda.getOmitidas());
        assertEquals(0, segunda.getRechazadas());
        assertEquals(total, ubicaciones.count());
    }

    // ---------- Reglas de validación ----------

    @Test
    void importaLasFilasValidasYReportaLasInvalidasDelUbigeo() {
        ResultadoImportacion r = importar(TipoCatalogo.UBIGEO, String.join("\r\n",
                "codigo,nombre,tipo,estado",
                "A1,Uno,DEPARTAMENTO,ACTIVO",
                "A2,,DEPARTAMENTO,ACTIVO",
                "A3,Tres,REGION,ACTIVO",
                "A4,Cuatro,DEPARTAMENTO,SUSPENDIDO",
                "A1,Repetido,DEPARTAMENTO,ACTIVO",
                ",SinCodigo,DEPARTAMENTO,ACTIVO",
                "A5,Cinco,DEPARTAMENTO",
                "CODIGOMUYLARGO1,Largo,DEPARTAMENTO,ACTIVO",
                "a6,Seis,departamento,activo"));
        assertEquals(1 + 1, r.getImportadas());
        assertEquals(7, r.getRechazadas());
        assertEquals(Estado.IMPORTADA, linea(r, 2).getEstado());
        assertTrue(linea(r, 3).getMensaje().contains("nombre es obligatorio"));
        assertTrue(linea(r, 4).getMensaje().contains("tipo no es válido"));
        assertTrue(linea(r, 5).getMensaje().contains("estado no es válido"));
        assertTrue(linea(r, 6).getMensaje().contains("repetido dentro del archivo"));
        assertTrue(linea(r, 7).getMensaje().contains("código es obligatorio"));
        assertTrue(linea(r, 8).getMensaje().contains("Se esperaban 4 columnas"));
        assertTrue(linea(r, 9).getMensaje().contains("supera los 10"));
        assertEquals(Estado.IMPORTADA, linea(r, 10).getEstado());
        assertTrue(ubicaciones.findByCodigoIgnoreCase("A1").isPresent());
        assertTrue(ubicaciones.findByCodigoIgnoreCase("A6").isPresent());
        assertEquals("DEPARTAMENTO", ubicaciones.findByCodigoIgnoreCase("A6").orElseThrow().getTipo());
        assertTrue(ubicaciones.findByCodigoIgnoreCase("A2").isEmpty());
        assertTrue(ubicaciones.findByCodigoIgnoreCase("A3").isEmpty());
        assertTrue(ubicaciones.findByCodigoIgnoreCase("A5").isEmpty());
    }

    @Test
    void validaPrioridadColorYEstadoDeTipoAlerta() {
        ResultadoImportacion r = importar(TipoCatalogo.TIPO_ALERTA, String.join("\n",
                "codigo,nombre,color,prioridad,descripcion,estado",
                "T1,Uno,VERDE,1,,ACTIVO",
                "T2,Dos,VERDE,0,x,ACTIVO",
                "T3,Tres,VERDE,abc,x,ACTIVO",
                "T4,Cuatro,VERDE,-3,x,ACTIVO",
                "T5,Cinco,AZUL,2,x,ACTIVO",
                "T6,Seis,ROJO,2,x,TAL VEZ",
                "T7,Siete,ROJO,2.5,x,ACTIVO"));
        assertEquals(1, r.getImportadas());
        assertEquals(6, r.getRechazadas());
        assertNull(alertas.findByCodigoIgnoreCase("T1").orElseThrow().getDescripcion());
        for (int n = 3; n <= 5; n++) assertTrue(linea(r, n).getMensaje().contains("prioridad"), "línea " + n);
        assertTrue(linea(r, 6).getMensaje().contains("color no es válido"));
        assertTrue(linea(r, 7).getMensaje().contains("estado no es válido"));
        assertTrue(linea(r, 8).getMensaje().contains("prioridad"));
    }

    @Test
    void validaNombreSectorYEstadoObligatoriosDeTipoDano() {
        ResultadoImportacion r = importar(TipoCatalogo.TIPO_DANO, String.join("\n",
                "codigo,nombre,sector,descripcion,estado",
                "D1,Uno,SALUD,,ACTIVO",
                "D2,,SALUD,x,ACTIVO",
                "D3,Tres,,x,ACTIVO",
                "D4,Cuatro,MINERIA,x,ACTIVO",
                "D5,Cinco,SALUD,x,",
                "D6,Seis,SALUD,x,OTRO"));
        assertEquals(1, r.getImportadas());
        assertEquals(5, r.getRechazadas());
        assertTrue(linea(r, 3).getMensaje().contains("nombre es obligatorio"));
        assertTrue(linea(r, 4).getMensaje().contains("sector es obligatorio"));
        assertTrue(linea(r, 5).getMensaje().contains("sector no es válido"));
        assertTrue(linea(r, 6).getMensaje().contains("estado es obligatorio"));
        assertTrue(linea(r, 7).getMensaje().contains("estado no es válido"));
    }

    @Test
    void omiteSinModificarLosCodigosQueYaExistenSinImportarMayusculas() {
        Ubicacion existente = ubicacion("ACTIVO");
        String codigo = existente.getCodigo();
        ResultadoImportacion r = importar(TipoCatalogo.UBIGEO, "codigo,nombre,tipo,estado\n"
                + codigo.toLowerCase() + ",Nombre distinto,DEPARTAMENTO,INACTIVO\n");
        assertEquals(1, r.getOmitidas());
        assertEquals(0, r.getImportadas());
        Ubicacion despues = ubicaciones.findById(existente.getId()).orElseThrow();
        assertEquals(existente.getNombre(), despues.getNombre());
        assertEquals("ACTIVO", despues.getEstado());
    }

    @Test
    void aceptaLasColumnasEnCualquierOrdenYColumnasExtra() {
        ResultadoImportacion r = importar(TipoCatalogo.UBIGEO, "estado,extra,tipo,nombre,codigo\nACTIVO,x,DEPARTAMENTO,Orden libre,OL1\n");
        assertEquals(1, r.getImportadas());
        assertEquals("Orden libre", ubicaciones.findByCodigoIgnoreCase("OL1").orElseThrow().getNombre());
    }

    @Test
    void rechazaElArchivoCompletoSiFaltanColumnas() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> importar(TipoCatalogo.TIPO_ALERTA, "codigo,nombre,color\nA,B,ROJO\n"));
        assertTrue(error.getMessage().contains("faltan columnas"));
        assertTrue(error.getMessage().contains("prioridad"));
    }

    @Test
    void avisaCuandoElSeparadorEsPuntoYComa() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> importar(TipoCatalogo.UBIGEO, "codigo;nombre;tipo;estado\n01;Amazonas;DEPARTAMENTO;ACTIVO\n"));
        assertTrue(error.getMessage().contains("\";\""));
    }

    @Test
    void unArchivoSoloConEncabezadoNoImportaNada() {
        ResultadoImportacion r = importar(TipoCatalogo.UBIGEO, "codigo,nombre,tipo,estado\n");
        assertEquals(0, r.getTotal());
    }
}
