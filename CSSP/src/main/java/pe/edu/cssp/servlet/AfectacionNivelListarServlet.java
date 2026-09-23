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
import java.util.List;

@WebServlet(name = "AfectacionNivelListarServlet", urlPatterns = "/afectacionniveles")
public class AfectacionNivelListarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String codigoTexto = limpiar(request.getParameter("codigo"));

        AfectacionNivelRepository repositorio = repositorio(request);
        List<AfectacionNivel> niveles = repositorio.listar();

        if (!codigoTexto.isBlank()) {

            niveles = niveles.stream()
                    .filter(nivel -> nivel.getCodigo() != null
                            && nivel.getCodigo().toUpperCase().contains(codigoTexto.toUpperCase()))
                    .toList();
        }

        request.setAttribute("niveles", niveles);
        request.setAttribute("totalNiveles", niveles.size());
        request.setAttribute("codigoSeleccionado", codigoTexto);

        request.getRequestDispatcher(
                "/WEB-INF/views/afectacionniveles/listar.jsp"
        ).forward(request, response);
    }

    private AfectacionNivelRepository repositorio(HttpServletRequest request) {
        return (AfectacionNivelRepository) request.getServletContext()
                .getAttribute(AplicacionListener.REPOSITORIO_AFECTACION_NIVELES);
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
