package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodigoAfectacionNivelDisponibleValidator.class)
public @interface CodigoAfectacionNivelDisponible {
    String message() default "El código del nivel de afectación ya está registrado.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
