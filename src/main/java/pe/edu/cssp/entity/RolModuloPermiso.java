package pe.edu.cssp.entity;

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
@Table(name = "Rol_Modulo_Permiso")
public class RolModuloPermiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;
    
    @Column(name = "puede_crear", nullable = false)
    private boolean puedeCrear;

    @Column(name = "puede_leer", nullable = false)
    private boolean puedeLeer;

    @Column(name = "puede_actualizar", nullable = false)
    private boolean puedeActualizar;

    @Column(name = "puede_eliminar", nullable = false)
    private boolean puedeEliminar;
    
    @Column(name = "creacion_fecha", nullable = false)
    private LocalDateTime creacionFecha;
    
    @Column(name = "creacion_usuario", nullable = false)
    private String creacionUsuario;
    
    @Column(name = "actualizacion_fecha")
    private LocalDateTime actualizacionFecha;
    
    @Column(name = "actualizacion_usuario")
    private String actualizacionUsuario;
    
    public RolModuloPermiso() {}
        
    public RolModuloPermiso(int id, Rol rol, Modulo modulo, boolean puedeCrear, boolean puedeLeer, boolean puedeActualizar, boolean puedeEliminar, LocalDateTime creacionFecha, String creacionUsuario, LocalDateTime actualizacionFecha, String actualizacionUsuario) {
        this.id = id;
        this.rol = rol;
        this.modulo = modulo;
        this.puedeCrear = puedeCrear;
        this.puedeLeer = puedeLeer;
        this.puedeActualizar = puedeActualizar;
        this.puedeEliminar = puedeEliminar;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public boolean isPuedeCrear() {
        return puedeCrear;
    }

    public void setPuedeCrear(boolean puedeCrear) {
        this.puedeCrear = puedeCrear;
    }

    public boolean isPuedeLeer() {
        return puedeLeer;
    }

    public void setPuedeLeer(boolean puedeLeer) {
        this.puedeLeer = puedeLeer;
    }

    public boolean isPuedeActualizar() {
        return puedeActualizar;
    }

    public void setPuedeActualizar(boolean puedeActualizar) {
        this.puedeActualizar = puedeActualizar;
    }

    public boolean isPuedeEliminar() {
        return puedeEliminar;
    }

    public void setPuedeEliminar(boolean puedeEliminar) {
        this.puedeEliminar = puedeEliminar;
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
}
