package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TipoDanoActivoValidator.class)
public @interface TipoDanoActivo {
    String message() default "Seleccione un tipo de daño activo.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
