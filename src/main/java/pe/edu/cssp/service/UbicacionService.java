package pe.edu.cssp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.UbicacionRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.UbicacionForm;

@Service
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Ubicacion> listar() {
        return ubicacionRepository.findAllByOrderByCodigoAsc();
    }

    @Transactional(readOnly = true)
    public Ubicacion buscarPorId(int id) {
        return ubicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la ubicación solicitada."));
    }

    @Transactional
    public Ubicacion registrar(UbicacionForm form) {
        String codigo = form.getCodigo().trim();
        if (ubicacionRepository.findByCodigoIgnoreCase(codigo).isPresent()) {
            throw new ReglaNegocioException("El código de la ubicación ya está registrado.");
        }
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setCreacionFecha(fecha());
        ubicacion.setCreacionUsuario("sistema");
        completar(ubicacion, form, codigo);
        return ubicacionRepository.save(ubicacion);
    }

    @Transactional
    public Ubicacion actualizar(int id, UbicacionForm form) {
        Ubicacion ubicacion = buscarPorId(id);
        String codigo = form.getCodigo().trim();
        ubicacionRepository.findByCodigoIgnoreCase(codigo).ifPresent(otra -> {
            if (otra.getId() != id) {
                throw new ReglaNegocioException("El código ya pertenece a otra ubicación.");
            }
        });
        ubicacion.setActualizacionFecha(fecha());
        ubicacion.setActualizacionUsuario("sistema");
        completar(ubicacion, form, codigo);
        return ubicacionRepository.save(ubicacion);
    }

    @Transactional
    public void eliminar(int id) {
        Ubicacion ubicacion = buscarPorId(id);
        if ("INACTIVO".equals(ubicacion.getEstado())) {
            throw new ReglaNegocioException("La ubicación ya fue eliminada.");
        }
        ubicacion.setEstado("INACTIVO");
        ubicacion.setActualizacionFecha(fecha());
        ubicacion.setActualizacionUsuario("sistema");
        ubicacionRepository.save(ubicacion);
    }

    private void completar(Ubicacion ubicacion, UbicacionForm form, String codigo) {
        ubicacion.setCodigo(codigo);
        ubicacion.setNombre(form.getNombre().trim());
        ubicacion.setTipo(form.getTipo());
        ubicacion.setEstado(form.getEstado());
    }

    public static String fecha() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
