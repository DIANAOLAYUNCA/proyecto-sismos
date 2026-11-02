package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.Ubicacion;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    List<Ubicacion> findAllByOrderByCodigoAsc();
    List<Ubicacion> findByEstadoOrderByCodigoAsc(String estado);
    List<Ubicacion> findByEstadoOrderByNombreAsc(String estado);
    Optional<Ubicacion> findByCodigoIgnoreCase(String codigo);
}
