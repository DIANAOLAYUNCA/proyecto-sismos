package pe.edu.cssp.service;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.repository.AfectacionNivelRepository;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.ReporteDanoRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.AfectacionNivelForm;

@Service
public class AfectacionNivelService {
    private final AfectacionNivelRepository afectacionNivelRepository;
    private final EventoRepository eventoRepository;
    private final ReporteDanoRepository reporteDanoRepository;

    public AfectacionNivelService(AfectacionNivelRepository afectacionNivelRepository,
                                  EventoRepository eventoRepository,
                                  ReporteDanoRepository reporteDanoRepository) {
        this.afectacionNivelRepository = afectacionNivelRepository;
        this.eventoRepository = eventoRepository;
        this.reporteDanoRepository = reporteDanoRepository;
    }

    @Transactional(readOnly = true)
    public List<AfectacionNivel> listar() { return afectacionNivelRepository.findAllByOrderByIdAsc(); }

    @Transactional(readOnly = true)
    public AfectacionNivel buscarPorId(int id) {
        return afectacionNivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el nivel de afectación solicitado."));
    }

    @Transactional
    public AfectacionNivel registrar(AfectacionNivelForm form) {
        String codigo = normalizarCodigo(form.getCodigo());
        if (afectacionNivelRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ReglaNegocioException("El código del nivel de afectación ya está registrado.");
        }
        AfectacionNivel nivel = new AfectacionNivel();
        nivel.setCreacionFecha(UbicacionService.fecha());
        nivel.setCreacionUsuario("sistema");
        completar(nivel, form, codigo);
        return afectacionNivelRepository.save(nivel);
    }

    @Transactional
    public AfectacionNivel actualizar(int id, AfectacionNivelForm form) {
        AfectacionNivel nivel = buscarPorId(id);
        String codigo = normalizarCodigo(form.getCodigo());
        afectacionNivelRepository.findByCodigoIgnoreCase(codigo).ifPresent(otro -> {
            if (otro.getId() != id) throw new ReglaNegocioException("El código ya pertenece a otro nivel de afectación.");
        });
        nivel.setActualizacionFecha(UbicacionService.fecha());
        nivel.setActualizacionUsuario("sistema");
        completar(nivel, form, codigo);
        return afectacionNivelRepository.save(nivel);
    }

    @Transactional
    public void eliminar(int id) {
        AfectacionNivel nivel = buscarPorId(id);
        long sismos = eventoRepository.countByAfectacionNivel_Id(id);
        long reportes = reporteDanoRepository.countByAfectacionNivel_Id(id);
        if (sismos > 0 || reportes > 0) {
            throw new ReglaNegocioException("No se puede eliminar el nivel de afectación: lo usan " + sismos
                    + " sismo(s) y " + reportes + " reporte(s) de daño.");
        }
        afectacionNivelRepository.delete(nivel);
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private void completar(AfectacionNivel nivel, AfectacionNivelForm form, String codigo) {
        nivel.setCodigo(codigo);
        nivel.setNombre(form.getNombre().trim());
    }
}
