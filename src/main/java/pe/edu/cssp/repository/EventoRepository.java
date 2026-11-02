package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
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
}
