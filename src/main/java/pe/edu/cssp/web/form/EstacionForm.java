package pe.edu.cssp.web.form;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import pe.edu.cssp.validation.CodigoEstacionDisponible;
import pe.edu.cssp.validation.UbicacionActiva;

@CodigoEstacionDisponible
public class EstacionForm {

    private Integer id;
    @NotBlank(message = "El código es obligatorio.")
    @Size(max = 20, message = "El código debe tener como máximo 20 caracteres.")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 150, message = "El nombre debe tener como máximo 150 caracteres.")
    private String nombre;
    @NotNull(message = "Seleccione una ubicación.")
    @UbicacionActiva
    private Integer ubicacionId;
    @NotNull(message = "La latitud es obligatoria.")
    @DecimalMin(value = "-90.0", message = "Latitud mínima: -90.")
    @DecimalMax(value = "90.0", message = "Latitud máxima: 90.")
    @Digits(integer = 3, fraction = 6, message = "La latitud admite hasta 6 decimales.")
    private BigDecimal latitud;
    @NotNull(message = "La longitud es obligatoria.")
    @DecimalMin(value = "-180.0", message = "Longitud mínima: -180.")
    @DecimalMax(value = "180.0", message = "Longitud máxima: 180.")
    @Digits(integer = 3, fraction = 6, message = "La longitud admite hasta 6 decimales.")
    private BigDecimal longitud;
    @NotBlank(message = "La fecha de instalación es obligatoria.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Use el formato AAAA-MM-DD.")
    private String instalacionFecha;
    @NotBlank
    @Pattern(regexp = "ACTIVA|INACTIVA", message = "Estado inválido.")
    private String estado = "ACTIVA";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(Integer ubicacionId) {
        this.ubicacionId = ubicacionId;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public String getInstalacionFecha() {
        return instalacionFecha;
    }

    public void setInstalacionFecha(String instalacionFecha) {
        this.instalacionFecha = instalacionFecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
