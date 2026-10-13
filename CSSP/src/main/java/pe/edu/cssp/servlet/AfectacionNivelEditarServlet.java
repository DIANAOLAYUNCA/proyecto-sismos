
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;

@WebServlet(
    name = "AfectacionNivelEditarServlet",
    urlPatterns = "/afectacionniveles/editar"
)
public class AfectacionNivelEditarServlet extends HttpServlet {

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
                "/WEB-INF/views/afectacionniveles/editar.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        int id = leerId(request.getParameter("id"));

        AfectacionNivelRepository repositorio = repositorio(request);

        AfectacionNivel existente = repositorio
                .buscarId(id)
                .orElse(null);

        if (existente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nombre = limpiar(request.getParameter("nombre"));
        String codigo = limpiar(request.getParameter("codigo"))
                .toUpperCase(Locale.ROOT);

        // Validar campos obligatorios
        if (nombre.isBlank() || codigo.isBlank()) {

            mostrarError(
                    request,
                    response,
                    crearNivel(id, nombre, codigo, existente),
                    "El nombre y el código son obligatorios."
            );
            return;
        }

        // Validar longitud de los campos
        if (nombre.length() > 100 || codigo.length() > 20) {

            mostrarError(
                    request,
                    response,
                    crearNivel(id, nombre, codigo, existente),
                    "El nombre o código supera la longitud permitida."
            );
            return;
        }

        // Validar que el código no pertenezca a otro registro
        AfectacionNivel duplicado = repositorio
                .buscarCod(codigo)
                .orElse(null);

        if (duplicado != null && duplicado.getId() != id) {

            mostrarError(
                    request,
                    response,
                    crearNivel(id, nombre, codigo, existente),
                    "El código ya pertenece a otro nivel de afectación."
            );
            return;
        }

        // Preparar el registro actualizado
        AfectacionNivel nivel = crearNivel(
                id, nombre, codigo, existente
        );

        nivel.setActualizacionFecha(
        LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )
);
        nivel.setActualizacionUsuario("sistema");

        try {

            boolean actualizado = repositorio.actualizar(nivel);

            if (!actualizado) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/afectacionniveles/detalle?id="
                    + id + "&actualizado=1"
            );

        } catch (Exception e) {

            getServletContext().log(
                    "Error al actualizar nivel de afectación", e
            );

            mostrarError(
                    request,
                    response,
                    nivel,
                    "No se pudo actualizar el nivel de afectación."
            );
        }
    }

    private AfectacionNivel crearNivel(
            int id,
            String nombre,
            String codigo,
            AfectacionNivel existente) {

        return new AfectacionNivel(
                id,
                nombre,
                codigo,
                existente.getCreacionFecha(),
                existente.getCreacionUsuario(),
                existente.getActualizacionFecha(),
                existente.getActualizacionUsuario()
        );
    }

    private void mostrarError(
            HttpServletRequest request,
            HttpServletResponse response,
            AfectacionNivel nivel,
            String mensaje)
            throws ServletException, IOException {

        request.setAttribute("error", mensaje);
        request.setAttribute("nivel", nivel);

        request.getRequestDispatcher(
                "/WEB-INF/views/afectacionniveles/editar.jsp"
        ).forward(request, response);
    }

    private AfectacionNivelRepository repositorio(
            HttpServletRequest request) {

        return (AfectacionNivelRepository)
                request.getServletContext().getAttribute(
                        AplicacionListener.REPOSITORIO_AFECTACION_NIVELES
                );
    }

    private int leerId(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
