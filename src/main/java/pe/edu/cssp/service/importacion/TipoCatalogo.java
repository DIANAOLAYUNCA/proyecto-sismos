package pe.edu.cssp.service.importacion;

import java.util.List;
import pe.edu.cssp.web.error.RecursoNoEncontradoException;

public enum TipoCatalogo {
    UBIGEO("ubigeo", "ubigeo.csv", "Ubigeo (ubicaciones)", List.of("codigo", "nombre", "tipo", "estado")),
    TIPO_ALERTA("tipo-alerta", "tipo_alerta.csv", "Tipos de alerta", List.of("codigo", "nombre", "color", "prioridad", "descripcion", "estado")),
    TIPO_DANO("tipo-dano", "tipo_dano.csv", "Tipos de daño", List.of("codigo", "nombre", "sector", "descripcion", "estado"));

    private final String ruta;
    private final String archivo;
    private final String titulo;
    private final List<String> columnas;

    TipoCatalogo(String ruta, String archivo, String titulo, List<String> columnas) {
        this.ruta = ruta;
        this.archivo = archivo;
        this.titulo = titulo;
        this.columnas = columnas;
    }

    public String getRuta() { return ruta; }
    public String getArchivo() { return archivo; }
    public String getTitulo() { return titulo; }
    public List<String> getColumnas() { return columnas; }

    public static TipoCatalogo porRuta(String ruta) {
        for (TipoCatalogo tipo : values()) {
            if (tipo.ruta.equals(ruta)) return tipo;
        }
        throw new RecursoNoEncontradoException("No existe el catálogo solicitado.");
    }
}
