package pe.edu.cssp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import pe.edu.cssp.config.JpaUtil;
import pe.edu.cssp.model.AfectacionNivel;
import pe.edu.cssp.model.Estacion;
import pe.edu.cssp.model.Evento;
import pe.edu.cssp.model.Ubicacion;

import java.util.List;
import java.util.Optional;

public class EventoRepository {

    public List<Evento> listar() {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                "SELECT e FROM Evento e "
                + "JOIN FETCH e.estacion "
                + "JOIN FETCH e.ubicacion "
                + "LEFT JOIN FETCH e.afectacionNivel "
                + "ORDER BY e.id",
                    Evento.class).getResultList();
        } catch (RuntimeException ex) {
            throw new PersistenciaException("No se pudieron listar los sismos.", ex);
        } finally {
            em.close();
        }
    }

    public Optional<Evento> buscarId(int id) {
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return Optional.ofNullable(em.find(Evento.class, id));
        } catch (RuntimeException ex) {
            throw new PersistenciaException("No se pudo buscar el sismo.", ex);
        } finally {
            em.close();
        }
    }

    public Optional<Evento> buscarCod(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        EntityManager em = JpaUtil.crearEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Evento e WHERE UPPER(e.codigo) = :codigo",
                    Evento.class)
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

    public Evento agregar(Evento evento) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            asignarReferencias(em, evento);
            em.persist(evento);
            tx.commit();
            return evento;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw new PersistenciaException("No se pudo guardar el sismo. El código debe ser único.", ex);
        } finally {
            em.close();
        }
    }

    public boolean actualizar(Evento evento) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Evento actual = em.find(Evento.class, evento.getId());
            if (actual == null) {
                tx.rollback();
                return false;
            }
            asignarReferencias(em, evento);
            em.merge(evento);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw new PersistenciaException("No se pudo actualizar el sismo. El código debe ser único.", ex);
        } finally {
            em.close();
        }
    }

    public boolean eliminar(int id) {
        EntityManager em = JpaUtil.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Evento evento = em.find(Evento.class, id);
            if (evento == null) {
                tx.rollback();
                return false;
            }
            em.remove(evento);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw new PersistenciaException("No se pudo eliminar el sismo.", ex);
        } finally {
            em.close();
        }
    }

    private void asignarReferencias(EntityManager em, Evento evento) {
        evento.setEstacion(em.getReference(Estacion.class, evento.getEstacion().getId()));
        evento.setUbicacion(em.getReference(Ubicacion.class, evento.getUbicacion().getId()));
        if (evento.getAfectacionNivel() != null) {
            evento.setAfectacionNivel(
                    em.getReference(AfectacionNivel.class, evento.getAfectacionNivel().getId()));
        }
    }
}