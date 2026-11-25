package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.cssp.entity.Evento;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    @Query("""
           select e from Evento e
           join fetch e.estacion
           join fetch e.ubicacion
           join fetch e.tipoAlerta
           left join fetch e.afectacionNivel
           order by e.id
           """)
    List<Evento> listar();

    Optional<Evento> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    List<Evento> findByEstacion_Id(int estacionId);

    List<Evento> findByUbicacion_Distrito_Id(int distritoId);

    long countByAfectacionNivel_Id(int afectacionNivelId);

    /**
     * Búsqueda paginada de sismos hecha por la base de datos (no en memoria).
     * Cadena vacía / 0 significan "sin filtrar"; el rango [desde, hasta) siempre llega con valor.
     * Los alias (u, t, es, di) permiten ordenar por columnas de las tablas unidas.
     */
    @Query(value = """
           select e from Evento e
           join fetch e.estacion es
           join fetch e.ubicacion u
           join fetch e.tipoAlerta t
           left join fetch e.afectacionNivel
           left join fetch e.distrito di
           where (:codigo = '' or upper(e.codigo) like upper(concat('%', :codigo, '%')))
             and e.fechaHora >= :desde and e.fechaHora < :hasta
             and e.magnitud >= :magnitudMinima
             and (:estado = '' or e.estado = :estado)
             and (:ubicacionId = 0 or u.id = :ubicacionId)
           """,
           countQuery = """
           select count(e) from Evento e
           where (:codigo = '' or upper(e.codigo) like upper(concat('%', :codigo, '%')))
             and e.fechaHora >= :desde and e.fechaHora < :hasta
             and e.magnitud >= :magnitudMinima
             and (:estado = '' or e.estado = :estado)
             and (:ubicacionId = 0 or e.ubicacion.id = :ubicacionId)
           """)
    Page<Evento> buscar(@Param("codigo") String codigo, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta,
                        @Param("magnitudMinima") double magnitudMinima, @Param("estado") String estado,
                        @Param("ubicacionId") int ubicacionId, Pageable pageable);

    /** Sismos vigentes (no dados de baja) de un periodo y, opcionalmente, de una ubicación: base de los KPI del dashboard. */
    @Query("""
           select e from Evento e
           join fetch e.ubicacion u
           join fetch e.tipoAlerta
           left join fetch e.distrito
           where e.estado <> 'INACTIVO'
             and e.fechaHora >= :desde and e.fechaHora < :hasta
             and (:ubicacionId = 0 or u.id = :ubicacionId)
           """)
    List<Evento> paraDashboard(@Param("ubicacionId") int ubicacionId, @Param("desde") LocalDateTime desde,
                               @Param("hasta") LocalDateTime hasta);
}
