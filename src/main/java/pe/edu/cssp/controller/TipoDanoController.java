package pe.edu.cssp.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.service.TipoDanoService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.TipoDanoForm;

@Controller
@RequestMapping("/tipos-dano")
public class TipoDanoController {
    private final TipoDanoService tipoDanoService;
    public TipoDanoController(TipoDanoService tipoDanoService) { this.tipoDanoService = tipoDanoService; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tipos", tipoDanoService.listar());
        return "tiposdano/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("tipoDano", tipoDanoService.buscarPorId(id));
        return "tiposdano/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new TipoDanoForm(), null);
        return "tiposdano/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute TipoDanoForm tipoDanoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, tipoDanoForm, null); return "tiposdano/formulario"; }
        try {
            TipoDano creado = tipoDanoService.registrar(tipoDanoForm);
            redirect.addFlashAttribute("exito", "Tipo de daño registrado correctamente.");
            return "redirect:/tipos-dano/" + creado.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, tipoDanoForm, null);
            model.addAttribute("error", error.getMessage());
            return "tiposdano/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        TipoDano t = tipoDanoService.buscarPorId(id);
        TipoDanoForm f = new TipoDanoForm();
        f.setId(id);
        f.setCodigo(t.getCodigo());
        f.setNombre(t.getNombre());
        f.setSector(t.getSector());
        f.setDescripcion(t.getDescripcion());
        f.setEstado(t.getEstado());
        cargarFormulario(model, f, id);
        return "tiposdano/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute TipoDanoForm tipoDanoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, tipoDanoForm, id); return "tiposdano/formulario"; }
        try {
            tipoDanoService.actualizar(id, tipoDanoForm);
            redirect.addFlashAttribute("exito", "Tipo de daño actualizado correctamente.");
            return "redirect:/tipos-dano/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, tipoDanoForm, id);
            model.addAttribute("error", error.getMessage());
            return "tiposdano/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("tipoDano", tipoDanoService.buscarPorId(id));
        return "tiposdano/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            tipoDanoService.eliminar(id);
            redirect.addFlashAttribute("exito", "El tipo de daño fue eliminado correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/tipos-dano";
    }

    private void cargarFormulario(Model model, TipoDanoForm form, Integer id) {
        model.addAttribute("tipoDanoForm", form);
        model.addAttribute("tipoDanoId", id);
    }
}
