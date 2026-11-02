package pe.edu.cssp.service.importacion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import pe.edu.cssp.web.error.ReglaNegocioException;

public final class CsvLector {
    public static final int MAX_FILAS = 5000;

    private CsvLector() { }

    public static final class Fila {
        private final int linea;
        private final List<String> valores;
        Fila(int linea, List<String> valores) { this.linea = linea; this.valores = valores; }
        public int getLinea() { return linea; }
        public List<String> getValores() { return valores; }
    }

    public static final class Contenido {
        private final List<String> encabezados;
        private final List<Fila> filas;
        Contenido(List<String> encabezados, List<Fila> filas) { this.encabezados = encabezados; this.filas = filas; }
        public List<String> getEncabezados() { return encabezados; }
        public List<Fila> getFilas() { return filas; }
    }

    public static Contenido leer(InputStream entrada) throws IOException {
        String texto = decodificar(entrada.readAllBytes());
        if (!texto.isEmpty() && texto.charAt(0) == '﻿') texto = texto.substring(1);

        List<String> encabezados = null;
        List<Fila> filas = new ArrayList<>();
        int numero = 0;
        for (String linea : texto.split("\r\n|\n|\r", -1)) {
            numero++;
            if (linea.isBlank()) continue;
            List<String> valores = dividir(linea, numero);
            if (encabezados == null) {
                encabezados = valores.stream().map(v -> v.toLowerCase(Locale.ROOT)).toList();
                continue;
            }
            if (filas.size() >= MAX_FILAS) {
                throw new ReglaNegocioException("El archivo supera el máximo de " + MAX_FILAS + " filas.");
            }
            filas.add(new Fila(numero, valores));
        }
        if (encabezados == null) throw new ReglaNegocioException("El archivo está vacío.");
        return new Contenido(encabezados, filas);
    }

    private static String decodificar(byte[] bytes) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException ex) {
            throw new ReglaNegocioException("El archivo no está codificado en UTF-8. Guárdelo como \"CSV UTF-8\" y vuelva a intentarlo.");
        }
    }

    private static List<String> dividir(String linea, int numero) {
        List<String> valores = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (enComillas) {
                if (c == '"') {
                    if (i + 1 < linea.length() && linea.charAt(i + 1) == '"') { actual.append('"'); i++; }
                    else enComillas = false;
                } else {
                    actual.append(c);
                }
            } else if (c == '"') {
                enComillas = true;
            } else if (c == ',') {
                valores.add(actual.toString().trim());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (enComillas) throw new ReglaNegocioException("Línea " + numero + ": hay comillas sin cerrar.");
        valores.add(actual.toString().trim());
        return valores;
    }
}
