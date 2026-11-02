package pe.edu.cssp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.AfectacionNivel;

public interface AfectacionNivelRepository extends JpaRepository<AfectacionNivel, Integer> {

    List<AfectacionNivel> findAllByOrderByIdAsc();

    Optional<AfectacionNivel> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);
}
