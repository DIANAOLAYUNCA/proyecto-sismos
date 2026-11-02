package pe.edu.cssp.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.service.TipoAlertaService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.TipoAlertaForm;

@Controller
@RequestMapping("/tipos-alerta")
public class TipoAlertaController {
    private final TipoAlertaService tipoAlertaService;
    public TipoAlertaController(TipoAlertaService tipoAlertaService) { this.tipoAlertaService = tipoAlertaService; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tipos", tipoAlertaService.listar());
        return "tiposalerta/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("tipoAlerta", tipoAlertaService.buscarPorId(id));
        return "tiposalerta/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new TipoAlertaForm(), null);
        return "tiposalerta/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute TipoAlertaForm tipoAlertaForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, tipoAlertaForm, null); return "tiposalerta/formulario"; }
        try {
            TipoAlerta creado = tipoAlertaService.registrar(tipoAlertaForm);
            redirect.addFlashAttribute("exito", "Tipo de alerta registrado correctamente.");
            return "redirect:/tipos-alerta/" + creado.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, tipoAlertaForm, null);
            model.addAttribute("error", error.getMessage());
            return "tiposalerta/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        TipoAlerta t = tipoAlertaService.buscarPorId(id);
        TipoAlertaForm f = new TipoAlertaForm();
        f.setId(id);
        f.setCodigo(t.getCodigo());
        f.setNombre(t.getNombre());
        f.setColor(t.getColor());
        f.setPrioridad(t.getPrioridad());
        f.setDescripcion(t.getDescripcion());
        f.setEstado(t.getEstado());
        cargarFormulario(model, f, id);
        return "tiposalerta/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute TipoAlertaForm tipoAlertaForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, tipoAlertaForm, id); return "tiposalerta/formulario"; }
        try {
            tipoAlertaService.actualizar(id, tipoAlertaForm);
            redirect.addFlashAttribute("exito", "Tipo de alerta actualizado correctamente.");
            return "redirect:/tipos-alerta/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, tipoAlertaForm, id);
            model.addAttribute("error", error.getMessage());
            return "tiposalerta/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("tipoAlerta", tipoAlertaService.buscarPorId(id));
        return "tiposalerta/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            tipoAlertaService.eliminar(id);
            redirect.addFlashAttribute("exito", "El tipo de alerta fue eliminado correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/tipos-alerta";
    }

    private void cargarFormulario(Model model, TipoAlertaForm form, Integer id) {
        model.addAttribute("tipoAlertaForm", form);
        model.addAttribute("tipoAlertaId", id);
    }
}