package pe.edu.cssp.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.cssp.entity.Usuario;
import pe.edu.cssp.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuarioAndEstado(username, "ACTIVO")
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos."));

        return User.withUsername(usuario.getUsuario())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRol().getCodigo())
                .build();
    }
}
