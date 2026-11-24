package pe.edu.cssp.entity;

import jakarta.persistence.CascadeType;
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
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

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
    private LocalDateTime fechaHora;

    @Column(name = "magnitud", nullable = false, columnDefinition = "DECIMAL(4,2)")
    private double magnitud;

    @Column(name = "profundidad", nullable = false, columnDefinition = "DECIMAL(6,2)")
    private double profundidad;

    @Column(name = "latitud", nullable = false, columnDefinition = "DECIMAL(9,6)")
    private double latitud;

    @Column(name = "longitud", nullable = false, columnDefinition = "DECIMAL(9,6)")
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
    private LocalDateTime creacionFecha;

    @Column(name = "creacion_usuario", nullable = false)
    private String creacionUsuario;

    @Column(name = "actualizacion_fecha")
    private LocalDateTime actualizacionFecha;

    @Column(name = "actualizacion_usuario")
    private String actualizacionUsuario;

    @Column(name = "referencia", nullable = false, length = 255)
    private String referencia;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tipo_alerta_id", nullable = false)
    private TipoAlerta tipoAlerta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "distrito_id")
    private Distrito distrito;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seguimiento> seguimientos = new ArrayList<>();

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReporteDano> reportes = new ArrayList<>();

    public Evento() {
    }

    public Evento(int id, Estacion estacion, String codigo, LocalDateTime fechaHora,
            double magnitud, double profundidad, double latitud, double longitud,
            Ubicacion ubicacion, AfectacionNivel afectacionNivel, String estado,
            LocalDateTime creacionFecha, String creacionUsuario,
            LocalDateTime actualizacionFecha, String actualizacionUsuario) {
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

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public List<Seguimiento> getSeguimientos() {
        return seguimientos;
    }

    public void setSeguimientos(List<Seguimiento> seguimientos) {
        this.seguimientos = seguimientos;
    }

    public List<ReporteDano> getReportes() {
        return reportes;
    }

    public void setReportes(List<ReporteDano> reportes) {
        this.reportes = reportes;
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public TipoAlerta getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(TipoAlerta tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }
}
