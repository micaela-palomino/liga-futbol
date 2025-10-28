package com.uade.ligafutbol.config;

import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.Estadio;
import com.uade.ligafutbol.model.Partido;
import com.uade.ligafutbol.model.ConexionEquipo;
import com.uade.ligafutbol.model.ConexionEstadio;
import com.uade.ligafutbol.repository.EquipoRepository;
import com.uade.ligafutbol.repository.EstadioRepository;
import com.uade.ligafutbol.repository.PartidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private EstadioRepository estadioRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Iniciando carga de datos iniciales...");

        // Always attempt idempotent upserts so the loader can be run multiple times.
        // This replaces the previous shortcut that skipped loading when any equipos existed.

        try {
            cargarEstadios();
            cargarConexionesEstadios();
            cargarEquipos();
            cargarConexionesEquipos();
            cargarPartidos();
            logger.info("Carga de datos iniciales completada exitosamente.");
        } catch (Exception e) {
            logger.error("Error durante la carga de datos iniciales: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void cargarEstadios() {
        logger.info("Cargando estadios...");

        List<Estadio> estadios = Arrays.asList(
            new Estadio("Estadio Monumental", "Buenos Aires", 70074, -34.5456, -58.4497),
            new Estadio("La Bombonera", "Buenos Aires", 54000, -34.6355, -58.3641),
            new Estadio("Estadio Libertadores de América", "Avellaneda", 50000, -34.6667, -58.3667),
            new Estadio("Estadio Presidente Perón", "Avellaneda", 45000, -34.6667, -58.3667),
            new Estadio("Estadio Mario Alberto Kempes", "Córdoba", 57000, -31.4201, -64.1888),
            new Estadio("Estadio Único Madre de Ciudades", "Santiago del Estero", 30000, -27.7834, -64.2642),
            new Estadio("Estadio Brigadier General Estanislao López", "Santa Fe", 40000, -31.6333, -60.7000),
            new Estadio("Estadio Malvinas Argentinas", "Mendoza", 40000, -32.8908, -68.8272),
            new Estadio("Estadio José María Minella", "Mar del Plata", 35000, -38.0023, -57.5575),
            new Estadio("Estadio Gigante de Arroyito", "Rosario", 41654, -32.9500, -60.6667)
        );

        int createdOrUpdated = 0;
            for (Estadio e : estadios) {
                Estadio persisted = null;
                java.util.List<Estadio> found = estadioRepository.findByNombre(e.getNombre());
                if (found != null && !found.isEmpty()) persisted = found.get(0);
                if (persisted == null) {
                    estadioRepository.save(e);
                    createdOrUpdated++;
                } else {
                    // update mutable fields
                    persisted.setCiudad(e.getCiudad());
                    // set capacity only if provided
                    persisted.setCapacidad(e.getCapacidad());
                    persisted.setLatitud(e.getLatitud());
                    persisted.setLongitud(e.getLongitud());
                    estadioRepository.save(persisted);
                    createdOrUpdated++;
                }
        }
        logger.info("Estadios creados/actualizados: {}", createdOrUpdated);
    }

    private void cargarConexionesEstadios() {
        logger.info("Cargando conexiones entre estadios...");

        List<Estadio> estadios = estadioRepository.findAll();

        Estadio monumental = buscarEstadioPorNombre(estadios, "Estadio Monumental");
        Estadio bombonera = buscarEstadioPorNombre(estadios, "La Bombonera");
        Estadio libertadores = buscarEstadioPorNombre(estadios, "Estadio Libertadores de América");
        Estadio presidentePeron = buscarEstadioPorNombre(estadios, "Estadio Presidente Perón");

        if (monumental != null && bombonera != null) {
            agregarConexionEstadio(monumental, new ConexionEstadio(bombonera, 12.5, 500.0, 30));
            agregarConexionEstadio(bombonera, new ConexionEstadio(monumental, 12.5, 500.0, 30));
        }

        if (libertadores != null && presidentePeron != null) {
            agregarConexionEstadio(libertadores, new ConexionEstadio(presidentePeron, 2.1, 100.0, 10));
            agregarConexionEstadio(presidentePeron, new ConexionEstadio(libertadores, 2.1, 100.0, 10));
        }

        if (monumental != null && libertadores != null) {
            agregarConexionEstadio(monumental, new ConexionEstadio(libertadores, 15.2, 600.0, 35));
            agregarConexionEstadio(libertadores, new ConexionEstadio(monumental, 15.2, 600.0, 35));
        }

        if (bombonera != null && presidentePeron != null) {
            agregarConexionEstadio(bombonera, new ConexionEstadio(presidentePeron, 8.3, 350.0, 25));
            agregarConexionEstadio(presidentePeron, new ConexionEstadio(bombonera, 8.3, 350.0, 25));
        }

        // Save any estadio that had its conexiones modified
        for (Estadio e : estadios) {
            estadioRepository.save(e);
        }
        logger.info("Conexiones entre estadios creadas/actualizadas");
    }

    private void cargarEquipos() {
        logger.info("Cargando equipos...");

        List<Estadio> estadios = estadioRepository.findAll();

        Equipo riverPlate = new Equipo("River Plate", "Buenos Aires");
        asignarEstadio(riverPlate, buscarEstadioPorNombre(estadios, "Estadio Monumental"));

        Equipo bocaJuniors = new Equipo("Boca Juniors", "Buenos Aires");
        asignarEstadio(bocaJuniors, buscarEstadioPorNombre(estadios, "La Bombonera"));

        Equipo racingClub = new Equipo("Racing Club", "Avellaneda");
        asignarEstadio(racingClub, buscarEstadioPorNombre(estadios, "Estadio Libertadores de América"));

        Equipo independiente = new Equipo("Independiente", "Avellaneda");
        asignarEstadio(independiente, buscarEstadioPorNombre(estadios, "Estadio Presidente Perón"));

        Equipo sanLorenzo = new Equipo("San Lorenzo", "Buenos Aires");
        asignarEstadio(sanLorenzo, buscarEstadioPorNombre(estadios, "Estadio Monumental"));

        Equipo huracan = new Equipo("Huracán", "Buenos Aires");
        asignarEstadio(huracan, buscarEstadioPorNombre(estadios, "La Bombonera"));

        Equipo talleres = new Equipo("Talleres", "Córdoba");
        asignarEstadio(talleres, buscarEstadioPorNombre(estadios, "Estadio Mario Alberto Kempes"));

        Equipo centralCordoba = new Equipo("Central Córdoba", "Santiago del Estero");
        asignarEstadio(centralCordoba, buscarEstadioPorNombre(estadios, "Estadio Único Madre de Ciudades"));

        Equipo colon = new Equipo("Colón", "Santa Fe");
        asignarEstadio(colon, buscarEstadioPorNombre(estadios, "Estadio Brigadier General Estanislao López"));

        Equipo godoyCruz = new Equipo("Godoy Cruz", "Mendoza");
        asignarEstadio(godoyCruz, buscarEstadioPorNombre(estadios, "Estadio Malvinas Argentinas"));

        List<Equipo> equipos = Arrays.asList(
            riverPlate, bocaJuniors, racingClub, independiente, sanLorenzo,
            huracan, talleres, centralCordoba, colon, godoyCruz
        );
        int createdOrUpdated = 0;
            for (Equipo eq : equipos) {
                Equipo persisted = null;
                java.util.List<Equipo> foundEq = equipoRepository.findByNombre(eq.getNombre());
                if (foundEq != null && !foundEq.isEmpty()) persisted = foundEq.get(0);
                if (persisted == null) {
                    equipoRepository.save(eq);
                    createdOrUpdated++;
                } else {
                // update mutable fields and stadium association
                persisted.setCiudad(eq.getCiudad());
                persisted.setPuntos(eq.getPuntos());
                persisted.setPartidosJugados(eq.getPartidosJugados());
                persisted.setPartidosGanados(eq.getPartidosGanados());
                persisted.setPartidosEmpatados(eq.getPartidosEmpatados());
                persisted.setPartidosPerdidos(eq.getPartidosPerdidos());
                persisted.setGolesAFavor(eq.getGolesAFavor());
                persisted.setGolesEnContra(eq.getGolesEnContra());
                persisted.setEstadio(eq.getEstadio());
                equipoRepository.save(persisted);
                createdOrUpdated++;
            }
        }
        logger.info("Equipos creados/actualizados: {}", createdOrUpdated);
    }

    private void cargarConexionesEquipos() {
        logger.info("Cargando conexiones entre equipos...");

        List<Equipo> equipos = equipoRepository.findAll();

        Equipo riverPlate = buscarEquipoPorNombre(equipos, "River Plate");
        Equipo bocaJuniors = buscarEquipoPorNombre(equipos, "Boca Juniors");
        Equipo racingClub = buscarEquipoPorNombre(equipos, "Racing Club");
        Equipo independiente = buscarEquipoPorNombre(equipos, "Independiente");
        Equipo sanLorenzo = buscarEquipoPorNombre(equipos, "San Lorenzo");
        Equipo huracan = buscarEquipoPorNombre(equipos, "Huracán");

        if (riverPlate != null && bocaJuniors != null) {
            agregarConexionEquipo(riverPlate, new ConexionEquipo(bocaJuniors, 12.5, 500.0, 30));
            agregarConexionEquipo(bocaJuniors, new ConexionEquipo(riverPlate, 12.5, 500.0, 30));
        }

        if (racingClub != null && independiente != null) {
            agregarConexionEquipo(racingClub, new ConexionEquipo(independiente, 2.1, 100.0, 10));
            agregarConexionEquipo(independiente, new ConexionEquipo(racingClub, 2.1, 100.0, 10));
        }

        if (riverPlate != null && racingClub != null) {
            agregarConexionEquipo(riverPlate, new ConexionEquipo(racingClub, 15.2, 600.0, 35));
            agregarConexionEquipo(racingClub, new ConexionEquipo(riverPlate, 15.2, 600.0, 35));
        }

        if (bocaJuniors != null && independiente != null) {
            agregarConexionEquipo(bocaJuniors, new ConexionEquipo(independiente, 8.3, 350.0, 25));
            agregarConexionEquipo(independiente, new ConexionEquipo(bocaJuniors, 8.3, 350.0, 25));
        }

        if (sanLorenzo != null && huracan != null) {
            agregarConexionEquipo(sanLorenzo, new ConexionEquipo(huracan, 8.5, 360.0, 25));
            agregarConexionEquipo(huracan, new ConexionEquipo(sanLorenzo, 8.5, 360.0, 25));
        }

        if (riverPlate != null && sanLorenzo != null) {
            agregarConexionEquipo(riverPlate, new ConexionEquipo(sanLorenzo, 15.5, 610.0, 36));
            agregarConexionEquipo(sanLorenzo, new ConexionEquipo(riverPlate, 15.5, 610.0, 36));
        }

        if (bocaJuniors != null && huracan != null) {
            agregarConexionEquipo(bocaJuniors, new ConexionEquipo(huracan, 8.0, 320.0, 24));
            agregarConexionEquipo(huracan, new ConexionEquipo(bocaJuniors, 8.0, 320.0, 24));
        }

        for (Equipo e : equipos) {
            equipoRepository.save(e);
        }
        logger.info("Conexiones entre equipos creadas/actualizadas");
    }

    private void cargarPartidos() {
        logger.info("Cargando partidos...");

        List<Equipo> equipos = equipoRepository.findAll();
        List<Estadio> estadios = estadioRepository.findAll();
        // Defensive: ensure we have enough equipos/estadios to index into the lists.
        if (equipos.size() < 10 || estadios.size() < 8) {
            logger.warn("No hay suficientes equipos ({}) o estadios ({}) para crear la programación de partidos. Se omite la carga de partidos.", equipos.size(), estadios.size());
            return;
        }

        List<Partido> partidos = Arrays.asList(
            new Partido(equipos.get(0), equipos.get(1), estadios.get(0), LocalDateTime.of(2024, 2, 10, 16, 0), 1),
            new Partido(equipos.get(2), equipos.get(3), estadios.get(2), LocalDateTime.of(2024, 2, 10, 19, 0), 1),
            new Partido(equipos.get(4), equipos.get(5), estadios.get(0), LocalDateTime.of(2024, 2, 11, 16, 0), 1),
            new Partido(equipos.get(6), equipos.get(7), estadios.get(4), LocalDateTime.of(2024, 2, 11, 19, 0), 1),
            new Partido(equipos.get(8), equipos.get(9), estadios.get(6), LocalDateTime.of(2024, 2, 12, 16, 0), 1),

            new Partido(equipos.get(1), equipos.get(2), estadios.get(1), LocalDateTime.of(2024, 2, 17, 16, 0), 2),
            new Partido(equipos.get(3), equipos.get(4), estadios.get(3), LocalDateTime.of(2024, 2, 17, 19, 0), 2),
            new Partido(equipos.get(5), equipos.get(6), estadios.get(1), LocalDateTime.of(2024, 2, 18, 16, 0), 2),
            new Partido(equipos.get(7), equipos.get(8), estadios.get(5), LocalDateTime.of(2024, 2, 18, 19, 0), 2),
            new Partido(equipos.get(9), equipos.get(0), estadios.get(7), LocalDateTime.of(2024, 2, 19, 16, 0), 2),

            new Partido(equipos.get(0), equipos.get(2), estadios.get(0), LocalDateTime.of(2024, 2, 24, 16, 0), 3),
            new Partido(equipos.get(1), equipos.get(3), estadios.get(1), LocalDateTime.of(2024, 2, 24, 19, 0), 3),
            new Partido(equipos.get(4), equipos.get(6), estadios.get(0), LocalDateTime.of(2024, 2, 25, 16, 0), 3),
            new Partido(equipos.get(5), equipos.get(7), estadios.get(1), LocalDateTime.of(2024, 2, 25, 19, 0), 3),
            new Partido(equipos.get(8), equipos.get(9), estadios.get(6), LocalDateTime.of(2024, 2, 26, 16, 0), 3)
        );

        // Registrar resultados de ejemplo
        partidos.get(0).registrarResultado(2, 1);
        partidos.get(1).registrarResultado(1, 1);
        partidos.get(2).registrarResultado(3, 0);
        partidos.get(3).registrarResultado(0, 2);
        partidos.get(4).registrarResultado(1, 0);
        partidos.get(5).registrarResultado(2, 0);
        partidos.get(6).registrarResultado(1, 2);
        partidos.get(7).registrarResultado(0, 1);
        partidos.get(8).registrarResultado(1, 1);
        partidos.get(9).registrarResultado(0, 3);

        int createdOrUpdated = 0;
        for (Partido p : partidos) {
            // try to find an existing partido with same fecha and local/visitante
            List<Partido> candidatos = partidoRepository.findByJornada(p.getJornada());
            Partido existente = candidatos.stream()
                    .filter(pp -> pp.getFecha() != null && pp.getFecha().equals(p.getFecha())
                            && pp.getEquipoLocal() != null && pp.getEquipoVisitante() != null
                            && pp.getEquipoLocal().getNombre().equals(p.getEquipoLocal().getNombre())
                            && pp.getEquipoVisitante().getNombre().equals(p.getEquipoVisitante().getNombre()))
                    .findFirst().orElse(null);

            if (existente == null) {
                partidoRepository.save(p);
                createdOrUpdated++;
            } else {
                // update result fields
                existente.setGolesLocal(p.getGolesLocal());
                existente.setGolesVisitante(p.getGolesVisitante());
                existente.setJugado(p.getJugado());
                partidoRepository.save(existente);
                createdOrUpdated++;
            }
        }
        logger.info("Partidos creados/actualizados: {}", createdOrUpdated);
    }

    private Estadio buscarEstadioPorNombre(List<Estadio> estadios, String nombre) {
        return estadios.stream()
                .filter(e -> e.getNombre().equals(nombre))
                .findFirst().orElse(null);
    }

    private Equipo buscarEquipoPorNombre(List<Equipo> equipos, String nombre) {
        return equipos.stream()
                .filter(e -> e.getNombre().equals(nombre))
                .findFirst().orElse(null);
    }

    private void asignarEstadio(Equipo equipo, Estadio estadio) {
        if (estadio != null) {
            equipo.setEstadio(estadio);
        }
    }

    private void agregarConexionEstadio(Estadio estadio, ConexionEstadio conexion) {
        if (estadio != null && conexion != null) {
            estadio.getConexiones().add(conexion);
        }
    }

    private void agregarConexionEquipo(Equipo equipo, ConexionEquipo conexion) {
        if (equipo != null && conexion != null) {
            equipo.getConexiones().add(conexion);
        }
    }
}
