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

@WebServlet(name = "EventoNuevoServlet", urlPatterns = "/eventos/nuevo")
public class EventoNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        mostrarFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

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

        guardarValores(request, estacionIdTexto, codigo, fechaHora, magnitudTexto,
                profundidadTexto, latitudTexto, longitudTexto, ubicacionIdTexto,
                afectacionNivelIdTexto, estado);

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

            EventoRepository repositorio = repositorio(request);

            if (repositorio.existCodigo(codigo)) {
                request.setAttribute("error", "El codigo ya existe.");
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
                    0, estacion, codigo, normalizarFechaHora(fechaHora),
                    magnitud, profundidad, latitud, longitud,
                    ubicacion, nivel, estado,
                    LocalDateTime.now().format(FORMATO_FECHA), "sistema", null, null);

            Evento creado = repositorio.agregar(evento);

            response.sendRedirect(request.getContextPath()
                    + "/eventos/detalle?id=" + creado.getId() + "&creado=1");

        } catch (PersistenciaException ex) {
            request.setAttribute("error", ex.getMessage());
            mostrarFormulario(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error",
                    "Revise los datos ingresados (la estación, la ubicación y el nivel deben existir).");
           mostrarFormulario(request, response);
        }
    }

    static String normalizarFechaHora(String valor) {
        String texto = valor.replace('T', ' ');
        return texto.length() == 16 ? texto + ":00" : texto;
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

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private boolean estadoValido(String estado) {
        return "REGISTRADO".equals(estado)
                || "EN_SEGUIMIENTO".equals(estado)
                || "EN_EVALUACION".equals(estado)
                || "CERRADO".equals(estado);
    }

    private void guardarValores(HttpServletRequest request, String estacionId,
            String codigo, String fechaHora, String magnitud, String profundidad,
            String latitud, String longitud, String ubicacionId,
            String afectacionNivelId, String estado) {

        request.setAttribute("estacionIdIngresada", estacionId);
        request.setAttribute("codigoIngresado", codigo);
        request.setAttribute("fechaHoraIngresada", fechaHora);
        request.setAttribute("magnitudIngresada", magnitud);
        request.setAttribute("profundidadIngresada", profundidad);
        request.setAttribute("latitudIngresada", latitud);
        request.setAttribute("longitudIngresada", longitud);
        request.setAttribute("ubicacionIdIngresada", ubicacionId);
        request.setAttribute("afectacionNivelIdIngresada", afectacionNivelId);
        request.setAttribute("estadoIngresado", estado);
    }
    
        private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("estaciones", repositorioEstaciones(request).listar());
        request.setAttribute("ubicaciones", repositorioUbicaciones(request).listar());
        request.setAttribute("niveles", repositorioNiveles(request).listar());
        request.getRequestDispatcher("/WEB-INF/views/sismos/formulario.jsp")
                .forward(request, response);
    }
}