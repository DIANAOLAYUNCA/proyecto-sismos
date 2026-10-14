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
@Table(name = "Evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estacion_id", nullable = false)
    private Estacion estacion;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "fecha_hora", nullable = false)
    private String fechaHora;

    @Column(name = "magnitud", nullable = false)
    private double magnitud;

    @Column(name = "profundidad", nullable = false)
    private double profundidad;

    @Column(name = "latitud", nullable = false)
    private double latitud;

    @Column(name = "longitud", nullable = false)
    private double longitud;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;

    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "afectacion_nivel_id")
    private AfectacionNivel afectacionNivel;

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

    public Evento() { }

    public Evento(int id, Estacion estacion, String codigo, String fechaHora,
            double magnitud, double profundidad, double latitud, double longitud,
            Ubicacion ubicacion, AfectacionNivel afectacionNivel, String estado,
            String creacionFecha, String creacionUsuario,
            String actualizacionFecha, String actualizacionUsuario) {
        this.id = id;
        this.estacion = estacion;
        this.codigo = codigo;
        this.fechaHora = fechaHora;
        this.magnitud = magnitud;
        this.profundidad = profundidad;
        this.latitud = latitud;
        this.longitud = longitud;
        this.ubicacion = ubicacion;
        this.afectacionNivel = afectacionNivel;
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

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getMagnitud() {
        return magnitud;
    }

    public void setMagnitud(double magnitud) {
        this.magnitud = magnitud;
    }

    public double getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(double profundidad) {
        this.profundidad = profundidad;
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

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public AfectacionNivel getAfectacionNivel() {
        return afectacionNivel;
    }

    public void setAfectacionNivel(AfectacionNivel afectacionNivel) {
        this.afectacionNivel = afectacionNivel;
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