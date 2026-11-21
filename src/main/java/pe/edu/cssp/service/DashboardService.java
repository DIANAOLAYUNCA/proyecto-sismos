package pe.edu.cssp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.dto.reporte.DanosPorTipoDto;
import pe.edu.cssp.dto.reporte.EventosPorAlertaDto;
import pe.edu.cssp.dto.reporte.IndicadoresDashboardDto;
import pe.edu.cssp.dto.reporte.SismoAlertaAltaDto;
import pe.edu.cssp.dto.reporte.SismoMensualDto;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.ReporteDano;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.ReporteDanoRepository;

@Service
public class DashboardService {

    private final EventoRepository eventoRepository;
    private final EstacionRepository estacionRepository;
    private final ReporteDanoRepository reporteDanoRepository;

    public DashboardService(EventoRepository eventoRepository,
                            EstacionRepository estacionRepository,
                            ReporteDanoRepository reporteDanoRepository) {
        this.eventoRepository = eventoRepository;
        this.estacionRepository = estacionRepository;
        this.reporteDanoRepository = reporteDanoRepository;
    }

    @Transactional(readOnly = true)
    public IndicadoresDashboardDto indicadores() {
        List<Evento> eventos = eventoRepository.listar();
        YearMonth mesActual = YearMonth.now();

        long sismosMes = eventos.stream()
                .filter(e -> e.getFechaHora() != null && YearMonth.from(e.getFechaHora()).equals(mesActual))
                .count();

        BigDecimal magnitudMaxima = eventos.stream()
                .map(e -> BigDecimal.valueOf(e.getMagnitud()).setScale(2, RoundingMode.HALF_UP))
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        long estacionesActivas = estacionRepository.listarActivas().size();
        long reportesDano = reporteDanoRepository.count();

        return new IndicadoresDashboardDto(
                sismosMes,
                eventos.size(),
                magnitudMaxima,
                estacionesActivas,
                reportesDano
        );
    }

    @Transactional(readOnly = true)
    public List<SismoMensualDto> sismosUltimosSeisMeses() {
        List<Evento> eventos = eventoRepository.listar();
        YearMonth actual = YearMonth.now();
        YearMonth primero = actual.minusMonths(5);

        List<SismoMensualDto> resultado = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            YearMonth periodo = primero.plusMonths(i);
            long total = eventos.stream()
                    .filter(e -> e.getFechaHora() != null && YearMonth.from(e.getFechaHora()).equals(periodo))
                    .count();
            resultado.add(new SismoMensualDto(periodo.getYear(), periodo.getMonthValue(), total));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<EventosPorAlertaDto> eventosPorAlerta() {
        return eventoRepository.listar().stream()
                .filter(e -> e.getTipoAlerta() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getTipoAlerta().getNombre(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new EventosPorAlertaDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SismoAlertaAltaDto> sismosAlertaAlta() {
        return eventoRepository.listar().stream()
                .filter(e -> e.getMagnitud() >= 5.0 ||
                        (e.getTipoAlerta() != null &&
                         ("ROJO".equalsIgnoreCase(e.getTipoAlerta().getCodigo()) ||
                          "NARANJA".equalsIgnoreCase(e.getTipoAlerta().getCodigo()))))
                .sorted(Comparator.comparingDouble(Evento::getMagnitud).reversed())
                .limit(6)
                .map(e -> new SismoAlertaAltaDto(
                        e.getCodigo(),
                        e.getReferencia(),
                        BigDecimal.valueOf(e.getMagnitud()).setScale(2, RoundingMode.HALF_UP),
                        e.getTipoAlerta() != null ? e.getTipoAlerta().getNombre() : "ALTA"
                ))
                .toList();
    }

    @Transactional(readOnly = true)
        public List<DanosPorTipoDto> danosPorTipo() {
            return reporteDanoRepository.listar().stream()
                    .filter(r -> r.getTipoDano() != null)
                    .collect(Collectors.groupingBy(
                            r -> r.getTipoDano().getNombre(),
                            LinkedHashMap::new,
                            Collectors.summingLong(ReporteDano::getCantidadAfectada)
                    ))
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
