package pe.edu.cssp.web.paginacion;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Lo que necesita la vista para dibujar la barra de paginación y los encabezados ordenables.
 * Los enlaces conservan los filtros que ya tenga la URL (búsqueda, estado, etc.).
 */
public class PaginaVista<T> {
    private static final List<String> PARAMETROS_PROPIOS = List.of("pagina", "tamano", "orden", "dir");

    private final Page<T> pagina;
    private final Paginacion.Pedido pedido;
    private final String ruta;
    private final Map<String, String[]> parametros;

    public PaginaVista(Page<T> pagina, Paginacion.Pedido pedido, HttpServletRequest request) {
        this.pagina = pagina;
        this.pedido = pedido;
        this.ruta = request.getRequestURI();
        this.parametros = request.getParameterMap();
    }

    public List<T> getItems() { return pagina.getContent(); }
    public int getNumero() { return pagina.getNumber() + 1; }
    public int getTotalPaginas() { return Math.max(pagina.getTotalPages(), 1); }
    public long getTotalElementos() { return pagina.getTotalElements(); }
    public int getTamano() { return pedido.pageable().getPageSize(); }
    public List<Integer> getTamanos() { return Paginacion.TAMANOS; }
    public String getOrden() { return pedido.orden(); }
    public String getDir() { return pedido.dir(); }
    public boolean isPrimera() { return getNumero() <= 1; }
    public boolean isUltima() { return getNumero() >= getTotalPaginas(); }

    public long getDesde() { return pagina.getTotalElements() == 0 ? 0 : (long) pagina.getNumber() * getTamano() + 1; }
    public long getHasta() { return (long) pagina.getNumber() * getTamano() + pagina.getNumberOfElements(); }

    /** Números de página que se muestran: la actual y hasta dos a cada lado. */
    public List<Integer> getVentana() {
        List<Integer> numeros = new ArrayList<>();
        for (int i = Math.max(1, getNumero() - 2); i <= Math.min(getTotalPaginas(), getNumero() + 2); i++) numeros.add(i);
        return numeros;
    }

    public String urlPagina(int numero) {
        return url(Map.of("pagina", String.valueOf(numero)));
    }

    public String urlTamano(int tamano) {
        return url(Map.of("tamano", String.valueOf(tamano), "pagina", "1"));
    }

    /** Pulsar la columna activa invierte el sentido; otra columna empieza en ascendente. */
    public String urlOrden(String campo) {
        String nuevaDir = campo.equals(getOrden()) && "asc".equals(getDir()) ? "desc" : "asc";
        return url(Map.of("orden", campo, "dir", nuevaDir, "pagina", "1"));
    }

    public String flecha(String campo) {
        if (!campo.equals(getOrden())) return "";
        return "asc".equals(getDir()) ? "▲" : "▼";
    }

    private String url(Map<String, String> cambios) {
        Map<String, String> propios = new LinkedHashMap<>();
        propios.put("pagina", String.valueOf(getNumero()));
        propios.put("tamano", String.valueOf(getTamano()));
        propios.put("orden", getOrden());
        propios.put("dir", getDir());
        propios.putAll(cambios);

        UriComponentsBuilder uri = UriComponentsBuilder.fromPath(ruta);
        parametros.forEach((nombre, valores) -> {
            if (PARAMETROS_PROPIOS.contains(nombre)) return;
            for (String valor : valores) {
                if (valor != null && !valor.isBlank()) uri.queryParam(nombre, valor);
            }
        });
        propios.forEach(uri::queryParam);
        return uri.build().encode().toUriString();
    }
}
