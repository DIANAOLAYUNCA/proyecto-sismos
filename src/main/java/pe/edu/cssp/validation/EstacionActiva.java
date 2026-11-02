package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EstacionActivaValidator.class)
public @interface EstacionActiva {
    String message() default "Seleccione una estación activa.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}