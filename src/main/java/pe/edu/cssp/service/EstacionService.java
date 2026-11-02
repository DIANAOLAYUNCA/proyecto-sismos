package pe.edu.cssp.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.Estacion;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.UbicacionRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.EstacionForm;

@Service
public class EstacionService {
    private final EstacionRepository estacionRepository;
    private final UbicacionRepository ubicacionRepository;

    public EstacionService(EstacionRepository estacionRepository, UbicacionRepository ubicacionRepository) {
        this.estacionRepository = estacionRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Estacion> listar() { return estacionRepository.listar(); }

    @Transactional(readOnly = true)
    public List<Ubicacion> ubicacionesActivas() { return ubicacionRepository.findByEstadoOrderByCodigoAsc("ACTIVO"); }

    @Transactional(readOnly = true)
    public Estacion buscarPorId(int id) {
        return estacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la estación solicitada."));
    }

    @Transactional
    public Estacion registrar(EstacionForm form) {
        String codigo = form.getCodigo().trim().toUpperCase(Locale.ROOT);
        if (estacionRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ReglaNegocioException("El código de la estación ya está registrado.");
        }
        Estacion estacion = new Estacion();
        estacion.setCreacionFecha(UbicacionService.fecha());
        estacion.setCreacionUsuario("sistema");
        completar(estacion, form, codigo);
        return estacionRepository.save(estacion);
    }

    @Transactional
    public Estacion actualizar(int id, EstacionForm form) {
        Estacion estacion = buscarPorId(id);
        String codigo = form.getCodigo().trim().toUpperCase(Locale.ROOT);
        estacionRepository.findByCodigoIgnoreCase(codigo).ifPresent(otra -> {
            if (otra.getId() != id) throw new ReglaNegocioException("El código ya pertenece a otra estación.");
        });
        estacion.setActualizacionFecha(UbicacionService.fecha());
        estacion.setActualizacionUsuario("sistema");
        completar(estacion, form, codigo);
        return estacionRepository.save(estacion);
    }

    @Transactional
    public void eliminar(int id) {
        Estacion estacion = buscarPorId(id);
        if ("INACTIVA".equals(estacion.getEstado())) {
            throw new ReglaNegocioException("La estación ya fue dada de baja.");
        }
        estacion.setEstado("INACTIVA");
        estacion.setActualizacionFecha(UbicacionService.fecha());
        estacion.setActualizacionUsuario("sistema");
        estacionRepository.save(estacion);
    }

    private void completar(Estacion estacion, EstacionForm form, String codigo) {
        LocalDate instalacion;
        try {
            instalacion = LocalDate.parse(form.getInstalacionFecha());
        } catch (DateTimeParseException ex) {
            throw new ReglaNegocioException("La fecha de instalación no es válida.");
        }
        if (instalacion.isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La instalación no puede ser una fecha futura.");
        }
        Ubicacion ubicacion = ubicacionRepository.findById(form.getUbicacionId())
                .filter(u -> "ACTIVO".equals(u.getEstado()))
                .orElseThrow(() -> new ReglaNegocioException("Seleccione una ubicación activa."));
        estacion.setCodigo(codigo);
        estacion.setNombre(form.getNombre().trim());
        estacion.setUbicacion(ubicacion);
        estacion.setLatitud(form.getLatitud());
        estacion.setLongitud(form.getLongitud());
        estacion.setInstalacionFecha(form.getInstalacionFecha());
        estacion.setEstado(form.getEstado());
    }
}