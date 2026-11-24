package pe.edu.cssp.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.Departamento;
import java.util.List;
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
    List<Departamento> findAllByOrderByNombreAsc();
}
