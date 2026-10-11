package pe.edu.cssp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Distrito")
public class Distrito {
    /* Definición de estructura */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", unique = true, nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "codigo", length = 10)
    private String codigo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "provincia_id", nullable = false)
    private Provincia provincia;
    
    @Column(name = "creacion_fecha", nullable = false)
    private String creacionFecha;
    
    @Column(name = "creacion_usuario", nullable = false)
    private String creacionUsuario;
    
    @Column(name = "actualizacion_fecha")
    private String actualizacionFecha;
    
    @Column(name = "actualizacion_usuario")
    private String actualizacionUsuario;
    
    
    /* Clase publica para los servlets */
    public Distrito() {}
    
    
    /* Constructor */
    public Distrito(int id, String nombre, String codigo, Provincia provincia, String creacionFecha, String creacionUsuario, String actualizacionFecha, String actualizacionUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.provincia = provincia;
        this.creacionFecha = creacionFecha;
        this.creacionUsuario = creacionUsuario;
        this.actualizacionFecha = actualizacionFecha;
        this.actualizacionUsuario = actualizacionUsuario;
    }
    
    /* Definición de Getters y Setters */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
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
    
}
