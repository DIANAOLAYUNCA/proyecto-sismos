package pe.edu.cssp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.entity.Distrito;
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
    private final DistritoService distritoService;

    public EventoService(EventoRepository eventoRepository, EstacionRepository estacionRepository,
                         UbicacionRepository ubicacionRepository, AfectacionNivelRepository afectacionNivelRepository,
                         TipoAlertaRepository tipoAlertaRepository, DistritoService distritoService) {
        this.eventoRepository = eventoRepository;
        this.estacionRepository = estacionRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.afectacionNivelRepository = afectacionNivelRepository;
        this.tipoAlertaRepository = tipoAlertaRepository;
        this.distritoService = distritoService;
    }

    @Transactional(readOnly = true)
    public List<Distrito> distritosDisponibles(Integer ubicacionId) { return distritoService.opcionesPorUbicacion(ubicacionId); }

    /**
     * Búsqueda paginada de sismos resuelta por la base de datos (antes se traían todos y se filtraba en memoria).
     * La fecha (yyyy-MM-dd) se convierte en el rango [día 00:00, día siguiente 00:00).
     */
    @Transactional(readOnly = true)
    public Page<Evento> buscar(String codigo, String fecha, Double magnitudMinima, String estado, Integer ubicacionId, Pageable pageable) {
        LocalDateTime desde = LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(9999, 12, 31, 0, 0);
        if (fecha != null && !fecha.isBlank()) {
            try {
                LocalDate dia = LocalDate.parse(fecha.trim());
                desde = dia.atStartOfDay();
                hasta = dia.plusDays(1).atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new ReglaNegocioException("La fecha del filtro no es válida.");
            }
        }
        return eventoRepository.buscar(codigo == null ? "" : codigo.trim(), desde, hasta,
                magnitudMinima == null ? -1 : magnitudMinima, estado == null ? "" : estado.trim(),
                ubicacionId == null ? 0 : ubicacionId, pageable);
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
        evento.setCreacionFecha(LocalDateTime.now());
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
        evento.setActualizacionFecha(LocalDateTime.now());
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
        evento.setActualizacionFecha(LocalDateTime.now());
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
        evento.setActualizacionFecha(LocalDateTime.now());
        evento.setMagnitud(form.getMagnitud().doubleValue());
        evento.setProfundidad(form.getProfundidad().doubleValue());
        evento.setLatitud(form.getLatitud().doubleValue());
        evento.setLongitud(form.getLongitud().doubleValue());
        evento.setReferencia(form.getReferencia().trim());
        evento.setUbicacion(ubicacion);
        evento.setDistrito(distritoService.elegir(form.getDistritoId(), ubicacion, evento.getDistrito()));
        evento.setTipoAlerta(alerta);
        evento.setAfectacionNivel(nivel);
        evento.setEstado(form.getEstado());
    }

    private LocalDateTime normalizarFechaHora(String valor) {
        String texto = valor.trim().replace('T', ' ');
        if (texto.length() == 16) texto += ":00";
        try {
            return LocalDateTime.parse(texto, FORMATO);
        } catch (DateTimeParseException ex) {
            throw new ReglaNegocioException("La fecha y hora no son válidas.");
        }
    }
}