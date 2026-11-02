package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.UbicacionRepository;
import pe.edu.cssp.web.form.UbicacionForm;

@Component
public class CodigoUbicacionDisponibleValidator implements ConstraintValidator<CodigoUbicacionDisponible, UbicacionForm> {
    private final UbicacionRepository ubicacionRepository;
    public CodigoUbicacionDisponibleValidator(UbicacionRepository ubicacionRepository) { this.ubicacionRepository = ubicacionRepository; }
    @Override public boolean isValid(UbicacionForm form, ConstraintValidatorContext context) {
        if (form == null || form.getCodigo() == null || form.getCodigo().isBlank()) return true;
        boolean disponible = ubicacionRepository.findByCodigoIgnoreCase(form.getCodigo().trim())
                .map(u -> form.getId() != null && u.getId() == form.getId()).orElse(true);
        if (!disponible) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("codigo").addConstraintViolation();
        }
        return disponible;
    }
}
