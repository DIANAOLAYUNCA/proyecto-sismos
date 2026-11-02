package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.web.form.EventoForm;

@Component
public class CodigoEventoDisponibleValidator implements ConstraintValidator<CodigoEventoDisponible, EventoForm> {
    private final EventoRepository eventoRepository;
    public CodigoEventoDisponibleValidator(EventoRepository eventoRepository) { this.eventoRepository = eventoRepository; }
    @Override public boolean isValid(EventoForm form, ConstraintValidatorContext context) {
        if (form == null || form.getCodigo() == null || form.getCodigo().isBlank()) return true;
        boolean disponible = eventoRepository.findByCodigoIgnoreCase(form.getCodigo().trim())
                .map(e -> form.getId() != null && e.getId() == form.getId()).orElse(true);
        if (!disponible) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("codigo").addConstraintViolation();
        }
        return disponible;
    }
}
