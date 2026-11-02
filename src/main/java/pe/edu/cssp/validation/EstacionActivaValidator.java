package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.EstacionRepository;

@Component
public class EstacionActivaValidator implements ConstraintValidator<EstacionActiva, Integer> {
    private final EstacionRepository estacionRepository;
    public EstacionActivaValidator(EstacionRepository estacionRepository) { this.estacionRepository = estacionRepository; }
    @Override public boolean isValid(Integer id, ConstraintValidatorContext context) {
        return id == null || estacionRepository.findById(id).map(e -> "ACTIVA".equals(e.getEstado())).orElse(false);
    }
}