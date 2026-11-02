package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodigoTipoDanoDisponibleValidator.class)
public @interface CodigoTipoDanoDisponible {
    String message() default "El código del tipo de daño ya está registrado.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
