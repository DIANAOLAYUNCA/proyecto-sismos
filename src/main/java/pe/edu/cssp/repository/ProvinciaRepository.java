package pe.edu.cssp.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.cssp.entity.Provincia;
import java.util.List;
public interface ProvinciaRepository extends JpaRepository<Provincia, Integer> {
    List<Provincia> findAllByOrderByNombreAsc();

    @Query("select p from Provincia p join fetch p.departamento d order by d.nombre, p.nombre")
    List<Provincia> listarConDepartamento();
}
