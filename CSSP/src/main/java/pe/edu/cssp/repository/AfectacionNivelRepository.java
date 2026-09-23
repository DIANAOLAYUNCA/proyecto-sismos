package pe.edu.cssp.repository;

import pe.edu.cssp.model.AfectacionNivel;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class AfectacionNivelRepository {

    private final List<AfectacionNivel> niveles = new CopyOnWriteArrayList<>();
    private int siguienteId = 1;

    public AfectacionNivelRepository() {
        agregar(new AfectacionNivel(0, "Imperceptible", "IMP", "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new AfectacionNivel(0, "Leve", "LEV", "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new AfectacionNivel(0, "Moderado", "MOD", "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new AfectacionNivel(0, "Fuerte", "FUE", "2026-01-01 08:00:00", "sistema", null, null));
        agregar(new AfectacionNivel(0, "Catastrófico", "CAT", "2026-01-01 08:00:00", "sistema", null, null));
    }

    public List<AfectacionNivel> listar() {
        return List.copyOf(niveles);
    }

    public Optional<AfectacionNivel> buscarId(int id) {
        return niveles.stream()
                .filter(n -> n.getId() == id)
                .findFirst();
    }

    public Optional<AfectacionNivel> buscarCod(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return niveles.stream()
                .filter(n -> n.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst();
    }

    public boolean existCodigo(String codigo) {
        return buscarCod(codigo).isPresent();
    }

    public AfectacionNivel agregar(AfectacionNivel nivel) {
        nivel.setId(siguienteId++);
        niveles.add(nivel);
        return nivel;
    }

    public boolean actualizar(AfectacionNivel nivel) {
        Optional<AfectacionNivel> actual = buscarId(nivel.getId());
        if (actual.isEmpty()) {
            return false;
        }
        int indice = niveles.indexOf(actual.get());
        niveles.set(indice, nivel);
        return true;
    }

    public boolean eliminar(int id) {
        return niveles.removeIf(n -> n.getId() == id);
    }
}
