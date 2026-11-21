package pe.edu.cssp.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.cssp.dto.reporte.EventosPorAlertaDto;
import pe.edu.cssp.dto.reporte.SismoMensualDto;
import pe.edu.cssp.service.DashboardService;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard", "/reportes"})
    public String mostrar(Model model) {
        List<SismoMensualDto> sismosMensuales = dashboardService.sismosUltimosSeisMeses();
        List<EventosPorAlertaDto> alertas = dashboardService.eventosPorAlerta();

        model.addAttribute("kpis", dashboardService.indicadores());
        model.addAttribute("sismosAlertaAlta", dashboardService.sismosAlertaAlta());
        model.addAttribute("danosPorTipo", dashboardService.danosPorTipo());
        model.addAttribute("etiquetasMeses", sismosMensuales.stream().map(dashboardService::etiquetaMes).toList());
        model.addAttribute("valoresSismos", sismosMensuales.stream().map(SismoMensualDto::getTotal).toList());
        model.addAttribute("etiquetasAlerta", alertas.stream().map(EventosPorAlertaDto::getAlerta).toList());
        model.addAttribute("valoresAlerta", alertas.stream().map(EventosPorAlertaDto::getCantidad).toList());

        return "dashboard/index";
    }

    @GetMapping("/modulos")
    public String modulos() {
        return "modulos";
    }
}
