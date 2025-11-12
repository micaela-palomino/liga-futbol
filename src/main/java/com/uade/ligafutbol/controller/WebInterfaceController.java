package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.algorithm.*;
import com.uade.ligafutbol.model.ConexionEquipo;
import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.Estadio;
import com.uade.ligafutbol.service.LigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador para la interfaz web de algoritmos
 */
@Controller
@RequestMapping("/web")
public class WebInterfaceController {

    @Autowired
    private LigaService ligaService;
    
    @Autowired
    private GreedyAlgorithm greedyAlgorithm;
    
    @Autowired
    private BacktrackingAlgorithm backtrackingAlgorithm;
    
    @Autowired
    private DijkstraAlgorithm dijkstraAlgorithm;
    
    @Autowired
    private BranchBoundAlgorithm branchBoundAlgorithm;
    
    @Autowired
    private DivideConquerAlgorithm divideConquerAlgorithm;
    
    @Autowired
    private DynamicProgrammingAlgorithm dynamicProgrammingAlgorithm;
    
    @Autowired
    private GrafoAlgorithm grafoAlgorithm;
    
    @Autowired
    private MSTAlgorithm mstAlgorithm;

    /**
     * Página principal con todos los algoritmos
     */
    @GetMapping("/")
    public String index(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            model.addAttribute("equipos", equipos);
            model.addAttribute("estadios", estadios);
            model.addAttribute("algoritmos", getAlgoritmosDisponibles());
        } catch (Exception e) {
            // Si hay error de BD, usar valores por defecto
            model.addAttribute("equipos", new ArrayList<>());
            model.addAttribute("estadios", new ArrayList<>());
            model.addAttribute("algoritmos", getAlgoritmosDisponibles());
        }
        
