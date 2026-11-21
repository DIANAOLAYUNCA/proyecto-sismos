package pe.edu.cssp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private BigDecimal latitud;

    @Column(name = "longitud", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitud;

    @Column(name = "instalacion_fecha", nullable = false)
    private LocalDate instalacionFecha;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "creacion_fecha", nullable = false)
    private LocalDateTime creacionFecha;

    @Column(name = "creacion_usuario", nullable = false)
    private String creacionUsuario;

    @Column(name = "actualizacion_fecha")
    private LocalDateTime actualizacionFecha;

    @Column(name = "actualizacion_usuario")
    private String actualizacionUsuario;
    
//  Otras relaciones
    @OneToMany(mappedBy = "estacion")
    private List<Evento> eventos = new ArrayList<>();

    public Estacion() { }

    public Estacion(int id, String codigo, String nombre, Ubicacion ubicacion,
            BigDecimal latitud, BigDecimal longitud, LocalDate instalacionFecha,
            String estado, LocalDateTime creacionFecha, String creacionUsuario,
            LocalDateTime actualizacionFecha, String actualizacionUsuario) {
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
    
    public LocalDate getInstalacionFecha() {
        return instalacionFecha;
    }
    
    public void setInstalacionFecha(LocalDate instalacionFecha) { 
        this.instalacionFecha = instalacionFecha; 
    }
    
    public String getEstado() { 
        return estado; 
    }
    
    public void setEstado(String estado) { 
        this.estado = estado;
    }
    
    public LocalDateTime getCreacionFecha() {
        return creacionFecha; 
    }
    
    public void setCreacionFecha(LocalDateTime creacionFecha) {
        this.creacionFecha = creacionFecha;
    }
    
    public String getCreacionUsuario() {
        return creacionUsuario; 
    }
    
    public void setCreacionUsuario(String creacionUsuario) {
        this.creacionUsuario = creacionUsuario; 
    }
    
    public LocalDateTime getActualizacionFecha() { 
        return actualizacionFecha;
    }
    
    public void setActualizacionFecha(LocalDateTime actualizacionFecha) {
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