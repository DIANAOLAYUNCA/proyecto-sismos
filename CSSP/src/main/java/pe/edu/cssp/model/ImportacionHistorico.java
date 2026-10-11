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
@Table(name = "Importacion_Historico")
public class ImportacionHistorico {
    /* Definición de estructura */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;
    
    @Column(name = "fecha_inicio", nullable = false)
    private String fechaInicio;

    @Column(name = "fecha_fin")
    private String fechaFin;
    
    @Column(name = "total_registros")
    private Integer totalRegistros;
    
    @Column(name = "registros_procesados")
    private Integer registrosProcesados;
    
    @Column(name = "registros_error")
    private Integer registrosError;
    
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    
    @Column(name = "detalle_errores", columnDefinition = "TEXT")
    private String detalleErrores;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "creacion_fecha", nullable = false)
    private String creacionFecha;
    
    @Column(name = "creacion_usuario", nullable = false)
    private String creacionUsuario;
    
    @Column(name = "actualizacion_fecha")
    private String actualizacionFecha;
    
    @Column(name = "actualizacion_usuario")
    private String actualizacionUsuario;
    
    
    /* Clase publica para los servlets */
    public ImportacionHistorico() {}
    
    
    /* Constructor */
    public ImportacionHistorico(int id, String nombreArchivo, String fechaInicio, String fechaFin, Integer totalRegistros, Integer registrosProcesados, Integer registrosError, String estado, String detalleErrores, Usuario usuario, String creacionFecha, String creacionUsuario, String actualizacionFecha, String actualizacionUsuario) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalRegistros = totalRegistros;
        this.registrosProcesados = registrosProcesados;
        this.registrosError = registrosError;
        this.estado = estado;
        this.detalleErrores = detalleErrores;
        this.usuario = usuario;
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Integer getRegistrosProcesados() {
        return registrosProcesados;
    }

    public void setRegistrosProcesados(Integer registrosProcesados) {
        this.registrosProcesados = registrosProcesados;
    }

    public Integer getRegistrosError() {
        return registrosError;
    }

    public void setRegistrosError(Integer registrosError) {
        this.registrosError = registrosError;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalleErrores() {
        return detalleErrores;
    }

    public void setDetalleErrores(String detalleErrores) {
        this.detalleErrores = detalleErrores;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
