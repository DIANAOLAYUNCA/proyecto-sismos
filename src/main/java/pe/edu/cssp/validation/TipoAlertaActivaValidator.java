package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.TipoAlertaRepository;

@Component
public class TipoAlertaActivaValidator implements ConstraintValidator<TipoAlertaActiva, Integer> {
    private final TipoAlertaRepository tipoAlertaRepository;
    public TipoAlertaActivaValidator(TipoAlertaRepository tipoAlertaRepository) { this.tipoAlertaRepository = tipoAlertaRepository; }
    @Override public boolean isValid(Integer id, ConstraintValidatorContext context) {
        return id == null || tipoAlertaRepository.findById(id).map(a -> "ACTIVO".equals(a.getEstado())).orElse(false);
    }
}
