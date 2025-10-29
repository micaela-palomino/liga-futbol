package com.uade.ligafutbol.service;

import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.Estadio;
import com.uade.ligafutbol.model.Partido;
import com.uade.ligafutbol.repository.EquipoRepository;
import com.uade.ligafutbol.repository.EstadioRepository;
import com.uade.ligafutbol.repository.PartidoRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión completa de la base de datos Neo4j
 * Incluye creación de estructuras, carga de datos y consultas
 */
@Service
public class DatabaseManagementService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagementService.class);

    @Autowired
    private Driver neo4jDriver;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private EstadioRepository estadioRepository;

    @Autowired
    private PartidoRepository partidoRepository;

    /**
     * 🏗️ PASO 1: Crear las estructuras de base de datos desde cero
     */
    public void crearEstructuraBaseDatos() {
        logger.info("🏗️ Creando estructura completa de base de datos...");

        try (Session session = neo4jDriver.session()) {
            // 1. Limpiar toda la base de datos
            logger.info("🧹 Limpiando base de datos...");
            session.run("MATCH (n) DETACH DELETE n");

            // 2. Crear restricciones (constraints)
            logger.info("🔒 Creando restricciones...");
            crearRestricciones(session);

            // 3. Crear índices para optimización
            logger.info("📊 Creando índices...");
            crearIndices(session);

            logger.info("✅ Estructura de base de datos creada exitosamente.");
        } catch (Exception e) {
            logger.error("❌ Error creando estructura: {}", e.getMessage(), e);
            throw new RuntimeException("Error creando estructura de BD", e);
        }
    }

    /**
     * 📊 PASO 2: Llenar la base de datos con información completa
     */
    public void llenarBaseDatosCompleta() {
        logger.info("📊 Llenando base de datos con información completa...");

        try {
            // 1. Cargar estadios
            cargarEstadiosCompletos();

            // 2. Cargar equipos
            cargarEquiposCompletos();

            // 3. Cargar partidos
            cargarPartidosCompletos();

            // 4. Crear relaciones
            crearRelacionesCompletas();

            logger.info("✅ Base de datos llenada exitosamente.");
        } catch (Exception e) {
            logger.error("❌ Error llenando BD: {}", e.getMessage(), e);
            throw new RuntimeException("Error llenando base de datos", e);
        }
    }

    /**
     * 🔍 PASO 3: Preparar y ejecutar consultas predefinidas
     */
    public Map<String, Object> ejecutarConsultasPredefinidas() {
        logger.info("🔍 Ejecutando consultas predefinidas...");

        try (Session session = neo4jDriver.session()) {
            Map<String, Object> resultados = Map.of(
                "equipos_por_ciudad", obtenerEquiposPorCiudad(session),
                "estadios_mas_grandes", obtenerEstadiosMasGrandes(session),
                "partidos_recientes", obtenerPartidosRecientes(session),
                "conexiones_equipos", obtenerConexionesEquipos(session),
                "estadisticas_generales", obtenerEstadisticasGenerales(session)
            );

            logger.info("✅ Consultas ejecutadas exitosamente.");
            return resultados;
        } catch (Exception e) {
            logger.error("❌ Error ejecutando consultas: {}", e.getMessage(), e);
            throw new RuntimeException("Error ejecutando consultas", e);
        }
    }

    // ========== MÉTODOS PRIVADOS DE APOYO ==========

    private void crearRestricciones(Session session) {
        String[] restricciones = {
            "CREATE CONSTRAINT equipo_nombre_unique IF NOT EXISTS FOR (e:Equipo) REQUIRE e.nombre IS UNIQUE",
            "CREATE CONSTRAINT estadio_nombre_unique IF NOT EXISTS FOR (s:Estadio) REQUIRE s.nombre IS UNIQUE",
            "CREATE CONSTRAINT partido_id_unique IF NOT EXISTS FOR (p:Partido) REQUIRE p.id IS UNIQUE"
        };

        for (String restriccion : restricciones) {
            try {
                session.run(restriccion);
                logger.info("✓ Restricción creada: {}", restriccion.split(" ")[2]);
            } catch (Exception e) {
                logger.warn("⚠️ Restricción ya existe o error: {}", e.getMessage());
            }
        }
    }

    private void crearIndices(Session session) {
        String[] indices = {
            "CREATE INDEX equipo_ciudad_idx IF NOT EXISTS FOR (e:Equipo) ON (e.ciudad)",
            "CREATE INDEX estadio_capacidad_idx IF NOT EXISTS FOR (s:Estadio) ON (s.capacidad)",
            "CREATE INDEX partido_fecha_idx IF NOT EXISTS FOR (p:Partido) ON (p.fecha)",
            "CREATE INDEX estadio_ciudad_idx IF NOT EXISTS FOR (s:Estadio) ON (s.ciudad)"
        };

        for (String indice : indices) {
            try {
                session.run(indice);
                logger.info("✓ Índice creado: {}", indice.split(" ")[2]);
            } catch (Exception e) {
                logger.warn("⚠️ Índice ya existe o error: {}", e.getMessage());
            }
        }
    }

    private void cargarEstadiosCompletos() {
        logger.info("🏟️ Cargando estadios completos...");

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

        estadioRepository.saveAll(estadios);
        logger.info("✓ {} estadios cargados", estadios.size());
    }

    private void cargarEquiposCompletos() {
        logger.info("⚽ Cargando equipos completos...");

        List<Equipo> equipos = Arrays.asList(
            new Equipo("Boca Juniors", "Buenos Aires"),
            new Equipo("River Plate", "Buenos Aires"),
            new Equipo("Racing Club", "Avellaneda"),
            new Equipo("Independiente", "Avellaneda"),
            new Equipo("San Lorenzo", "Buenos Aires"),
            new Equipo("Estudiantes", "La Plata"),
            new Equipo("Gimnasia La Plata", "La Plata"),
            new Equipo("Vélez Sarsfield", "Buenos Aires"),
            new Equipo("Talleres", "Córdoba"),
            new Equipo("Belgrano", "Córdoba")
        );

        equipoRepository.saveAll(equipos);
        logger.info("✓ {} equipos cargados", equipos.size());
    }

    private void cargarPartidosCompletos() {
        logger.info("🏆 Cargando partidos completos...");

        // Los partidos se cargarán después de que existan equipos y estadios
        // Implementaremos esto después de que las entidades estén guardadas
    }

    private void crearRelacionesCompletas() {
        logger.info("🔗 Creando relaciones completas...");

        try (Session session = neo4jDriver.session()) {
            // Crear relaciones JUEGA_EN entre equipos y estadios
            String queryRelacionesEstadio = """
                MATCH (e:Equipo), (s:Estadio)
                WHERE e.ciudad = s.ciudad
                CREATE (e)-[:JUEGA_EN]->(s)
            """;
            
            session.run(queryRelacionesEstadio);
            logger.info("✓ Relaciones JUEGA_EN creadas");

            // Crear relaciones de rivalidad
            String queryRivalidades = """
                MATCH (boca:Equipo {nombre: 'Boca Juniors'}), (river:Equipo {nombre: 'River Plate'})
                CREATE (boca)-[:RIVAL_DE {intensidad: 'ALTA', desde: 1913}]->(river)
                CREATE (river)-[:RIVAL_DE {intensidad: 'ALTA', desde: 1913}]->(boca)
            """;
            
            session.run(queryRivalidades);
            logger.info("✓ Relaciones de rivalidad creadas");
        }
    }

    // ========== CONSULTAS PREDEFINIDAS ==========

    private List<Map<String, Object>> obtenerEquiposPorCiudad(Session session) {
        String query = """
            MATCH (e:Equipo)
            RETURN e.ciudad as ciudad, collect(e.nombre) as equipos, count(e) as cantidad
            ORDER BY cantidad DESC
        """;
        
        return session.run(query).list(record -> Map.of(
            "ciudad", record.get("ciudad").asString(),
            "equipos", record.get("equipos").asList(),
            "cantidad", record.get("cantidad").asInt()
        ));
    }

    private List<Map<String, Object>> obtenerEstadiosMasGrandes(Session session) {
        String query = """
            MATCH (s:Estadio)
            RETURN s.nombre as nombre, s.capacidad as capacidad, s.ciudad as ciudad
            ORDER BY s.capacidad DESC
            LIMIT 5
        """;
        
        return session.run(query).list(record -> Map.of(
            "nombre", record.get("nombre").asString(),
            "capacidad", record.get("capacidad").asInt(),
            "ciudad", record.get("ciudad").asString()
        ));
    }

    private List<Map<String, Object>> obtenerPartidosRecientes(Session session) {
        String query = """
            MATCH (p:Partido)
            RETURN p.equipoLocal as local, p.equipoVisitante as visitante, 
            p.fecha as fecha, p.estadio as estadio
            ORDER BY p.fecha DESC
            LIMIT 10
        """;
        
        return session.run(query).list(record -> Map.of(
            "local", record.get("local").asString(),
            "visitante", record.get("visitante").asString(),
            "fecha", record.get("fecha").asString(),
            "estadio", record.get("estadio").asString()
        ));
    }

    private List<Map<String, Object>> obtenerConexionesEquipos(Session session) {
        String query = """
            MATCH (e1:Equipo)-[r:RIVAL_DE]->(e2:Equipo)
            RETURN e1.nombre as equipo1, e2.nombre as equipo2, 
                   r.intensidad as intensidad, r.desde as desde
        """;
        
        return session.run(query).list(record -> Map.of(
            "equipo1", record.get("equipo1").asString(),
            "equipo2", record.get("equipo2").asString(),
            "intensidad", record.get("intensidad").asString(),
            "desde", record.get("desde").asInt()
        ));
    }

    private Map<String, Object> obtenerEstadisticasGenerales(Session session) {
        String queryEquipos = "MATCH (e:Equipo) RETURN count(e) as total";
        String queryEstadios = "MATCH (s:Estadio) RETURN count(s) as total";
        String queryPartidos = "MATCH (p:Partido) RETURN count(p) as total";
        
        int totalEquipos = session.run(queryEquipos).single().get("total").asInt();
        int totalEstadios = session.run(queryEstadios).single().get("total").asInt();
        int totalPartidos = session.run(queryPartidos).single().get("total").asInt();
        
        return Map.of(
            "total_equipos", totalEquipos,
            "total_estadios", totalEstadios,
            "total_partidos", totalPartidos,
            "fecha_actualizacion", LocalDateTime.now().toString()
        );
    }

    /**
     * 📊 Obtener estadísticas generales de la base de datos
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        try (Session session = neo4jDriver.session()) {
            // Contar equipos
            Result equiposResult = session.run("MATCH (e:Equipo) RETURN count(e) as total");
            estadisticas.put("total_equipos", equiposResult.single().get("total").asInt());
            
            // Contar estadios
            Result estadiosResult = session.run("MATCH (est:Estadio) RETURN count(est) as total");
            estadisticas.put("total_estadios", estadiosResult.single().get("total").asInt());
            
            // Contar partidos
            Result partidosResult = session.run("MATCH (p:Partido) RETURN count(p) as total");
            estadisticas.put("total_partidos", partidosResult.single().get("total").asInt());
            
            // Contar relaciones
            Result relacionesResult = session.run("MATCH ()-[r]->() RETURN count(r) as total");
            estadisticas.put("total_relaciones", relacionesResult.single().get("total").asInt());
            
            logger.info("📊 Estadísticas obtenidas: equipos={}, estadios={}, partidos={}", 
                estadisticas.get("total_equipos"), 
                estadisticas.get("total_estadios"), 
                estadisticas.get("total_partidos"));
                
        } catch (Exception e) {
            logger.error("❌ Error obteniendo estadísticas: {}", e.getMessage(), e);
            // Devolver valores por defecto si hay error
            estadisticas.put("total_equipos", 0);
            estadisticas.put("total_estadios", 0);
            estadisticas.put("total_partidos", 0);
            estadisticas.put("total_relaciones", 0);
        }
        
        return estadisticas;
    }
}