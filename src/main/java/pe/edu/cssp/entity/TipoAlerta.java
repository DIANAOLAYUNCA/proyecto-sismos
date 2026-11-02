package pe.edu.cssp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tipo_Alerta")
public class TipoAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name = "color", nullable = false, length = 20)
    private String color;
    @Column(name = "prioridad", nullable = false)
    private int prioridad;
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    @Column(name = "creacion_fecha", nullable = false)
    private String creacionFecha;
    @Column(name = "creacion_usuario", nullable = false)
    private String creacionUsuario;
    @Column(name = "actualizacion_fecha")
    private String actualizacionFecha;
    @Column(name = "actualizacion_usuario")
    private String actualizacionUsuario;
    @OneToMany(mappedBy = "tipoAlerta")
    private List<Evento> eventos = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
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

    public String getCreacionFecha() {
        return creacionFecha;
    }

    public void setCreacionFecha(String creacionFecha) {
        this.creacionFecha = creacionFecha;
    }

    public String getCreacionUsuario() {
        return creacionUsuario;
    }

    public void setCreacionUsuario(String creacionUsuario) {
        this.creacionUsuario = creacionUsuario;
    }

    public String getActualizacionFecha() {
        return actualizacionFecha;
    }

    public void setActualizacionFecha(String actualizacionFecha) {
        this.actualizacionFecha = actualizacionFecha;
    }

    public String getActualizacionUsuario() {
        return actualizacionUsuario;
    }

    public void setActualizacionUsuario(String actualizacionUsuario) {
        this.actualizacionUsuario = actualizacionUsuario;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }
}
