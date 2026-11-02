package pe.edu.cssp.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.edu.cssp.validation.CodigoTipoDanoDisponible;

@CodigoTipoDanoDisponible
public class TipoDanoForm {
    private Integer id;
    @NotBlank(message = "El código es obligatorio.")
    @Size(max = 20, message = "El código debe tener como máximo 20 caracteres.")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres.")
    private String nombre;
    @NotBlank(message = "El sector es obligatorio.")
    @Pattern(regexp = "SALUD|VIVIENDA|EDUCACION|INFRAESTRUCTURA|SERVICIOS_BASICOS|AGROPECUARIO|POBLACION", message = "Sector inválido.")
    private String sector;
    @Size(max = 255, message = "La descripción debe tener como máximo 255 caracteres.")
    private String descripcion;
    @NotBlank(message = "El estado es obligatorio.")
    @Pattern(regexp = "ACTIVO|INACTIVO", message = "Estado inválido.")
    private String estado = "ACTIVO";

    public Integer getId() { return id; } public void setId(Integer id) { this.id = id; }
    public String getCodigo() { return codigo; } public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; } public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSector() { return sector; } public void setSector(String sector) { this.sector = sector; }
    public String getDescripcion() { return descripcion; } public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; } public void setEstado(String estado) { this.estado = estado; }
}
