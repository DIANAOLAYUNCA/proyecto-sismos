package pe.edu.cssp.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.Departamento;
import pe.edu.cssp.entity.Distrito;
import pe.edu.cssp.entity.Provincia;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.DepartamentoRepository;
import pe.edu.cssp.repository.DistritoRepository;
import pe.edu.cssp.repository.ProvinciaRepository;
import pe.edu.cssp.repository.UbicacionRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.DistritoForm;

@Service
public class DistritoService {
    private final DistritoRepository distritoRepository;
    private final ProvinciaRepository provinciaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final UbicacionRepository ubicacionRepository;

    public DistritoService(DistritoRepository distritoRepository, ProvinciaRepository provinciaRepository,
                           DepartamentoRepository departamentoRepository, UbicacionRepository ubicacionRepository) {
        this.distritoRepository = distritoRepository;
        this.provinciaRepository = provinciaRepository;
        this.departamentoRepository = departamentoRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    @Transactional(readOnly = true)
    public Page<Distrito> buscar(Integer departamentoId, Integer provinciaId, String estado, String texto, Pageable pageable) {
        return distritoRepository.buscar(departamentoId == null ? 0 : departamentoId, provinciaId == null ? 0 : provinciaId,
                estado == null ? "" : estado.trim(), texto == null ? "" : texto.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Distrito buscarPorId(int id) {
        return distritoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el distrito solicitado."));
    }

    @Transactional(readOnly = true)
    public List<Departamento> departamentos() { return departamentoRepository.findAllByOrderByNombreAsc(); }

    @Transactional(readOnly = true)
    public List<Provincia> provincias() { return provinciaRepository.listarConDepartamento(); }

    @Transactional
    public Distrito registrar(DistritoForm form) {
        Distrito distrito = new Distrito();
        distrito.setCreacionFecha(LocalDateTime.now());
        distrito.setCreacionUsuario("sistema");
        completar(distrito, form);
        return distritoRepository.save(distrito);
    }

    @Transactional
    public Distrito actualizar(int id, DistritoForm form) {
        Distrito distrito = buscarPorId(id);
        distrito.setActualizacionFecha(LocalDateTime.now());
        distrito.setActualizacionUsuario("sistema");
        completar(distrito, form);
        return distritoRepository.save(distrito);
    }

    /** Baja lógica: el distrito deja de ofrecerse en sismos y estaciones, pero los registros que ya lo usan no se pierden. */
    @Transactional
    public void eliminar(int id) {
        Distrito distrito = buscarPorId(id);
        if ("INACTIVO".equals(distrito.getEstado())) {
            throw new ReglaNegocioException("El distrito ya fue eliminado.");
        }
        distrito.setEstado("INACTIVO");
        distrito.setActualizacionFecha(LocalDateTime.now());
        distrito.setActualizacionUsuario("sistema");
        distritoRepository.save(distrito);
    }

    /** Distritos activos del departamento al que corresponde la ubicación elegida; vacío si no hay ubicación. */
    @Transactional(readOnly = true)
    public List<Distrito> opcionesPorUbicacion(Integer ubicacionId) {
        if (ubicacionId == null) return List.of();
        return ubicacionRepository.findById(ubicacionId)
                .map(u -> distritoRepository.activosPorDepartamento(u.getCodigo()))
                .orElse(List.of());
    }

    /**
     * Regla de sismos y estaciones: el distrito es opcional, pero si se elige debe existir, estar activo
     * y pertenecer al departamento de la ubicación. Un registro puede conservar su distrito aunque
     * este se haya dado de baja después ({@code actual}).
     */
    @Transactional(readOnly = true)
    public Distrito elegir(Integer distritoId, Ubicacion ubicacion, Distrito actual) {
        if (distritoId == null) return null;
        Distrito distrito = distritoRepository.findById(distritoId)
                .orElseThrow(() -> new ReglaNegocioException("Seleccione un distrito válido."));
        boolean sinCambio = actual != null && actual.getId() == distrito.getId();
        if (!sinCambio && !"ACTIVO".equals(distrito.getEstado())) {
            throw new ReglaNegocioException("El distrito seleccionado está inactivo.");
        }
        if (!distrito.getProvincia().getDepartamento().getCodigo().equals(ubicacion.getCodigo())) {
            throw new ReglaNegocioException("El distrito no pertenece a la ubicación seleccionada.");
        }
        return distrito;
    }

    private void completar(Distrito distrito, DistritoForm form) {
        Provincia provincia = provinciaRepository.findById(form.getProvinciaId())
                .orElseThrow(() -> new ReglaNegocioException("Seleccione una provincia válida."));
        String codigo = form.getCodigo().trim();
        distritoRepository.findByProvincia_IdAndCodigo(provincia.getId(), codigo).ifPresent(otro -> {
            if (otro.getId() != distrito.getId()) {
                throw new ReglaNegocioException("Ya existe un distrito con ese código en la provincia seleccionada.");
            }
        });
        distrito.setProvincia(provincia);
        distrito.setCodigo(codigo);
        distrito.setNombre(form.getNombre().trim());
        distrito.setEstado(form.getEstado());
    }
}
