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

@Controller
@RequestMapping("/ubicaciones")
public class UbicacionController {
    private final UbicacionService ubicacionService;
    public UbicacionController(UbicacionService ubicacionService) { this.ubicacionService = ubicacionService; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ubicaciones", ubicacionService.listar());
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