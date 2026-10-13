
package pe.edu.cssp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import pe.edu.cssp.config.JpaUtil;
import pe.edu.cssp.model.AfectacionNivel;

import java.util.List;
import java.util.Optional;

public class AfectacionNivelRepository {

    public List<AfectacionNivel> listar() {
        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return em.createQuery(
                "SELECT a FROM AfectacionNivel a ORDER BY a.id",
                AfectacionNivel.class
            ).getResultList();
        }
    }

    public Optional<AfectacionNivel> buscarId(int id) {
        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return Optional.ofNullable(
                em.find(AfectacionNivel.class, id)
            );
        }
    }

    public Optional<AfectacionNivel> buscarCod(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }

        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return em.createQuery(
                "SELECT a FROM AfectacionNivel a "
                + "WHERE UPPER(a.codigo) = :codigo",
                AfectacionNivel.class
            )
            .setParameter("codigo", codigo.trim().toUpperCase())
            .getResultStream()
            .findFirst();
        }
    }

    public boolean existCodigo(String codigo) {
        return buscarCod(codigo).isPresent();
    }

    public AfectacionNivel agregar(AfectacionNivel nivel) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(nivel);
            tx.commit();
            return nivel;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean actualizar(AfectacionNivel nivel) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            AfectacionNivel actual = em.find(
                AfectacionNivel.class, nivel.getId()
            );

            if (actual == null) {
                tx.rollback();
                return false;
            }

            em.merge(nivel);
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

            AfectacionNivel nivel = em.find(
                AfectacionNivel.class, id
            );

            if (nivel == null) {
                tx.rollback();
                return false;
            }

            em.remove(nivel);
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
