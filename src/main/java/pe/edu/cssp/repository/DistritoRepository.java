package pe.edu.cssp.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.Distrito;
import java.util.List;
public interface DistritoRepository extends JpaRepository<Distrito, Integer> {
    List<Distrito> findAllByOrderByNombreAsc();
}
