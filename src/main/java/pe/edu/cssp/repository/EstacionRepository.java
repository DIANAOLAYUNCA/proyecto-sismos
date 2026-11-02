package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.Estacion;

public interface EstacionRepository extends JpaRepository<Estacion, Integer> {
    @Query("select e from Estacion e join fetch e.ubicacion order by e.id")
    List<Estacion> listar();
    @Query("select e from Estacion e join fetch e.ubicacion where e.estado = 'ACTIVA' order by e.id")
    List<Estacion> listarActivas();
    Optional<Estacion> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCase(String codigo);
    List<Estacion> findByUbicacion_Distrito_Id(int distritoId);
    @Modifying
    @Transactional
    @Query("update Estacion e set e.estado = 'INACTIVA' where e.id = :id")
    int darDeBaja(@Param("id") int id);
}
