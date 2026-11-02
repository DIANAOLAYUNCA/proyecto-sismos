package pe.edu.cssp.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.service.EventoService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.EventoForm;

@Controller
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService eventoService;
    public EventoController(EventoService eventoService) { this.eventoService = eventoService; }

    @GetMapping
    public String listar(@RequestParam(required = false) String codigo,
                         @RequestParam(required = false) String fecha,
                         @RequestParam(required = false) Double magnitudMinima,
                         @RequestParam(required = false) String estado,
                         @RequestParam(required = false) Integer ubicacionId,
                         Model model) {
        model.addAttribute("eventos", eventoService.listar(codigo, fecha, magnitudMinima, estado, ubicacionId));
        model.addAttribute("ubicaciones", eventoService.ubicacionesActivas());
        model.addAttribute("codigoBuscado", codigo);
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("magnitudMinima", magnitudMinima);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("ubicacionSeleccionada", ubicacionId);
        return "sismos/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("evento", eventoService.buscarPorId(id));
        return "sismos/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new EventoForm(), null);
        return "sismos/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute EventoForm eventoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, eventoForm, null); return "sismos/formulario"; }
        try {
            Evento creado = eventoService.registrar(eventoForm);
            redirect.addFlashAttribute("exito", "Sismo registrado correctamente.");
            return "redirect:/eventos/" + creado.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, eventoForm, null);
            model.addAttribute("error", error.getMessage());
            return "sismos/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        Evento e = eventoService.buscarPorId(id);
        EventoForm f = new EventoForm();
        f.setId(id);
        f.setEstacionId(e.getEstacion().getId());
        f.setCodigo(e.getCodigo());
        f.setFechaHora(e.getFechaHora().replace(' ', 'T').substring(0, 16));
        f.setMagnitud(BigDecimal.valueOf(e.getMagnitud()));
        f.setProfundidad(BigDecimal.valueOf(e.getProfundidad()));
        f.setLatitud(BigDecimal.valueOf(e.getLatitud()));
        f.setLongitud(BigDecimal.valueOf(e.getLongitud()));
        f.setReferencia(e.getReferencia());
        f.setUbicacionId(e.getUbicacion().getId());
        f.setTipoAlertaId(e.getTipoAlerta().getId());
        if (e.getAfectacionNivel() != null) f.setAfectacionNivelId(e.getAfectacionNivel().getId());
        f.setEstado(e.getEstado());
        cargarFormulario(model, f, id);
        return "sismos/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute EventoForm eventoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, eventoForm, id); return "sismos/formulario"; }
        try {
            eventoService.actualizar(id, eventoForm);
            redirect.addFlashAttribute("exito", "Sismo actualizado correctamente.");
            return "redirect:/eventos/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, eventoForm, id);
            model.addAttribute("error", error.getMessage());
            return "sismos/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("evento", eventoService.buscarPorId(id));
        return "sismos/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            eventoService.eliminar(id);
            redirect.addFlashAttribute("exito", "Sismo eliminado correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/eventos";
    }

    private void cargarFormulario(Model model, EventoForm form, Integer id) {
        model.addAttribute("eventoForm", form);
        model.addAttribute("eventoId", id);
        model.addAttribute("estaciones", eventoService.estaciones());
        model.addAttribute("ubicaciones", eventoService.ubicacionesActivas());
        model.addAttribute("alertas", eventoService.alertasActivas());
        model.addAttribute("niveles", eventoService.niveles());
    }
}