package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodigoUbicacionDisponibleValidator.class)
public @interface CodigoUbicacionDisponible {
    String message() default "El código de la ubicación ya está registrado.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
