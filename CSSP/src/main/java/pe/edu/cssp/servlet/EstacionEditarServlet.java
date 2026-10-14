package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.Estacion;
import pe.edu.cssp.model.Ubicacion;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.PersistenciaException;
import pe.edu.cssp.repository.UbicacionRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "EstacionEditarServlet", urlPatterns = "/estaciones/editar")
public class EstacionEditarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = leerId(request.getParameter("id"));
        Estacion estacion = repositorio(request).buscarId(id).orElse(null);
        if (estacion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("estacion", estacion);
        mostrarFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        int id = leerId(request.getParameter("id"));
        EstacionRepository repositorio = repositorio(request);
        Estacion existente = repositorio.buscarId(id).orElse(null);
        if (existente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String codigo = limpiar(request.getParameter("codigo"));
        String nombre = limpiar(request.getParameter("nombre"));
        String ubicacionIdTexto = limpiar(request.getParameter("ubicacionId"));
        String latitudTexto = limpiar(request.getParameter("latitud"));
        String longitudTexto = limpiar(request.getParameter("longitud"));
        String instalacionFecha = limpiar(request.getParameter("instalacionFecha"));
        String estado = limpiar(request.getParameter("estado"));

        try {
            if (codigo.isBlank() || nombre.isBlank() || ubicacionIdTexto.isBlank()
                    || latitudTexto.isBlank() || longitudTexto.isBlank()
                    || instalacionFecha.isBlank() || estado.isBlank()) {
                throw new IllegalArgumentException();
            }

            int ubicacionId = Integer.parseInt(ubicacionIdTexto);
            double latitud = Double.parseDouble(latitudTexto.replace(',', '.'));
            double longitud = Double.parseDouble(longitudTexto.replace(',', '.'));

            if (ubicacionId <= 0 || !Double.isFinite(latitud) || !Double.isFinite(longitud)
                    || latitud < -90 || latitud > 90
                    || longitud < -180 || longitud > 180
                    || !estadoValido(estado)) {
                throw new IllegalArgumentException();
            }

            Estacion conCodigo = repositorio.buscarCod(codigo).orElse(null);

            if (conCodigo != null && conCodigo.getId() != id) {
                request.setAttribute("error", "El codigo ya pertenece a otra estación.");
                request.setAttribute("estacion", desdeTextos(id, codigo, nombre,
                        ubicacionIdTexto, latitudTexto, longitudTexto,
                        instalacionFecha, estado, existente));
                mostrarFormulario(request, response);
                return;
            }

            Ubicacion ubicacion = repositorioUbicaciones(request).buscarId(ubicacionId)
                    .orElseThrow(IllegalArgumentException::new);

            Estacion estacion = new Estacion(
                    id, codigo, nombre, ubicacion, latitud, longitud,
                    instalacionFecha, estado,
                    existente.getCreacionFecha(), existente.getCreacionUsuario(),
                    LocalDateTime.now().format(FORMATO_FECHA), "sistema");

            repositorio.actualizar(estacion);

            response.sendRedirect(request.getContextPath()
                    + "/estaciones/detalle?id=" + id + "&actualizado=1");

        } catch (PersistenciaException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("estacion", desdeTextos(id, codigo, nombre,
                    ubicacionIdTexto, latitudTexto, longitudTexto,
                    instalacionFecha, estado, existente));
            mostrarFormulario(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error",
                    "Revise los datos: campos obligatorios, ubicación existente, coordenadas válidas y estado correcto.");
            request.setAttribute("estacion", desdeTextos(id, codigo, nombre,
                    ubicacionIdTexto, latitudTexto, longitudTexto,
                    instalacionFecha, estado, existente));
            mostrarFormulario(request, response);
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    request.setAttribute("ubicaciones", repositorioUbicaciones(request).listar());
    request.getRequestDispatcher("/WEB-INF/views/estaciones/editar.jsp")
            .forward(request, response);
    }
    
    
    private Estacion desdeTextos(int id, String codigo, String nombre, String ubicacionId,
            String latitud, String longitud, String instalacionFecha, String estado,
            Estacion existente) {

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setId(convertirEntero(ubicacionId));

        return new Estacion(id, codigo, nombre, ubicacion,
                convertirDouble(latitud), convertirDouble(longitud),
                instalacionFecha, estado,
                existente.getCreacionFecha(), existente.getCreacionUsuario(),
                existente.getActualizacionFecha(), existente.getActualizacionUsuario());
    }
    
    

    private EstacionRepository repositorio(HttpServletRequest request) {
        return (EstacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }

    private UbicacionRepository repositorioUbicaciones(HttpServletRequest request) {
        return (UbicacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_UBICACIONES);
    }

    private int leerId(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private int convertirEntero(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private double convertirDouble(String texto) {
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private boolean estadoValido(String estado) {
        return "ACTIVA".equals(estado) || "INACTIVA".equals(estado);
    }
}