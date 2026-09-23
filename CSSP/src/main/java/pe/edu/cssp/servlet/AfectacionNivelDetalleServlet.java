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
import java.util.Optional;

@WebServlet(name = "AfectacionNivelDetalleServlet", urlPatterns = "/afectacionniveles/detalle")
public class AfectacionNivelDetalleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        AfectacionNivelRepository repositorio = repositorio(request);

        Optional<AfectacionNivel> encontrado =
                id > 0 ? repositorio.buscarId(id) : Optional.empty();

        if (encontrado.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("nivel", encontrado.get());

        request.getRequestDispatcher(
                "/WEB-INF/views/afectacionniveles/detalle.jsp"
        ).forward(request, response);
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
