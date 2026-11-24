package pe.edu.cssp.web.paginacion;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Convierte los parámetros de la URL (pagina, tamano, orden, dir) en un Pageable seguro:
 * solo se puede ordenar por las columnas que el módulo declara, y el tamaño sale de una lista fija.
 */
public final class Paginacion {
    public static final List<Integer> TAMANOS = List.of(10, 20, 50, 100);
    public static final int TAMANO_POR_DEFECTO = 10;

    private Paginacion() { }

    /** orden y dir ya normalizados, para que la vista marque la columna activa. */
    public record Pedido(Pageable pageable, String orden, String dir) { }

    /**
     * @param camposPermitidos nombre público de la columna -> propiedades JPA separadas por coma
     * @param ordenPorDefecto  nombre público de la columna con la que se ordena si no se pide otra
     */
    public static Pedido pedido(Integer pagina, Integer tamano, String orden, String dir,
                                Map<String, String> camposPermitidos, String ordenPorDefecto) {
        return pedido(pagina, tamano, orden, dir, camposPermitidos, ordenPorDefecto, "asc");
    }

    /** dirPorDefecto: sentido ("asc" o "desc") cuando la URL no trae uno; sirve para listas que muestran lo más reciente primero. */
    public static Pedido pedido(Integer pagina, Integer tamano, String orden, String dir,
                                Map<String, String> camposPermitidos, String ordenPorDefecto, String dirPorDefecto) {
        int paginaCero = pagina == null || pagina < 1 ? 0 : pagina - 1;
        int tamanoFinal = tamano != null && TAMANOS.contains(tamano) ? tamano : TAMANO_POR_DEFECTO;
        String ordenFinal = orden != null && camposPermitidos.containsKey(orden) ? orden : ordenPorDefecto;
        String dirPedida = dir == null || dir.isBlank() ? dirPorDefecto : dir;
        String dirFinal = "desc".equalsIgnoreCase(dirPedida) ? "desc" : "asc";

        Sort.Direction direccion = Sort.Direction.fromString(dirFinal);
        Sort sort = Sort.unsorted();
        for (String propiedad : camposPermitidos.get(ordenFinal).split(",")) {
            sort = sort.and(Sort.by(direccion, propiedad.trim()));
        }
        return new Pedido(PageRequest.of(paginaCero, tamanoFinal, sort), ordenFinal, dirFinal);
    }
}
