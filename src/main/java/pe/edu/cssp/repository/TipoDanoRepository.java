package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.TipoDano;

public interface TipoDanoRepository extends JpaRepository<TipoDano, Integer> {
    List<TipoDano> findAllByOrderByCodigoAsc();
    List<TipoDano> findByEstadoOrderByNombreAsc(String estado);
    Optional<TipoDano> findByCodigoIgnoreCase(String codigo);

    /** Búsqueda paginada: cadena vacía significa "sin filtrar". */
    @Query("select x from TipoDano x where (:texto = '' or upper(x.codigo) like upper(concat('%', :texto, '%')) "
            + "or lower(x.nombre) like lower(concat('%', :texto, '%'))) and (:estado = '' or x.estado = :estado) "
            + "and (:sector = '' or x.sector = :sector)")
    Page<TipoDano> buscar(@Param("texto") String texto, @Param("estado") String estado, @Param("sector") String sector, Pageable pageable);
}
