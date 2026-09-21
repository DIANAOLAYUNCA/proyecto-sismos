package pe.edu.cssp.repository;

import pe.edu.cssp.model.Evento;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


public class EventoRepository {

    private final List<Evento> eventos = new CopyOnWriteArrayList<>();
    private int siguienteId = 1;

    public EventoRepository() {
        agregar(new Evento(0, 335, "SIS-2026-0001", "2026-08-18 14:32:00", 5.60, 34.00, -16.409000, -71.537500, 1, 2, "EN_SEGUIMIENTO", "2026-09-01 08:00:00", "operador", null, null));
        agregar(new Evento(0, 1942, "SIS-2026-0002", "2026-09-02 03:10:00", 4.20, 60.00, -13.532000, -71.967500, 2, 1, "REGISTRADO", "2026-09-02 04:00:00", "operador", null, null));
        agregar(new Evento(0, 3259, "SIS-2026-0003", "2026-09-05 18:45:00", 4.80, 45.00, -12.065000, -75.210000, 3, 2, "EN_EVALUACION", "2026-09-05 19:00:00", "operador", null, null));
        agregar(new Evento(0, 406, "SIS-2026-0004", "2026-09-10 11:20:00", 3.70, 28.00, -14.067000, -75.728000, 4, 1, "CERRADO", "2026-09-10 12:00:00", "operador", null, null));
        agregar(new Evento(0, 562, "SIS-2026-0005", "2026-09-15 22:05:00", 5.10, 52.00, -18.014000, -70.253000, 5, 3, "EN_SEGUIMIENTO", "2026-09-15 22:30:00", "operador", null, null));
    }

    public List<Evento> listar() {
        return List.copyOf(eventos);
    }

    public Optional<Evento> buscarCod(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return eventos.stream()
                .filter(e -> e.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst();
    }

    public Optional<Evento> buscarId(int id) {
        return eventos.stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    public boolean existCodigo(String codigo) {
        return buscarCod(codigo).isPresent();
    }

    public Evento agregar(Evento evento) {
        evento.setId(siguienteId++);
        eventos.add(evento);
        return evento;
    }

    public boolean actualizar(Evento evento) {
        Optional<Evento> actual = buscarId(evento.getId());
        if (actual.isEmpty()) {
            return false;
        }
        int indice = eventos.indexOf(actual.get());
        eventos.set(indice, evento);
        return true;
    }

    public boolean eliminar(int id) {
        return eventos.removeIf(e -> e.getId() == id);
    }
}
