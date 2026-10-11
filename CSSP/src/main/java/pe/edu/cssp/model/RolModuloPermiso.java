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
@Table(name = "Rol_Modulo_Permiso")
public class RolModuloPermiso {
    /* Definición de estructura */
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
    private String creacionFecha;
    
    @Column(name = "creacion_usuario", nullable = false)
    private String creacionUsuario;
    
    @Column(name = "actualizacion_fecha")
    private String actualizacionFecha;
    
    @Column(name = "actualizacion_usuario")
    private String actualizacionUsuario;
    
    
    /* Clase publica para los servlets */
    public RolModuloPermiso() {}
        
    
    /* Constructor */
    public RolModuloPermiso(int id, Rol rol, Modulo modulo, boolean puedeCrear, boolean puedeLeer, boolean puedeActualizar, boolean puedeEliminar, String creacionFecha, String creacionUsuario, String actualizacionFecha, String actualizacionUsuario) {
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
    
    
    /* Definición de Getters y Setters */
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
