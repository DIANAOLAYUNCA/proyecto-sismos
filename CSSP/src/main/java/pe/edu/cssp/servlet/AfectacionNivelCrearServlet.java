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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "AfectacionNivelCrearServlet", urlPatterns = "/afectacionniveles/crear")
public class AfectacionNivelCrearServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/afectacionniveles/crear.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = valorTexto(request.getParameter("nombre"));
        String codigo = valorTexto(request.getParameter("codigo"));
        AfectacionNivelRepository repositorio = repositorio(request);

        String error = null;
        if (nombre == null) {
            error = "El nombre es obligatorio.";
        } else if (codigo == null) {
            error = "El código es obligatorio.";
        } else if (repositorio.existCodigo(codigo)) {
            error = "Ya existe un nivel de afectación con el código " + codigo + ".";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("nombre", request.getParameter("nombre"));
            request.setAttribute("codigo", request.getParameter("codigo"));
            request.getRequestDispatcher(
                    "/WEB-INF/views/afectacionniveles/crear.jsp"
            ).forward(request, response);
            return;
        }

        AfectacionNivel nivel = new AfectacionNivel(
                0,
                nombre,
                codigo.toUpperCase(),
                LocalDateTime.now().format(FORMATO_FECHA),
                "operador",
                null,
                null
        );

        repositorio.agregar(nivel);

        response.sendRedirect(request.getContextPath() + "/afectacionniveles?creado=true");
    }

    private AfectacionNivelRepository repositorio(HttpServletRequest request) {
        return (AfectacionNivelRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_AFECTACION_NIVELES);
    }

    private String valorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }
}
