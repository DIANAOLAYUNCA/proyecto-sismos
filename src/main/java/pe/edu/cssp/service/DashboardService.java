package pe.edu.cssp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.dto.reporte.DanosPorTipoDto;
import pe.edu.cssp.dto.reporte.EventosPorAlertaDto;
import pe.edu.cssp.dto.reporte.FichaIndicadorDto;
import pe.edu.cssp.dto.reporte.IndicadoresDashboardDto;
import pe.edu.cssp.dto.reporte.SismoAlertaAltaDto;
import pe.edu.cssp.dto.reporte.SismoMensualDto;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.ReporteDano;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.ReporteDanoRepository;
import pe.edu.cssp.repository.UbicacionRepository;

/**
 * Cálculo de los indicadores del dashboard. Todo se calcula sobre los sismos vigentes (no dados de baja)
 * que caen en el filtro: ubicación (departamento) y periodo (últimos N meses, contando el mes actual).
 */
@Service
public class DashboardService {
    /** Periodos que se pueden elegir en el filtro (en meses); 6 es el valor por defecto. */
    public static final List<Integer> PERIODOS = List.of(3, 6, 12, 24);
    public static final int PERIODO_POR_DEFECTO = 6;
    public static final double MAGNITUD_ALTA = 5.0;
    private static final Set<String> COLORES_ALTOS = Set.of("NARANJA", "ROJO");
    private static final Set<String> ESTADOS_ABIERTOS = Set.of("REGISTRADO", "EN_EVALUACION", "EN_SEGUIMIENTO");

    private static final List<FichaIndicadorDto> FICHAS = List.of(
            new FichaIndicadorDto("Sismos registrados",
                    "Cantidad de sismos vigentes ocurridos en el periodo y la ubicación filtrados.",
                    "COUNT de sismos con estado distinto de INACTIVO y fecha/hora dentro del periodo.",
                    "Tabla Evento (módulo Sismos).",
                    "Detectar zonas o meses con mayor actividad para reforzar el monitoreo y la preparación."),
            new FichaIndicadorDto("Magnitud máxima",
                    "El sismo más fuerte del periodo filtrado.",
                    "MAX(magnitud) de los sismos del filtro, en escala Mw.",
                    "Tabla Evento, columna magnitud.",
                    "Definir el nivel de respuesta y si corresponde activar protocolos de emergencia."),
            new FichaIndicadorDto("Sismos de alerta alta",
                    "Cuántos sismos del periodo fueron críticos y qué porcentaje del total representan.",
                    "COUNT de sismos con magnitud ≥ 5.0 o tipo de alerta de color NARANJA/ROJO; % = alerta alta / total × 100.",
                    "Tablas Evento y Tipo_Alerta.",
                    "Priorizar recursos de evaluación de daños y comunicación a la población."),
            new FichaIndicadorDto("Sismos abiertos",
                    "Sismos que aún no se cierran (REGISTRADO, EN_EVALUACION o EN_SEGUIMIENTO).",
                    "COUNT de sismos del filtro cuyo estado no es CERRADO.",
                    "Tabla Evento, columna estado.",
                    "Asignar personal para completar la evaluación y el seguimiento pendientes."),
            new FichaIndicadorDto("Estaciones activas",
                    "Estaciones sísmicas operativas en la ubicación filtrada (no depende del periodo).",
                    "COUNT de estaciones con estado ACTIVA (y de la ubicación, si se filtró).",
                    "Tabla Estacion (módulo Estaciones).",
                    "Evaluar la cobertura de la red y planificar nuevas estaciones o mantenimiento."),
            new FichaIndicadorDto("Afectados reportados",
                    "Total de personas o bienes afectados según los reportes de daño de los sismos filtrados.",
                    "SUM(cantidad_afectada) de reportes de daño no DESCARTADOS de los sismos del filtro.",
                    "Tabla Reporte_Dano (módulo Reportes de daño).",
                    "Dimensionar la ayuda humanitaria y la atención por tipo de daño."));

    private final EventoRepository eventoRepository;
    private final EstacionRepository estacionRepository;
    private final ReporteDanoRepository reporteDanoRepository;
    private final UbicacionRepository ubicacionRepository;

