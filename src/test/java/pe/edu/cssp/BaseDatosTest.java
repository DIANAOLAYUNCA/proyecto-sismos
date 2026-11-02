package pe.edu.cssp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cssp.entity.AfectacionNivel;
import pe.edu.cssp.entity.Estacion;
import pe.edu.cssp.entity.Evento;
import pe.edu.cssp.entity.TipoAlerta;
import pe.edu.cssp.entity.TipoDano;
import pe.edu.cssp.entity.Ubicacion;
import pe.edu.cssp.repository.AfectacionNivelRepository;
import pe.edu.cssp.repository.EstacionRepository;
import pe.edu.cssp.repository.EventoRepository;
import pe.edu.cssp.repository.TipoAlertaRepository;
import pe.edu.cssp.repository.TipoDanoRepository;
import pe.edu.cssp.repository.UbicacionRepository;

@SpringBootTest
@Transactional
abstract class BaseDatosTest {
    static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired UbicacionRepository ubicaciones;
    @Autowired EstacionRepository estaciones;
    @Autowired TipoAlertaRepository alertas;
    @Autowired TipoDanoRepository tiposDano;
    @Autowired AfectacionNivelRepository niveles;
    @Autowired EventoRepository eventos;

    private int contador = 0;

    String siguiente(String prefijo) { return prefijo + (++contador); }

    String hace(long horas) { return LocalDateTime.now().minusHours(horas).format(FORMATO); }

    String haceParaFormulario(long horas) { return hace(horas).substring(0, 16).replace(' ', 'T'); }

    Ubicacion ubicacion(String estado) {
        Ubicacion u = new Ubicacion();
        u.setCodigo(siguiente("U")); u.setNombre("Ubicación " + contador); u.setTipo("DEPARTAMENTO"); u.setEstado(estado);
        u.setCreacionFecha(hace(0)); u.setCreacionUsuario("prueba");
        return ubicaciones.save(u);
    }

    Estacion estacion(Ubicacion ubicacion) {
        Estacion e = new Estacion();
        e.setCodigo(siguiente("E")); e.setNombre("Estación " + contador); e.setUbicacion(ubicacion);
        e.setLatitud(new BigDecimal("-16.4")); e.setLongitud(new BigDecimal("-71.5")); e.setInstalacionFecha("2020-01-01"); e.setEstado("ACTIVA");
        e.setCreacionFecha(hace(0)); e.setCreacionUsuario("prueba");
        return estaciones.save(e);
    }

    TipoAlerta alerta() {
        TipoAlerta a = new TipoAlerta();
        a.setCodigo(siguiente("A")); a.setNombre("Alerta " + contador); a.setColor("VERDE"); a.setPrioridad(1); a.setEstado("ACTIVO");
        a.setCreacionFecha(hace(0)); a.setCreacionUsuario("prueba");
        return alertas.save(a);
    }

    TipoDano tipoDano(String estado) {
        TipoDano t = new TipoDano();
        t.setCodigo(siguiente("D")); t.setNombre("Daño " + contador); t.setSector("VIVIENDA"); t.setEstado(estado);
        t.setCreacionFecha(hace(0)); t.setCreacionUsuario("prueba");
        return tiposDano.save(t);
    }

    AfectacionNivel nivel() {
        AfectacionNivel n = new AfectacionNivel();
        n.setCodigo(siguiente("N")); n.setNombre("Nivel " + contador);
        n.setCreacionFecha(hace(0)); n.setCreacionUsuario("prueba");
        return niveles.save(n);
    }

    Evento evento(String estado, AfectacionNivel nivel) {
        Ubicacion ubicacion = ubicacion("ACTIVO");
        Evento ev = new Evento();
        ev.setEstacion(estacion(ubicacion)); ev.setUbicacion(ubicacion); ev.setTipoAlerta(alerta()); ev.setAfectacionNivel(nivel);
        ev.setCodigo(siguiente("SIS-")); ev.setFechaHora(hace(48)); ev.setMagnitud(5.1); ev.setProfundidad(30);
        ev.setLatitud(-16.4); ev.setLongitud(-71.5); ev.setReferencia("Cercanías de prueba"); ev.setEstado(estado);
        ev.setCreacionFecha(hace(0)); ev.setCreacionUsuario("prueba");
        return eventos.save(ev);
    }
}
