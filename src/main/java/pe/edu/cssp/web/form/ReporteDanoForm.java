package pe.edu.cssp.web.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.edu.cssp.validation.EventoVigente;
import pe.edu.cssp.validation.TipoDanoActivo;
import pe.edu.cssp.validation.UbicacionActiva;

public class ReporteDanoForm {
    private Integer id;
    @NotNull(message = "Seleccione el sismo asociado.")
    @EventoVigente
    private Integer eventoId;
    @NotNull(message = "Seleccione un tipo de daño.")
    @TipoDanoActivo
    private Integer tipoDanoId;
    @NotNull(message = "Seleccione una ubicación.")
    @UbicacionActiva
    private Integer ubicacionId;
    @NotBlank(message = "La fecha y hora son obligatorias.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}(:\\d{2})?", message = "Fecha y hora inválidas.")
    private String fechaHora;
    @NotNull(message = "La cantidad afectada es obligatoria.")
    @Min(value = 0, message = "La cantidad afectada no puede ser negativa.")
    @Max(value = 100000000, message = "La cantidad afectada es demasiado grande.")
    private Integer cantidadAfectada;
    @NotBlank(message = "La descripción es obligatoria.")
    @Size(max = 1000, message = "La descripción debe tener como máximo 1000 caracteres.")
    private String descripcion;
    private Integer afectacionNivelId;
    @NotBlank(message = "El estado es obligatorio.")
    @Pattern(regexp = "REGISTRADO|VERIFICADO|DESCARTADO", message = "Estado inválido.")
    private String estado = "REGISTRADO";

    public Integer getId() { return id; } public void setId(Integer id) { this.id = id; }
    public Integer getEventoId() { return eventoId; } public void setEventoId(Integer eventoId) { this.eventoId = eventoId; }
    public Integer getTipoDanoId() { return tipoDanoId; } public void setTipoDanoId(Integer tipoDanoId) { this.tipoDanoId = tipoDanoId; }
    public Integer getUbicacionId() { return ubicacionId; } public void setUbicacionId(Integer ubicacionId) { this.ubicacionId = ubicacionId; }
    public String getFechaHora() { return fechaHora; } public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }
    public Integer getCantidadAfectada() { return cantidadAfectada; } public void setCantidadAfectada(Integer cantidadAfectada) { this.cantidadAfectada = cantidadAfectada; }
    public String getDescripcion() { return descripcion; } public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getAfectacionNivelId() { return afectacionNivelId; } public void setAfectacionNivelId(Integer afectacionNivelId) { this.afectacionNivelId = afectacionNivelId; }
    public String getEstado() { return estado; } public void setEstado(String estado) { this.estado = estado; }
}
