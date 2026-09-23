package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.Estacion;
import pe.edu.cssp.repository.EstacionRepository;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "EstacionListarServlet", urlPatterns = "/estaciones")
public class EstacionListarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String estado = limpiar(request.getParameter("estado"));

        EstacionRepository repositorio = repositorio(request);
        List<Estacion> estaciones = repositorio.listar();

        if (!estado.isBlank()) {
            estaciones = estaciones.stream()
                    .filter(estacion -> estado.equalsIgnoreCase(estacion.getEstado()))
                    .toList();
        }

        request.setAttribute("estaciones", estaciones);
        request.setAttribute("totalEstaciones", estaciones.size());
        request.setAttribute("estadoSeleccionado", estado);

        request.getRequestDispatcher(
                "/WEB-INF/views/estaciones/listar.jsp"
        ).forward(request, response);
    }

    private EstacionRepository repositorio(HttpServletRequest request) {
        return (EstacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}