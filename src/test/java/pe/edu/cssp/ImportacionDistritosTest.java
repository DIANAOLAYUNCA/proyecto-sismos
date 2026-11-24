package pe.edu.cssp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.cssp.entity.Departamento;
import pe.edu.cssp.entity.Distrito;
import pe.edu.cssp.entity.Provincia;
import pe.edu.cssp.repository.DepartamentoRepository;
import pe.edu.cssp.repository.DistritoRepository;
import pe.edu.cssp.repository.ProvinciaRepository;
import pe.edu.cssp.service.ImportacionCatalogosService;
import pe.edu.cssp.service.importacion.ResultadoImportacion;
import pe.edu.cssp.service.importacion.ResultadoImportacion.Estado;
import pe.edu.cssp.service.importacion.TipoCatalogo;
import pe.edu.cssp.web.error.ReglaNegocioException;

class ImportacionDistritosTest extends BaseDatosTest {
    private static final String ENCABEZADO = "IDDIST;NOMBDEP;NOMBPROV;NOMBDIST;NOM_CAPITAL (LEGAL);COD_ REG_NAT;REGION NATURAL\n";

    @Autowired ImportacionCatalogosService servicio;
    @Autowired DepartamentoRepository departamentos;
    @Autowired ProvinciaRepository provincias;
    @Autowired DistritoRepository distritos;

    private ResultadoImportacion importar(String csv, Charset codificacion) {
        return servicio.importar(TipoCatalogo.DISTRITOS, "distritos.csv", new ByteArrayInputStream(csv.getBytes(codificacion)));
    }

    private ResultadoImportacion importar(String csv) {
        return importar(csv, StandardCharsets.UTF_8);
    }

    private Distrito distrito(String ubigeo) {
        return distritos.findAll().stream()
                .filter(d -> (d.getProvincia().getDepartamento().getCodigo() + d.getProvincia().getCodigo() + d.getCodigo()).equals(ubigeo))
                .findFirst().orElseThrow();
    }

    // ---------- Archivo real del INEI ----------

    @Test
    void cargaEl100PorCientoDelArchivoDeDistritos() {
        ResultadoImportacion r = servicio.importarIncluido(TipoCatalogo.DISTRITOS);

        assertEquals(1891, r.getTotal());
        assertEquals(1891, r.getImportadas());
        assertEquals(0, r.getOmitidas());
        assertEquals(0, r.getRechazadas());
        assertEquals(20, r.getIgnoradas());
        assertEquals(1891, distritos.count());
        assertEquals(196, provincias.count());
        assertEquals(25, departamentos.count());
    }

    @Test
    void creaLaJerarquiaYDejaLosNombresLegibles() {
        servicio.importarIncluido(TipoCatalogo.DISTRITOS);

        Distrito sjl = distrito("150132");
        assertEquals("San Juan de Lurigancho", sjl.getNombre());
        assertEquals("Lima", sjl.getProvincia().getNombre());
        assertEquals("Lima", sjl.getProvincia().getDepartamento().getNombre());
        assertEquals("32", sjl.getCodigo());
        assertEquals("01", sjl.getProvincia().getCodigo());
        assertEquals("importacion", sjl.getCreacionUsuario());
        assertEquals("Nepeña", distrito("021806").getNombre());
    }

    @Test
    void importarDosVecesElArchivoNoDuplicaNada() {
        servicio.importarIncluido(TipoCatalogo.DISTRITOS);
        ResultadoImportacion segunda = servicio.importarIncluido(TipoCatalogo.DISTRITOS);

        assertEquals(0, segunda.getImportadas());
        assertEquals(1891, segunda.getOmitidas());
        assertEquals(1891, distritos.count());
        assertTrue(segunda.isDetalleReducido());
        assertTrue(segunda.getLineasMostradas().isEmpty());
    }

    // ---------- Sobre una base que ya tiene ubigeo ----------

    @Test
    void reutilizaDepartamentosYProvinciasExistentesYSoloAgregaLosDistritosFaltantes() {
        Departamento ancash = departamento("02", "Áncash");
        Provincia santa = provincia(ancash, "06", "Santa");
        distrito(santa, "01", "Chimbote");

        ResultadoImportacion r = importar(ENCABEZADO
                + "020601;ANCASH;SANTA;CHIMBOTE;CHIMBOTE;1;COSTA\n"
                + "020606;ANCASH;SANTA;NEPEÑA;NEPEÑA;1;COSTA\n", StandardCharsets.ISO_8859_1);

        assertEquals(1, r.getImportadas());
        assertEquals(1, r.getOmitidas());
        assertEquals(1, departamentos.count());
        assertEquals(1, provincias.count());
        assertEquals(2, distritos.count());
        assertEquals("Áncash", distrito("020606").getProvincia().getDepartamento().getNombre());
        assertEquals("Nepeña", distrito("020606").getNombre());
    }