    public DashboardService(EventoRepository eventoRepository, EstacionRepository estacionRepository,
                            ReporteDanoRepository reporteDanoRepository, UbicacionRepository ubicacionRepository) {
        this.eventoRepository = eventoRepository;
        this.estacionRepository = estacionRepository;
        this.reporteDanoRepository = reporteDanoRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    /** Filtro ya normalizado: ubicacionId 0 = todas; meses siempre es uno de {@link #PERIODOS}. */
    public record Filtro(int ubicacionId, int meses) {
        public static Filtro de(Integer ubicacionId, Integer meses) {
            return new Filtro(ubicacionId == null || ubicacionId < 0 ? 0 : ubicacionId,
                    meses != null && PERIODOS.contains(meses) ? meses : PERIODO_POR_DEFECTO);
        }
        YearMonth primerMes() { return YearMonth.now().minusMonths(meses - 1L); }
        LocalDateTime desde() { return primerMes().atDay(1).atStartOfDay(); }
        LocalDateTime hasta() { return YearMonth.now().plusMonths(1).atDay(1).atStartOfDay(); }
    }

    /** Lo que necesita la vista del dashboard, calculado con una sola lectura de sismos y de reportes. */
    public record Datos(IndicadoresDashboardDto kpis, List<SismoMensualDto> sismosPorMes, List<EventosPorAlertaDto> eventosPorAlerta,
                        List<SismoAlertaAltaDto> sismosAlertaAlta, List<DanosPorTipoDto> danosPorTipo) { }

    @Transactional(readOnly = true)
    public Datos calcular(Filtro filtro) {
        List<Evento> eventos = eventoRepository.paraDashboard(filtro.ubicacionId(), filtro.desde(), filtro.hasta());
        List<ReporteDano> reportes = reporteDanoRepository.paraDashboard(filtro.ubicacionId(), filtro.desde(), filtro.hasta());
        return new Datos(indicadores(filtro, eventos, reportes), sismosPorMes(filtro, eventos), eventosPorAlerta(eventos),
                sismosAlertaAlta(eventos), danosPorTipo(reportes));
    }

    @Transactional(readOnly = true)
    public List<Ubicacion> ubicaciones() { return ubicacionRepository.findByEstadoOrderByNombreAsc("ACTIVO"); }

    public List<FichaIndicadorDto> fichas() { return FICHAS; }

    public static boolean esAlertaAlta(Evento e) {
        return e.getMagnitud() >= MAGNITUD_ALTA
                || (e.getTipoAlerta() != null && e.getTipoAlerta().getColor() != null
                    && COLORES_ALTOS.contains(e.getTipoAlerta().getColor().toUpperCase(Locale.ROOT)));
    }

    IndicadoresDashboardDto indicadores(Filtro filtro, List<Evento> eventos, List<ReporteDano> reportes) {
        BigDecimal magnitudMaxima = eventos.stream()
                .map(e -> BigDecimal.valueOf(e.getMagnitud()).setScale(2, RoundingMode.HALF_UP))
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        long alertaAlta = eventos.stream().filter(DashboardService::esAlertaAlta).count();
        long abiertos = eventos.stream().filter(e -> ESTADOS_ABIERTOS.contains(e.getEstado())).count();
        long afectados = reportes.stream().mapToLong(ReporteDano::getCantidadAfectada).sum();
        return new IndicadoresDashboardDto(eventos.size(), magnitudMaxima, alertaAlta, abiertos,
                estacionRepository.contarActivas(filtro.ubicacionId()), afectados);
    }

    List<SismoMensualDto> sismosPorMes(Filtro filtro, List<Evento> eventos) {
        List<SismoMensualDto> resultado = new ArrayList<>();
        YearMonth primero = filtro.primerMes();
        for (int i = 0; i < filtro.meses(); i++) {
            YearMonth periodo = primero.plusMonths(i);
            long total = eventos.stream().filter(e -> YearMonth.from(e.getFechaHora()).equals(periodo)).count();
            resultado.add(new SismoMensualDto(periodo.getYear(), periodo.getMonthValue(), total));
        }
        return resultado;
    }

    List<EventosPorAlertaDto> eventosPorAlerta(List<Evento> eventos) {
        return eventos.stream()
                .sorted(Comparator.comparingInt(e -> e.getTipoAlerta().getPrioridad()))
                .collect(Collectors.groupingBy(e -> e.getTipoAlerta().getNombre(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new EventosPorAlertaDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    List<SismoAlertaAltaDto> sismosAlertaAlta(List<Evento> eventos) {
        return eventos.stream()
                .filter(DashboardService::esAlertaAlta)
                .sorted(Comparator.comparingDouble(Evento::getMagnitud).reversed())
                .limit(6)
                .map(e -> new SismoAlertaAltaDto(
                        e.getCodigo(),
                        e.getUbicacion().getNombre() + (e.getDistrito() != null ? " - " + e.getDistrito().getNombre() : ""),
                        BigDecimal.valueOf(e.getMagnitud()).setScale(2, RoundingMode.HALF_UP),
                        e.getTipoAlerta().getNombre()))
                .toList();
    }

    List<DanosPorTipoDto> danosPorTipo(List<ReporteDano> reportes) {
        return reportes.stream()
                .collect(Collectors.groupingBy(r -> r.getTipoDano().getNombre(), LinkedHashMap::new,
                        Collectors.summingLong(ReporteDano::getCantidadAfectada)))
                .entrySet().stream()
                .map(entry -> new DanosPorTipoDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(DanosPorTipoDto::getTotalAfectados).reversed())
                .toList();
    }

    public String etiquetaMes(SismoMensualDto sismo) {
        Locale localePe = Locale.forLanguageTag("es-PE");
        YearMonth periodo = YearMonth.of(sismo.getAnio(), sismo.getMes());
        String etiqueta = periodo.format(DateTimeFormatter.ofPattern("MMM yyyy", localePe));
        return etiqueta.substring(0, 1).toUpperCase(localePe) + etiqueta.substring(1);
    }
}
