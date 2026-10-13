
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

@WebServlet(
    name = "AfectacionNivelNuevoServlet",
    urlPatterns = "/afectacionniveles/nuevo"
)
public class AfectacionNivelNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/afectacionniveles/formulario.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String nombre = limpiar(request.getParameter("nombre"));
        String codigo = limpiar(request.getParameter("codigo"))
                .toUpperCase(java.util.Locale.ROOT);

        guardarValores(request, nombre, codigo);

        // Validar campos obligatorios
        if (nombre.isBlank() || codigo.isBlank()) {

            mostrarError(
                    request,
                    response,
                    "El nombre y el código son obligatorios."
            );
            return;
        }

        // Validar longitud según la base de datos
        if (nombre.length() > 100 || codigo.length() > 20) {

            mostrarError(
                    request,
                    response,
                    "El nombre o código supera la longitud permitida."
            );
            return;
        }

        AfectacionNivelRepository repositorio = repositorio(request);

        // Verificar que el código no esté registrado
        if (repositorio.existCodigo(codigo)) {

            mostrarError(
                    request,
                    response,
                    "El código ya existe."
            );
            return;
        }

        // Crear nuevo nivel con datos de auditoría
        AfectacionNivel nivel = new AfectacionNivel(
                0,
                nombre,
                codigo,
                
                LocalDateTime.now().format(
        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
),
                "sistema",
                null,
                null
        );

        try {

            // Guardar mediante el repositorio JPA
            repositorio.agregar(nivel);

            response.sendRedirect(
                    request.getContextPath() + "/afectacionniveles"
            );

        } catch (Exception e) {

            getServletContext().log(
                    "Error al guardar nivel de afectación", e
            );

            mostrarError(
                    request,
                    response,
                    "No se pudo guardar el nivel. "
                    + "Verifica que el nombre y código no estén registrados."
            );
        }
    }

    private AfectacionNivelRepository repositorio(
            HttpServletRequest request) {

        return (AfectacionNivelRepository)
                request.getServletContext().getAttribute(
                        AplicacionListener.REPOSITORIO_AFECTACION_NIVELES
                );
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private void guardarValores(
            HttpServletRequest request,
            String nombre,
            String codigo) {

        request.setAttribute("nombreIngresado", nombre);
        request.setAttribute("codigoIngresado", codigo);
    }

    private void mostrarError(
            HttpServletRequest request,
            HttpServletResponse response,
            String mensaje)
            throws ServletException, IOException {

        request.setAttribute("error", mensaje);

        request.getRequestDispatcher(
                "/WEB-INF/views/afectacionniveles/formulario.jsp"
        ).forward(request, response);
    }
}
