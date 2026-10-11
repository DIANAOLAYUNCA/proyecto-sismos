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
@Table(name = "Usuario")
public class Usuario {
    /* Definición de estructura */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;
    
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;
    
    @Column(name = "username", nullable = false, length = 50)
    private String usuario;
    
    @Column(name = "email", nullable = false, length = 150)
    private String email;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
    
    
    /* Clase publica para los servlets */
    public Usuario() {}
    
    /* Constructor */
    public Usuario(int id, String nombres, String apellidos, String usuario, String email, String passwordHash, String estado, Rol rol) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.email = email;
        this.passwordHash = passwordHash;
        this.estado = estado;
        this.rol = rol;
    }
    
    
    /* Definición de Getters y Setters */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    
}
