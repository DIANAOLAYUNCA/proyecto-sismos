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
import java.util.Optional;

@WebServlet(name = "EstacionDetalleServlet", urlPatterns = "/estaciones/detalle")
public class EstacionDetalleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        EstacionRepository repositorio = repositorio(request);

        Optional<Estacion> encontrado =
                id > 0 ? repositorio.buscarId(id) : Optional.empty();

        if (encontrado.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("estacion", encontrado.get());

        request.setAttribute(
                "estacionCreada",
                "1".equals(request.getParameter("creado"))
        );

        request.setAttribute(
                "estacionActualizada",
                "1".equals(request.getParameter("actualizado"))
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/estaciones/detalle.jsp"
        ).forward(request, response);
    }

    private EstacionRepository repositorio(HttpServletRequest request) {
        return (EstacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }

    private int leerId(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}