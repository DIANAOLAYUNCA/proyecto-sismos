package pe.edu.cssp.repository;

import pe.edu.cssp.model.Ubicacion;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class UbicacionRepository {

    private final List<Ubicacion> ubicaciones = new CopyOnWriteArrayList<>();
    private int siguienteId = 1;

    public UbicacionRepository() {
        agregar(new Ubicacion(0, 1, 1, "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new Ubicacion(0, 1, 2, "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new Ubicacion(0, 1, 3, "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new Ubicacion(0, 1, 4, "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new Ubicacion(0, 1, 5, "2026-01-01 08:00:00", "sistema", null, null));
    }

    public List<Ubicacion> listar() {
        return List.copyOf(ubicaciones);
    }

    public Optional<Ubicacion> buscarId(int id) {
        return ubicaciones.stream()
                .filter(u -> u.getId() == id)
                .findFirst();
    }

    public Ubicacion agregar(Ubicacion ubicacion) {
        ubicacion.setId(siguienteId++);
        ubicaciones.add(ubicacion);
        return ubicacion;
    }

    public boolean actualizar(Ubicacion ubicacion) {
        Optional<Ubicacion> actual = buscarId(ubicacion.getId());
        if (actual.isEmpty()) {
            return false;
        }
        int indice = ubicaciones.indexOf(actual.get());
        ubicaciones.set(indice, ubicacion);
        return true;
    }

    public boolean eliminar(int id) {
        return ubicaciones.removeIf(u -> u.getId() == id);
    }
}
