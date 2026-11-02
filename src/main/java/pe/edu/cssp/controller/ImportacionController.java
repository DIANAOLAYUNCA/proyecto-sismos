package pe.edu.cssp.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.cssp.service.ImportacionCatalogosService;
import pe.edu.cssp.service.importacion.ResultadoImportacion;
import pe.edu.cssp.service.importacion.TipoCatalogo;
import pe.edu.cssp.web.error.ReglaNegocioException;

@Controller
@RequestMapping("/importacion")
public class ImportacionController {
    private final ImportacionCatalogosService importacionService;
    public ImportacionController(ImportacionCatalogosService importacionService) { this.importacionService = importacionService; }

    @GetMapping
    public String index(Model model) {
        return vista(model, List.of(), null);
    }

    @PostMapping("/incluidos")
    public String importarIncluidos(Model model) {
        try {
            List<ResultadoImportacion> resultados = Arrays.stream(TipoCatalogo.values())
                    .map(importacionService::importarIncluido).toList();
            return vista(model, resultados, null);
        } catch (ReglaNegocioException error) {
            return vista(model, List.of(), error.getMessage());
        }
    }

    @PostMapping("/{catalogo}")
    public String importar(@PathVariable String catalogo, @RequestParam("archivo") MultipartFile archivo, Model model) {
        TipoCatalogo tipo = TipoCatalogo.porRuta(catalogo);
        if (archivo.isEmpty()) {
            return vista(model, List.of(), "Seleccione un archivo CSV para " + tipo.getTitulo() + ".");
        }
        String nombre = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename();
        if (!nombre.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            return vista(model, List.of(), "Solo se aceptan archivos con extensión .csv (recibido: " + nombre + ").");
        }
        try {
            ResultadoImportacion resultado = importacionService.importar(tipo, nombre, archivo.getInputStream());
            return vista(model, List.of(resultado), null);
        } catch (ReglaNegocioException error) {
            return vista(model, List.of(), error.getMessage());
        } catch (IOException error) {
            return vista(model, List.of(), "No se pudo leer el archivo.");
        }
    }

    static String vista(Model model, List<ResultadoImportacion> resultados, String error) {
        model.addAttribute("catalogos", TipoCatalogo.values());
        model.addAttribute("resultados", resultados);
        if (error != null) model.addAttribute("error", error);
        return "importacion/index";
    }
}
