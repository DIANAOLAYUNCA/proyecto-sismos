package pe.edu.cssp.dto.reporte;

import java.math.BigDecimal;

/** Valores de los KPI del dashboard para el filtro elegido (ubicación y periodo). */
public class IndicadoresDashboardDto {
    private final long totalSismos;
    private final BigDecimal magnitudMaxima;
    private final long sismosAlertaAlta;
    private final long sismosAbiertos;
    private final long estacionesActivas;
    private final long afectadosReportados;

    public IndicadoresDashboardDto(long totalSismos, BigDecimal magnitudMaxima, long sismosAlertaAlta,
                                   long sismosAbiertos, long estacionesActivas, long afectadosReportados) {
        this.totalSismos = totalSismos;
        this.magnitudMaxima = magnitudMaxima;
        this.sismosAlertaAlta = sismosAlertaAlta;
        this.sismosAbiertos = sismosAbiertos;
        this.estacionesActivas = estacionesActivas;
        this.afectadosReportados = afectadosReportados;
    }

    public long getTotalSismos() { return totalSismos; }
    public BigDecimal getMagnitudMaxima() { return magnitudMaxima; }
    public long getSismosAlertaAlta() { return sismosAlertaAlta; }
    public long getSismosAbiertos() { return sismosAbiertos; }
    public long getEstacionesActivas() { return estacionesActivas; }
    public long getAfectadosReportados() { return afectadosReportados; }

    /** Porcentaje de sismos del periodo que fueron de alerta alta (0 si no hubo sismos). */
    public long getPorcentajeAlertaAlta() {
        return totalSismos == 0 ? 0 : Math.round(sismosAlertaAlta * 100.0 / totalSismos);
    }
}
