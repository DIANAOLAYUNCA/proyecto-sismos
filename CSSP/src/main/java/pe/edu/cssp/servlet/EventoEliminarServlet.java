package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.Evento;
import pe.edu.cssp.repository.EventoRepository;

import java.io.IOException;

@WebServlet(name = "EventoEliminarServlet", urlPatterns = "/eventos/eliminar")
public class EventoEliminarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Evento evento = repositorio(request)
                .buscarId(leerId(request.getParameter("id")))
                .orElse(null);
        if (evento == null) {
            response.sendRedirect(request.getContextPath() + "/eventos");
            return;
        }
        request.setAttribute("evento", evento);
        request.getRequestDispatcher(
                "/WEB-INF/views/sismos/eliminar.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = leerId(request.getParameter("id"));
        boolean eliminado = repositorio(request).eliminar(id);
        response.sendRedirect(
                request.getContextPath() + "/eventos?eliminado=" + eliminado
        );
    }

    private EventoRepository repositorio(HttpServletRequest request) {
        return (EventoRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_EVENTOS);
    }

    private int leerId(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}