package pe.edu.cssp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.cssp.dto.reporte.EventosPorAlertaDto;
import pe.edu.cssp.dto.reporte.SismoMensualDto;
import pe.edu.cssp.service.DashboardService;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** Filtros: ubicación geográfica (departamento) y periodo en meses. Sin parámetros: todo el Perú, últimos 6 meses. */
    @GetMapping({"/", "/dashboard", "/reportes"})
    public String mostrar(@RequestParam(required = false) Integer ubicacionId,
                          @RequestParam(required = false) Integer meses,
                          Model model) {
        DashboardService.Filtro filtro = DashboardService.Filtro.de(ubicacionId, meses);
        DashboardService.Datos datos = dashboardService.calcular(filtro);

        model.addAttribute("kpis", datos.kpis());
        model.addAttribute("fichas", dashboardService.fichas());
        model.addAttribute("sismosAlertaAlta", datos.sismosAlertaAlta());
        model.addAttribute("danosPorTipo", datos.danosPorTipo());
        model.addAttribute("etiquetasMeses", datos.sismosPorMes().stream().map(dashboardService::etiquetaMes).toList());
        model.addAttribute("valoresSismos", datos.sismosPorMes().stream().map(SismoMensualDto::getTotal).toList());
        model.addAttribute("etiquetasAlerta", datos.eventosPorAlerta().stream().map(EventosPorAlertaDto::getAlerta).toList());
        model.addAttribute("valoresAlerta", datos.eventosPorAlerta().stream().map(EventosPorAlertaDto::getCantidad).toList());

        model.addAttribute("ubicaciones", dashboardService.ubicaciones());
        model.addAttribute("periodos", DashboardService.PERIODOS);
        model.addAttribute("ubicacionSeleccionada", filtro.ubicacionId());
        model.addAttribute("mesesSeleccionados", filtro.meses());
        model.addAttribute("magnitudAlta", DashboardService.MAGNITUD_ALTA);
        return "dashboard/index";
    }

    @GetMapping("/modulos")
    public String modulos() {
        return "modulos";
    }
}
