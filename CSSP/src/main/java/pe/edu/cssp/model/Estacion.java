package pe.edu.cssp.model;

public class Estacion {
    private int id;
    private String codigo;
    private String nombre;
    private int ubicacionId;
    private double latitud;
    private double longitud;
    private String instalacionFecha;
    private String estado;
    private String creacionFecha;
    private String creacionUsuario;
    private String actualizacionFecha;
    private String actualizacionUsuario;

    public Estacion() {
    }

    public Estacion(int id, String codigo, String nombre, int ubicacionId,
                    double latitud, double longitud, String instalacionFecha,
                    String estado, String creacionFecha, String creacionUsuario,
                    String actualizacionFecha, String actualizacionUsuario) {

        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.ubicacionId = ubicacionId;
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

    public int getUbicacionId() {
        return ubicacionId;
    }

    public void setUbicacionId(int ubicacionId) {
        this.ubicacionId = ubicacionId;
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

    public String getInstalacionFecha() {
        return instalacionFecha;
    }

    public void setInstalacionFecha(String instalacionFecha) {
        this.instalacionFecha = instalacionFecha;
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
