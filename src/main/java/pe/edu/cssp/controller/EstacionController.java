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
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import pe.edu.cssp.web.paginacion.PaginaVista;
import pe.edu.cssp.web.paginacion.Paginacion;

@Controller
@RequestMapping("/estaciones")
public class EstacionController {
    /** Columna pública -> propiedades de la consulta (u = ubicación, di = distrito). */
    private static final Map<String, String> ORDEN = Map.of(
            "id", "id",
            "codigo", "codigo,id",
            "nombre", "nombre,id",
            "ubicacion", "u.nombre,id",
            "distrito", "di.nombre,id",
            "estado", "estado,id");

    private final EstacionService estacionService;
    public EstacionController(EstacionService estacionService) { this.estacionService = estacionService; }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String texto,
                         @RequestParam(required = false) String estado,
                         @RequestParam(required = false) Integer ubicacionId,
                         @RequestParam(required = false) Integer pagina,
                         @RequestParam(required = false) Integer tamano,
                         @RequestParam(required = false) String orden,
                         @RequestParam(required = false) String dir,
                         HttpServletRequest request, Model model) {
        Paginacion.Pedido pedido = Paginacion.pedido(pagina, tamano, orden, dir, ORDEN, "id");
        model.addAttribute("pagina", new PaginaVista<>(estacionService.buscar(texto, estado, ubicacionId, pedido.pageable()), pedido, request));
        model.addAttribute("ubicaciones", estacionService.ubicacionesActivas());
        model.addAttribute("textoBuscado", texto);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("ubicacionSeleccionada", ubicacionId);
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
        if (e.getDistrito() != null) f.setDistritoId(e.getDistrito().getId());
        f.setLatitud(e.getLatitud());
        f.setLongitud(e.getLongitud());
        f.setInstalacionFecha(e.getInstalacionFecha() != null ? e.getInstalacionFecha().toString() : null);
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
        model.addAttribute("distritos", estacionService.distritosDisponibles(form.getUbicacionId()));
    }
}