package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventoVigenteValidator.class)
public @interface EventoVigente {
    String message() default "Seleccione un sismo vigente (existente y no eliminado).";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
