package pe.edu.cssp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cssp.entity.Distrito;
import pe.edu.cssp.service.DistritoService;
import pe.edu.cssp.web.error.ReglaNegocioException;
import pe.edu.cssp.web.form.DistritoForm;
import pe.edu.cssp.web.paginacion.PaginaVista;
import pe.edu.cssp.web.paginacion.Paginacion;

@Controller
@RequestMapping("/distritos")
public class DistritoController {
    /** Columna pública -> propiedades de la consulta (d = departamento, p = provincia, sin prefijo = distrito). */
    private static final Map<String, String> ORDEN = Map.of(
            "ubigeo", "d.codigo,p.codigo,codigo",
            "codigo", "codigo,id",
            "nombre", "nombre,id",
            "provincia", "p.nombre,nombre,id",
            "departamento", "d.nombre,p.nombre,nombre,id",
            "estado", "estado,d.codigo,p.codigo,codigo");

    private final DistritoService distritoService;
    public DistritoController(DistritoService distritoService) { this.distritoService = distritoService; }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer departamentoId,
                         @RequestParam(required = false) Integer provinciaId,
                         @RequestParam(required = false) String estado,
                         @RequestParam(name = "q", required = false) String texto,
                         @RequestParam(required = false) Integer pagina,
                         @RequestParam(required = false) Integer tamano,
                         @RequestParam(required = false) String orden,
                         @RequestParam(required = false) String dir,
                         HttpServletRequest request, Model model) {
        Paginacion.Pedido pedido = Paginacion.pedido(pagina, tamano, orden, dir, ORDEN, "ubigeo");
        Page<Distrito> resultado = distritoService.buscar(departamentoId, provinciaId, estado, texto, pedido.pageable());
        model.addAttribute("pagina", new PaginaVista<>(resultado, pedido, request));
        model.addAttribute("departamentos", distritoService.departamentos());
        model.addAttribute("provincias", distritoService.provincias());
        model.addAttribute("departamentoSeleccionado", departamentoId);
        model.addAttribute("provinciaSeleccionada", provinciaId);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("textoBuscado", texto);
        return "distritos/lista";
    }

    /** Alimenta el desplegable de distrito de sismos y estaciones: solo los del departamento de la ubicación elegida. */
    @GetMapping("/por-ubicacion/{ubicacionId}")
    @ResponseBody
    public List<Map<String, Object>> porUbicacion(@PathVariable int ubicacionId) {
        return distritoService.opcionesPorUbicacion(ubicacionId).stream().map(d -> {
            Map<String, Object> opcion = new LinkedHashMap<>();
            opcion.put("id", d.getId());
            opcion.put("nombre", d.getNombre());
            opcion.put("provincia", d.getProvincia().getNombre());
            return opcion;
        }).toList();
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("distrito", distritoService.buscarPorId(id));
        return "distritos/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario(model, new DistritoForm(), null);
        return "distritos/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute DistritoForm distritoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, distritoForm, null); return "distritos/formulario"; }
        try {
            Distrito creado = distritoService.registrar(distritoForm);
            redirect.addFlashAttribute("exito", "Distrito registrado correctamente.");
            return "redirect:/distritos/" + creado.getId();
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, distritoForm, null);
            model.addAttribute("error", error.getMessage());
            return "distritos/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        Distrito d = distritoService.buscarPorId(id);
        DistritoForm f = new DistritoForm();
        f.setId(id);
        f.setProvinciaId(d.getProvincia().getId());
        f.setCodigo(d.getCodigo());
        f.setNombre(d.getNombre());
        f.setEstado(d.getEstado());
        cargarFormulario(model, f, id);
        return "distritos/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute DistritoForm distritoForm, BindingResult binding, Model model, RedirectAttributes redirect) {
        if (binding.hasErrors()) { cargarFormulario(model, distritoForm, id); return "distritos/formulario"; }
        try {
            distritoService.actualizar(id, distritoForm);
            redirect.addFlashAttribute("exito", "Distrito actualizado correctamente.");
            return "redirect:/distritos/" + id;
        } catch (ReglaNegocioException error) {
            cargarFormulario(model, distritoForm, id);
            model.addAttribute("error", error.getMessage());
            return "distritos/formulario";
        }
    }

    @GetMapping("/{id}/eliminar")
    public String confirmarEliminar(@PathVariable int id, Model model) {
        model.addAttribute("distrito", distritoService.buscarPorId(id));
        return "distritos/eliminar";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            distritoService.eliminar(id);
            redirect.addFlashAttribute("exito", "El distrito fue eliminado correctamente.");
        } catch (ReglaNegocioException error) {
            redirect.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/distritos";
    }

    private void cargarFormulario(Model model, DistritoForm form, Integer id) {
        model.addAttribute("distritoForm", form);
        model.addAttribute("distritoId", id);
        model.addAttribute("provincias", distritoService.provincias());
    }
}
