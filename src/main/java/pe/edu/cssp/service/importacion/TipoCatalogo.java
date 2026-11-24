package pe.edu.cssp.service.importacion;

import java.util.List;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;

public enum TipoCatalogo {
    UBIGEO("ubigeo", "ubigeo.csv", "Ubigeo (ubicaciones)", List.of("codigo", "nombre", "tipo", "estado")),
    TIPO_ALERTA("tipo-alerta", "tipo_alerta.csv", "Tipos de alerta", List.of("codigo", "nombre", "color", "prioridad", "descripcion", "estado")),
    TIPO_DANO("tipo-dano", "tipo_dano.csv", "Tipos de daño", List.of("codigo", "nombre", "sector", "descripcion", "estado")),
    DISTRITOS("distritos", "ubigeo_distritos.csv", "Distritos (ubigeo INEI)", List.of("iddist", "nombdep", "nombprov", "nombdist"), ';', true);

    private final String ruta;
    private final String archivo;
    private final String titulo;
    private final List<String> columnas;
    private final char separador;
    private final boolean formatoInei;

    TipoCatalogo(String ruta, String archivo, String titulo, List<String> columnas) {
        this(ruta, archivo, titulo, columnas, ',', false);
    }

    /** formatoInei: separado por ";", acepta Windows-1252 e ignora las filas vacías y las notas al pie. */
    TipoCatalogo(String ruta, String archivo, String titulo, List<String> columnas, char separador, boolean formatoInei) {
        this.ruta = ruta;
        this.archivo = archivo;
        this.titulo = titulo;
        this.columnas = columnas;
        this.separador = separador;
        this.formatoInei = formatoInei;
    }

    public String getRuta() { return ruta; }
    public String getArchivo() { return archivo; }
    public String getTitulo() { return titulo; }
    public List<String> getColumnas() { return columnas; }
    public char getSeparador() { return separador; }
    public boolean isFormatoInei() { return formatoInei; }

    public String getFormato() {
        return formatoInei
                ? "CSV del INEI separado por punto y coma (acepta Excel/ISO-8859-1 o UTF-8); se leen solo estas columnas y se ignoran las demás"
                : "Archivo en UTF-8 separado por comas";
    }

    public static TipoCatalogo porRuta(String ruta) {
        for (TipoCatalogo tipo : values()) {
            if (tipo.ruta.equals(ruta)) return tipo;
        }
        throw new RecursoNoEncontradoException("No existe el catálogo solicitado.");
    }
}
