package pe.edu.cssp.dto.reporte;

public class SismoMensualDto {
    private final Integer anio;
    private final Integer mes;
    private final Long total;

    public SismoMensualDto(Integer anio, Integer mes, Long total) {
        this.anio = anio;
        this.mes = mes;
        this.total = total;
    }

    public Integer getAnio() { return anio; }
    public Integer getMes() { return mes; }
    public Long getTotal() { return total; }
}
