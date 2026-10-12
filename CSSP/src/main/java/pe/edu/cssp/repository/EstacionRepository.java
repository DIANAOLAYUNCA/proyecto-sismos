package pe.edu.cssp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import pe.edu.cssp.config.JpaUtil;
import pe.edu.cssp.model.Estacion;

import java.util.List;
import java.util.Optional;

public class EstacionRepository {

    // LISTAR ESTACIONES
    public List<Estacion> listar() {
        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return em.createQuery(
                "SELECT e FROM Estacion e ORDER BY e.id",
                Estacion.class
            ).getResultList();
        }
    }

    // BUSCAR POR ID
    public Optional<Estacion> buscarId(int id) {
        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return Optional.ofNullable(
                em.find(Estacion.class, id)
            );
        }
    }

    // BUSCAR POR CODIGO
    public Optional<Estacion> buscarCod(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }

        try (EntityManager em = JpaUtil.crearEntityManager()) {
            return em.createQuery(
                "SELECT e FROM Estacion e "
                + "WHERE UPPER(e.codigo) = :codigo",
                Estacion.class
            )
            .setParameter("codigo", codigo.trim().toUpperCase())
            .getResultStream()
            .findFirst();
        }
    }

    // VERIFICAR CODIGO EXISTENTE
    public boolean existCodigo(String codigo) {
        return buscarCod(codigo).isPresent();
    }

    // REGISTRAR ESTACION
    public Estacion agregar(Estacion estacion) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(estacion);
            tx.commit();

            return estacion;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    // ACTUALIZAR ESTACION
    public boolean actualizar(Estacion estacion) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Estacion actual = em.find(
                Estacion.class,
                estacion.getId()
            );

            if (actual == null) {
                tx.rollback();
                return false;
            }

            em.merge(estacion);
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

    // ELIMINACION LOGICA
    public boolean eliminar(int id) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Estacion estacion = em.find(
                Estacion.class,
                id
            );

            if (estacion == null) {
                tx.rollback();
                return false;
            }

            estacion.setEstado("INACTIVA");

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
