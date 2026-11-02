package pe.edu.cssp.web.form;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import pe.edu.cssp.validation.CodigoEventoDisponible;
import pe.edu.cssp.validation.EstacionActiva;
import pe.edu.cssp.validation.TipoAlertaActiva;
import pe.edu.cssp.validation.UbicacionActiva;

@CodigoEventoDisponible
public class EventoForm {

    private Integer id;
    @NotNull(message = "Seleccione una estación.")
    @EstacionActiva
    private Integer estacionId;
    @NotBlank(message = "El código es obligatorio.")
    @Size(max = 30, message = "El código debe tener como máximo 30 caracteres.")
    private String codigo;
    @NotBlank(message = "La fecha y hora son obligatorias.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}(:\\d{2})?", message = "Fecha y hora inválidas.")
    private String fechaHora;
    @NotNull(message = "La magnitud es obligatoria.")
    @DecimalMin(value = "0.01", message = "La magnitud debe ser mayor que cero.")
    private BigDecimal magnitud;
    @NotNull(message = "La profundidad es obligatoria.")
    @DecimalMin(value = "0.0", message = "La profundidad no puede ser negativa.")
    private BigDecimal profundidad;
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
    @NotBlank(message = "La referencia es obligatoria.")
    @Size(max = 255, message = "La referencia debe tener como máximo 255 caracteres.")
    private String referencia;
    @NotNull(message = "Seleccione una ubicación.")
    @UbicacionActiva
    private Integer ubicacionId;
    @NotNull(message = "Seleccione un tipo de alerta.")
    @TipoAlertaActiva
    private Integer tipoAlertaId;
    @NotNull(message = "Seleccione un nivel de afectación.")
    private Integer afectacionNivelId;
    @NotBlank
    @Pattern(regexp = "REGISTRADO|EN_EVALUACION|EN_SEGUIMIENTO|CERRADO|INACTIVO", message = "Estado inválido.")
    private String estado = "REGISTRADO";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEstacionId() {
        return estacionId;
    }

    public void setEstacionId(Integer estacionId) {
        this.estacionId = estacionId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getMagnitud() {
        return magnitud;
    }

    public void setMagnitud(BigDecimal magnitud) {
        this.magnitud = magnitud;
    }

    public BigDecimal getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(BigDecimal profundidad) {
        this.profundidad = profundidad;
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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(Integer ubicacionId) {
        this.ubicacionId = ubicacionId;
    }

    public Integer getTipoAlertaId() {
        return tipoAlertaId;
    }

    public void setTipoAlertaId(Integer tipoAlertaId) {
        this.tipoAlertaId = tipoAlertaId;
    }

    public Integer getAfectacionNivelId() {
        return afectacionNivelId;
    }

    public void setAfectacionNivelId(Integer afectacionNivelId) {
        this.afectacionNivelId = afectacionNivelId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
