package pe.edu.cssp.repository;

import pe.edu.cssp.model.Estacion;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class EstacionRepository {

    private final List<Estacion> estaciones = new CopyOnWriteArrayList<>();
    private int siguienteId = 1;

    public EstacionRepository() {
    agregar(new Estacion(0, "EST-001", "Estación Sismológica Characato - Arequipa", 335, -16.465000, -71.488000, "2015-03-10", "ACTIVA", "2026-01-01 08:00:00", "sistema", null, null));
    agregar(new Estacion(0, "ñoEST-002", "Estación Sismológica Cusco", 1942, -13.532000, -71.967500, "2018-07-22", "ACTIVA", "2026-01-01 08:05:00", "sistema", null, null));
    agregar(new Estacion(0, "EST-003", "Estación Sismológica Huancayo", 3259, -12.065000, -75.210000, "2019-05-14", "ACTIVA", "2026-01-01 08:10:00", "sistema", null, null));
    agregar(new Estacion(0, "EST-004", "Estación Sismológica Ica", 406, -14.067000, -75.728000, "2020-08-03", "ACTIVA", "2026-01-01 08:15:00", "sistema", null, null));
    agregar(new Estacion(0, "EST-005", "Estación Sismológica Tacna", 562, -18.014000, -70.253000, "2021-11-19", "INACTIVA", "2026-01-01 08:20:00", "sistema", null, null));
    }

    public List<Estacion> listar() {
        return List.copyOf(estaciones);
    }

    public Optional<Estacion> buscarCod(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return estaciones.stream()
                .filter(e -> e.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst();
    }

    public Optional<Estacion> buscarId(int id) {
        return estaciones.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    public boolean existCodigo(String codigo) {
        return buscarCod(codigo).isPresent();
    }

    public Estacion agregar(Estacion estacion) {
        estacion.setId(siguienteId++);
        estaciones.add(estacion);
        return estacion;
    }

    public boolean actualizar(Estacion estacion) {
        Optional<Estacion> actual = buscarId(estacion.getId());
        if (actual.isEmpty()) {
            return false;
        }
        int indice = estaciones.indexOf(actual.get());
        estaciones.set(indice, estacion);
        return true;
    }

   public boolean eliminar(int id) {
    Optional<Estacion> estacion = buscarId(id);

    if (estacion.isEmpty()) {
        return false;
    }

    estacion.get().setEstado("INACTIVA");
    return true;
    }
}