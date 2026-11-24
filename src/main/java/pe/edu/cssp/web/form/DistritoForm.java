package pe.edu.cssp.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.edu.cssp.validation.CodigoDistritoDisponible;

@CodigoDistritoDisponible
public class DistritoForm {
    private Integer id;
    @NotNull(message = "Seleccione la provincia del distrito.")
    private Integer provinciaId;
    @NotBlank(message = "El código es obligatorio.")
    @Pattern(regexp = "\\d{2}", message = "El código debe tener 2 dígitos (por ejemplo 01).")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres.")
    private String nombre;
    @NotBlank(message = "El estado es obligatorio.")
    @Pattern(regexp = "ACTIVO|INACTIVO", message = "Estado inválido.")
    private String estado = "ACTIVO";

    public Integer getId() { return id; } public void setId(Integer id) { this.id = id; }
    public Integer getProvinciaId() { return provinciaId; } public void setProvinciaId(Integer provinciaId) { this.provinciaId = provinciaId; }
    public String getCodigo() { return codigo; } public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; } public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEstado() { return estado; } public void setEstado(String estado) { this.estado = estado; }
}
