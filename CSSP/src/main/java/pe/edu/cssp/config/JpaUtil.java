
package pe.edu.cssp.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public final class JpaUtil {

    private static EntityManagerFactory emf;

    private JpaUtil() {
    }

    public static synchronized EntityManager crearEntityManager() {

        if (emf == null) {

            String password = System.getenv("CSSP_DB_PASSWORD");

            if (password == null || password.isBlank()) {
                throw new IllegalStateException(
                    "Falta configurar CSSP_DB_PASSWORD"
                );
            }

            Map<String, Object> propiedades = new HashMap<>();

            propiedades.put(
                "jakarta.persistence.jdbc.password",
                password
            );

            emf = Persistence.createEntityManagerFactory(
                "CSSPPU",
                propiedades
            );
        }

        return emf.createEntityManager();
    }

    public static synchronized void cerrar() {

    if (emf != null && emf.isOpen()) {
        emf.close();
    }

    emf = null;
}
}
