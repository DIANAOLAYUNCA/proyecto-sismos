package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.AfectacionNivel;
import pe.edu.cssp.model.Estacion;
import pe.edu.cssp.model.Evento;
import pe.edu.cssp.model.Ubicacion;
import pe.edu.cssp.repository.AfectacionNivelRepository;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.PersistenciaException;
import pe.edu.cssp.repository.UbicacionRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "EventoEditarServlet", urlPatterns = "/eventos/editar")
public class EventoEditarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = leerId(request.getParameter("id"));
        Evento evento = repositorio(request).buscarId(id).orElse(null);
        if (evento == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("evento", evento);
        mostrarFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        int id = leerId(request.getParameter("id"));
        EventoRepository repositorio = repositorio(request);
        Evento existente = repositorio.buscarId(id).orElse(null);
        if (existente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String estacionIdTexto = limpiar(request.getParameter("estacionId"));
        String codigo = limpiar(request.getParameter("codigo"));
        String fechaHora = limpiar(request.getParameter("fechaHora"));
        String magnitudTexto = limpiar(request.getParameter("magnitud"));
        String profundidadTexto = limpiar(request.getParameter("profundidad"));
        String latitudTexto = limpiar(request.getParameter("latitud"));
        String longitudTexto = limpiar(request.getParameter("longitud"));
        String ubicacionIdTexto = limpiar(request.getParameter("ubicacionId"));
        String afectacionNivelIdTexto = limpiar(request.getParameter("afectacionNivelId"));
        String estado = limpiar(request.getParameter("estado"));

        try {
            if (estacionIdTexto.isBlank() || codigo.isBlank() || fechaHora.isBlank()
                    || magnitudTexto.isBlank() || profundidadTexto.isBlank()
                    || latitudTexto.isBlank() || longitudTexto.isBlank()
                    || ubicacionIdTexto.isBlank() || afectacionNivelIdTexto.isBlank()
                    || estado.isBlank()) {
                throw new IllegalArgumentException();
            }

            int estacionId = Integer.parseInt(estacionIdTexto);
            double magnitud = Double.parseDouble(magnitudTexto.replace(',', '.'));
            double profundidad = Double.parseDouble(profundidadTexto.replace(',', '.'));
            double latitud = Double.parseDouble(latitudTexto.replace(',', '.'));
            double longitud = Double.parseDouble(longitudTexto.replace(',', '.'));
            int ubicacionId = Integer.parseInt(ubicacionIdTexto);
            int afectacionNivelId = Integer.parseInt(afectacionNivelIdTexto);

            if (estacionId <= 0 || ubicacionId <= 0 || afectacionNivelId <= 0
                    || !Double.isFinite(magnitud) || !Double.isFinite(profundidad)
                    || !Double.isFinite(latitud) || !Double.isFinite(longitud)
                    || magnitud <= 0 || profundidad < 0
                    || latitud < -90 || latitud > 90
                    || longitud < -180 || longitud > 180
                    || !estadoValido(estado)) {
                throw new IllegalArgumentException();
            }

            Evento conCodigo = repositorio.buscarCod(codigo).orElse(null);

            if (conCodigo != null && conCodigo.getId() != id) {
                request.setAttribute("error", "El codigo ya pertenece a otro evento.");
                request.setAttribute("evento", desdeTextos(id, estacionIdTexto, codigo,
                        fechaHora, magnitudTexto, profundidadTexto, latitudTexto,
                        longitudTexto, ubicacionIdTexto, afectacionNivelIdTexto,
                        estado, existente));
                mostrarFormulario(request, response);
                return;
            }

            Estacion estacion = repositorioEstaciones(request).buscarId(estacionId)
                    .orElseThrow(IllegalArgumentException::new);
            Ubicacion ubicacion = repositorioUbicaciones(request).buscarId(ubicacionId)
                    .orElseThrow(IllegalArgumentException::new);
            AfectacionNivel nivel = repositorioNiveles(request).buscarId(afectacionNivelId)
                    .orElseThrow(IllegalArgumentException::new);

            Evento evento = new Evento(
                    id, estacion, codigo,
                    EventoNuevoServlet.normalizarFechaHora(fechaHora),
                    magnitud, profundidad, latitud, longitud,
                    ubicacion, nivel, estado,
                    existente.getCreacionFecha(), existente.getCreacionUsuario(),
                    LocalDateTime.now().format(FORMATO_FECHA), "sistema");

            repositorio.actualizar(evento);

            response.sendRedirect(request.getContextPath()
                    + "/eventos/detalle?id=" + id + "&actualizado=1");

        } catch (PersistenciaException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("evento", desdeTextos(id, estacionIdTexto, codigo,
                    fechaHora, magnitudTexto, profundidadTexto, latitudTexto,
                    longitudTexto, ubicacionIdTexto, afectacionNivelIdTexto,
                    estado, existente));
            mostrarFormulario(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error",
                    "Revise los datos ingresados (la estación, la ubicación y el nivel deben existir).");
            request.setAttribute("evento", desdeTextos(id, estacionIdTexto, codigo,
                    fechaHora, magnitudTexto, profundidadTexto, latitudTexto,
                    longitudTexto, ubicacionIdTexto, afectacionNivelIdTexto,
                    estado, existente));
            mostrarFormulario(request, response);
        }
    }

    private Evento desdeTextos(int id, String estacionId, String codigo, String fechaHora,
            String magnitud, String profundidad, String latitud, String longitud,
            String ubicacionId, String afectacionNivelId, String estado, Evento existente) {

        Estacion estacion = new Estacion();
        estacion.setId(convertirEntero(estacionId));
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setId(convertirEntero(ubicacionId));
        AfectacionNivel nivel = new AfectacionNivel();
        nivel.setId(convertirEntero(afectacionNivelId));

        return new Evento(id, estacion, codigo, fechaHora,
                convertirDouble(magnitud), convertirDouble(profundidad),
                convertirDouble(latitud), convertirDouble(longitud),
                ubicacion, nivel, estado,
                existente.getCreacionFecha(), existente.getCreacionUsuario(),
                existente.getActualizacionFecha(), existente.getActualizacionUsuario());
    }

    private EventoRepository repositorio(HttpServletRequest request) {
        return (EventoRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_EVENTOS);
    }

    private EstacionRepository repositorioEstaciones(HttpServletRequest request) {
        return (EstacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }

    private UbicacionRepository repositorioUbicaciones(HttpServletRequest request) {
        return (UbicacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_UBICACIONES);
    }

    private AfectacionNivelRepository repositorioNiveles(HttpServletRequest request) {
        return (AfectacionNivelRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_AFECTACION_NIVELES);
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
        return "REGISTRADO".equals(estado)
                || "EN_SEGUIMIENTO".equals(estado)
                || "EN_EVALUACION".equals(estado)
                || "CERRADO".equals(estado);
    }
    
        private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("estaciones", repositorioEstaciones(request).listar());
        request.setAttribute("ubicaciones", repositorioUbicaciones(request).listar());
        request.setAttribute("niveles", repositorioNiveles(request).listar());
        request.getRequestDispatcher("/WEB-INF/views/sismos/editar.jsp")
                .forward(request, response);
    }
}