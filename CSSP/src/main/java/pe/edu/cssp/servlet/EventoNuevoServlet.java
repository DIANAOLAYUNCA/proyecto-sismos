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

@WebServlet(name = "EventoNuevoServlet", urlPatterns = "/eventos/nuevo")
public class EventoNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/sismos/formulario.jsp"
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

        guardarValores(
                request,
                estacionIdTexto,
                codigo,
                fechaHora,
                magnitudTexto,
                profundidadTexto,
                latitudTexto,
                longitudTexto,
                ubicacionIdTexto,
                afectacionNivelIdTexto,
                estado
        );

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

            EventoRepository repositorio =
                    repositorio(request);

            if (repositorio.existCodigo(codigo)) {

                request.setAttribute(
                        "error",
                        "El codigo ya existe."
                );

                request.getRequestDispatcher(
                        "/WEB-INF/views/sismos/formulario.jsp"
                ).forward(request, response);

                return;
            }

            Evento evento = new Evento(
                    0,
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
                    null,
                    null,
                    null,
                    null
            );

            repositorio.agregar(evento);

            response.sendRedirect(
                    request.getContextPath()
                    + "/eventos"
            );

        } catch (IllegalArgumentException ex) {

            request.setAttribute(
                    "error",
                    "Revise los datos ingresados."
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/sismos/formulario.jsp"
            ).forward(request, response);
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

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private boolean estadoValido(String estado) {
        return "REGISTRADO".equals(estado)
                || "EN_SEGUIMIENTO".equals(estado)
                || "EN_EVALUACION".equals(estado)
                || "CERRADO".equals(estado);
    }

    private void guardarValores(
            HttpServletRequest request,
            String estacionId,
            String codigo,
            String fechaHora,
            String magnitud,
            String profundidad,
            String latitud,
            String longitud,
            String ubicacionId,
            String afectacionNivelId,
            String estado) {

        request.setAttribute(
                "estacionIdIngresada",
                estacionId
        );

        request.setAttribute(
                "codigoIngresado",
                codigo
        );

        request.setAttribute(
                "fechaHoraIngresada",
                fechaHora
        );

        request.setAttribute(
                "magnitudIngresada",
                magnitud
        );

        request.setAttribute(
                "profundidadIngresada",
                profundidad
        );

        request.setAttribute(
                "latitudIngresada",
                latitud
        );

        request.setAttribute(
                "longitudIngresada",
                longitud
        );

        request.setAttribute(
                "ubicacionIdIngresada",
                ubicacionId
        );

        request.setAttribute(
                "afectacionNivelIdIngresada",
                afectacionNivelId
        );

        request.setAttribute(
                "estadoIngresado",
                estado
        );
    }
}