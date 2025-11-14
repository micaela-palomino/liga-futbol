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

    /**
     * Limpia todas las tablas de la base de datos
     */
    public void limpiarTodasLasTablas() {
        logger.info("🧹 Iniciando limpieza completa de la base de datos...");
        
        try {
            // Limpiar en orden inverso por las dependencias
            logger.info("Eliminando partidos...");
            partidoRepository.deleteAll();
            
            logger.info("Eliminando equipos...");
            equipoRepository.deleteAll();
            
            logger.info("Eliminando estadios...");
            estadioRepository.deleteAll();
            
            logger.info("✅ Limpieza completa finalizada exitosamente.");
        } catch (Exception e) {
            logger.error("❌ Error durante la limpieza: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Recarga completa de datos: limpia y vuelve a cargar todo
     */
    public void recargarDatosCompletos() throws Exception {
        logger.info("🔄 Iniciando recarga completa de datos...");
        
        limpiarTodasLasTablas();
        cargarDatosIniciales();
        
        logger.info("🎉 Recarga completa finalizada exitosamente.");
    }

    /**
     * Carga los datos iniciales (método extraído del run original)
     */
    public void cargarDatosIniciales() throws Exception {
        logger.info("📊 Cargando datos iniciales...");
        
        cargarEstadios();
        cargarConexionesEstadios();
        cargarEquipos();
        cargarConexionesEquipos();
        cargarPartidos();
        
        logger.info("✅ Datos iniciales cargados exitosamente.");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 DataLoader iniciado - Cargando datos automáticamente");
        
        try {
            // Verificar si ya hay datos cargados
            long estadiosCount = estadioRepository.count();
            long equiposCount = equipoRepository.count();
            long partidosCount = partidoRepository.count();
            
            if (estadiosCount > 0 || equiposCount > 0 || partidosCount > 0) {
                logger.info("📊 Datos ya existentes detectados:");
                logger.info("   - Estadios: {}", estadiosCount);
                logger.info("   - Equipos: {}", equiposCount);
                logger.info("   - Partidos: {}", partidosCount);
                logger.info("🔗 Verificando y creando conexiones para Dijkstra...");
                
                try {
                    // Asegurar que el set de estadios/equipos quede limitado y sus conexiones
                    cargarEstadios();
                    cargarEquipos();
                    cargarConexionesEstadios();
                    cargarConexionesEquipos();
                    logger.info("✅ Conexiones verificadas/creadas exitosamente.");
                } catch (Exception ex) {
                    logger.warn("⚠️  Warning al crear conexiones: {}", ex.getMessage());
                }
                
                logger.info("✅ Usando datos existentes. Verificación completada.");
                logger.info("🌐 Interfaz web disponible en: http://localhost:8080/web/");
                logger.info("⚽ ¡Dijkstra debería funcionar ahora con las conexiones!");
                return;
            }
            
            logger.info("📊 Base de datos vacía. Iniciando carga de datos...");
            cargarDatosIniciales();
            
            logger.info("✅ Carga de datos iniciales completada exitosamente.");
            logger.info("🌐 Interfaz web disponible en: http://localhost:8080/web/");
            logger.info("⚽ ¡Ya puedes usar Dijkstra con equipos y estadios!");
            
        } catch (Exception e) {
            logger.warn("⚠️  Warning durante la carga de datos: {}", e.getMessage());
            logger.info("📍 Algunos warnings de Neo4j son esperados (funciones deprecated)");
            logger.info("📍 La aplicación debería funcionar correctamente a pesar de los warnings");
            logger.info("🌐 Interfaz web disponible en: http://localhost:8080/web/");
            
            // Intentar continuar con datos parciales si es posible
            try {
                long estadiosCount = estadioRepository.count();
                long equiposCount = equipoRepository.count();
                if (estadiosCount > 0 && equiposCount > 0) {
                    logger.info("✅ Datos básicos disponibles - la aplicación puede funcionar");
                } else {
                    logger.info("📍 Usa /web/database/crear-estructura para crear la estructura manualmente");
                }
            } catch (Exception countEx) {
                logger.info("📍 Usa /web/database/crear-estructura para crear la estructura manualmente");
                logger.info("📍 Usa /web/database/llenar-datos para llenar con datos manualmente");
            }
        }
    }

    private void cargarEstadios() {
        logger.info("Cargando estadios...");

        // Mantener solo los estadios asociados a los 4 equipos
        java.util.Set<String> permitidos = new java.util.HashSet<>(java.util.Arrays.asList(
                "Estadio Monumental",
                "La Bombonera",
                "Estadio Libertadores de América",
                "Estadio Presidente Perón"
        ));

        // Eliminar estadios no permitidos si existen
        List<Estadio> existentes = estadioRepository.findAll();
        int eliminados = 0;
        for (Estadio ex : existentes) {
            if (!permitidos.contains(ex.getNombre())) {
                estadioRepository.delete(ex);
                eliminados++;
            }
        }
        if (eliminados > 0) {
            logger.info("Estadios eliminados (no permitidos): {}", eliminados);
        }

        List<Estadio> estadios = Arrays.asList(
            new Estadio("Estadio Monumental", "Buenos Aires", 70074, -34.5456, -58.4497),
            new Estadio("La Bombonera", "Buenos Aires", 54000, -34.6355, -58.3641),
            new Estadio("Estadio Libertadores de América", "Avellaneda", 50000, -34.6667, -58.3667),
            new Estadio("Estadio Presidente Perón", "Avellaneda", 45000, -34.6667, -58.3667)
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

        if (estadios == null || estadios.size() < 2) {
            logger.warn("No hay suficientes estadios ({}) para crear conexiones", estadios != null ? estadios.size() : 0);
            return;
        }

        int kVecinos = Math.min(3, Math.max(1, estadios.size() - 1));
        double velocidadKmH = 80.0;
        double costoPorKm = 40.0;

        for (Estadio origen : estadios) {
            if (origen.getLatitud() == null || origen.getLongitud() == null) continue;

            java.util.List<Estadio> candidatos = new java.util.ArrayList<>(estadios);
            candidatos.remove(origen);

            candidatos.sort((a, b) -> {
                double da = distanciaKm(origen.getLatitud(), origen.getLongitud(), a.getLatitud(), a.getLongitud());
                double db = distanciaKm(origen.getLatitud(), origen.getLongitud(), b.getLatitud(), b.getLongitud());
                return Double.compare(da, db);
            });

            for (int i = 0; i < Math.min(kVecinos, candidatos.size()); i++) {
                Estadio destino = candidatos.get(i);
                if (destino.getLatitud() == null || destino.getLongitud() == null) continue;

                double dist = distanciaKm(origen.getLatitud(), origen.getLongitud(), destino.getLatitud(), destino.getLongitud());
                double costo = dist * costoPorKm;
                int minutos = (int) Math.round((dist / velocidadKmH) * 60.0);

                boolean yaConectado = origen.getConexiones().stream()
                        .anyMatch(c -> c.getEstadioDestino() != null && c.getEstadioDestino().getId().equals(destino.getId()));
                if (!yaConectado) {
                    agregarConexionEstadio(origen, new ConexionEstadio(destino, dist, costo, minutos));
                }

                boolean yaConectadoBack = destino.getConexiones().stream()
                        .anyMatch(c -> c.getEstadioDestino() != null && c.getEstadioDestino().getId().equals(origen.getId()));
                if (!yaConectadoBack) {
                    agregarConexionEstadio(destino, new ConexionEstadio(origen, dist, costo, minutos));
                }
            }
        }

        for (Estadio e : estadios) {
            estadioRepository.save(e);
        }
        logger.info("Conexiones entre estadios creadas/actualizadas (K vecinos: {})", kVecinos);
    }

    private double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void cargarEquipos() {
        logger.info("Cargando equipos...");

        List<Estadio> estadios = estadioRepository.findAll();

        // Mantener solo estos 4 equipos
        java.util.Set<String> permitidos = new java.util.HashSet<>(java.util.Arrays.asList(
                "River Plate", "Boca Juniors", "Independiente", "Racing Club"
        ));

        // Eliminar equipos no permitidos si existen
        List<Equipo> existentes = equipoRepository.findAll();
        int eliminados = 0;
        for (Equipo ex : existentes) {
            if (!permitidos.contains(ex.getNombre())) {
                equipoRepository.delete(ex);
                eliminados++;
            }
        }
        if (eliminados > 0) {
            logger.info("Equipos eliminados (no permitidos): {}", eliminados);
        }

        Equipo riverPlate = new Equipo("River Plate", "Buenos Aires");
        asignarEstadio(riverPlate, buscarEstadioPorNombre(estadios, "Estadio Monumental"));

        Equipo bocaJuniors = new Equipo("Boca Juniors", "Buenos Aires");
        asignarEstadio(bocaJuniors, buscarEstadioPorNombre(estadios, "La Bombonera"));

        Equipo racingClub = new Equipo("Racing Club", "Avellaneda");
        asignarEstadio(racingClub, buscarEstadioPorNombre(estadios, "Estadio Libertadores de América"));

        Equipo independiente = new Equipo("Independiente", "Avellaneda");
        asignarEstadio(independiente, buscarEstadioPorNombre(estadios, "Estadio Presidente Perón"));

        List<Equipo> equipos = Arrays.asList(
            riverPlate, bocaJuniors, racingClub, independiente
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
        
        // Limpiar conexiones existentes que apunten a equipos no permitidos
        java.util.Set<String> permitidos = new java.util.HashSet<>(java.util.Arrays.asList(
                "River Plate", "Boca Juniors", "Independiente", "Racing Club"
        ));
        for (Equipo eq : equipos) {
            if (eq.getConexiones() != null) {
                eq.getConexiones().removeIf(c -> c.getEquipoDestino() == null || !permitidos.contains(c.getEquipoDestino().getNombre()));
            }
        }

        if (riverPlate != null && bocaJuniors != null) {
            agregarConexionEquipo(riverPlate, new ConexionEquipo(bocaJuniors, 12.5, 500.0, 30));
            agregarConexionEquipo(bocaJuniors, new ConexionEquipo(riverPlate, 14.5, 500.0, 50));
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

        for (Equipo e : equipos) {
            equipoRepository.save(e);
        }
        logger.info("Conexiones entre equipos creadas/actualizadas");
    }

    private void cargarPartidos() {
        logger.info("Cargando partidos...");

        // Limpiar partidos previos para evitar residuos
        try {
            partidoRepository.deleteAll();
        } catch (Exception ex) {
            logger.warn("No se pudieron eliminar partidos previos: {}", ex.getMessage());
        }

        List<Equipo> equipos = equipoRepository.findAll();
        List<Estadio> estadios = estadioRepository.findAll();

        // Mantener solo partidos entre los 4 equipos permitidos
        java.util.Map<String, Equipo> map = new java.util.HashMap<>();
        for (Equipo e : equipos) map.put(e.getNombre(), e);
        Equipo river = map.get("River Plate");
        Equipo boca = map.get("Boca Juniors");
        Equipo racing = map.get("Racing Club");
        Equipo indie = map.get("Independiente");
        if (river == null || boca == null || racing == null || indie == null || estadios.isEmpty()) {
            logger.warn("No hay equipos/estadios suficientes para crear partidos de 4 equipos");
            return;
        }

        List<Partido> partidos = Arrays.asList(
            new Partido(river, boca, estadios.get(0), LocalDateTime.of(2025, 10, 10, 10, 0), 1),
            new Partido(racing, indie, estadios.get(1 % estadios.size()), LocalDateTime.of(2025, 10, 10, 19, 0), 1),
            new Partido(river, racing, estadios.get(2 % estadios.size()), LocalDateTime.of(2025, 10, 17, 16, 0), 2),
            new Partido(boca, indie, estadios.get(3 % estadios.size()), LocalDateTime.of(2025, 10, 17, 19, 0), 2),
            new Partido(river, indie, estadios.get(4 % estadios.size()), LocalDateTime.of(2025, 10, 24, 16, 0), 3),
            new Partido(boca, racing, estadios.get(5 % estadios.size()), LocalDateTime.of(2025, 10, 24, 19, 0), 3)
        );

        // Registrar resultados de ejemplo (ajustado al tamaño de la lista)
        for (int i = 0; i < partidos.size(); i++) {
            switch (i % 6) {
                case 0 -> partidos.get(i).registrarResultado(2, 1);
                case 1 -> partidos.get(i).registrarResultado(1, 1);
                case 2 -> partidos.get(i).registrarResultado(3, 0);
                case 3 -> partidos.get(i).registrarResultado(0, 2);
                case 4 -> partidos.get(i).registrarResultado(1, 0);
                default -> partidos.get(i).registrarResultado(2, 0);
            }
        }
        

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