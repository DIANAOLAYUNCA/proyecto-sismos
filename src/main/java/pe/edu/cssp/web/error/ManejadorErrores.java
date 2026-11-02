package pe.edu.cssp.web.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManejadorErrores {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String noEncontrado(RecursoNoEncontradoException error, Model model) {
        model.addAttribute("mensaje", error.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String integridad(DataIntegrityViolationException error, Model model) {
        model.addAttribute("mensaje", "No se pudo guardar la operación porque los datos incumplen una regla de integridad.");
        return "error/negocio";
    }
}

