package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.UbicacionRepository;

@Component
public class UbicacionActivaValidator implements ConstraintValidator<UbicacionActiva, Integer> {
    private final UbicacionRepository ubicacionRepository;
    public UbicacionActivaValidator(UbicacionRepository ubicacionRepository) { this.ubicacionRepository = ubicacionRepository; }
    @Override public boolean isValid(Integer id, ConstraintValidatorContext context) {
        return id == null || ubicacionRepository.findById(id).map(u -> "ACTIVO".equals(u.getEstado())).orElse(false);
    }
}
