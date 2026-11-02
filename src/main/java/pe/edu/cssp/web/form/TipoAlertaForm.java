package pe.edu.cssp.web.form;

import jakarta.validation.constraints.*;
import pe.edu.cssp.validation.CodigoTipoAlertaDisponible;

@CodigoTipoAlertaDisponible
public class TipoAlertaForm {

    private Integer id;
    @NotBlank(message = "El código es obligatorio.")
    @Size(max = 20, message = "El código debe tener como máximo 20 caracteres.")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres.")
    private String nombre;
    @NotBlank(message = "El color es obligatorio.")
    @Pattern(regexp = "VERDE|AMARILLO|NARANJA|ROJO", message = "Color inválido.")
    private String color;
    @NotNull(message = "La prioridad es obligatoria.")
    @Positive(message = "La prioridad debe ser un número positivo.")
    private Integer prioridad;
    @Size(max = 255, message = "La descripción debe tener como máximo 255 caracteres.")
    private String descripcion;
    @NotBlank(message = "El estado es obligatorio.")
    @Pattern(regexp = "ACTIVO|INACTIVO", message = "Estado inválido.")
    private String estado = "ACTIVO";

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
