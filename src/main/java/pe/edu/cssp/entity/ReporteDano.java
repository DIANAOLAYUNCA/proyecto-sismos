package pe.edu.cssp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Reporte_Dano")
public class ReporteDano {

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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "afectacion_nivel_id")
    private AfectacionNivel afectacionNivel;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tipo_dano_id", nullable = false)
    private TipoDano tipoDano;
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    @Column(name = "cantidad_afectada", nullable = false)
    private int cantidadAfectada;
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

    public AfectacionNivel getAfectacionNivel() {
        return afectacionNivel;
    }

    public void setAfectacionNivel(AfectacionNivel afectacionNivel) {
        this.afectacionNivel = afectacionNivel;
    }

    public TipoDano getTipoDano() {
        return tipoDano;
    }

    public void setTipoDano(TipoDano tipoDano) {
        this.tipoDano = tipoDano;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidadAfectada() {
        return cantidadAfectada;
    }

    public void setCantidadAfectada(int cantidadAfectada) {
        this.cantidadAfectada = cantidadAfectada;
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
