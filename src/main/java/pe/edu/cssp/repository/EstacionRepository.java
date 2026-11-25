package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
           select e from Estacion e
           join fetch e.ubicacion u
           left join fetch e.distrito di
           where (:texto = '' or upper(e.codigo) like upper(concat('%', :texto, '%'))
                  or lower(e.nombre) like lower(concat('%', :texto, '%')))
             and (:estado = '' or e.estado = :estado)
             and (:ubicacionId = 0 or u.id = :ubicacionId)
           """,
           countQuery = """
           select count(e) from Estacion e
           where (:texto = '' or upper(e.codigo) like upper(concat('%', :texto, '%'))
                  or lower(e.nombre) like lower(concat('%', :texto, '%')))
             and (:estado = '' or e.estado = :estado)
             and (:ubicacionId = 0 or e.ubicacion.id = :ubicacionId)
           """)
    Page<Estacion> buscar(@Param("texto") String texto, @Param("estado") String estado,
                          @Param("ubicacionId") int ubicacionId, Pageable pageable);

    @Query("select count(e) from Estacion e where e.estado = 'ACTIVA' and (:ubicacionId = 0 or e.ubicacion.id = :ubicacionId)")
    long contarActivas(@Param("ubicacionId") int ubicacionId);
}
