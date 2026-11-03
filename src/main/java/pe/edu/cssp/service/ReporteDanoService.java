package pe.edu.cssp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.ReporteDano;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.AfectacionNivelRepository;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.ReporteDanoRepository;
import pe.edu.cssp.repository.TipoDanoRepository;
import pe.edu.cssp.repository.UbicacionRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.ReporteDanoForm;

@Service
public class ReporteDanoService {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ReporteDanoRepository reporteDanoRepository;
    private final EventoRepository eventoRepository;
    private final TipoDanoRepository tipoDanoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final AfectacionNivelRepository afectacionNivelRepository;

    public ReporteDanoService(ReporteDanoRepository reporteDanoRepository, EventoRepository eventoRepository,
                              TipoDanoRepository tipoDanoRepository, UbicacionRepository ubicacionRepository,
                              AfectacionNivelRepository afectacionNivelRepository) {
        this.reporteDanoRepository = reporteDanoRepository;
        this.eventoRepository = eventoRepository;
        this.tipoDanoRepository = tipoDanoRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.afectacionNivelRepository = afectacionNivelRepository;
    }

    @Transactional(readOnly = true)
    public List<ReporteDano> listar(Integer eventoId) {
        return eventoId == null ? reporteDanoRepository.listar() : reporteDanoRepository.listarPorEvento(eventoId);
    }

    public int totalAfectados(List<ReporteDano> reportes) {
        return reportes.stream()
                .filter(r -> !"DESCARTADO".equals(r.getEstado()))
                .mapToInt(ReporteDano::getCantidadAfectada)
                .sum();
    }

    @Transactional(readOnly = true)
    public ReporteDano buscarPorId(int id) {
        return reporteDanoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el reporte de daño solicitado."));
    }

    @Transactional(readOnly = true)
    public Evento buscarEvento(int id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el sismo solicitado."));
    }

    @Transactional(readOnly = true)
    public List<Evento> todosLosEventos() { return eventoRepository.listar(); }

    @Transactional(readOnly = true)
    public List<Evento> eventosVigentes() {
        return eventoRepository.listar().stream().filter(e -> !"INACTIVO".equals(e.getEstado())).toList();
    }

    @Transactional(readOnly = true)
    public List<TipoDano> tiposDanoActivos() { return tipoDanoRepository.findByEstadoOrderByNombreAsc("ACTIVO"); }

    @Transactional(readOnly = true)
    public List<Ubicacion> ubicacionesActivas() { return ubicacionRepository.findByEstadoOrderByCodigoAsc("ACTIVO"); }

    @Transactional(readOnly = true)
    public List<AfectacionNivel> niveles() { return afectacionNivelRepository.findAllByOrderByIdAsc(); }

    @Transactional
    public ReporteDano registrar(ReporteDanoForm form) {
        Evento evento = eventoRepository.findById(form.getEventoId())
                .filter(e -> !"INACTIVO".equals(e.getEstado()))
                .orElseThrow(() -> new ReglaNegocioException("El reporte solo puede crearse para un sismo existente y vigente."));
        ReporteDano reporte = new ReporteDano();
        reporte.setCreacionFecha(UbicacionService.fecha());
        reporte.setCreacionUsuario("sistema");
        reporte.setEvento(evento);
        reporte.setEstado("REGISTRADO");
        completar(reporte, form);
        return reporteDanoRepository.save(reporte);
    }

    @Transactional
    public ReporteDano actualizar(int id, ReporteDanoForm form) {
        ReporteDano reporte = buscarPorId(id);
        if ("DESCARTADO".equals(reporte.getEstado())) {
            throw new ReglaNegocioException("Un reporte descartado no puede modificarse.");
        }
        reporte.setActualizacionFecha(UbicacionService.fecha());
        reporte.setActualizacionUsuario("sistema");
        reporte.setEstado(form.getEstado());
        completar(reporte, form);
        return reporteDanoRepository.save(reporte);
    }

