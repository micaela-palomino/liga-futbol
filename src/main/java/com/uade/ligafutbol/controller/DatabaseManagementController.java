package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.config.DataLoader;
import com.uade.ligafutbol.repository.EquipoRepository;
import com.uade.ligafutbol.repository.EstadioRepository;
import com.uade.ligafutbol.repository.PartidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador para gestión completa de base de datos
 * Permite crear tablas, llenar datos y ejecutar consultas
 */
@Controller
@RequestMapping("/web/database")
public class DatabaseManagementController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagementController.class);

    @Autowired
    private DataLoader dataLoader;
    
    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private EstadioRepository estadioRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;

    /**
     * Página principal de gestión de BD
     */
    @GetMapping("/")
    public String databaseManagement(Model model) {
        model.addAttribute("pageTitle", "🗄️ Gestión de Base de Datos - Liga de Fútbol");
        return "web/database-management";
    }

    /**
     * 🏗️ Crear estructura - Simplificado (DataLoader se encarga de todo)
     */
    @PostMapping("/crear-estructura")
    @ResponseBody
    public ResponseEntity<?> crearEstructura() {
        try {
            logger.info("🏗️ Estructura manejada automáticamente por DataLoader");
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "✅ DataLoader maneja la estructura automáticamente",
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error: " + e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 📊 Los datos se cargan automáticamente por DataLoader al iniciar la aplicación
     */
    @PostMapping("/llenar-datos")
    @ResponseBody
    public ResponseEntity<?> llenarDatos() {
        try {
            logger.info("📊 Los datos se cargan automáticamente por DataLoader al iniciar");
            
            // Verificar si ya hay datos
            long equipos = equipoRepository.count();
            long estadios = estadioRepository.count();
            long partidos = partidoRepository.count();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "✅ DataLoader carga los datos automáticamente al iniciar la aplicación",
                "datos_actuales", Map.of(
                    "equipos", equipos,
                    "estadios", estadios, 
                    "partidos", partidos
                ),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error: " + e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 🔍 Mostrar datos básicos de la base de datos
     */
    @PostMapping("/ejecutar-consultas")
    @ResponseBody
    public ResponseEntity<?> ejecutarConsultas() {
        try {
            logger.info("🔍 Mostrando datos básicos...");
            
            // Datos simples y seguros
            long equipos = equipoRepository.count();
            long estadios = estadioRepository.count(); 
            long partidos = partidoRepository.count();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "✅ Datos obtenidos exitosamente",
                "resultados", Map.of(
                    "total_equipos", equipos,
                    "total_estadios", estadios,
                    "total_partidos", partidos,
                    "estado", equipos > 0 ? "Con datos" : "Vacía"
                ),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error obteniendo datos: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error", 
                "message", "❌ Error obteniendo datos: " + e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 🔄 Proceso completo: Solo mostrar estado de DataLoader
     */
    @PostMapping("/proceso-completo")
    @ResponseBody
    public ResponseEntity<?> procesoCompleto() {
        try {
            logger.info("🔄 Mostrando estado completo de la base de datos...");
            
            // Obtener estadísticas simples
            long equipos = equipoRepository.count();
            long estadios = estadioRepository.count(); 
            long partidos = partidoRepository.count();
            
            String mensaje = equipos > 0 ? 
                "🎉 DataLoader ha cargado los datos exitosamente" : 
                "⚠️ La base de datos está vacía - reinicia la aplicación para que DataLoader cargue los datos";
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", mensaje,
                "pasos", Map.of(
                    "dataloader_funcionando", equipos > 0 ? "✅ Completado" : "⚠️ Requiere reinicio",
                    "datos_disponibles", equipos > 0 ? "✅ Sí" : "❌ No"
                ),
                "estadisticas", Map.of(
                    "total_equipos", equipos,
                    "total_estadios", estadios,
                    "total_partidos", partidos,
                    "fecha_verificacion", LocalDateTime.now().toString()
                ),
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error en proceso completo: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error en proceso completo: " + e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 🔍 Query personalizada no disponible (simplificado)
     */
    @PostMapping("/ejecutar-query")
    @ResponseBody
    public ResponseEntity<?> ejecutarQueryPersonalizada(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of(
            "status", "info",
            "message", "💡 Queries personalizadas deshabilitadas. Usa solo DataLoader automático.",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * 📊 Obtener estadísticas simples usando repositories directamente
     */
    @GetMapping("/estadisticas")
    @ResponseBody
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            // Usar repositories directamente - más simple y confiable
            long equipos = equipoRepository.count();
            long estadios = estadioRepository.count();
            long partidos = partidoRepository.count();
            
            Map<String, Object> estadisticas = Map.of(
                "total_equipos", equipos,
                "total_estadios", estadios,
                "total_partidos", partidos,
                "total_relaciones", 0L // Simplificado
            );
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "estadisticas", estadisticas,
                "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error obteniendo estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error obteniendo estadísticas: " + e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }
}