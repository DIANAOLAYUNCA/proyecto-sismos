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

@WebServlet(name = "AfectacionNivelNuevoServlet", urlPatterns = "/afectacionniveles/nuevo")
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
        String codigo = limpiar(request.getParameter("codigo"));

        guardarValores(request, nombre, codigo);

        if (nombre.isBlank() || codigo.isBlank()) {

            request.setAttribute(
                    "error",
                    "El nombre y el código son obligatorios."
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/afectacionniveles/formulario.jsp"
            ).forward(request, response);

            return;
        }

        AfectacionNivelRepository repositorio = repositorio(request);

        if (repositorio.existCodigo(codigo)) {

            request.setAttribute(
                    "error",
                    "El código ya existe."
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/afectacionniveles/formulario.jsp"
            ).forward(request, response);

            return;
        }

        AfectacionNivel nivel = new AfectacionNivel(
                0,
                nombre,
                codigo.toUpperCase(),
                null,
                null,
                null,
                null
        );

        repositorio.agregar(nivel);

        response.sendRedirect(
                request.getContextPath() + "/afectacionniveles"
        );
    }

    private AfectacionNivelRepository repositorio(HttpServletRequest request) {
        return (AfectacionNivelRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_AFECTACION_NIVELES);
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
}
