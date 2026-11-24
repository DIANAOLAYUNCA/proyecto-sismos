package pe.edu.cssp.service.importacion;

import java.util.ArrayList;
import java.util.List;

public class ResultadoImportacion {
    public enum Estado { IMPORTADA, OMITIDA, RECHAZADA }

    public static class Linea {
        private final int numero;
        private final String codigo;
        private final Estado estado;
        private final String mensaje;

        Linea(int numero, String codigo, Estado estado, String mensaje) {
            this.numero = numero;
            this.codigo = codigo;
            this.estado = estado;
            this.mensaje = mensaje;
        }

        public int getNumero() { return numero; }
        public String getCodigo() { return codigo; }
        public Estado getEstado() { return estado; }
        public String getMensaje() { return mensaje; }
    }

    private final TipoCatalogo catalogo;
    private final String archivo;
    private static final int LIMITE_DETALLE_COMPLETO = 200;
    private final List<Linea> lineas = new ArrayList<>();
    private int ignoradas;

    public ResultadoImportacion(TipoCatalogo catalogo, String archivo) {
        this.catalogo = catalogo;
        this.archivo = archivo;
    }

    public void agregar(int numero, String codigo, Estado estado, String mensaje) {
        lineas.add(new Linea(numero, codigo, estado, mensaje));
    }

    /** Fila vacía o nota al pie que no es un registro: no cuenta como leída. */
    public void ignorar() { ignoradas++; }

    /** En archivos grandes el detalle solo lista lo que cambió o falló; las omitidas quedan en el resumen. */
    public List<Linea> getLineasMostradas() {
        if (lineas.size() <= LIMITE_DETALLE_COMPLETO) return lineas;
        return lineas.stream().filter(l -> l.getEstado() != Estado.OMITIDA).toList();
    }

    public boolean isDetalleReducido() { return lineas.size() > LIMITE_DETALLE_COMPLETO; }
    public int getIgnoradas() { return ignoradas; }

    public TipoCatalogo getCatalogo() { return catalogo; }
    public String getArchivo() { return archivo; }
    public List<Linea> getLineas() { return lineas; }
    public int getTotal() { return lineas.size(); }
    public long getImportadas() { return contar(Estado.IMPORTADA); }
    public long getOmitidas() { return contar(Estado.OMITIDA); }
    public long getRechazadas() { return contar(Estado.RECHAZADA); }

    private long contar(Estado estado) {
        return lineas.stream().filter(l -> l.getEstado() == estado).count();
    }
}
