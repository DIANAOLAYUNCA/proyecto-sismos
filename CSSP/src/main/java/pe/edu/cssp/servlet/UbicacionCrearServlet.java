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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "UbicacionCrearServlet", urlPatterns = "/ubicaciones/crear")
public class UbicacionCrearServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/ubicaciones/crear.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer provinciaId = leerEntero(request.getParameter("provinciaId"));
        Integer distritoId = leerEntero(request.getParameter("distritoId"));

        if (provinciaId == null) {
            request.setAttribute("error", "La provincia es obligatoria y debe ser un número válido.");
            request.setAttribute("provinciaId", request.getParameter("provinciaId"));
            request.setAttribute("distritoId", request.getParameter("distritoId"));
            request.getRequestDispatcher(
                    "/WEB-INF/views/ubicaciones/crear.jsp"
            ).forward(request, response);
            return;
        }

        Ubicacion ubicacion = new Ubicacion(
                0,
                provinciaId,
                distritoId,
                LocalDateTime.now().format(FORMATO_FECHA),
                "operador",
                null,
                null
        );

        repositorio(request).agregar(ubicacion);

        response.sendRedirect(request.getContextPath() + "/ubicaciones?creado=true");
    }

    private UbicacionRepository repositorio(HttpServletRequest request) {
        return (UbicacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_UBICACIONES);
    }

    private Integer leerEntero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
