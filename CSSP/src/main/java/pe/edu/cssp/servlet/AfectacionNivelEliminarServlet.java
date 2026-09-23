package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.AfectacionNivel;
import pe.edu.cssp.repository.AfectacionNivelRepository;

import java.io.IOException;

@WebServlet(name = "AfectacionNivelEliminarServlet", urlPatterns = "/afectacionniveles/eliminar")
public class AfectacionNivelEliminarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        AfectacionNivel nivel = repositorio(request)
                .buscarId(id)
                .orElse(null);

        if (nivel == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("nivel", nivel);

        request.getRequestDispatcher(
                "/WEB-INF/views/afectacionniveles/eliminar.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        int id = leerId(request.getParameter("id"));

        boolean eliminado = repositorio(request).eliminar(id);

        response.sendRedirect(
                request.getContextPath()
                + "/afectacionniveles?eliminado="
                + eliminado
        );
    }

    private AfectacionNivelRepository repositorio(HttpServletRequest request) {
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
}
