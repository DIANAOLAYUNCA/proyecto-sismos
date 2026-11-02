package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.TipoDanoRepository;

@Component
public class TipoDanoActivoValidator implements ConstraintValidator<TipoDanoActivo, Integer> {
    private final TipoDanoRepository tipoDanoRepository;
    public TipoDanoActivoValidator(TipoDanoRepository tipoDanoRepository) { this.tipoDanoRepository = tipoDanoRepository; }
    @Override public boolean isValid(Integer id, ConstraintValidatorContext context) {
        return id == null || tipoDanoRepository.findById(id).map(t -> "ACTIVO".equals(t.getEstado())).orElse(false);
    }
}
