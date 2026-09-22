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
import java.util.Optional;

@WebServlet(name = "EventoDetalleServlet", urlPatterns = "/eventos/detalle")
public class EventoDetalleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        EventoRepository repositorio = repositorio(request);

        Optional<Evento> encontrado =
                id > 0
                        ? repositorio.buscarId(id)
                        : Optional.empty();

        if (encontrado.isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        request.setAttribute(
                "evento",
                encontrado.get()
        );

        request.setAttribute(
                "eventoCreado",
                "1".equals(request.getParameter("creado"))
        );

        request.setAttribute(
                "eventoActualizado",
                "1".equals(request.getParameter("actualizado"))
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/sismos/detalle.jsp"
        ).forward(request, response);
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