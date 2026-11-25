package pe.edu.cssp.dto.reporte;

/** Ficha técnica de un KPI: qué mide, cómo se calcula, de dónde salen los datos y qué decisión apoya. */
public record FichaIndicadorDto(String nombre, String queMide, String calculo, String fuente, String decision) { }
