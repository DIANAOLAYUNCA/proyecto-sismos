package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    List<Ubicacion> findAllByOrderByCodigoAsc();
    List<Ubicacion> findByEstadoOrderByCodigoAsc(String estado);
    List<Ubicacion> findByEstadoOrderByNombreAsc(String estado);
    Optional<Ubicacion> findByCodigoIgnoreCase(String codigo);

    /** Búsqueda paginada: cadena vacía significa "sin filtrar". */
    @Query("select x from Ubicacion x where (:texto = '' or upper(x.codigo) like upper(concat('%', :texto, '%')) "
            + "or lower(x.nombre) like lower(concat('%', :texto, '%'))) and (:estado = '' or x.estado = :estado) "
            + "and (:tipo = '' or x.tipo = :tipo)")
    Page<Ubicacion> buscar(@Param("texto") String texto, @Param("estado") String estado, @Param("tipo") String tipo, Pageable pageable);
}
