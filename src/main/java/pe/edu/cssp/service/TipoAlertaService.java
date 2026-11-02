package pe.edu.cssp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.repository.TipoAlertaRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.TipoAlertaForm;

@Service
public class TipoAlertaService {
    private static final List<String> COLORES = List.of("VERDE", "AMARILLO", "NARANJA", "ROJO");
    private final TipoAlertaRepository tipoAlertaRepository;
    public TipoAlertaService(TipoAlertaRepository tipoAlertaRepository) { this.tipoAlertaRepository = tipoAlertaRepository; }

    @Transactional(readOnly = true)
    public List<TipoAlerta> listar() { return tipoAlertaRepository.findAllByOrderByPrioridadAsc(); }

    @Transactional(readOnly = true)
    public TipoAlerta buscarPorId(int id) {
        return tipoAlertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de alerta solicitado."));
    }

    @Transactional
    public TipoAlerta registrar(TipoAlertaForm form) {
        String codigo = form.getCodigo().trim();
        if (tipoAlertaRepository.findByCodigoIgnoreCase(codigo).isPresent()) {
            throw new ReglaNegocioException("El código del tipo de alerta ya está registrado.");
        }
        TipoAlerta tipoAlerta = new TipoAlerta();
        tipoAlerta.setCreacionFecha(UbicacionService.fecha());
        tipoAlerta.setCreacionUsuario("sistema");
        completar(tipoAlerta, form, codigo);
        return tipoAlertaRepository.save(tipoAlerta);
    }

    @Transactional
    public TipoAlerta actualizar(int id, TipoAlertaForm form) {
        TipoAlerta tipoAlerta = buscarPorId(id);
        String codigo = form.getCodigo().trim();
        tipoAlertaRepository.findByCodigoIgnoreCase(codigo).ifPresent(otro -> {
            if (otro.getId() != id) throw new ReglaNegocioException("El código ya pertenece a otro tipo de alerta.");
        });
        tipoAlerta.setActualizacionFecha(UbicacionService.fecha());
        tipoAlerta.setActualizacionUsuario("sistema");
        completar(tipoAlerta, form, codigo);
        return tipoAlertaRepository.save(tipoAlerta);
    }

    @Transactional
    public void eliminar(int id) {
        TipoAlerta tipoAlerta = buscarPorId(id);
        if ("INACTIVO".equals(tipoAlerta.getEstado())) {
            throw new ReglaNegocioException("El tipo de alerta ya fue eliminado.");
        }
        tipoAlerta.setEstado("INACTIVO");
        tipoAlerta.setActualizacionFecha(UbicacionService.fecha());
        tipoAlerta.setActualizacionUsuario("sistema");
        tipoAlertaRepository.save(tipoAlerta);
    }

    private void completar(TipoAlerta tipoAlerta, TipoAlertaForm form, String codigo) {
        if (form.getPrioridad() == null || form.getPrioridad() <= 0) {
            throw new ReglaNegocioException("La prioridad debe ser un número positivo.");
        }
        if (!COLORES.contains(form.getColor())) {
            throw new ReglaNegocioException("El color no es válido.");
        }
        tipoAlerta.setCodigo(codigo);
        tipoAlerta.setNombre(form.getNombre().trim());
        tipoAlerta.setColor(form.getColor());
        tipoAlerta.setPrioridad(form.getPrioridad());
        tipoAlerta.setDescripcion(form.getDescripcion() == null || form.getDescripcion().isBlank() ? null : form.getDescripcion().trim());
        tipoAlerta.setEstado(form.getEstado());
    }
}
