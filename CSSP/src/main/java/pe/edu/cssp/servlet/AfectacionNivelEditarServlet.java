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

@WebServlet(name = "AfectacionNivelEditarServlet", urlPatterns = "/afectacionniveles/editar")
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

        AfectacionNivel existente = repositorio.buscarId(id).orElse(null);

        if (existente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nombre = limpiar(request.getParameter("nombre"));
        String codigo = limpiar(request.getParameter("codigo"));

        if (nombre.isBlank() || codigo.isBlank()) {

            request.setAttribute(
                    "error",
                    "El nombre y el código son obligatorios."
            );

            request.setAttribute(
                    "nivel",
                    crearNivel(id, nombre, codigo, existente)
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/afectacionniveles/editar.jsp"
            ).forward(request, response);

            return;
        }

        AfectacionNivel duplicado = repositorio.buscarCod(codigo).orElse(null);

        if (duplicado != null && duplicado.getId() != id) {

            request.setAttribute(
                    "error",
                    "El código ya pertenece a otro nivel de afectación."
            );

            request.setAttribute(
                    "nivel",
                    crearNivel(id, nombre, codigo, existente)
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/afectacionniveles/editar.jsp"
            ).forward(request, response);

            return;
        }

        AfectacionNivel nivel = crearNivel(id, nombre, codigo.toUpperCase(), existente);

        repositorio.actualizar(nivel);

        response.sendRedirect(
                request.getContextPath()
                + "/afectacionniveles/detalle?id=" + id + "&actualizado=1"
        );
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

    private AfectacionNivelRepository repositorio(HttpServletRequest request) {
        return (AfectacionNivelRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_AFECTACION_NIVELES);
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
