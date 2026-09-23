package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.Ubicacion;
import pe.edu.cssp.repository.UbicacionRepository;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "UbicacionDetalleServlet", urlPatterns = "/ubicaciones/detalle")
public class UbicacionDetalleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        UbicacionRepository repositorio = repositorio(request);

        Optional<Ubicacion> encontrada =
                id > 0 ? repositorio.buscarId(id) : Optional.empty();

        if (encontrada.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("ubicacion", encontrada.get());

        request.getRequestDispatcher(
                "/WEB-INF/views/ubicaciones/detalle.jsp"
        ).forward(request, response);
    }

    private UbicacionRepository repositorio(HttpServletRequest request) {
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
}
