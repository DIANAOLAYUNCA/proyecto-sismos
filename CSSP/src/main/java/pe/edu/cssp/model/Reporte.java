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
import java.time.LocalDateTime;

@Entity
@Table(name = "Reporte")
public class Reporte {
    /* Definición de estructura */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;
    
    @Column(name = "fecha_hora", nullable = false)
    private String fechaHora;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "afectacion_tipo_id", nullable = false)
    private AfectacionTipo afectacionTipo;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "afectacion_nivel_id", nullable = false)
    private AfectacionNivel afectacionNivel;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "num_afectados")
    private Integer numAfectados;
    
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
    public Reporte() {}
    
    
    /* Constructor */
    public Reporte(int id, Evento evento, Ubicacion ubicacion, String fechaHora, AfectacionTipo afectacionTipo, AfectacionNivel afectacionNivel, String descripcion, Integer numAfectados, String estado, String creacionFecha, String creacionUsuario, String actualizacionFecha, String actualizacionUsuario) {
        this.id = id;
        this.evento = evento;
        this.ubicacion = ubicacion;
        this.fechaHora = fechaHora;
        this.afectacionTipo = afectacionTipo;
        this.afectacionNivel = afectacionNivel;
        this.descripcion = descripcion;
        this.numAfectados = numAfectados;
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

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public AfectacionTipo getAfectacionTipo() {
        return afectacionTipo;
    }

    public void setAfectacionTipo(AfectacionTipo afectacionTipo) {
        this.afectacionTipo = afectacionTipo;
    }

    public AfectacionNivel getAfectacionNivel() {
        return afectacionNivel;
    }

    public void setAfectacionNivel(AfectacionNivel afectacionNivel) {
        this.afectacionNivel = afectacionNivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getNumAfectados() {
        return numAfectados;
    }

    public void setNumAfectados(Integer numAfectados) {
        this.numAfectados = numAfectados;
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
