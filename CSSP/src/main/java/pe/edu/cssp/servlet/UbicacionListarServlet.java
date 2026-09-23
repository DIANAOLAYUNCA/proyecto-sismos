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
import java.util.List;

@WebServlet(name = "UbicacionListarServlet", urlPatterns = "/ubicaciones")
public class UbicacionListarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String provinciaTexto = limpiar(request.getParameter("provinciaId"));

        UbicacionRepository repositorio = repositorio(request);
        List<Ubicacion> ubicaciones = repositorio.listar();

        if (!provinciaTexto.isBlank()) {

            try {

                int provinciaId = Integer.parseInt(provinciaTexto);

                ubicaciones = ubicaciones.stream()
                        .filter(ubicacion -> ubicacion.getProvinciaId() == provinciaId)
                        .toList();

            } catch (NumberFormatException ex) {

                request.setAttribute("error", "La provincia no es válida.");
            }
        }

        request.setAttribute("ubicaciones", ubicaciones);
        request.setAttribute("totalUbicaciones", ubicaciones.size());
        request.setAttribute("provinciaSeleccionada", provinciaTexto);

        request.getRequestDispatcher(
                "/WEB-INF/views/ubicaciones/listar.jsp"
        ).forward(request, response);
    }

    private UbicacionRepository repositorio(HttpServletRequest request) {
        return (UbicacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_UBICACIONES);
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
