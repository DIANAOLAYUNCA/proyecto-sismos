package pe.edu.cssp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodigoDistritoDisponibleValidator.class)
public @interface CodigoDistritoDisponible {
    String message() default "Ya existe un distrito con ese código en la provincia seleccionada.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
