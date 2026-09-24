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
import java.util.List;

@WebServlet(name = "EventoListarServlet", urlPatterns = "/eventos")
public class EventoListarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String magnitudMinimaTexto =
                limpiar(request.getParameter("magnitudMinima"));

        String estado =
                limpiar(request.getParameter("estado"));

        String ubicacionTexto =
                limpiar(request.getParameter("ubicacion"));

        String codigoTexto =
                limpiar(request.getParameter("codigo"));

        String fechaTexto =
                limpiar(request.getParameter("fecha"));

        EventoRepository repositorio =
                repositorio(request);

        List<Evento> eventos =
                repositorio.listar();

        if (!codigoTexto.isBlank()) {

            String busqueda = codigoTexto.toUpperCase();

            eventos = eventos.stream()
                    .filter(evento ->
                            evento.getCodigo() != null
                                    && evento.getCodigo().toUpperCase().contains(busqueda))
                    .toList();
        }

        if (!fechaTexto.isBlank()) {

            eventos = eventos.stream()
                    .filter(evento ->
                            evento.getFechaHora() != null
                                    && evento.getFechaHora().startsWith(fechaTexto))
                    .toList();
        }

        if (!magnitudMinimaTexto.isBlank()) {

            try {

                double magnitudMinima =
                        Double.parseDouble(
                                magnitudMinimaTexto.replace(',', '.')
                        );

                eventos = eventos.stream()
                        .filter(evento ->
                                evento.getMagnitud() >= magnitudMinima)
                        .toList();

            } catch (NumberFormatException ex) {

                request.setAttribute(
                        "error",
                        "La magnitud mínima no es válida."
                );
            }
        }

        if (!estado.isBlank()) {

            eventos = eventos.stream()
                    .filter(evento ->
                            estado.equalsIgnoreCase(
                                    evento.getEstado()
                            ))
                    .toList();
        }

        if (!ubicacionTexto.isBlank()) {

            try {

                int ubicacion =
                        Integer.parseInt(ubicacionTexto);

                eventos = eventos.stream()
                        .filter(evento ->
                                evento.getUbicacionId() == ubicacion)
                        .toList();

            } catch (NumberFormatException ex) {

                request.setAttribute(
                        "error",
                        "La ubicación no es válida."
                );
            }
        }

        request.setAttribute(
                "eventos",
                eventos
        );

        request.setAttribute(
                "totalEventos",
                eventos.size()
        );

        request.setAttribute(
                "magnitudMinima",
                magnitudMinimaTexto
        );

        request.setAttribute(
                "estadoSeleccionado",
                estado
        );

        request.setAttribute(
                "ubicacionSeleccionada",
                ubicacionTexto
        );

        request.setAttribute(
                "codigoBuscado",
                codigoTexto
        );

        request.setAttribute(
                "fechaSeleccionada",
                fechaTexto
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/sismos/listar.jsp"
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

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}