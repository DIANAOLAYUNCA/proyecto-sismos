package pe.edu.cssp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.TipoAlertaRepository;
import pe.edu.cssp.repository.TipoDanoRepository;
import pe.edu.cssp.repository.UbicacionRepository;
import pe.edu.cssp.service.importacion.CsvLector;
import pe.edu.cssp.service.importacion.ResultadoImportacion;
import pe.edu.cssp.service.importacion.ResultadoImportacion.Estado;
import pe.edu.cssp.service.importacion.TipoCatalogo;
import pe.edu.cssp.web.error.ReglaNegocioException;

@Service
public class ImportacionCatalogosService {
    private static final Set<String> TIPOS_UBIGEO = Set.of("DEPARTAMENTO", "PROVINCIA_CONSTITUCIONAL");
    private static final Set<String> ESTADOS = Set.of("ACTIVO", "INACTIVO");
    private static final Set<String> COLORES = Set.of("VERDE", "AMARILLO", "NARANJA", "ROJO");
    private static final Set<String> SECTORES = Set.of("SALUD", "VIVIENDA", "EDUCACION", "INFRAESTRUCTURA",
            "SERVICIOS_BASICOS", "AGROPECUARIO", "POBLACION");
    private static final String USUARIO = "importacion";

    private final UbicacionRepository ubicacionRepository;
    private final TipoAlertaRepository tipoAlertaRepository;
    private final TipoDanoRepository tipoDanoRepository;

    public ImportacionCatalogosService(UbicacionRepository ubicacionRepository,
                                       TipoAlertaRepository tipoAlertaRepository,
                                       TipoDanoRepository tipoDanoRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.tipoAlertaRepository = tipoAlertaRepository;
        this.tipoDanoRepository = tipoDanoRepository;
    }

    @Transactional
    public ResultadoImportacion importarIncluido(TipoCatalogo tipo) {
        String ruta = "datos/" + tipo.getArchivo();
        try (InputStream entrada = getClass().getClassLoader().getResourceAsStream(ruta)) {
            if (entrada == null) {
                throw new ReglaNegocioException("No se encontró el archivo incluido " + tipo.getArchivo() + ".");
            }
            return importar(tipo, tipo.getArchivo(), entrada);
        } catch (IOException ex) {
            throw new ReglaNegocioException("No se pudo leer el archivo incluido " + tipo.getArchivo() + ".");
        }
    }

    @Transactional
    public ResultadoImportacion importar(TipoCatalogo tipo, String nombreArchivo, InputStream csv) {
        CsvLector.Contenido contenido;
        try {
            contenido = CsvLector.leer(csv);
        } catch (IOException ex) {
            throw new ReglaNegocioException("No se pudo leer el archivo.");
        }
        Map<String, Integer> posiciones = validarEncabezados(tipo, contenido.getEncabezados());
        ResultadoImportacion resultado = new ResultadoImportacion(tipo, nombreArchivo);
        switch (tipo) {
            case UBIGEO -> procesar(resultado, contenido, posiciones, existentes(ubicacionRepository.findAll(), Ubicacion::getCodigo),
                    this::validarUbigeo, this::crearUbicacion, ubicacionRepository::saveAll);
            case TIPO_ALERTA -> procesar(resultado, contenido, posiciones, existentes(tipoAlertaRepository.findAll(), TipoAlerta::getCodigo),
                    this::validarTipoAlerta, this::crearTipoAlerta, tipoAlertaRepository::saveAll);
            case TIPO_DANO -> procesar(resultado, contenido, posiciones, existentes(tipoDanoRepository.findAll(), TipoDano::getCodigo),
                    this::validarTipoDano, this::crearTipoDano, tipoDanoRepository::saveAll);
        }
        return resultado;
    }

    private <T> Set<String> existentes(List<T> registros, Function<T, String> codigo) {
        Set<String> codigos = new HashSet<>();
        for (T registro : registros) codigos.add(clave(codigo.apply(registro)));
        return codigos;
    }

    private <T> void procesar(ResultadoImportacion resultado, CsvLector.Contenido contenido, Map<String, Integer> posiciones,
                              Set<String> existentes, Function<Map<String, String>, List<String>> validar,
                              Function<Map<String, String>, T> crear, Function<List<T>, ?> guardar) {
        Set<String> vistos = new HashSet<>();
        List<T> nuevos = new ArrayList<>();
        for (CsvLector.Fila fila : contenido.getFilas()) {
            Map<String, String> campos = campos(posiciones, fila);
            String codigo = campos.get("codigo");
            if (fila.getValores().size() != contenido.getEncabezados().size()) {
                resultado.agregar(fila.getLinea(), codigo, Estado.RECHAZADA, "Se esperaban " + contenido.getEncabezados().size()
                        + " columnas y la fila tiene " + fila.getValores().size() + ".");
                continue;
            }
            List<String> errores = validar.apply(campos);
            if (!errores.isEmpty()) {
                resultado.agregar(fila.getLinea(), codigo, Estado.RECHAZADA, String.join(" ", errores));
            } else if (vistos.contains(clave(codigo))) {
                resultado.agregar(fila.getLinea(), codigo, Estado.RECHAZADA, "El código está repetido dentro del archivo.");
            } else if (existentes.contains(clave(codigo))) {
                vistos.add(clave(codigo));
                resultado.agregar(fila.getLinea(), codigo, Estado.OMITIDA, "El código ya existe; no se modificó el registro.");
            } else {
                vistos.add(clave(codigo));
                nuevos.add(crear.apply(campos));
                resultado.agregar(fila.getLinea(), codigo, Estado.IMPORTADA, "Registro creado.");
            }
        }
        guardar.apply(nuevos);
    }