    // ---------- Formato del archivo ----------

    @Test
    void leeArchivosGuardadosEnWindows1252YEnUtf8() {
        ResultadoImportacion latin1 = importar(ENCABEZADO + "030411;APURIMAC;AYMARAES;SAN JUAN DE CHACÑA;X;2;SIERRA\n", StandardCharsets.ISO_8859_1);
        assertEquals("San Juan de Chacña", distrito("030411").getNombre());
        assertEquals(1, latin1.getImportadas());

        ResultadoImportacion utf8 = importar(ENCABEZADO + "030412;APURIMAC;AYMARAES;SAÑAYCA;X;2;SIERRA\n");
        assertEquals(1, utf8.getImportadas());
        assertEquals("Sañayca", distrito("030412").getNombre());
    }

    @Test
    void ignoraFilasVaciasYNotasAlPieSinContarlasComoErrores() {
        ResultadoImportacion r = importar(ENCABEZADO
                + "010101;AMAZONAS;CHACHAPOYAS;CHACHAPOYAS;CHACHAPOYAS;2;SIERRA\n"
                + ";;;;;;\n"
                + ";;;;1/ SU CAPITAL LEGAL ES PUEBLO LIBRE, DE HECHO SAN JUAN;;\n");

        assertEquals(1, r.getTotal());
        assertEquals(1, r.getImportadas());
        assertEquals(0, r.getRechazadas());
        assertEquals(2, r.getIgnoradas());
    }

    @Test
    void aceptaCualquierOrdenDeColumnasYColumnasExtra() {
        ResultadoImportacion r = importar("REGION NATURAL;NOMBDIST;NOMBPROV;NOMBDEP;IDDIST\nSIERRA;BALSAS;CHACHAPOYAS;AMAZONAS;010103\n");
        assertEquals(1, r.getImportadas());
        assertEquals("Balsas", distrito("010103").getNombre());
    }

    @Test
    void rechazaElArchivoSiFaltanColumnasObligatorias() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> importar("IDDIST;NOMBDEP;NOMBPROV\n010101;AMAZONAS;CHACHAPOYAS\n"));
        assertTrue(error.getMessage().contains("faltan columnas"));
        assertTrue(error.getMessage().contains("nombdist"));
    }

    // ---------- Validaciones por fila ----------

    @Test
    void rechazaCodigosInvalidosNombresVaciosYCodigosRepetidosEnElArchivo() {
        ResultadoImportacion r = importar(ENCABEZADO
                + "01010;AMAZONAS;CHACHAPOYAS;CORTO;X;2;SIERRA\n"
                + "010102;AMAZONAS;CHACHAPOYAS;;X;2;SIERRA\n"
                + "010103;AMAZONAS;CHACHAPOYAS;BALSAS;X;3;SELVA ALTA\n"
                + "010103;AMAZONAS;CHACHAPOYAS;BALSAS;X;3;SELVA ALTA\n"
                + "0101AB;AMAZONAS;CHACHAPOYAS;LETRAS;X;2;SIERRA\n");

        assertEquals(1, r.getImportadas());
        assertEquals(4, r.getRechazadas());
        assertEquals(Estado.RECHAZADA, r.getLineas().get(0).getEstado());
        assertTrue(r.getLineas().get(0).getMensaje().contains("6 dígitos"));
        assertTrue(r.getLineas().get(1).getMensaje().contains("distrito es obligatorio"));
        assertTrue(r.getLineas().get(3).getMensaje().contains("repetido"));
        assertEquals(1, distritos.count());
    }

    @Test
    void permiteNombresDeDistritoRepetidosEnProvinciasDistintas() {
        ResultadoImportacion r = importar(ENCABEZADO
                + "010101;AMAZONAS;CHACHAPOYAS;SANTA ROSA;X;2;SIERRA\n"
                + "020101;ANCASH;HUARAZ;SANTA ROSA;X;2;SIERRA\n");

        assertEquals(2, r.getImportadas());
        assertEquals(2, distritos.count());
    }

    // ---------- Apoyo ----------

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
