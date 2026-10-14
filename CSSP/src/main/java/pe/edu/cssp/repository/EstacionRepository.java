package pe.edu.cssp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import pe.edu.cssp.config.JpaUtil;
import pe.edu.cssp.model.Estacion;
import pe.edu.cssp.model.Ubicacion;

import java.util.List;
import java.util.Optional;

public class EstacionRepository {

    public List<Estacion> listar() {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
               "SELECT e FROM Estacion e JOIN FETCH e.ubicacion ORDER BY e.id",
                Estacion.class).getResultList();
        } catch (RuntimeException ex) {
            throw new PersistenciaException("No se pudieron listar las estaciones.", ex);
        } finally {
            em.close();
        }
    }

    public Optional<Estacion> buscarId(int id) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return Optional.ofNullable(em.find(Estacion.class, id));
        } catch (RuntimeException ex) {
            throw new PersistenciaException("No se pudo buscar la estación.", ex);
        } finally {
            em.close();
        }
    }

    public Optional<Estacion> buscarCod(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
               "SELECT e FROM Estacion e WHERE UPPER(e.codigo) = :codigo",
                 Estacion.class)
                 .setParameter("codigo", codigo.trim().toUpperCase())
                 .setMaxResults(1)
                 .getResultList()
                 .stream().findFirst();
        } catch (RuntimeException ex) {
            throw new PersistenciaException("No se pudo validar el código.", ex);
        } finally {
            em.close();
        }
    }

    public boolean existCodigo(String codigo) {
        return buscarCod(codigo).isPresent();
    }

    public Estacion agregar(Estacion estacion) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            estacion.setUbicacion(em.getReference(Ubicacion.class, estacion.getUbicacion().getId()));
            em.persist(estacion);
            tx.commit();
            return estacion;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw new PersistenciaException("No se pudo guardar la estación. El código debe ser único.", ex);
        } finally {
            em.close();
        }
    }

    public boolean actualizar(Estacion estacion) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Estacion actual = em.find(Estacion.class, estacion.getId());
            if (actual == null) {
                tx.rollback();
                return false;
            }
            estacion.setUbicacion(em.getReference(Ubicacion.class, estacion.getUbicacion().getId()));
            em.merge(estacion);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw new PersistenciaException("No se pudo actualizar la estación. El código debe ser único.", ex);
        } finally {
            em.close();
        }
    }

    public boolean eliminar(int id) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Estacion estacion = em.find(Estacion.class, id);
            if (estacion == null) {
                tx.rollback();
                return false;
            }
            estacion.setEstado("INACTIVA");
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw new PersistenciaException("No se pudo dar de baja la estación.", ex);
        } finally {
            em.close();
        }
    }
}
