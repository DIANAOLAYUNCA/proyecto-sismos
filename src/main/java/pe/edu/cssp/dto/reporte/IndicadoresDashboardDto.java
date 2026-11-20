package pe.edu.cssp.dto.reporte;

import java.math.BigDecimal;

public class IndicadoresDashboardDto {
    private final long sismosMes;
    private final long totalEventos;
    private final BigDecimal magnitudMaxima;
    private final long estacionesActivas;
    private final long importacionesRealizadas;

    public IndicadoresDashboardDto(long sismosMes, long totalEventos,
                                   BigDecimal magnitudMaxima, long estacionesActivas,
                                   long importacionesRealizadas) {
        this.sismosMes = sismosMes;
        this.totalEventos = totalEventos;
        this.magnitudMaxima = magnitudMaxima;
        this.estacionesActivas = estacionesActivas;
        this.importacionesRealizadas = importacionesRealizadas;
    }

    public long getSismosMes() { return sismosMes; }
    public long getTotalEventos() { return totalEventos; }
    public BigDecimal getMagnitudMaxima() { return magnitudMaxima; }
    public long getEstacionesActivas() { return estacionesActivas; }
    public long getImportacionesRealizadas() { return importacionesRealizadas; }
}