    @Transactional
    public void eliminar(int id) {
        ReporteDano reporte = buscarPorId(id);
        if ("DESCARTADO".equals(reporte.getEstado())) {
            throw new ReglaNegocioException("El reporte de daño ya fue descartado.");
        }
        reporte.setEstado("DESCARTADO");
        reporte.setActualizacionFecha(UbicacionService.fecha());
        reporte.setActualizacionUsuario("sistema");
        reporteDanoRepository.save(reporte);
    }

    @Transactional
    public ReporteDano reactivar(int id) {
        ReporteDano reporte = buscarPorId(id);
        if (!"DESCARTADO".equals(reporte.getEstado())) {
            throw new ReglaNegocioException("Solo se puede reactivar un reporte descartado.");
        }
        if ("INACTIVO".equals(reporte.getEvento().getEstado())) {
            throw new ReglaNegocioException("No se puede reactivar el reporte porque su sismo está inactivo.");
        }
        if (!"ACTIVO".equals(reporte.getTipoDano().getEstado())) {
            throw new ReglaNegocioException("No se puede reactivar el reporte porque su tipo de daño está inactivo.");
        }
        if (!"ACTIVO".equals(reporte.getUbicacion().getEstado())) {
            throw new ReglaNegocioException("No se puede reactivar el reporte porque su ubicación está inactiva.");
        }
        reporte.setEstado("REGISTRADO");
        reporte.setActualizacionFecha(UbicacionService.fecha());
        reporte.setActualizacionUsuario("sistema");
        return reporteDanoRepository.save(reporte);
    }

    private void completar(ReporteDano reporte, ReporteDanoForm form) {
        TipoDano tipoDano = tipoDanoRepository.findById(form.getTipoDanoId())
                .filter(t -> "ACTIVO".equals(t.getEstado()))
                .orElseThrow(() -> new ReglaNegocioException("Seleccione un tipo de daño activo."));
        Ubicacion ubicacion = ubicacionRepository.findById(form.getUbicacionId())
                .filter(u -> "ACTIVO".equals(u.getEstado()))
                .orElseThrow(() -> new ReglaNegocioException("Seleccione una ubicación activa."));
        AfectacionNivel nivel = null;
        if (form.getAfectacionNivelId() != null) {
            nivel = afectacionNivelRepository.findById(form.getAfectacionNivelId())
                    .orElseThrow(() -> new ReglaNegocioException("Seleccione un nivel de afectación válido."));
        }
        String fechaHora = normalizarFechaHora(form.getFechaHora());
        validarFecha(fechaHora, reporte.getEvento());
        reporte.setTipoDano(tipoDano);
        reporte.setUbicacion(ubicacion);
        reporte.setAfectacionNivel(nivel);
        reporte.setFechaHora(fechaHora);
        reporte.setCantidadAfectada(form.getCantidadAfectada());
        reporte.setDescripcion(form.getDescripcion().trim());
    }

    private String normalizarFechaHora(String valor) {
        String texto = valor.trim().replace('T', ' ');
        if (texto.length() == 16) texto += ":00";
        try {
            LocalDateTime.parse(texto, FORMATO);
        } catch (DateTimeParseException ex) {
            throw new ReglaNegocioException("La fecha y hora no son válidas.");
        }
        return texto;
    }

    private void validarFecha(String fechaHora, Evento evento) {
        LocalDateTime fecha = LocalDateTime.parse(fechaHora, FORMATO);
        if (fecha.isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha y hora del reporte no pueden ser futuras.");
        }
        try {
            if (fecha.isBefore(LocalDateTime.parse(evento.getFechaHora(), FORMATO))) {
                throw new ReglaNegocioException("La fecha y hora del reporte no pueden ser anteriores a las del sismo.");
            }
        } catch (DateTimeParseException ex) {
            // La fecha del sismo no tiene el formato esperado: no se puede comparar.
        }
    }
}
