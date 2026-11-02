package pe.edu.cssp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.entity.Estacion;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.AfectacionNivelRepository;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.TipoAlertaRepository;
import pe.edu.cssp.repository.UbicacionRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.EventoForm;

@Service
public class EventoService {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventoRepository eventoRepository;
    private final EstacionRepository estacionRepository;
    private final UbicacionRepository ubicacionRepository;
    private final AfectacionNivelRepository afectacionNivelRepository;
    private final TipoAlertaRepository tipoAlertaRepository;

    public EventoService(EventoRepository eventoRepository, EstacionRepository estacionRepository,
                         UbicacionRepository ubicacionRepository, AfectacionNivelRepository afectacionNivelRepository,
                         TipoAlertaRepository tipoAlertaRepository) {
        this.eventoRepository = eventoRepository;
        this.estacionRepository = estacionRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.afectacionNivelRepository = afectacionNivelRepository;
        this.tipoAlertaRepository = tipoAlertaRepository;
    }

    @Transactional(readOnly = true)
    public List<Evento> listar(String codigo, String fecha, Double magnitudMinima, String estado, Integer ubicacionId) {
        return eventoRepository.listar().stream()
                .filter(e -> codigo == null || codigo.isBlank() || e.getCodigo().toUpperCase().contains(codigo.trim().toUpperCase()))
                .filter(e -> fecha == null || fecha.isBlank() || e.getFechaHora().startsWith(fecha.trim()))
                .filter(e -> magnitudMinima == null || e.getMagnitud() >= magnitudMinima)
                .filter(e -> estado == null || estado.isBlank() || estado.equalsIgnoreCase(e.getEstado()))
                .filter(e -> ubicacionId == null || e.getUbicacion().getId() == ubicacionId)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Estacion> estaciones() { return estacionRepository.listarActivas(); }

    @Transactional(readOnly = true)
    public List<Ubicacion> ubicacionesActivas() { return ubicacionRepository.findByEstadoOrderByCodigoAsc("ACTIVO"); }

    @Transactional(readOnly = true)
    public List<TipoAlerta> alertasActivas() { return tipoAlertaRepository.findByEstadoOrderByPrioridadAsc("ACTIVO"); }

    @Transactional(readOnly = true)
    public List<AfectacionNivel> niveles() { return afectacionNivelRepository.findAllByOrderByIdAsc(); }

    @Transactional(readOnly = true)
    public Evento buscarPorId(int id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el sismo solicitado."));
    }

    @Transactional
    public Evento registrar(EventoForm form) {
        String codigo = form.getCodigo().trim();
        if (eventoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ReglaNegocioException("El código del sismo ya está registrado.");
        }
        Evento evento = new Evento();
        evento.setCreacionFecha(UbicacionService.fecha());
        evento.setCreacionUsuario("sistema");
        completar(evento, form, codigo);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento actualizar(int id, EventoForm form) {
        Evento evento = buscarPorId(id);
        String codigo = form.getCodigo().trim();
        eventoRepository.findByCodigoIgnoreCase(codigo).ifPresent(otro -> {
            if (otro.getId() != id) throw new ReglaNegocioException("El código ya pertenece a otro sismo.");
        });
        evento.setActualizacionFecha(UbicacionService.fecha());
        evento.setActualizacionUsuario("sistema");
        completar(evento, form, codigo);
        return eventoRepository.save(evento);
    }

    @Transactional
    public void eliminar(int id) {
        Evento evento = buscarPorId(id);
        if ("INACTIVO".equals(evento.getEstado())) {
            throw new ReglaNegocioException("El sismo ya fue eliminado.");
        }
        evento.setEstado("INACTIVO");
        evento.setActualizacionFecha(UbicacionService.fecha());
        evento.setActualizacionUsuario("sistema");
        eventoRepository.save(evento);
    }

    private void completar(Evento evento, EventoForm form, String codigo) {
        Estacion estacion = estacionRepository.findById(form.getEstacionId())
                .filter(e -> "ACTIVA".equals(e.getEstado()))
                .orElseThrow(() -> new ReglaNegocioException("Seleccione una estación activa."));
        Ubicacion ubicacion = ubicacionRepository.findById(form.getUbicacionId())
                .filter(u -> "ACTIVO".equals(u.getEstado()))
                .orElseThrow(() -> new ReglaNegocioException("Seleccione una ubicación activa."));
        TipoAlerta alerta = tipoAlertaRepository.findById(form.getTipoAlertaId())
                .filter(a -> "ACTIVO".equals(a.getEstado()))
                .orElseThrow(() -> new ReglaNegocioException("Seleccione un tipo de alerta activo."));
        AfectacionNivel nivel = afectacionNivelRepository.findById(form.getAfectacionNivelId())
                .orElseThrow(() -> new ReglaNegocioException("Seleccione un nivel de afectación válido."));
        evento.setEstacion(estacion);
        evento.setCodigo(codigo);
        evento.setFechaHora(normalizarFechaHora(form.getFechaHora()));
        evento.setMagnitud(form.getMagnitud().doubleValue());
        evento.setProfundidad(form.getProfundidad().doubleValue());
        evento.setLatitud(form.getLatitud().doubleValue());
        evento.setLongitud(form.getLongitud().doubleValue());
        evento.setReferencia(form.getReferencia().trim());
        evento.setUbicacion(ubicacion);
        evento.setTipoAlerta(alerta);
        evento.setAfectacionNivel(nivel);
        evento.setEstado(form.getEstado());
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
}