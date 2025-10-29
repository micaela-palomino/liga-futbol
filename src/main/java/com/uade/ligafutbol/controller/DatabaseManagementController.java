package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.service.DatabaseManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private DatabaseManagementService databaseService;

    /**
     * Página principal de gestión de BD
     */
    @GetMapping("/")
    public String databaseManagement(Model model) {
        model.addAttribute("pageTitle", "🗄️ Gestión de Base de Datos - Liga de Fútbol");
        return "web/database-management";
    }

    /**
     * 🏗️ Crear estructura completa de BD
     */
    @PostMapping("/crear-estructura")
    @ResponseBody
    public ResponseEntity<?> crearEstructura() {
        try {
            logger.info("🏗️ Iniciando creación de estructura de BD...");
            databaseService.crearEstructuraBaseDatos();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "✅ Estructura de base de datos creada exitosamente",
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error creando estructura: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error creando estructura: " + e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 📊 Llenar BD con información completa
     */
    @PostMapping("/llenar-datos")
    @ResponseBody
    public ResponseEntity<?> llenarDatos() {
        try {
            logger.info("📊 Iniciando carga de datos...");
            databaseService.llenarBaseDatosCompleta();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "✅ Base de datos llenada exitosamente",
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error llenando datos: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error llenando datos: " + e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 🔍 Ejecutar consultas predefinidas
     */
    @PostMapping("/ejecutar-consultas")
    @ResponseBody
    public ResponseEntity<?> ejecutarConsultas() {
        try {
            logger.info("🔍 Ejecutando consultas predefinidas...");
            Map<String, Object> resultados = databaseService.ejecutarConsultasPredefinidas();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "✅ Consultas ejecutadas exitosamente",
                "resultados", resultados,
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error ejecutando consultas: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error ejecutando consultas: " + e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 🔄 Proceso completo: Crear + Llenar + Consultar
     */
    @PostMapping("/proceso-completo")
    @ResponseBody
    public ResponseEntity<?> procesoCompleto() {
        try {
            logger.info("🔄 Iniciando proceso completo de BD...");
            
            // Paso 1: Crear estructura
            databaseService.crearEstructuraBaseDatos();
            
            // Paso 2: Llenar datos  
            databaseService.llenarBaseDatosCompleta();
            
            // Paso 3: Ejecutar consultas
            Map<String, Object> resultados = databaseService.ejecutarConsultasPredefinidas();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "🎉 Proceso completo ejecutado exitosamente",
                "pasos", Map.of(
                    "estructura_creada", "✅ Completado",
                    "datos_cargados", "✅ Completado", 
                    "consultas_ejecutadas", "✅ Completado"
                ),
                "resultados", resultados,
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error en proceso completo: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error en proceso completo: " + e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        }
    }

    /**
     * 📊 Obtener estadísticas actuales
     */
    @GetMapping("/estadisticas")
    @ResponseBody
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Map<String, Object> resultados = databaseService.ejecutarConsultasPredefinidas();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "estadisticas", resultados.get("estadisticas_generales"),
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("❌ Error obteniendo estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "❌ Error obteniendo estadísticas: " + e.getMessage()
            ));
        }
    }
}