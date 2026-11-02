package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.TipoDanoRepository;
import pe.edu.cssp.web.form.TipoDanoForm;

@Component
public class CodigoTipoDanoDisponibleValidator implements ConstraintValidator<CodigoTipoDanoDisponible, TipoDanoForm> {
    private final TipoDanoRepository tipoDanoRepository;
    public CodigoTipoDanoDisponibleValidator(TipoDanoRepository tipoDanoRepository) { this.tipoDanoRepository = tipoDanoRepository; }
    @Override public boolean isValid(TipoDanoForm form, ConstraintValidatorContext context) {
        if (form == null || form.getCodigo() == null || form.getCodigo().isBlank()) return true;
        boolean disponible = tipoDanoRepository.findByCodigoIgnoreCase(form.getCodigo().trim())
                .map(t -> form.getId() != null && t.getId() == form.getId()).orElse(true);
        if (!disponible) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("codigo").addConstraintViolation();
        }
        return disponible;
    }
}
