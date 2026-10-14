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
@Table(name = "Estacion")
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;

    @Column(name = "latitud", nullable = false, precision = 9, scale = 6)
    private double latitud;

    @Column(name = "longitud", nullable = false, precision = 9, scale = 6)
    private double longitud;

    @Column(name = "instalacion_fecha", nullable = false)
    private String instalacionFecha;

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

    public Estacion() { }

    public Estacion(int id, String codigo, String nombre, Ubicacion ubicacion,
            double latitud, double longitud, String instalacionFecha,
            String estado, String creacionFecha, String creacionUsuario,
            String actualizacionFecha, String actualizacionUsuario)
    {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.instalacionFecha = instalacionFecha;
        this.estado = estado;
        this.creacionFecha = creacionFecha;
        this.creacionUsuario = creacionUsuario;
        this.actualizacionFecha = actualizacionFecha;
        this.actualizacionUsuario = actualizacionUsuario;
    }

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
    
    public Ubicacion getUbicacion() { 
        return ubicacion; 
    }
    
    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public double getLatitud() {
        return latitud;
    }
    
    public void setLatitud(double latitud) { 
        this.latitud = latitud;
    }
    
    public double getLongitud() {
        return longitud; 
    }
    
    public void setLongitud(double longitud) {
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