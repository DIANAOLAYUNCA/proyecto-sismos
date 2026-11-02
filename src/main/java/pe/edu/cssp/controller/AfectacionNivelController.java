package pe.edu.cssp.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.service.AfectacionNivelService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.AfectacionNivelForm;

@Controller
@RequestMapping("/afectacionniveles")
public class AfectacionNivelController {
    private final AfectacionNivelService afectacionNivelService;
    public AfectacionNivelController(AfectacionNivelService afectacionNivelService) { this.afectacionNivelService = afectacionNivelService; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("niveles", afectacionNivelService.listar());
        return "afectacionniveles/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("nivel", afectacionNivelService.buscarPorId(id));
        return "afectacionniveles/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new AfectacionNivelForm(), null);
        return "afectacionniveles/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute AfectacionNivelForm afectacionNivelForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, afectacionNivelForm, null); return "afectacionniveles/formulario"; }
        try {
            AfectacionNivel creado = afectacionNivelService.registrar(afectacionNivelForm);
            redirect.addFlashAttribute("exito", "Nivel de afectación registrado correctamente.");
            return "redirect:/afectacionniveles/" + creado.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, afectacionNivelForm, null);
            model.addAttribute("error", error.getMessage());
            return "afectacionniveles/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        AfectacionNivel n = afectacionNivelService.buscarPorId(id);
        AfectacionNivelForm f = new AfectacionNivelForm();
        f.setId(id);
        f.setCodigo(n.getCodigo());
        f.setNombre(n.getNombre());
        cargarFormulario(model, f, id);
        return "afectacionniveles/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute AfectacionNivelForm afectacionNivelForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        afectacionNivelForm.setId(id);
        if (binding.hasErrors()) { cargarFormulario(model, afectacionNivelForm, id); return "afectacionniveles/formulario"; }
        try {
            afectacionNivelService.actualizar(id, afectacionNivelForm);
            redirect.addFlashAttribute("exito", "Nivel de afectación actualizado correctamente.");
            return "redirect:/afectacionniveles/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, afectacionNivelForm, id);
            model.addAttribute("error", error.getMessage());
            return "afectacionniveles/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("nivel", afectacionNivelService.buscarPorId(id));
        return "afectacionniveles/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            afectacionNivelService.eliminar(id);
            redirect.addFlashAttribute("exito", "El nivel de afectación fue eliminado correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/afectacionniveles";
    }

    private void cargarFormulario(Model model, AfectacionNivelForm form, Integer id) {
        model.addAttribute("afectacionNivelForm", form);
        model.addAttribute("nivelId", id);
    }
}
