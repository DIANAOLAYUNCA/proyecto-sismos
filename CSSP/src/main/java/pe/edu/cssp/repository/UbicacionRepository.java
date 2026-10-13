package pe.edu.cssp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import pe.edu.cssp.config.JpaUtil;
import pe.edu.cssp.model.Ubicacion;

import java.util.List;
import java.util.Optional;

public class UbicacionRepository {

    public List<Ubicacion> listar() {
        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return em.createQuery(
                "SELECT u FROM Ubicacion u ORDER BY u.id",
                Ubicacion.class
            ).getResultList();
        }
    }

    public Optional<Ubicacion> buscarId(int id) {
        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return Optional.ofNullable(
                em.find(Ubicacion.class, id)
            );
        }
    }

    public Ubicacion agregar(Ubicacion ubicacion) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(ubicacion);
            tx.commit();
            return ubicacion;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean actualizar(Ubicacion ubicacion) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Ubicacion actual = em.find(
                Ubicacion.class, ubicacion.getId()
            );

            if (actual == null) {
                tx.rollback();
                return false;
            }

            em.merge(ubicacion);
            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean eliminar(int id) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Ubicacion ubicacion = em.find(Ubicacion.class, id);

            if (ubicacion == null) {
                tx.rollback();
                return false;
            }

            em.remove(ubicacion);
            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
