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

@WebServlet(name = "UbicacionEditarServlet", urlPatterns = "/ubicaciones/editar")
public class UbicacionEditarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        Ubicacion ubicacion = repositorio(request)
                .buscarId(id)
                .orElse(null);

        if (ubicacion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("ubicacion", ubicacion);

        request.getRequestDispatcher(
                "/WEB-INF/views/ubicaciones/editar.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        int id = leerId(request.getParameter("id"));

        UbicacionRepository repositorio = repositorio(request);

        Ubicacion existente = repositorio.buscarId(id).orElse(null);

        if (existente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String provinciaIdTexto = limpiar(request.getParameter("provinciaId"));
        String distritoIdTexto = limpiar(request.getParameter("distritoId"));

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

            Ubicacion ubicacion = crearUbicacion(id, provinciaId, distritoId, existente);

            repositorio.actualizar(ubicacion);

            response.sendRedirect(
                    request.getContextPath()
                    + "/ubicaciones/detalle?id=" + id + "&actualizado=1"
            );

        } catch (IllegalArgumentException ex) {

            request.setAttribute(
                    "error",
                    "Revise los datos: la provincia es obligatoria y debe ser un número positivo; el distrito, si se indica, también debe serlo."
            );

            request.setAttribute(
                    "ubicacion",
                    crearUbicacion(
                            id,
                            convertirEntero(provinciaIdTexto),
                            convertirEnteroOpcional(distritoIdTexto),
                            existente
                    )
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/ubicaciones/editar.jsp"
            ).forward(request, response);
        }
    }

    private Ubicacion crearUbicacion(
            int id,
            int provinciaId,
            Integer distritoId,
            Ubicacion existente) {

        return new Ubicacion(
                id,
                provinciaId,
                distritoId,
                existente.getCreacionFecha(),
                existente.getCreacionUsuario(),
                existente.getActualizacionFecha(),
                existente.getActualizacionUsuario()
        );
    }

    private UbicacionRepository repositorio(HttpServletRequest request) {
        return (UbicacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_UBICACIONES);
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

    private Integer convertirEnteroOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
