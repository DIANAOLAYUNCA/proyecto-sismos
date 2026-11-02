package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.TipoDano;

public interface TipoDanoRepository extends JpaRepository<TipoDano, Integer> {
    List<TipoDano> findAllByOrderByCodigoAsc();
    List<TipoDano> findByEstadoOrderByNombreAsc(String estado);
    Optional<TipoDano> findByCodigoIgnoreCase(String codigo);
}
