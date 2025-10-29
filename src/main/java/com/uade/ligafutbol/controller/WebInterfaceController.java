package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.algorithm.*;
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
import java.util.List;
import java.util.Map;

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
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            List<Equipo> equipos = equiposSeleccionados != null && !equiposSeleccionados.isEmpty() 
                ? ligaService.obtenerEquiposPorIds(equiposSeleccionados)
                : ligaService.obtenerTodosLosEquipos();
            
            List<List<BacktrackingAlgorithm.Cruce>> resultado = 
                backtrackingAlgorithm.generarCombinacionesCruces(equipos);
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("totalCombinaciones", resultado.size());
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
        // Página simplificada sin consultas DB para carga rápida
        model.addAttribute("algoritmo", "Dijkstra");
        model.addAttribute("status", "ready");
        return "web/dijkstra_simple";
    }

    /**
     * Ejecutar algoritmo Dijkstra para equipos
     */
    @PostMapping("/dijkstra/equipos")
    public String ejecutarDijkstraEquipos(@RequestParam Long equipoOrigenId,
                                        @RequestParam Long equipoDestinoId,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        try {
            Equipo origen = ligaService.obtenerEquipoPorId(equipoOrigenId);
            Equipo destino = ligaService.obtenerEquipoPorId(equipoDestinoId);
            
            DijkstraAlgorithm.ResultadoDijkstra<Equipo> resultado = 
                dijkstraAlgorithm.caminoMasCortoEquipos(origen, destino);
            
            model.addAttribute("resultado", resultado);
            model.addAttribute("equipos", ligaService.obtenerTodosLosEquipos());
            model.addAttribute("estadios", ligaService.obtenerTodosLosEstadios());
            model.addAttribute("tipoResultado", "equipos");
            model.addAttribute("success", true);
            
            return "web/dijkstra";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al ejecutar Dijkstra: " + e.getMessage());
            return "redirect:/web/dijkstra";
        }
    }

    /**
     * Ejecutar algoritmo Dijkstra para estadios
     */
    @PostMapping("/dijkstra/estadios")
    public String ejecutarDijkstraEstadios(@RequestParam Long estadioOrigenId,
                                        @RequestParam Long estadioDestinoId,
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