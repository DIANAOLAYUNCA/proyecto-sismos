package pe.edu.cssp.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cssp.entity.ReporteDano;

public interface ReporteDanoRepository extends JpaRepository<ReporteDano, Integer> {

    @Query("""
           select r from ReporteDano r
           join fetch r.evento
           join fetch r.tipoDano
           join fetch r.ubicacion
           left join fetch r.afectacionNivel
           order by r.id desc
           """)
    List<ReporteDano> listar();

    @Query("""
           select r from ReporteDano r
           join fetch r.evento
           join fetch r.tipoDano
           join fetch r.ubicacion
           left join fetch r.afectacionNivel
           where r.evento.id = :eventoId
           order by r.id desc
           """)
    List<ReporteDano> listarPorEvento(@Param("eventoId") int eventoId);

    /** eventoId = 0 significa todos los sismos. Los alias (e, t) permiten ordenar por columnas de las tablas unidas. */
    @Query(value = """
           select r from ReporteDano r
           join fetch r.evento e
           join fetch r.tipoDano t
           join fetch r.ubicacion
           left join fetch r.afectacionNivel
           where (:eventoId = 0 or e.id = :eventoId)
           """,
           countQuery = "select count(r) from ReporteDano r where (:eventoId = 0 or r.evento.id = :eventoId)")
    Page<ReporteDano> buscar(@Param("eventoId") int eventoId, Pageable pageable);

    @Query("select coalesce(sum(r.cantidadAfectada), 0) from ReporteDano r "
            + "where r.estado <> 'DESCARTADO' and (:eventoId = 0 or r.evento.id = :eventoId)")
    long sumaAfectados(@Param("eventoId") int eventoId);

    long countByAfectacionNivel_Id(int afectacionNivelId);
}