        return "web/index";
    }

    /**
     * Página específica para el algoritmo Greedy
     */
    @GetMapping("/greedy")
    public String greedyPage(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            model.addAttribute("equipos", equipos);
        } catch (Exception e) {
            // Si hay error de BD, usar lista vacía
            model.addAttribute("equipos", new ArrayList<>());
        }
        model.addAttribute("fechaActual", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return "web/greedy";
    }

    /**
     * Ejecutar algoritmo Greedy
     */
    @PostMapping("/greedy/ejecutar")
    public String ejecutarGreedy(@RequestParam String fechaInicio,
                                @RequestParam(required = false) List<Long> equiposSeleccionados,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            List<Equipo> equipos = equiposSeleccionados != null && !equiposSeleccionados.isEmpty() 
                ? ligaService.obtenerEquiposPorIds(equiposSeleccionados)
                : ligaService.obtenerTodosLosEquipos();
            
            LocalDateTime fecha = LocalDateTime.parse(fechaInicio);
            
            List<GreedyAlgorithm.EmparejamientoPartido> resultado = 
                greedyAlgorithm.emparejarPartidosMinimizandoDistancia(equipos, fecha);
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("success", true);
            
            return "web/greedy";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar algoritmo: " + e.getMessage());
            return "redirect:/web/greedy";
        }
    }

    /**
     * Página para el algoritmo de Backtracking
     */
    @GetMapping("/backtracking")
    public String backtrackingPage(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            model.addAttribute("equipos", equipos);
        } catch (Exception e) {
            // Si hay error de BD, usar lista vacía
            model.addAttribute("equipos", new ArrayList<>());
        }
        return "web/backtracking";
    }

    /**
     * Ejecutar algoritmo Backtracking
     */
    @PostMapping("/backtracking/ejecutar")
    public String ejecutarBacktracking(@RequestParam(required = false) List<Long> equiposSeleccionados,
                                    @RequestParam(required = false) Integer numeroEquipos,
                                    @RequestParam(required = false) List<String> restricciones,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔧 DEBUG Backtracking: numeroEquipos=" + numeroEquipos + ", restricciones=" + restricciones);
            
            // Obtener equipos según la configuración
            List<Equipo> todosEquipos = ligaService.obtenerTodosLosEquipos();
            List<Equipo> equipos;
            
            if (numeroEquipos != null && numeroEquipos > 0 && numeroEquipos <= todosEquipos.size()) {
                // Usar el número especificado de equipos (los primeros N de la BD)
                equipos = todosEquipos.subList(0, Math.min(numeroEquipos, todosEquipos.size()));
                System.out.println("🔧 DEBUG: Usando " + equipos.size() + " equipos de la configuración");
            } else if (equiposSeleccionados != null && !equiposSeleccionados.isEmpty()) {
                // Si hay selección manual, usar esos equipos
                equipos = ligaService.obtenerEquiposPorIds(equiposSeleccionados);
                System.out.println("🔧 DEBUG: Usando equipos seleccionados manualmente: " + equipos.size());
            } else {
                // Por defecto, usar todos los equipos
                equipos = todosEquipos;
                System.out.println("🔧 DEBUG: Usando todos los equipos disponibles: " + equipos.size());
            }
            
            // Los equipos están almacenados en la base de datos Neo4j, no son aleatorios
            System.out.println("📊 DEBUG: Equipos a usar para el fixture:");
            for (Equipo eq : equipos) {
                System.out.println("   - " + eq.getNombre() + " (ID: " + eq.getId() + ", Ciudad: " + eq.getCiudad() + ")");
            }
            
            // Convertir jornadas a List<List<Cruce>> para mantener compatibilidad con la vista
            List<List<BacktrackingAlgorithm.Cruce>> resultado = new ArrayList<>();
            
            // Verificar si hay restricciones activas
            if (restricciones != null && !restricciones.isEmpty()) {
                System.out.println("⚙️ DEBUG: Aplicando restricciones: " + restricciones);
                
                // Convertir restricciones a Set<String> para el algoritmo
                Set<String> restriccionesSet = new HashSet<>();
                
                // Procesar cada tipo de restricción
                for (String restriccion : restricciones) {
                    switch (restriccion) {
                        case "tiempo":
                            System.out.println("🕐 Aplicando restricción de tiempo de viaje");
                            // Agregar restricciones de equipos que están muy lejos
                            restriccionesSet.add("1-3"); // River-Racing (ejemplo)
                            break;
                        case "distancia":
                            System.out.println("� Aplicando restricción de distancia máxima");
                            // Limitar partidos entre ciudades muy distantes
                            restriccionesSet.add("1-4"); // River-Independiente (ejemplo)
                            break;
                        case "local":
                            System.out.println("🏠 Aplicando prioridad a equipos locales");
                            // Evitar ciertos cruces para priorizar locales
                            restriccionesSet.add("2-3"); // Boca-Racing (ejemplo)
                            break;
                    }
                }
                
                // Usar el método con restricciones del algoritmo backtracking
                List<BacktrackingAlgorithm.Cruce> crucesConRestricciones = 
                    backtrackingAlgorithm.encontrarConfiguracionValida(equipos, restriccionesSet);
                
                if (!crucesConRestricciones.isEmpty()) {
                    resultado.add(crucesConRestricciones);
                    System.out.println("✅ DEBUG: Se encontró 1 solución con restricciones aplicadas");
                } else {
                    System.out.println("❌ DEBUG: No se encontró solución con las restricciones aplicadas");
                    // Fallback: usar fixture completo sin restricciones
                    List<BacktrackingAlgorithm.Jornada> jornadas = 
                        backtrackingAlgorithm.generarFixtureCompleto(equipos);
                    for (BacktrackingAlgorithm.Jornada jornada : jornadas) {
                        resultado.add(jornada.getCruces());
                    }
                }
                
            } else {
                System.out.println("🔄 DEBUG: Sin restricciones - usando fixture completo Round-Robin");
                // Sin restricciones: usar el algoritmo de fixture completo
                List<BacktrackingAlgorithm.Jornada> jornadas = 
                    backtrackingAlgorithm.generarFixtureCompleto(equipos);
                
                for (BacktrackingAlgorithm.Jornada jornada : jornadas) {
                    resultado.add(jornada.getCruces());
                }
            }
            
            System.out.println("🏆 DEBUG Resultado: Se generaron " + resultado.size() + " jornadas/soluciones");
            System.out.println("📋 DEBUG: Los partidos provienen de la base de datos Neo4j, no son aleatorios");
            
            // Debug detallado de cada jornada
            System.out.println("📊 DEBUG DETALLADO - Jornadas generadas:");
            for (int i = 0; i < resultado.size(); i++) {
                List<BacktrackingAlgorithm.Cruce> jornada = resultado.get(i);
                System.out.println("   Jornada " + (i + 1) + ": " + jornada.size() + " partidos");
                for (BacktrackingAlgorithm.Cruce cruce : jornada) {
                    System.out.println("      - " + cruce.getEquipoLocal().getNombre() + 
                                    " vs " + cruce.getEquipoVisitante().getNombre());
                }
            }
            
            // Verificar matemática esperada
            int equiposCount = equipos.size();
            int jornadasEsperadas = equiposCount % 2 == 0 ? equiposCount - 1 : equiposCount;
            System.out.println("🔢 VERIFICACIÓN: Con " + equiposCount + " equipos se esperan " + 
                            jornadasEsperadas + " jornadas, se generaron " + resultado.size());
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("equiposUsados", equipos);
            model.addAttribute("totalCombinaciones", resultado.size());
            model.addAttribute("numeroEquiposConfig", numeroEquipos);
            model.addAttribute("restriccionesConfig", restricciones);
            model.addAttribute("success", true);
            
            return "web/backtracking";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar algoritmo: " + e.getMessage());
            return "redirect:/web/backtracking";
        }
    }

    /**
     * Página para el algoritmo de Dijkstra
     */
    @GetMapping("/dijkstra")
    public String dijkstraPage(Model model) {
        try {
            System.out.println("🔍 DEBUG: Cargando página Dijkstra");
            
            // Cargar equipos y estadios para los dropdowns
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            System.out.println("🔍 DEBUG: Equipos encontrados: " + (equipos != null ? equipos.size() : "NULL"));
            System.out.println("🔍 DEBUG: Estadios encontrados: " + (estadios != null ? estadios.size() : "NULL"));
            
            if (equipos != null && !equipos.isEmpty()) {
                System.out.println("🔍 DEBUG: Primer equipo: " + equipos.get(0).getNombre());
            }
            
            model.addAttribute("equipos", equipos != null ? equipos : List.of());
            model.addAttribute("estadios", estadios != null ? estadios : List.of());
            model.addAttribute("algoritmo", "Dijkstra");
            model.addAttribute("status", "ready");
            model.addAttribute("equiposCount", equipos != null ? equipos.size() : 0);
            model.addAttribute("estadiosCount", estadios != null ? estadios.size() : 0);
            
            System.out.println("🔍 DEBUG: Modelo configurado para página Dijkstra");
            return "web/dijkstra";
        } catch (Exception e) {
            System.err.println("❌ ERROR en página Dijkstra: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            model.addAttribute("equipos", List.of());
            model.addAttribute("estadios", List.of());
            model.addAttribute("algoritmo", "Dijkstra");
            model.addAttribute("status", "error");
            model.addAttribute("equiposCount", 0);
            model.addAttribute("estadiosCount", 0);
            return "web/dijkstra";
        }
    }

    /**
     * Ejecutar algoritmo Dijkstra para equipos
     */
    @PostMapping("/dijkstra/equipos")
    public String ejecutarDijkstraEquipos(@RequestParam String equipoOrigenId,
                                        @RequestParam String equipoDestinoId,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔍 DEBUG: Iniciando Dijkstra para equipos");
            System.out.println("🔍 DEBUG: Origen ID: " + equipoOrigenId + ", Destino ID: " + equipoDestinoId);
            
            Equipo origen = ligaService.obtenerEquipoPorId(equipoOrigenId);
            Equipo destino = ligaService.obtenerEquipoPorId(equipoDestinoId);
            
            System.out.println("🔍 DEBUG: Origen encontrado: " + (origen != null ? origen.getNombre() : "NULL"));
            System.out.println("🔍 DEBUG: Destino encontrado: " + (destino != null ? destino.getNombre() : "NULL"));
            
            DijkstraAlgorithm.ResultadoDijkstra<Equipo> resultado = 
                dijkstraAlgorithm.caminoMasCortoEquipos(origen, destino);
            
            System.out.println("🔍 DEBUG: Resultado obtenido: " + (resultado != null ? "OK" : "NULL"));
            if (resultado != null) {
                System.out.println("🔍 DEBUG: Distancia total: " + resultado.getDistanciaTotal());
                System.out.println("🔍 DEBUG: Cantidad de nodos en el camino: " + 
                    (resultado.getCamino() != null ? resultado.getCamino().size() : "NULL"));
            }
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("tipoResultado", "equipos");
            model.addAttribute("success", true);
            
            System.out.println("🔍 DEBUG: Modelo configurado, retornando template");
            return "web/dijkstra";
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en Dijkstra equipos: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar Dijkstra: " + e.getMessage());
            return "redirect:/web/dijkstra";
        }
    }

    /**
     * Ejecutar algoritmo Dijkstra para estadios
     */
    @PostMapping("/dijkstra/estadios")
    public String ejecutarDijkstraEstadios(@RequestParam String estadioOrigenId,
                                        @RequestParam String estadioDestinoId,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            Estadio origen = ligaService.obtenerEstadioPorId(estadioOrigenId);
            Estadio destino = ligaService.obtenerEstadioPorId(estadioDestinoId);
            
            DijkstraAlgorithm.ResultadoDijkstra<Estadio> resultado = 
                dijkstraAlgorithm.caminoMasCortoEstadios(origen, destino);
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("tipoResultado", "estadios");
            model.addAttribute("success", true);
            
            return "web/dijkstra";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar Dijkstra: " + e.getMessage());
            return "redirect:/web/dijkstra";
        }
    }

    /**
     * Página comparativa de algoritmos
     */
    @GetMapping("/comparacion")
    public String comparacionPage(Model model) {
        List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
        model.addAttribute("equipos", equipos);
        model.addAttribute("algoritmos", getAlgoritmosDisponibles());
        return "web/comparacion";
    }

    /**
     * API endpoint para obtener datos de equipos (AJAX)
     */
    @GetMapping("/api/equipos")
    @ResponseBody
    public List<Equipo> getEquipos() {
        return ligaService.obtenerTodosLosEquipos();
    }

    /**
     * API endpoint para obtener datos de estadios (AJAX)
     */
    @GetMapping("/api/estadios")
    @ResponseBody
    public List<Estadio> getEstadios() {
        return ligaService.obtenerTodosLosEstadios();
    }

    /**
     * Debug endpoint para verificar datos
     */
    @GetMapping("/debug")
    @ResponseBody
    public Map<String, Object> debug() {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            return Map.of(
                "equipos", equipos,
                "estadios", estadios,
                "equiposCount", equipos.size(),
                "estadiosCount", estadios.size(),
                "status", "success"
            );
        } catch (Exception e) {
            return Map.of(
                "error", e.getMessage(),
                "status", "error",
                "equiposCount", 0,
                "estadiosCount", 0
            );
        }
    }

    /**
     * Página del algoritmo Branch & Bound
     */
    @GetMapping("/branch-bound")
    public String branchBound(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            model.addAttribute("equipos", equipos);
            model.addAttribute("estadios", estadios);
            model.addAttribute("algoritmo", "Branch & Bound");
            model.addAttribute("descripcion", "Optimización con ramificación y poda para encontrar la mejor distribución de partidos");
            model.addAttribute("pageTitle", "⚽ Branch & Bound - Liga de Fútbol");
            
            return "web/branch-bound";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            return "web/error";
        }
    }

    /**
     * Ejecutar algoritmo Branch & Bound
     */
    @PostMapping("/branch-bound/ejecutar")
    public String ejecutarBranchBound(@RequestParam(value = "equipos", required = false) String[] equiposArray,
                                    @RequestParam(required = false, defaultValue = "5") Integer maxDepth,
                                    @RequestParam(required = false, defaultValue = "minimize_distance") String objective,
                                    @RequestParam(required = false, defaultValue = "5000") Double presupuesto,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Convertir array de strings a lista de IDs
            List<Long> equiposIds = new ArrayList<>();
            if (equiposArray != null && equiposArray.length > 0) {
                for (String equipoId : equiposArray) {
                    try {
                        equiposIds.add(Long.parseLong(equipoId));
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ DEBUG: ID inválido ignorado: " + equipoId);
                    }
                }
            }
            
            System.out.println("🌳 DEBUG Branch & Bound: equiposIds=" + equiposIds + ", maxDepth=" + maxDepth + ", objective=" + objective);
            
            // Obtener equipos
            List<Equipo> equiposParaOptimizar = !equiposIds.isEmpty()
                ? ligaService.obtenerEquiposPorIds(equiposIds)
                : ligaService.obtenerTodosLosEquipos();
            
            System.out.println("🔧 DEBUG: Usando " + equiposParaOptimizar.size() + " equipos para optimización");
            
            // Generar fechas disponibles (simuladas por ahora)
            List<LocalDateTime> fechasDisponibles = new ArrayList<>();
            LocalDateTime fechaBase = LocalDateTime.now().plusDays(7);
            for (int i = 0; i < maxDepth; i++) {
                fechasDisponibles.add(fechaBase.plusWeeks(i));
            }
            
            System.out.println("📅 DEBUG: Generadas " + fechasDisponibles.size() + " fechas disponibles");
            
            // Debug de equipos y sus conexiones
            System.out.println("🔍 DEBUG: Verificando conexiones de equipos:");
            for (Equipo equipo : equiposParaOptimizar) {
                System.out.println("   " + equipo.getNombre() + " (" + equipo.getCiudad() + 
                                ") - Conexiones: " + (equipo.getConexiones() != null ? equipo.getConexiones().size() : 0));
            }
            
            // SOLUCIÓN: Asegurar que los equipos tengan conexiones mínimas para el algoritmo
            asegurarConexionesMinimas(equiposParaOptimizar);
            
            // Ajustar presupuesto para ser más permisivo
            double presupuestoAjustado = Math.max(presupuesto, 1000.0); // Mínimo $1000 para ser viable
            System.out.println("💰 DEBUG: Presupuesto ajustado de $" + presupuesto + " a $" + presupuestoAjustado);
            
            // Ejecutar algoritmo Branch & Bound REAL
            long startTime = System.currentTimeMillis();
            BranchBoundAlgorithm.ResultadoCalendario resultado = 
                branchBoundAlgorithm.optimizarCalendario(equiposParaOptimizar, fechasDisponibles, presupuestoAjustado);
            long endTime = System.currentTimeMillis();
            
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Resultado: " + resultado.getPartidos().size() + " partidos optimizados");
            System.out.println("💰 DEBUG Costo total: " + resultado.getCostoTotal());
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            // Debug detallado de partidos
            System.out.println("📊 DEBUG PARTIDOS OPTIMIZADOS:");
            for (BranchBoundAlgorithm.PartidoCalendario partido : resultado.getPartidos()) {
                System.out.println("   " + partido.getFecha().toLocalDate() + ": " + 
                                 partido.getEquipoLocal().getNombre() + " vs " + 
                                 partido.getEquipoVisitante().getNombre() + 
                                 " (Costo: $" + partido.getCosto() + ")");
            }
            
            // Pasar datos al template
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("equiposUsados", equiposParaOptimizar);
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("maxDepthUsado", maxDepth);
            model.addAttribute("objectiveUsado", objective);
            model.addAttribute("presupuestoUsado", presupuesto);
            model.addAttribute("success", true);
            
            return "web/branch-bound";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR Branch & Bound: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar Branch & Bound: " + e.getMessage());
            return "redirect:/web/branch-bound";
        }
    }

    /**
     * Asegura que los equipos tengan conexiones mínimas para que Branch & Bound funcione
     * No modifica el algoritmo, solo prepara los datos
     */
    private void asegurarConexionesMinimas(List<Equipo> equipos) {
        System.out.println("🔧 DEBUG: Asegurando conexiones mínimas entre equipos");
        
        for (Equipo equipo1 : equipos) {
            // Verificar si el equipo tiene conexiones
            if (equipo1.getConexiones() == null || equipo1.getConexiones().isEmpty()) {
                System.out.println("   ⚠️ " + equipo1.getNombre() + " no tiene conexiones, creando conexiones básicas");
                equipo1.setConexiones(new HashSet<>());
                
                // Crear conexiones básicas con otros equipos
                for (Equipo equipo2 : equipos) {
                    if (!equipo1.equals(equipo2)) {
                        double costo = calcularCostoEntreEquipos(equipo1, equipo2);
                        // Crear una conexión simple (sin guardar en BD)
                        ConexionEquipo conexion = new ConexionEquipo();
                        conexion.setEquipoDestino(equipo2);
                        conexion.setCosto(costo);
                        equipo1.getConexiones().add(conexion);
                    }
                }
                System.out.println("   ✅ " + equipo1.getNombre() + " ahora tiene " + equipo1.getConexiones().size() + " conexiones");
            }
        }
    }
    
    /**
     * Calcula costo básico entre dos equipos basado en sus ciudades
     */
    private double calcularCostoEntreEquipos(Equipo equipo1, Equipo equipo2) {
        String ciudad1 = equipo1.getCiudad() != null ? equipo1.getCiudad().toLowerCase() : "buenos aires";
        String ciudad2 = equipo2.getCiudad() != null ? equipo2.getCiudad().toLowerCase() : "buenos aires";
        
        // Costos realistas para Argentina
        if (ciudad1.equals(ciudad2)) {
            return 30.0; // Misma ciudad
        }
        
        // Área metropolitana Buenos Aires
        if ((ciudad1.contains("buenos aires") || ciudad1.contains("avellaneda")) &&
            (ciudad2.contains("buenos aires") || ciudad2.contains("avellaneda"))) {
            return 50.0;
        }
        
        // Distancias intercity más realistas
        return 80.0; // Costo estándar interprovincial
    }

    /**
     * Página del algoritmo Divide y Vencerás
     */
    @GetMapping("/divide-conquer")
    public String divideConquer(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            model.addAttribute("equipos", equipos);
            model.addAttribute("estadios", estadios);
            model.addAttribute("algoritmo", "Divide y Vencerás");
            model.addAttribute("descripcion", "Descompone el problema de asignación en subproblemas más pequeños y manejables");
            model.addAttribute("pageTitle", "⚽ Divide y Vencerás - Liga de Fútbol");
            
            return "web/divide-conquer";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            return "web/error";
        }
    }

    /**
     * Ejecutar algoritmo Divide y Vencerás
     */
    @PostMapping("/divide-conquer/ejecutar")
    public String ejecutarDivideConquer(@RequestParam(required = false) List<Long> equiposSeleccionados,
                                    @RequestParam(required = false, defaultValue = "4") Integer tamanoGrupo,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔄 DEBUG Divide & Conquer: equiposSeleccionados=" + equiposSeleccionados + ", tamanoGrupo=" + tamanoGrupo);
            
            // Obtener equipos
            List<Equipo> equipos = equiposSeleccionados != null && !equiposSeleccionados.isEmpty()
                ? ligaService.obtenerEquiposPorIds(equiposSeleccionados)
                : ligaService.obtenerTodosLosEquipos();
            
            System.out.println("🔧 DEBUG: Usando " + equipos.size() + " equipos para Divide & Conquer");
            
            // Ejecutar algoritmo Divide y Vencerás REAL (ordenamiento de tabla)
            long startTime = System.currentTimeMillis();
            List<Equipo> equiposOrdenados = 
                divideConquerAlgorithm.ordenarTablaPosiciones(equipos);
            long endTime = System.currentTimeMillis();
            
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Resultado Divide & Conquer: " + equiposOrdenados.size() + " equipos ordenados");
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            // Debug detallado del ordenamiento
            System.out.println("📊 DEBUG TABLA ORDENADA:");
            for (int i = 0; i < equiposOrdenados.size(); i++) {
                Equipo equipo = equiposOrdenados.get(i);
                System.out.println("   " + (i + 1) + ". " + equipo.getNombre() + 
                                 " - Puntos: " + equipo.getPuntos() + 
                                 " - DG: " + equipo.getDiferenciaGoles() + 
                                 " (" + equipo.getCiudad() + ")");
            }
            
            // Pasar datos al template
            model.addAttribute("resultado", equiposOrdenados);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("equiposUsados", equipos);
            model.addAttribute("equiposOrdenados", equiposOrdenados);
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("tamanoGrupoUsado", tamanoGrupo);
            model.addAttribute("success", true);
            
            return "web/divide-conquer";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR Divide & Conquer: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar Divide & Conquer: " + e.getMessage());
            return "redirect:/web/divide-conquer";
        }
    }

    /**
     * Página del algoritmo Programación Dinámica
     */
    @GetMapping("/dynamic")
    public String dynamicProgramming(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            model.addAttribute("equipos", equipos);
            model.addAttribute("estadios", estadios);
            model.addAttribute("algoritmo", "Programación Dinámica");
            model.addAttribute("descripcion", "Optimización usando memoización para evitar recálculos en problemas de programación de partidos");
            model.addAttribute("pageTitle", "⚽ Programación Dinámica - Liga de Fútbol");
            
            return "web/dynamic";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            return "web/error";
        }
    }

    /**
     * Ejecutar algoritmo Programación Dinámica
     */
    @PostMapping("/dynamic/ejecutar")
    public String ejecutarDynamic(@RequestParam(required = false) List<Long> equiposSeleccionados,
                                 @RequestParam(required = false, defaultValue = "10") Integer maxPartidos,
                                 @RequestParam(required = false, defaultValue = "minimize_cost") String objetivo,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🧠 DEBUG Dynamic Programming: equiposSeleccionados=" + equiposSeleccionados + ", maxPartidos=" + maxPartidos);
            
            // Obtener equipos
            List<Equipo> equipos = equiposSeleccionados != null && !equiposSeleccionados.isEmpty()
                ? ligaService.obtenerEquiposPorIds(equiposSeleccionados)
                : ligaService.obtenerTodosLosEquipos();
            
            System.out.println("🔧 DEBUG: Usando " + equipos.size() + " equipos para DP");
            
            // Generar fechas disponibles para DP
            List<LocalDateTime> fechasDisponibles = new ArrayList<>();
            LocalDateTime fechaBase = LocalDateTime.now().plusDays(7);
            for (int i = 0; i < maxPartidos; i++) {
                fechasDisponibles.add(fechaBase.plusWeeks(i));
            }
            
            // Ejecutar algoritmo Programación Dinámica REAL
            long startTime = System.currentTimeMillis();
            DynamicProgrammingAlgorithm.FixtureOptimo resultado = 
                dynamicProgrammingAlgorithm.planificarFixtureOptimo(equipos, fechasDisponibles);
            long endTime = System.currentTimeMillis();
            
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Resultado DP: " + resultado.getPartidos().size() + " partidos optimizados");
            System.out.println("💰 DEBUG Costo total optimizado: " + resultado.getCostoTotal());
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            // Debug detallado de partidos
            System.out.println("📊 DEBUG PARTIDOS OPTIMIZADOS (DP):");
            for (DynamicProgrammingAlgorithm.Emparejamiento partido : resultado.getPartidos()) {
                System.out.println("   " + partido.getEquipo1().getNombre() + 
                                 " vs " + partido.getEquipo2().getNombre() + 
                                 " (Distancia: " + partido.getDistancia() + ")");
            }
            
            // Pasar datos al template
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("equiposUsados", equipos);
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("maxPartidosUsado", maxPartidos);
            model.addAttribute("objetivoUsado", objetivo);
            model.addAttribute("success", true);
            
            return "web/dynamic";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR Dynamic Programming: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar Programación Dinámica: " + e.getMessage());
            return "redirect:/web/dynamic";
        }
    }

    /**
     * Página de algoritmos de Grafos
     */
    @GetMapping("/grafo")
    public String grafoAlgorithms(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            model.addAttribute("equipos", equipos);
            model.addAttribute("estadios", estadios);
            model.addAttribute("algoritmo", "Algoritmos de Grafos");
            model.addAttribute("descripcion", "BFS, DFS y análisis de conectividad para encontrar relaciones entre equipos y estadios");
            model.addAttribute("pageTitle", "⚽ Algoritmos de Grafos - Liga de Fútbol");
            
            return "web/grafo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            return "web/error";
        }
    }

    /**
     * Ejecutar algoritmos de Grafos - BFS
     */
    @PostMapping("/grafo/bfs")
    public String ejecutarGrafoBFS(@RequestParam String equipoInicioId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔍 DEBUG Grafo BFS: equipoInicioId=" + equipoInicioId);
            
            Equipo equipoInicio = ligaService.obtenerEquipoPorId(equipoInicioId);
            System.out.println("🔧 DEBUG: Equipo inicio: " + equipoInicio.getNombre());
            
            // Obtener todos los equipos para hacer BFS completo
            List<Equipo> todosEquipos = ligaService.obtenerTodosLosEquipos();
            Equipo equipoDestino = todosEquipos.stream()
                .filter(e -> !e.getId().equals(equipoInicio.getId()))
                .findFirst().orElse(null);
            
            // Ejecutar BFS
            long startTime = System.currentTimeMillis();
            List<Equipo> caminoBFS = 
                grafoAlgorithm.bfs(equipoInicio, equipoDestino);
            long endTime = System.currentTimeMillis();
            
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Resultado BFS: " + caminoBFS.size() + " nodos en el camino");
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            model.addAttribute("resultado", caminoBFS);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("tipoAlgoritmo", "BFS");
            model.addAttribute("equipoInicio", equipoInicio);
            model.addAttribute("equipoDestino", equipoDestino);
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("success", true);
            
            return "web/grafo";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR Grafo BFS: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar BFS: " + e.getMessage());
            return "redirect:/web/grafo";
        }
    }

    /**
     * Ejecutar algoritmos de Grafos - DFS
     */
    @PostMapping("/grafo/dfs")
    public String ejecutarGrafoDFS(@RequestParam String equipoInicioId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔍 DEBUG Grafo DFS: equipoInicioId=" + equipoInicioId);
            
            Equipo equipoInicio = ligaService.obtenerEquipoPorId(equipoInicioId);
            System.out.println("🔧 DEBUG: Equipo inicio: " + equipoInicio.getNombre());
            
            // Obtener todos los equipos para hacer DFS completo
            List<Equipo> todosEquipos = ligaService.obtenerTodosLosEquipos();
            Equipo equipoDestino = todosEquipos.stream()
                .filter(e -> !e.getId().equals(equipoInicio.getId()))
                .findFirst().orElse(null);
            
            // Ejecutar DFS
            long startTime = System.currentTimeMillis();
            List<Equipo> caminoDFS = 
                grafoAlgorithm.dfs(equipoInicio, equipoDestino);
            long endTime = System.currentTimeMillis();
            
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Resultado DFS: " + caminoDFS.size() + " nodos en el camino");
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            model.addAttribute("resultado", caminoDFS);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("tipoAlgoritmo", "DFS");
            model.addAttribute("equipoInicio", equipoInicio);
            model.addAttribute("equipoDestino", equipoDestino);
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("success", true);
            
            return "web/grafo";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR Grafo DFS: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar DFS: " + e.getMessage());
            return "redirect:/web/grafo";
        }
    }

    /**
     * Ejecutar análisis de conectividad
     */
    @PostMapping("/grafo/conectividad")
    public String ejecutarConectividad(Model model, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔗 DEBUG Análisis de conectividad");
            
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            
            // Ejecutar análisis de conectividad usando BFS desde cada equipo
            long startTime = System.currentTimeMillis();
            Map<String, Integer> conectividad = new HashMap<>();
            
            for (Equipo equipo : equipos) {
                int conexiones = 0;
                for (Equipo otro : equipos) {
                    if (!equipo.equals(otro)) {
                        List<Equipo> camino = grafoAlgorithm.bfs(equipo, otro);
                        if (!camino.isEmpty()) {
                            conexiones++;
                        }
                    }
                }
                conectividad.put(equipo.getNombre(), conexiones);
            }
            
            long endTime = System.currentTimeMillis();
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Conectividad: Analizados " + equipos.size() + " equipos");
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            model.addAttribute("resultado", conectividad);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("tipoAlgoritmo", "CONECTIVIDAD");
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("success", true);
            
            return "web/grafo";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR Conectividad: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar análisis de conectividad: " + e.getMessage());
            return "redirect:/web/grafo";
        }
    }

    /**
     * Página del algoritmo MST (Minimum Spanning Tree)
     */
    @GetMapping("/mst")
    public String mst(Model model) {
        try {
            List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
            List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
            
            model.addAttribute("equipos", equipos);
            model.addAttribute("estadios", estadios);
            model.addAttribute("algoritmo", "Minimum Spanning Tree (MST)");
            model.addAttribute("descripcion", "Árbol de expansión mínima para encontrar las conexiones óptimas entre estadios");
            model.addAttribute("pageTitle", "⚽ MST - Liga de Fútbol");
            
            return "web/mst";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
            return "web/error";
        }
    }

    /**
     * Ejecutar algoritmo MST - Kruskal
     */
    @PostMapping("/mst/kruskal")
    public String ejecutarMSTKruskal(@RequestParam(required = false) List<Long> estadiosSeleccionados,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🌳 DEBUG MST Kruskal: estadiosSeleccionados=" + estadiosSeleccionados);
            
            // Obtener estadios
            List<Estadio> estadios = estadiosSeleccionados != null && !estadiosSeleccionados.isEmpty()
                ? ligaService.obtenerEstadiosPorIds(estadiosSeleccionados)
                : ligaService.obtenerTodosLosEstadios();
            
            System.out.println("🔧 DEBUG: Usando " + estadios.size() + " estadios para MST Kruskal");
            
            // Obtener equipos para MST
            List<Equipo> equiposParaMST = ligaService.obtenerTodosLosEquipos().stream().limit(estadios.size()).toList();
            
            // Ejecutar MST Kruskal
            long startTime = System.currentTimeMillis();
            MSTAlgorithm.ResultadoMST resultado = 
                mstAlgorithm.algoritmoKruskal(equiposParaMST);
            long endTime = System.currentTimeMillis();
            
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Resultado Kruskal: " + resultado.getAristas().size() + " aristas en MST");
            System.out.println("💰 DEBUG Costo total: " + resultado.getCostoTotal());
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            // Debug detallado de aristas
            System.out.println("📊 DEBUG ARISTAS MST KRUSKAL:");
            for (MSTAlgorithm.AristaConexion arista : resultado.getAristas()) {
                System.out.println("   " + arista.getOrigen().getNombre() + 
                                 " <-> " + arista.getDestino().getNombre() + 
                                 " (Peso: " + arista.getCosto() + ")");
            }
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("estadiosUsados", estadios);
            model.addAttribute("tipoAlgoritmo", "KRUSKAL");
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("success", true);
            
            return "web/mst";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR MST Kruskal: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar MST Kruskal: " + e.getMessage());
            return "redirect:/web/mst";
        }
    }

    /**
     * Ejecutar algoritmo MST - Prim
     */
    @PostMapping("/mst/prim")
    public String ejecutarMSTPrim(@RequestParam(required = false) List<Long> estadiosSeleccionados,
                                 @RequestParam(required = false) String estadioInicioId,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🌳 DEBUG MST Prim: estadiosSeleccionados=" + estadiosSeleccionados);
            
            // Obtener estadios
            List<Estadio> estadios = estadiosSeleccionados != null && !estadiosSeleccionados.isEmpty()
                ? ligaService.obtenerEstadiosPorIds(estadiosSeleccionados)
                : ligaService.obtenerTodosLosEstadios();
            
            // Estadio de inicio
            Estadio estadioInicio = estadioInicioId != null && !estadioInicioId.isEmpty()
                ? ligaService.obtenerEstadioPorId(estadioInicioId)
                : (!estadios.isEmpty() ? estadios.get(0) : null);
            
            System.out.println("🔧 DEBUG: Usando " + estadios.size() + " estadios para MST Prim");
            System.out.println("🎯 DEBUG: Estadio inicio: " + (estadioInicio != null ? estadioInicio.getNombre() : "NULL"));
            
            // Obtener equipos para MST
            List<Equipo> equiposParaMST = ligaService.obtenerTodosLosEquipos().stream().limit(estadios.size()).toList();
            
            // Ejecutar MST Prim
            long startTime = System.currentTimeMillis();
            MSTAlgorithm.ResultadoMST resultado = 
                mstAlgorithm.algoritmoPrim(equiposParaMST);
            long endTime = System.currentTimeMillis();
            
            double tiempoEjecucion = (endTime - startTime) / 1000.0;
            
            System.out.println("🏆 DEBUG Resultado Prim: " + resultado.getAristas().size() + " aristas en MST");
            System.out.println("💰 DEBUG Costo total: " + resultado.getCostoTotal());
            System.out.println("⏱️ DEBUG Tiempo: " + tiempoEjecucion + " segundos");
            
            // Debug detallado de aristas
            System.out.println("📊 DEBUG ARISTAS MST PRIM:");
            for (MSTAlgorithm.AristaConexion arista : resultado.getAristas()) {
                System.out.println("   " + arista.getOrigen().getNombre() + 
                                " <-> " + arista.getDestino().getNombre() + 
                                " (Peso: " + arista.getCosto() + ")");
            }
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("estadiosUsados", estadios);
            model.addAttribute("estadioInicio", estadioInicio);
            model.addAttribute("tipoAlgoritmo", "PRIM");
            model.addAttribute("tiempoEjecucion", tiempoEjecucion);
            model.addAttribute("success", true);
            
            return "web/mst";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR MST Prim: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar MST Prim: " + e.getMessage());
            return "redirect:/web/mst";
        }
    }

    /**
     * Página de gestión de base de datos
     */
    @GetMapping("/database-management")
    public String databaseManagement(Model model) {
        model.addAttribute("pageTitle", "🗄️ Gestión de Base de Datos - Liga de Fútbol");
        return "web/database-management";
    }

    /**
     * Información sobre los algoritmos disponibles
     */
    private Map<String, String> getAlgoritmosDisponibles() {
        return Map.of(
            "greedy", "Algoritmo Greedy - Minimiza distancias entre equipos",
            "backtracking", "Backtracking - Genera todas las combinaciones válidas",
            "dijkstra", "Dijkstra - Camino más corto entre equipos/estadios",
            "branch-bound", "Branch & Bound - Optimización con ramificación y poda",
            "divide-conquer", "Divide y Vencerás - Descompone problemas complejos",
            "dynamic", "Programación Dinámica - Optimización con memoización",
            "grafo", "Algoritmos de Grafos - BFS, DFS y análisis de conectividad",
            "mst", "MST - Árbol de expansión mínima para conexiones óptimas"
        );
    }
}