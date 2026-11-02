package pe.edu.cssp.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.Provincia;
import java.util.List;
public interface ProvinciaRepository extends JpaRepository<Provincia, Integer> {
    List<Provincia> findAllByOrderByNombreAsc();
}
