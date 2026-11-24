package pe.edu.cssp;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Las pruebas web ejecutan cada petición como el administrador y con token CSRF,
 * para probar los módulos sin repetir el inicio de sesión en cada prueba.
 * Una prueba que necesite una petición sin sesión o con otro rol puede sobrescribir este valor
 * con request postprocessors propios.
 */
@TestConfiguration
class AutenticacionPruebas {
    @Bean
    MockMvcBuilderCustomizer administradorPorDefecto() {
        return builder -> builder.defaultRequest(get("/").with(user("admin").roles("ADMIN")).with(csrf()));
    }
}
