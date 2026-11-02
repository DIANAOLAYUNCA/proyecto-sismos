package pe.edu.cssp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pe.edu.cssp.repository.EventoRepository;

@Component
public class EventoVigenteValidator implements ConstraintValidator<EventoVigente, Integer> {
    private final EventoRepository eventoRepository;
    public EventoVigenteValidator(EventoRepository eventoRepository) { this.eventoRepository = eventoRepository; }
    @Override public boolean isValid(Integer id, ConstraintValidatorContext context) {
        return id == null || eventoRepository.findById(id).map(e -> !"INACTIVO".equals(e.getEstado())).orElse(false);
    }
}
