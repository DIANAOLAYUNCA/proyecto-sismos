package pe.edu.cssp.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.cssp.listener.AplicacionListener;
import pe.edu.cssp.model.Estacion;
import pe.edu.cssp.repository.EstacionRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "EstacionNuevoServlet", urlPatterns = "/estaciones/nuevo")
public class EstacionNuevoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/estaciones/formulario.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String codigo = limpiar(request.getParameter("codigo"));
        String nombre = limpiar(request.getParameter("nombre"));
        String ubicacionIdTexto = limpiar(request.getParameter("ubicacionId"));
        String latitudTexto = limpiar(request.getParameter("latitud"));
        String longitudTexto = limpiar(request.getParameter("longitud"));
        String instalacionFecha = limpiar(request.getParameter("instalacionFecha"));
        String estado = limpiar(request.getParameter("estado"));

        guardarValores(
                request,
                codigo,
                nombre,
                ubicacionIdTexto,
                latitudTexto,
                longitudTexto,
                instalacionFecha,
                estado
        );

        try {
            if (codigo.isBlank()
                    || nombre.isBlank()
                    || ubicacionIdTexto.isBlank()
                    || latitudTexto.isBlank()
                    || longitudTexto.isBlank()
                    || instalacionFecha.isBlank()
                    || estado.isBlank()) {

                throw new IllegalArgumentException();
            }

            int ubicacionId = Integer.parseInt(ubicacionIdTexto);
            double latitud = Double.parseDouble(
                    latitudTexto.replace(',', '.')
            );
            double longitud = Double.parseDouble(
                    longitudTexto.replace(',', '.')
            );

            if (ubicacionId <= 0
                    || !Double.isFinite(latitud)
                    || !Double.isFinite(longitud)
                    || latitud < -90
                    || latitud > 90
                    || longitud < -180
                    || longitud > 180
                    || !estadoValido(estado)) {

                throw new IllegalArgumentException();
            }

            EstacionRepository repositorio = repositorio(request);

            if (repositorio.existCodigo(codigo)) {
                request.setAttribute(
                        "error",
                        "El codigo ya existe."
                );

                request.getRequestDispatcher(
                        "/WEB-INF/views/estaciones/formulario.jsp"
                ).forward(request, response);

                return;
            }

            Estacion estacion = new Estacion(
                    0,
                    codigo,
                    nombre,
                    ubicacionId,
                    latitud,
                    longitud,
                    instalacionFecha,
                    estado,
                    null,
                    null,
                    null,
                    null
            );

            repositorio.agregar(estacion);

            response.sendRedirect(
                    request.getContextPath()
                    + "/estaciones"
            );

        } catch (IllegalArgumentException ex) {

            request.setAttribute(
                    "error",
                    "Revise los datos: campos obligatorios, ubicación positiva, coordenadas válidas y estado correcto."
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/estaciones/formulario.jsp"
            ).forward(request, response);
        }
    }

    private EstacionRepository repositorio(HttpServletRequest request) {
        return (EstacionRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_ESTACIONES);
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private boolean estadoValido(String estado) {
        return "ACTIVA".equals(estado)
                || "INACTIVA".equals(estado);
    }

    private void guardarValores(
            HttpServletRequest request,
            String codigo,
            String nombre,
            String ubicacionId,
            String latitud,
            String longitud,
            String instalacionFecha,
            String estado) {

        request.setAttribute("codigoIngresado", codigo);
        request.setAttribute("nombreIngresado", nombre);
        request.setAttribute("ubicacionIdIngresada", ubicacionId);
        request.setAttribute("latitudIngresada", latitud);
        request.setAttribute("longitudIngresada", longitud);
        request.setAttribute("instalacionFechaIngresada", instalacionFecha);
        request.setAttribute("estadoIngresado", estado);
    }
}