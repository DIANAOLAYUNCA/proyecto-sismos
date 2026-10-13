package pe.edu.cssp.test;

import jakarta.persistence.EntityManager;
import pe.edu.cssp.config.JpaUtil;
import pe.edu.cssp.model.AfectacionNivel;
import java.util.List;

public class PruebaConexion {

    public static void main(String[] args) {

        EntityManager em = null;

        try {
            em = JpaUtil.crearEntityManager();

            System.out.println("Conexion MySQL exitosa");

            List<AfectacionNivel> niveles =
                em.createQuery(
                    "SELECT a FROM AfectacionNivel a",
                    AfectacionNivel.class
                ).getResultList();

            System.out.println("Total de niveles: "
                    + niveles.size());

            for (AfectacionNivel nivel : niveles) {

                System.out.println(
                    nivel.getId() + " - "
                    + nivel.getNombre() + " - "
                    + nivel.getCodigo()
                );
            }

        } catch (Exception e) {

            System.err.println(
                "Error al consultar MySQL:"
            );

            e.printStackTrace();

        } finally {

            if (em != null && em.isOpen()) {
                em.close();
            }

            JpaUtil.cerrar();
        }
    }
}