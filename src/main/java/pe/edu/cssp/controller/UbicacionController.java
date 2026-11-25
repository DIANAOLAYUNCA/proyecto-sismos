package pe.edu.cssp.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.service.UbicacionService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.UbicacionForm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import pe.edu.cssp.web.paginacion.PaginaVista;
import pe.edu.cssp.web.paginacion.Paginacion;

@Controller
@RequestMapping("/ubicaciones")
public class UbicacionController {
    private static final Map<String, String> ORDEN = Map.of(
            "id", "id",
            "codigo", "codigo,id",
            "nombre", "nombre,id",
            "tipo", "tipo,codigo",
            "estado", "estado,codigo");

    private final UbicacionService ubicacionService;
    public UbicacionController(UbicacionService ubicacionService) { this.ubicacionService = ubicacionService; }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String texto,
                         @RequestParam(required = false) String estado,
                         @RequestParam(required = false) String tipo,
                         @RequestParam(required = false) Integer pagina,
                         @RequestParam(required = false) Integer tamano,
                         @RequestParam(required = false) String orden,
                         @RequestParam(required = false) String dir,
                         HttpServletRequest request, Model model) {
        Paginacion.Pedido pedido = Paginacion.pedido(pagina, tamano, orden, dir, ORDEN, "codigo");
        model.addAttribute("pagina", new PaginaVista<>(ubicacionService.buscar(texto, estado, tipo, pedido.pageable()), pedido, request));
        model.addAttribute("textoBuscado", texto);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("tipoSeleccionado", tipo);
        return "ubicaciones/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("ubicacion", ubicacionService.buscarPorId(id));
        return "ubicaciones/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new UbicacionForm(), null);
        return "ubicaciones/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute UbicacionForm ubicacionForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, ubicacionForm, null); return "ubicaciones/formulario"; }
        try {
            Ubicacion creada = ubicacionService.registrar(ubicacionForm);
            redirect.addFlashAttribute("exito", "Ubicación registrada correctamente.");
            return "redirect:/ubicaciones/" + creada.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, ubicacionForm, null);
            model.addAttribute("error", error.getMessage());
            return "ubicaciones/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        Ubicacion u = ubicacionService.buscarPorId(id);
        UbicacionForm f = new UbicacionForm();
        f.setId(id);
        f.setCodigo(u.getCodigo());
        f.setNombre(u.getNombre());
        f.setTipo(u.getTipo());
        f.setEstado(u.getEstado());
        cargarFormulario(model, f, id);
        return "ubicaciones/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute UbicacionForm ubicacionForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, ubicacionForm, id); return "ubicaciones/formulario"; }
        try {
            ubicacionService.actualizar(id, ubicacionForm);
            redirect.addFlashAttribute("exito", "Ubicación actualizada correctamente.");
            return "redirect:/ubicaciones/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, ubicacionForm, id);
            model.addAttribute("error", error.getMessage());
            return "ubicaciones/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("ubicacion", ubicacionService.buscarPorId(id));
        return "ubicaciones/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            ubicacionService.eliminar(id);
            redirect.addFlashAttribute("exito", "La ubicación fue eliminada correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/ubicaciones";
    }

    private void cargarFormulario(Model model, UbicacionForm form, Integer id) {
        model.addAttribute("ubicacionForm", form);
        model.addAttribute("ubicacionId", id);
    }
}