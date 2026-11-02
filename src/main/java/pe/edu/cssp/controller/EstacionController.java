package pe.edu.cssp.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.Estacion;
import pe.edu.cssp.service.EstacionService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.EstacionForm;

@Controller
@RequestMapping("/estaciones")
public class EstacionController {
    private final EstacionService estacionService;
    public EstacionController(EstacionService estacionService) { this.estacionService = estacionService; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("estaciones", estacionService.listar());
        return "estaciones/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("estacion", estacionService.buscarPorId(id));
        return "estaciones/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new EstacionForm(), null);
        return "estaciones/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute EstacionForm estacionForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, estacionForm, null); return "estaciones/formulario"; }
        try {
            Estacion creada = estacionService.registrar(estacionForm);
            redirect.addFlashAttribute("exito", "Estación registrada correctamente.");
            return "redirect:/estaciones/" + creada.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, estacionForm, null);
            model.addAttribute("error", error.getMessage());
            return "estaciones/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        Estacion e = estacionService.buscarPorId(id);
        EstacionForm f = new EstacionForm();
        f.setId(id);
        f.setCodigo(e.getCodigo());
        f.setNombre(e.getNombre());
        f.setUbicacionId(e.getUbicacion().getId());
        f.setLatitud(e.getLatitud());
        f.setLongitud(e.getLongitud());
        f.setInstalacionFecha(e.getInstalacionFecha());
        f.setEstado(e.getEstado());
        cargarFormulario(model, f, id);
        return "estaciones/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute EstacionForm estacionForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, estacionForm, id); return "estaciones/formulario"; }
        try {
            estacionService.actualizar(id, estacionForm);
            redirect.addFlashAttribute("exito", "Estación actualizada correctamente.");
            return "redirect:/estaciones/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, estacionForm, id);
            model.addAttribute("error", error.getMessage());
            return "estaciones/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("estacion", estacionService.buscarPorId(id));
        return "estaciones/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            estacionService.eliminar(id);
            redirect.addFlashAttribute("exito", "La estación fue dada de baja correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/estaciones";
    }

    private void cargarFormulario(Model model, EstacionForm form, Integer id) {
        model.addAttribute("estacionForm", form);
        model.addAttribute("estacionId", id);
        model.addAttribute("ubicaciones", estacionService.ubicacionesActivas());
    }
}