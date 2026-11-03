package pe.edu.cssp.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.ReporteDano;
import pe.edu.cssp.service.ReporteDanoService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.ReporteDanoForm;

@Controller
@RequestMapping("/reportes-dano")
public class ReporteDanoController {
    private final ReporteDanoService reporteDanoService;
    public ReporteDanoController(ReporteDanoService reporteDanoService) { this.reporteDanoService = reporteDanoService; }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer eventoId, Model model) {
        List<ReporteDano> reportes = reporteDanoService.listar(eventoId);
        model.addAttribute("reportes", reportes);
        model.addAttribute("totalAfectados", reporteDanoService.totalAfectados(reportes));
        model.addAttribute("eventos", reporteDanoService.todosLosEventos());
        model.addAttribute("eventoId", eventoId);
        return "reportesdano/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("reporte", reporteDanoService.buscarPorId(id));
        return "reportesdano/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Integer eventoId, Model model) {
        ReporteDanoForm form = new ReporteDanoForm();
        if (eventoId != null) {
            Evento evento = reporteDanoService.buscarEvento(eventoId);
            form.setEventoId(evento.getId());
            form.setUbicacionId(evento.getUbicacion().getId());
        }
        cargarFormulario(model, form, null);
        return "reportesdano/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute ReporteDanoForm reporteDanoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, reporteDanoForm, null); return "reportesdano/formulario"; }
        try {
            ReporteDano creado = reporteDanoService.registrar(reporteDanoForm);
            redirect.addFlashAttribute("exito", "Reporte de daño registrado correctamente.");
            return "redirect:/reportes-dano/" + creado.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, reporteDanoForm, null);
            model.addAttribute("error", error.getMessage());
            return "reportesdano/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        ReporteDano r = reporteDanoService.buscarPorId(id);
        ReporteDanoForm f = new ReporteDanoForm();
        f.setId(id);
        f.setEventoId(r.getEvento().getId());
        f.setTipoDanoId(r.getTipoDano().getId());
        f.setUbicacionId(r.getUbicacion().getId());
        f.setFechaHora(r.getFechaHora().substring(0, 16).replace(' ', 'T'));
        f.setCantidadAfectada(r.getCantidadAfectada());
        f.setDescripcion(r.getDescripcion());
        f.setAfectacionNivelId(r.getAfectacionNivel() == null ? null : r.getAfectacionNivel().getId());
        f.setEstado(r.getEstado());
        cargarFormulario(model, f, id);
        return "reportesdano/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute ReporteDanoForm reporteDanoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, reporteDanoForm, id); return "reportesdano/formulario"; }
        try {
            reporteDanoService.actualizar(id, reporteDanoForm);
            redirect.addFlashAttribute("exito", "Reporte de daño actualizado correctamente.");
            return "redirect:/reportes-dano/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, reporteDanoForm, id);
            model.addAttribute("error", error.getMessage());
            return "reportesdano/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("reporte", reporteDanoService.buscarPorId(id));
        return "reportesdano/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            reporteDanoService.eliminar(id);
            redirect.addFlashAttribute("exito", "El reporte de daño fue descartado correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/reportes-dano";
    }

    @PostMapping("/{id}/reactivar")
    public String reactivar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            reporteDanoService.reactivar(id);
            redirect.addFlashAttribute("exito", "El reporte de daño fue reactivado y volvió al estado REGISTRADO.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/reportes-dano/" + id;
    }

    private void cargarFormulario(Model model, ReporteDanoForm form, Integer id) {
        model.addAttribute("reporteDanoForm", form);
        model.addAttribute("reporteId", id);
        model.addAttribute("eventos", reporteDanoService.eventosVigentes());
        model.addAttribute("tiposDano", reporteDanoService.tiposDanoActivos());
        model.addAttribute("ubicaciones", reporteDanoService.ubicacionesActivas());
        model.addAttribute("niveles", reporteDanoService.niveles());
    }
}
