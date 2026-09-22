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

@WebServlet(name = "EstacionEditarServlet", urlPatterns = "/estaciones/editar")
public class EstacionEditarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id = leerId(request.getParameter("id"));

        Estacion estacion = repositorio(request)
                .buscarId(id)
                .orElse(null);

        if (estacion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("estacion", estacion);

        request.getRequestDispatcher(
                "/WEB-INF/views/estaciones/editar.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        int id = leerId(request.getParameter("id"));

        EstacionRepository repositorio = repositorio(request);

        Estacion existente = repositorio
                .buscarId(id)
                .orElse(null);

        if (existente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String codigo = limpiar(request.getParameter("codigo"));
        String nombre = limpiar(request.getParameter("nombre"));
        String ubicacionIdTexto =
                limpiar(request.getParameter("ubicacionId"));
        String latitudTexto =
                limpiar(request.getParameter("latitud"));
        String longitudTexto =
                limpiar(request.getParameter("longitud"));
        String instalacionFecha =
                limpiar(request.getParameter("instalacionFecha"));
        String estado =
                limpiar(request.getParameter("estado"));

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

            int ubicacionId =
                    Integer.parseInt(ubicacionIdTexto);

            double latitud =
                    Double.parseDouble(
                            latitudTexto.replace(',', '.')
                    );

            double longitud =
                    Double.parseDouble(
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

            if (repositorio.existCodigo(codigo)) {

                Estacion encontrada =
                        repositorio.buscarCod(codigo)
                                .orElse(null);

                if (encontrada != null
                        && encontrada.getId() != id) {

                    request.setAttribute(
                            "error",
                            "El codigo ya pertenece a otra estación."
                    );

                    request.setAttribute(
                            "estacion",
                            crearEstacion(
                                    id,
                                    codigo,
                                    nombre,
                                    ubicacionId,
                                    latitud,
                                    longitud,
                                    instalacionFecha,
                                    estado,
                                    existente
                            )
                    );

                    request.getRequestDispatcher(
                            "/WEB-INF/views/estaciones/editar.jsp"
                    ).forward(request, response);

                    return;
                }
            }

            Estacion estacion = crearEstacion(
                    id,
                    codigo,
                    nombre,
                    ubicacionId,
                    latitud,
                    longitud,
                    instalacionFecha,
                    estado,
                    existente
            );

            repositorio.actualizar(estacion);

            response.sendRedirect(
                    request.getContextPath()
                    + "/estaciones/detalle?id="
                    + id
                    + "&actualizado=1"
            );

        } catch (IllegalArgumentException ex) {

            request.setAttribute(
                    "error",
                    "Revise los datos: campos obligatorios, ubicación positiva, coordenadas válidas y estado correcto."
            );

            request.setAttribute(
                    "estacion",
                    crearEstacion(
                            id,
                            codigo,
                            nombre,
                            convertirEntero(ubicacionIdTexto),
                            convertirDouble(latitudTexto),
                            convertirDouble(longitudTexto),
                            instalacionFecha,
                            estado,
                            existente
                    )
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/estaciones/editar.jsp"
            ).forward(request, response);
        }
    }

    private Estacion crearEstacion(
            int id,
            String codigo,
            String nombre,
            int ubicacionId,
            double latitud,
            double longitud,
            String instalacionFecha,
            String estado,
            Estacion existente) {

        return new Estacion(
                id,
                codigo,
                nombre,
                ubicacionId,
                latitud,
                longitud,
                instalacionFecha,
                estado,
                existente.getCreacionFecha(),
                existente.getCreacionUsuario(),
                existente.getActualizacionFecha(),
                existente.getActualizacionUsuario()
        );
    }

    private EstacionRepository repositorio(
            HttpServletRequest request) {

        return (EstacionRepository) request
                .getServletContext()
                .getAttribute(
                        AplicacionListener.REPOSITORIO_ESTACIONES
                );
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

    private double convertirDouble(String texto) {
        try {
            return Double.parseDouble(
                    texto.replace(',', '.')
            );
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private boolean estadoValido(String estado) {
        return "ACTIVA".equals(estado)
                || "INACTIVA".equals(estado);
    }
}