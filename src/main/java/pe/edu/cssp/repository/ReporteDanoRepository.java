package pe.edu.cssp.repository;

import java.util.List;
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

    long countByAfectacionNivel_Id(int afectacionNivelId);
}
