package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.web.form.EstacionForm;

@Component
public class CodigoEstacionDisponibleValidator implements ConstraintValidator<CodigoEstacionDisponible, EstacionForm> {
    private final EstacionRepository estacionRepository;
    public CodigoEstacionDisponibleValidator(EstacionRepository estacionRepository) { this.estacionRepository = estacionRepository; }
    @Override public boolean isValid(EstacionForm form, ConstraintValidatorContext context) {
        if (form == null || form.getCodigo() == null || form.getCodigo().isBlank()) return true;
        boolean disponible = estacionRepository.findByCodigoIgnoreCase(form.getCodigo().trim())
                .map(e -> form.getId() != null && e.getId() == form.getId()).orElse(true);
        if (!disponible) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("codigo").addConstraintViolation();
        }
        return disponible;
    }
}
