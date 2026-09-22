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

@WebServlet(name = "EstacionEliminarServlet", urlPatterns = "/estaciones/eliminar")
public class EstacionEliminarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        Estacion estacion = repositorio(request)
                .buscarId(id)
                .orElse(null);

        if (estacion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("estacion", estacion);

        request.getRequestDispatcher(
                "/WEB-INF/views/estaciones/eliminar.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        int id = leerId(request.getParameter("id"));

        boolean eliminado =
                repositorio(request).eliminar(id);

        response.sendRedirect(
                request.getContextPath()
                + "/estaciones?eliminado="
                + eliminado
        );
    }

    private EstacionRepository repositorio(
            HttpServletRequest request) {

        return (EstacionRepository) request
                .getServletContext()
                .getAttribute(
                        AplicacionListener.REPOSITORIO_ESTACIONES
                );
    }

    private int leerId(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}