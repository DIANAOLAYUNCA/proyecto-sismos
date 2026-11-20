package pe.edu.cssp.dto.reporte;

import java.math.BigDecimal;

public class SismoAlertaAltaDto {
    private final String codigo;
    private final String ubicacion;
    private final BigDecimal magnitud;
    private final String tipoAlerta;

    public SismoAlertaAltaDto(String codigo, String ubicacion, BigDecimal magnitud, String tipoAlerta) {
        this.codigo = codigo;
        this.ubicacion = ubicacion;
        this.magnitud = magnitud;
        this.tipoAlerta = tipoAlerta;
    }

    public String getCodigo() { return codigo; }
    public String getUbicacion() { return ubicacion; }
    public BigDecimal getMagnitud() { return magnitud; }
    public String getTipoAlerta() { return tipoAlerta; }
}