    private Map<String, Integer> validarEncabezados(TipoCatalogo tipo, List<String> encabezados) {
        if (encabezados.size() == 1 && encabezados.get(0).contains(";")) {
            throw new ReglaNegocioException("El archivo parece usar \";\" como separador. Use comas (CSV estándar).");
        }
        Map<String, Integer> posiciones = new java.util.HashMap<>();
        for (int i = 0; i < encabezados.size(); i++) posiciones.putIfAbsent(encabezados.get(i), i);
        List<String> faltantes = tipo.getColumnas().stream().filter(c -> !posiciones.containsKey(c)).toList();
        if (!faltantes.isEmpty()) {
            throw new ReglaNegocioException("Al archivo le faltan columnas: " + String.join(", ", faltantes)
                    + ". Se esperaban: " + String.join(", ", tipo.getColumnas()) + ".");
        }
        return posiciones;
    }

    private Map<String, String> campos(Map<String, Integer> posiciones, CsvLector.Fila fila) {
        Map<String, String> campos = new java.util.HashMap<>();
        posiciones.forEach((nombre, indice) ->
                campos.put(nombre, indice < fila.getValores().size() ? fila.getValores().get(indice) : ""));
        return campos;
    }

    private List<String> validarUbigeo(Map<String, String> c) {
        List<String> errores = new ArrayList<>();
        codigo(c, 10, errores);
        nombre(c, errores);
        enumerado(c, "tipo", TIPOS_UBIGEO, "El tipo no es válido (DEPARTAMENTO o PROVINCIA_CONSTITUCIONAL).", errores);
        enumerado(c, "estado", ESTADOS, "El estado no es válido (ACTIVO o INACTIVO).", errores);
        return errores;
    }

    private List<String> validarTipoAlerta(Map<String, String> c) {
        List<String> errores = new ArrayList<>();
        codigo(c, 20, errores);
        nombre(c, errores);
        enumerado(c, "color", COLORES, "El color no es válido (VERDE, AMARILLO, NARANJA o ROJO).", errores);
        if (!c.get("prioridad").matches("\\d{1,9}") || Integer.parseInt(c.get("prioridad")) <= 0) {
            errores.add("La prioridad debe ser un número entero positivo.");
        }
        descripcion(c, errores);
        enumerado(c, "estado", ESTADOS, "El estado no es válido (ACTIVO o INACTIVO).", errores);
        return errores;
    }

    private List<String> validarTipoDano(Map<String, String> c) {
        List<String> errores = new ArrayList<>();
        codigo(c, 20, errores);
        nombre(c, errores);
        if (c.get("sector").isBlank()) errores.add("El sector es obligatorio.");
        else enumerado(c, "sector", SECTORES, "El sector no es válido.", errores);
        descripcion(c, errores);
        if (c.get("estado").isBlank()) errores.add("El estado es obligatorio.");
        else enumerado(c, "estado", ESTADOS, "El estado no es válido (ACTIVO o INACTIVO).", errores);
        return errores;
    }

    private void codigo(Map<String, String> c, int maximo, List<String> errores) {
        String codigo = c.get("codigo");
        if (codigo.isBlank()) errores.add("El código es obligatorio.");
        else if (codigo.length() > maximo) errores.add("El código supera los " + maximo + " caracteres.");
    }

    private void nombre(Map<String, String> c, List<String> errores) {
        String nombre = c.get("nombre");
        if (nombre.isBlank()) errores.add("El nombre es obligatorio.");
        else if (nombre.length() > 100) errores.add("El nombre supera los 100 caracteres.");
    }

    private void descripcion(Map<String, String> c, List<String> errores) {
        if (c.get("descripcion").length() > 255) errores.add("La descripción supera los 255 caracteres.");
    }

    private void enumerado(Map<String, String> c, String campo, Set<String> validos, String mensaje, List<String> errores) {
        if (!validos.contains(c.get(campo).toUpperCase(Locale.ROOT))) errores.add(mensaje);
    }

    private Ubicacion crearUbicacion(Map<String, String> c) {
        Ubicacion u = new Ubicacion();
        u.setCodigo(c.get("codigo"));
        u.setNombre(c.get("nombre"));
        u.setTipo(c.get("tipo").toUpperCase(Locale.ROOT));
        u.setEstado(c.get("estado").toUpperCase(Locale.ROOT));
        auditoria(u::setCreacionFecha, u::setCreacionUsuario);
        return u;
    }

    private TipoAlerta crearTipoAlerta(Map<String, String> c) {
        TipoAlerta a = new TipoAlerta();
        a.setCodigo(c.get("codigo"));
        a.setNombre(c.get("nombre"));
        a.setColor(c.get("color").toUpperCase(Locale.ROOT));
        a.setPrioridad(Integer.parseInt(c.get("prioridad")));
        a.setDescripcion(c.get("descripcion").isBlank() ? null : c.get("descripcion"));
        a.setEstado(c.get("estado").toUpperCase(Locale.ROOT));
        auditoria(a::setCreacionFecha, a::setCreacionUsuario);
        return a;
    }

    private TipoDano crearTipoDano(Map<String, String> c) {
        TipoDano t = new TipoDano();
        t.setCodigo(c.get("codigo"));
        t.setNombre(c.get("nombre"));
        t.setSector(c.get("sector").toUpperCase(Locale.ROOT));
        t.setDescripcion(c.get("descripcion").isBlank() ? null : c.get("descripcion"));
        t.setEstado(c.get("estado").toUpperCase(Locale.ROOT));
        auditoria(t::setCreacionFecha, t::setCreacionUsuario);
        return t;
    }

    private void auditoria(java.util.function.Consumer<String> fecha, java.util.function.Consumer<String> usuario) {
        fecha.accept(UbicacionService.fecha());
        usuario.accept(USUARIO);
    }

    private String clave(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
    }
}
