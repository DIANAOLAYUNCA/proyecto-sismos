package pe.edu.cssp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cssp.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsuarioAndEstado(String usuario, String estado);
}