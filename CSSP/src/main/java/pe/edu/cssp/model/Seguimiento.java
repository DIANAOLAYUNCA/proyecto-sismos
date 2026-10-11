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
@Table(name = "Seguimiento")
public class Seguimiento {
    /* Definición de estructura */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Usuario responsable;
    
    @Column(name = "acciones")
    private String acciones;
    
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
    
    
    /* Clase publica para los servlets */
    public Seguimiento() {}
    
    
    /* Constructor */
    public Seguimiento(int id, Evento evento, String observaciones, Usuario responsable, String acciones, String estado, String creacionFecha, String creacionUsuario, String actualizacionFecha, String actualizacionUsuario) {
        this.id = id;
        this.evento = evento;
        this.observaciones = observaciones;
        this.responsable = responsable;
        this.acciones = acciones;
        this.estado = estado;
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

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public String getAcciones() {
        return acciones;
    }

    public void setAcciones(String acciones) {
        this.acciones = acciones;
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
