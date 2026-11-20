package pe.edu.cssp.dto.reporte;

public class DanosPorTipoDto {
    private final String tipoDano;
    private final Long totalAfectados;

    public DanosPorTipoDto(String tipoDano, Long totalAfectados) {
        this.tipoDano = tipoDano;
        this.totalAfectados = totalAfectados;
    }

    public String getTipoDano() { return tipoDano; }
    public Long getTotalAfectados() { return totalAfectados; }
}
