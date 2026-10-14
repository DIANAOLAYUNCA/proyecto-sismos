package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.Evento;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.PersistenciaException;

import java.io.IOException;

@WebServlet(name = "EventoEliminarServlet", urlPatterns = "/eventos/eliminar")
public class EventoEliminarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        Evento evento = repositorio(request)
                .buscarId(id)
                .orElse(null);

        if (evento == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        request.setAttribute(
                "evento",
                evento
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/sismos/eliminar.jsp"
        ).forward(request, response);
    }

        @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = leerId(request.getParameter("id"));

        try {
            boolean eliminado = repositorio(request).eliminar(id);
            response.sendRedirect(request.getContextPath() + "/eventos?eliminado=" + eliminado);
        } catch (PersistenciaException ex) {
            response.sendRedirect(request.getContextPath() + "/eventos?eliminado=false");
        }
    }

    private EventoRepository repositorio(
            HttpServletRequest request) {

        return (EventoRepository) request
                .getServletContext()
                .getAttribute(
                        AplicacionListener.REPOSITORIO_EVENTOS
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