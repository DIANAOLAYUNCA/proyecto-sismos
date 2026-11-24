package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.DistritoRepository;
import pe.edu.cssp.web.form.DistritoForm;

@Component
public class CodigoDistritoDisponibleValidator implements ConstraintValidator<CodigoDistritoDisponible, DistritoForm> {
    private final DistritoRepository distritoRepository;
    public CodigoDistritoDisponibleValidator(DistritoRepository distritoRepository) { this.distritoRepository = distritoRepository; }
    @Override public boolean isValid(DistritoForm form, ConstraintValidatorContext context) {
        if (form == null || form.getProvinciaId() == null || form.getCodigo() == null || form.getCodigo().isBlank()) return true;
        boolean disponible = distritoRepository.findByProvincia_IdAndCodigo(form.getProvinciaId(), form.getCodigo().trim())
                .map(d -> form.getId() != null && d.getId() == form.getId()).orElse(true);
        if (!disponible) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("codigo").addConstraintViolation();
        }
        return disponible;
    }
}
