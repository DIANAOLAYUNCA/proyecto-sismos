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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "UbicacionNuevoServlet", urlPatterns = "/ubicaciones/nuevo")
public class UbicacionNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/ubicaciones/formulario.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String provinciaIdTexto = limpiar(request.getParameter("provinciaId"));
        String distritoIdTexto = limpiar(request.getParameter("distritoId"));

        guardarValores(request, provinciaIdTexto, distritoIdTexto);

        try {

            if (provinciaIdTexto.isBlank()) {
                throw new IllegalArgumentException();
            }

            int provinciaId = Integer.parseInt(provinciaIdTexto);

            Integer distritoId = distritoIdTexto.isBlank()
                    ? null
                    : Integer.parseInt(distritoIdTexto);

            if (provinciaId <= 0 || (distritoId != null && distritoId <= 0)) {
                throw new IllegalArgumentException();
            }

            Ubicacion ubicacion = new Ubicacion(
                    0,
                    provinciaId,
                    distritoId,
                    LocalDateTime.now().format(FORMATO_FECHA),
                    "sistema",
                    null,
                    null
            );

            repositorio(request).agregar(ubicacion);

            response.sendRedirect(
                    request.getContextPath() + "/ubicaciones"
            );

        } catch (IllegalArgumentException ex) {

            mostrarError(
                    request,
                    response,
                    "Revise los datos: la provincia es obligatoria y debe ser un número positivo; el distrito, si se indica, también debe serlo."
            );

        } catch (RuntimeException ex) {

            getServletContext().log("Error al guardar la ubicación", ex);

            mostrarError(
                    request,
                    response,
                    "No se pudo guardar la ubicación. Verifique que la provincia y el distrito existan en el catálogo."
            );
        }
    }

    private void mostrarError(
            HttpServletRequest request,
            HttpServletResponse response,
            String mensaje)
            throws ServletException, IOException {

        request.setAttribute("error", mensaje);

        request.getRequestDispatcher(
                "/WEB-INF/views/ubicaciones/formulario.jsp"
        ).forward(request, response);
    }

    private UbicacionRepository repositorio(HttpServletRequest request) {
        return (UbicacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_UBICACIONES);
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private void guardarValores(
            HttpServletRequest request,
            String provinciaId,
            String distritoId) {

        request.setAttribute("provinciaIdIngresada", provinciaId);
        request.setAttribute("distritoIdIngresada", distritoId);
    }
}
