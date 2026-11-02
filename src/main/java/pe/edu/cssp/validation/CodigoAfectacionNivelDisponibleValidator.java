package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.AfectacionNivelRepository;
import pe.edu.cssp.web.form.AfectacionNivelForm;

@Component
public class CodigoAfectacionNivelDisponibleValidator implements ConstraintValidator<CodigoAfectacionNivelDisponible, AfectacionNivelForm> {
    private final AfectacionNivelRepository afectacionNivelRepository;
    public CodigoAfectacionNivelDisponibleValidator(AfectacionNivelRepository afectacionNivelRepository) { this.afectacionNivelRepository = afectacionNivelRepository; }
    @Override public boolean isValid(AfectacionNivelForm form, ConstraintValidatorContext context) {
        if (form == null || form.getCodigo() == null || form.getCodigo().isBlank()) return true;
        boolean disponible = afectacionNivelRepository.findByCodigoIgnoreCase(form.getCodigo().trim())
                .map(n -> form.getId() != null && n.getId() == form.getId()).orElse(true);
        if (!disponible) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("codigo").addConstraintViolation();
        }
        return disponible;
    }
}
