package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UbicacionActivaValidator.class)
public @interface UbicacionActiva {
    String message() default "Seleccione una ubicación activa.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
