package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TipoAlertaActivaValidator.class)
public @interface TipoAlertaActiva {
    String message() default "Seleccione un tipo de alerta activo.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}