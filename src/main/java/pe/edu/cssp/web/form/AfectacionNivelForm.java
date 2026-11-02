package pe.edu.cssp.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pe.edu.cssp.validation.CodigoAfectacionNivelDisponible;

@CodigoAfectacionNivelDisponible
public class AfectacionNivelForm {
    private Integer id;
    @NotBlank(message = "El código es obligatorio.")
    @Size(max = 20, message = "El código debe tener como máximo 20 caracteres.")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres.")
    private String nombre;

    public Integer getId() { return id; } public void setId(Integer id) { this.id = id; }
    public String getCodigo() { return codigo; } public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; } public void setNombre(String nombre) { this.nombre = nombre; }
}
