package pe.edu.cssp.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.EventoRepository;

@WebListener
public class AplicacionListener implements ServletContextListener {

    public static final String REPOSITORIO_ESTACIONES = "repositorioEstaciones";
    public static final String REPOSITORIO_EVENTOS = "repositorioEventos";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute(
                REPOSITORIO_ESTACIONES,
                new EstacionRepository()
        );

        event.getServletContext().setAttribute(
                REPOSITORIO_EVENTOS,
                new EventoRepository()
        );
    }
}
