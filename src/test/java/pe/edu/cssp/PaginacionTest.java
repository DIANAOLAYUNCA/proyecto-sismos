package pe.edu.cssp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import pe.edu.cssp.web.paginacion.Paginacion;

class PaginacionTest {
    private static final Map<String, String> ORDEN = Map.of("id", "id", "nombre", "nombre,id", "ubigeo", "d.codigo,p.codigo,codigo");

    @Test
    void sinParametrosUsaLaPrimeraPaginaDe10OrdenadaPorElCampoPorDefecto() {
        Paginacion.Pedido p = Paginacion.pedido(null, null, null, null, ORDEN, "id");
        assertEquals(0, p.pageable().getPageNumber());
        assertEquals(10, p.pageable().getPageSize());
        assertEquals("id", p.orden());
        assertEquals("asc", p.dir());
    }

    @Test
    void laPaginaSeCuentaDesdeUnoYLosValoresInvalidosVuelvenAlInicio() {
        assertEquals(2, Paginacion.pedido(3, 20, null, null, ORDEN, "id").pageable().getPageNumber());
        assertEquals(0, Paginacion.pedido(0, null, null, null, ORDEN, "id").pageable().getPageNumber());
        assertEquals(0, Paginacion.pedido(-5, null, null, null, ORDEN, "id").pageable().getPageNumber());
    }

    @Test
    void elTamanoSoloPuedeSerUnoDeLosPermitidos() {
        assertEquals(50, Paginacion.pedido(1, 50, null, null, ORDEN, "id").pageable().getPageSize());
        assertEquals(10, Paginacion.pedido(1, 9999, null, null, ORDEN, "id").pageable().getPageSize());
        assertEquals(10, Paginacion.pedido(1, 0, null, null, ORDEN, "id").pageable().getPageSize());
    }

    @Test
    void soloSeOrdenaPorLasColumnasDeclaradasParaEvitarInyectarPropiedades() {
        assertEquals("id", Paginacion.pedido(1, 10, "password", "asc", ORDEN, "id").orden());
        assertEquals("id", Paginacion.pedido(1, 10, "nombre; drop table", "asc", ORDEN, "id").orden());
        assertEquals("nombre", Paginacion.pedido(1, 10, "nombre", "asc", ORDEN, "id").orden());
    }

    @Test
    void unaColumnaPuedeOrdenarPorVariasPropiedadesEnElSentidoPedido() {
        Sort sort = Paginacion.pedido(1, 10, "ubigeo", "desc", ORDEN, "id").pageable().getSort();
        assertEquals(3, sort.stream().count());
        assertTrue(sort.stream().allMatch(o -> o.getDirection() == Sort.Direction.DESC));
        assertEquals("d.codigo", sort.stream().findFirst().orElseThrow().getProperty());
    }

    @Test
    void elSentidoPorDefectoSePuedeInvertirPeroLaUrlManda() {
        assertEquals("desc", Paginacion.pedido(1, 10, null, null, ORDEN, "id", "desc").dir());
        assertEquals("asc", Paginacion.pedido(1, 10, null, "asc", ORDEN, "id", "desc").dir());
        assertEquals("asc", Paginacion.pedido(1, 10, null, "cualquier-cosa", ORDEN, "id", "asc").dir());
    }
}
