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
import java.nio.charset.StandardCharsets;

@WebServlet(name = "EventoEditarServlet", urlPatterns = "/eventos/editar")
public class EventoEditarServlet extends HttpServlet {

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
                "/WEB-INF/views/sismos/editar.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        int id = leerId(
                request.getParameter("id")
        );

        EventoRepository repositorio =
                repositorio(request);

        Evento existente = repositorio
                .buscarId(id)
                .orElse(null);

        if (existente == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        String estacionIdTexto =
                limpiar(request.getParameter("estacionId"));

        String codigo =
                limpiar(request.getParameter("codigo"));

        String fechaHora =
                limpiar(request.getParameter("fechaHora"));

        String magnitudTexto =
                limpiar(request.getParameter("magnitud"));

        String profundidadTexto =
                limpiar(request.getParameter("profundidad"));

        String latitudTexto =
                limpiar(request.getParameter("latitud"));

        String longitudTexto =
                limpiar(request.getParameter("longitud"));

        String ubicacionIdTexto =
                limpiar(request.getParameter("ubicacionId"));

        String afectacionNivelIdTexto =
                limpiar(request.getParameter("afectacionNivelId"));

        String estado =
                limpiar(request.getParameter("estado"));

        try {

            if (estacionIdTexto.isBlank()
                    || codigo.isBlank()
                    || fechaHora.isBlank()
                    || magnitudTexto.isBlank()
                    || profundidadTexto.isBlank()
                    || latitudTexto.isBlank()
                    || longitudTexto.isBlank()
                    || ubicacionIdTexto.isBlank()
                    || afectacionNivelIdTexto.isBlank()
                    || estado.isBlank()) {

                throw new IllegalArgumentException();
            }

            int estacionId =
                    Integer.parseInt(estacionIdTexto);

            double magnitud =
                    Double.parseDouble(
                            magnitudTexto.replace(',', '.')
                    );

            double profundidad =
                    Double.parseDouble(
                            profundidadTexto.replace(',', '.')
                    );

            double latitud =
                    Double.parseDouble(
                            latitudTexto.replace(',', '.')
                    );

            double longitud =
                    Double.parseDouble(
                            longitudTexto.replace(',', '.')
                    );

            int ubicacionId =
                    Integer.parseInt(ubicacionIdTexto);

            int afectacionNivelId =
                    Integer.parseInt(afectacionNivelIdTexto);

            if (estacionId <= 0
                    || ubicacionId <= 0
                    || afectacionNivelId <= 0
                    || !Double.isFinite(magnitud)
                    || !Double.isFinite(profundidad)
                    || !Double.isFinite(latitud)
                    || !Double.isFinite(longitud)
                    || magnitud < 0
                    || profundidad < 0
                    || latitud < -90
                    || latitud > 90
                    || longitud < -180
                    || longitud > 180
                    || !estadoValido(estado)) {

                throw new IllegalArgumentException();
            }

            if (repositorio.existCodigo(codigo)) {

                Evento encontrado =
                        repositorio.buscarCod(codigo)
                                .orElse(null);

                if (encontrado != null
                        && encontrado.getId() != id) {

                    request.setAttribute(
                            "error",
                            "El codigo ya pertenece a otro evento."
                    );

                    request.setAttribute(
                            "evento",
                            crearEvento(
                                    id,
                                    estacionId,
                                    codigo,
                                    fechaHora,
                                    magnitud,
                                    profundidad,
                                    latitud,
                                    longitud,
                                    ubicacionId,
                                    afectacionNivelId,
                                    estado,
                                    existente
                            )
                    );

                    request.getRequestDispatcher(
                            "/WEB-INF/views/sismos/editar.jsp"
                    ).forward(request, response);

                    return;
                }
            }

            Evento evento = crearEvento(
                    id,
                    estacionId,
                    codigo,
                    fechaHora,
                    magnitud,
                    profundidad,
                    latitud,
                    longitud,
                    ubicacionId,
                    afectacionNivelId,
                    estado,
                    existente
            );

            repositorio.actualizar(evento);

            response.sendRedirect(
                    request.getContextPath()
                    + "/eventos/detalle?id="
                    + id
                    + "&actualizado=1"
            );

        } catch (IllegalArgumentException ex) {

            request.setAttribute(
                    "error",
                    "Revise los datos ingresados."
            );

            request.setAttribute(
                    "evento",
                    crearEvento(
                            id,
                            convertirEntero(estacionIdTexto),
                            codigo,
                            fechaHora,
                            convertirDouble(magnitudTexto),
                            convertirDouble(profundidadTexto),
                            convertirDouble(latitudTexto),
                            convertirDouble(longitudTexto),
                            convertirEntero(ubicacionIdTexto),
                            convertirEntero(afectacionNivelIdTexto),
                            estado,
                            existente
                    )
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/sismos/editar.jsp"
            ).forward(request, response);
        }
    }

    private Evento crearEvento(
            int id,
            int estacionId,
            String codigo,
            String fechaHora,
            double magnitud,
            double profundidad,
            double latitud,
            double longitud,
            int ubicacionId,
            int afectacionNivelId,
            String estado,
            Evento existente) {

        return new Evento(
                id,
                estacionId,
                codigo,
                fechaHora,
                magnitud,
                profundidad,
                latitud,
                longitud,
                ubicacionId,
                afectacionNivelId,
                estado,
                existente.getCreacionFecha(),
                existente.getCreacionUsuario(),
                existente.getActualizacionFecha(),
                existente.getActualizacionUsuario()
        );
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

    private int convertirEntero(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private double convertirDouble(String texto) {
        try {
            return Double.parseDouble(
                    texto.replace(',', '.')
            );
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private boolean estadoValido(String estado) {
        return "REGISTRADO".equals(estado)
                || "EN_SEGUIMIENTO".equals(estado)
                || "EN_EVALUACION".equals(estado)
                || "CERRADO".equals(estado);
    }
}