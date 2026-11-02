package pe.edu.cssp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.repository.TipoDanoRepository;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.TipoDanoForm;

@Service
public class TipoDanoService {
    private static final List<String> SECTORES = List.of("SALUD", "VIVIENDA", "EDUCACION", "INFRAESTRUCTURA", "SERVICIOS_BASICOS", "AGROPECUARIO", "POBLACION");
    private final TipoDanoRepository tipoDanoRepository;
    public TipoDanoService(TipoDanoRepository tipoDanoRepository) { this.tipoDanoRepository = tipoDanoRepository; }

    @Transactional(readOnly = true)
    public List<TipoDano> listar() { return tipoDanoRepository.findAllByOrderByCodigoAsc(); }

    @Transactional(readOnly = true)
    public TipoDano buscarPorId(int id) {
        return tipoDanoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de daño solicitado."));
    }

    @Transactional
    public TipoDano registrar(TipoDanoForm form) {
        String codigo = form.getCodigo().trim();
        if (tipoDanoRepository.findByCodigoIgnoreCase(codigo).isPresent()) {
            throw new ReglaNegocioException("El código del tipo de daño ya está registrado.");
        }
        TipoDano tipoDano = new TipoDano();
        tipoDano.setCreacionFecha(UbicacionService.fecha());
        tipoDano.setCreacionUsuario("sistema");
        completar(tipoDano, form, codigo);
        return tipoDanoRepository.save(tipoDano);
    }

    @Transactional
    public TipoDano actualizar(int id, TipoDanoForm form) {
        TipoDano tipoDano = buscarPorId(id);
        String codigo = form.getCodigo().trim();
        tipoDanoRepository.findByCodigoIgnoreCase(codigo).ifPresent(otro -> {
            if (otro.getId() != id) throw new ReglaNegocioException("El código ya pertenece a otro tipo de daño.");
        });
        tipoDano.setActualizacionFecha(UbicacionService.fecha());
        tipoDano.setActualizacionUsuario("sistema");
        completar(tipoDano, form, codigo);
        return tipoDanoRepository.save(tipoDano);
    }

    @Transactional
    public void eliminar(int id) {
        TipoDano tipoDano = buscarPorId(id);
        if ("INACTIVO".equals(tipoDano.getEstado())) {
            throw new ReglaNegocioException("El tipo de daño ya fue eliminado.");
        }
        tipoDano.setEstado("INACTIVO");
        tipoDano.setActualizacionFecha(UbicacionService.fecha());
        tipoDano.setActualizacionUsuario("sistema");
        tipoDanoRepository.save(tipoDano);
    }

    private void completar(TipoDano tipoDano, TipoDanoForm form, String codigo) {
        if (!SECTORES.contains(form.getSector())) {
            throw new ReglaNegocioException("El sector no es válido.");
        }
        tipoDano.setCodigo(codigo);
        tipoDano.setNombre(form.getNombre().trim());
        tipoDano.setSector(form.getSector());
        tipoDano.setDescripcion(form.getDescripcion() == null || form.getDescripcion().isBlank() ? null : form.getDescripcion().trim());
        tipoDano.setEstado(form.getEstado());
    }
}