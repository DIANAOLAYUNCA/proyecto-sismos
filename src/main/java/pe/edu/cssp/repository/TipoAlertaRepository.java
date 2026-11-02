package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.TipoAlerta;

public interface TipoAlertaRepository extends JpaRepository<TipoAlerta, Integer> {
    List<TipoAlerta> findAllByOrderByPrioridadAsc();
    List<TipoAlerta> findByEstadoOrderByPrioridadAsc(String estado);
    Optional<TipoAlerta> findByCodigoIgnoreCase(String codigo);
}
