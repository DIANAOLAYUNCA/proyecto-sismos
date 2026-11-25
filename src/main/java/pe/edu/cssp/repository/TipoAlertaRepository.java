package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.TipoAlerta;

public interface TipoAlertaRepository extends JpaRepository<TipoAlerta, Integer> {
    List<TipoAlerta> findAllByOrderByPrioridadAsc();
    List<TipoAlerta> findByEstadoOrderByPrioridadAsc(String estado);
    Optional<TipoAlerta> findByCodigoIgnoreCase(String codigo);

    /** Búsqueda paginada: cadena vacía significa "sin filtrar". */
    @Query("select x from TipoAlerta x where (:texto = '' or upper(x.codigo) like upper(concat('%', :texto, '%')) "
            + "or lower(x.nombre) like lower(concat('%', :texto, '%'))) and (:estado = '' or x.estado = :estado)")
    Page<TipoAlerta> buscar(@Param("texto") String texto, @Param("estado") String estado, Pageable pageable);
}
