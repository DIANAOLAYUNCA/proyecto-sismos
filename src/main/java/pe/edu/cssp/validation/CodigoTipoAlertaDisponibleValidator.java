package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.TipoAlertaRepository;
import pe.edu.cssp.web.form.TipoAlertaForm;

@Component
public class CodigoTipoAlertaDisponibleValidator implements ConstraintValidator<CodigoTipoAlertaDisponible, TipoAlertaForm> {
    private final TipoAlertaRepository tipoAlertaRepository;
    public CodigoTipoAlertaDisponibleValidator(TipoAlertaRepository tipoAlertaRepository) { this.tipoAlertaRepository = tipoAlertaRepository; }
    @Override public boolean isValid(TipoAlertaForm form, ConstraintValidatorContext context) {
        if (form == null || form.getCodigo() == null || form.getCodigo().isBlank()) return true;
        boolean disponible = tipoAlertaRepository.findByCodigoIgnoreCase(form.getCodigo().trim())
                .map(t -> form.getId() != null && t.getId() == form.getId()).orElse(true);
        if (!disponible) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("codigo").addConstraintViolation();
        }
        return disponible;
    }
}
