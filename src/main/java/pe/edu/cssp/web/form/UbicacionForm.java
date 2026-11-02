package pe.edu.cssp.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.edu.cssp.validation.CodigoUbicacionDisponible;

@CodigoUbicacionDisponible
public class UbicacionForm {

    private Integer id;
    @NotBlank(message = "El código es obligatorio.")
    @Size(max = 10, message = "El código debe tener como máximo 10 caracteres.")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres.")
    private String nombre;
    @NotBlank(message = "El tipo es obligatorio.")
    @Pattern(regexp = "DEPARTAMENTO|PROVINCIA_CONSTITUCIONAL", message = "Tipo inválido.")
    private String tipo = "DEPARTAMENTO";
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
