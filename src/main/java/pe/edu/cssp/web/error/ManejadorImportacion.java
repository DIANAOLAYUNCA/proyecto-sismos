package pe.edu.cssp.web.error;

import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pe.edu.cssp.service.importacion.TipoCatalogo;

@ControllerAdvice
public class ManejadorImportacion {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String archivoMuyGrande(Model model) {
        model.addAttribute("catalogos", TipoCatalogo.values());
        model.addAttribute("resultados", List.of());
        model.addAttribute("error", "El archivo supera el tamaño máximo permitido (1 MB).");
        return "importacion/index";
    }
}
