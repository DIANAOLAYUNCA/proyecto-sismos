package pe.edu.cssp.dto.reporte;

public class EventosPorAlertaDto {
    private final String alerta;
    private final Long cantidad;

    public EventosPorAlertaDto(String alerta, Long cantidad) {
        this.alerta = alerta;
        this.cantidad = cantidad;
    }

    public String getAlerta() { return alerta; }
    public Long getCantidad() { return cantidad; }
}
