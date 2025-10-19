package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.algorithm.*;
import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.Estadio;
import com.uade.ligafutbol.service.LigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/algoritmos")
@CrossOrigin(origins = "*")
public class AlgorithmController {
    
    @Autowired
    private LigaService ligaService;
    
    // ========== BFS/DFS ==========
    
    @GetMapping("/bfs/{origenId}/{destinoId}")
    public ResponseEntity<List<Equipo>> buscarCaminoBFS(
            @PathVariable Long origenId,
            @PathVariable Long destinoId) {
        List<Equipo> camino = ligaService.encontrarCaminoBFS(origenId, destinoId);
        return ResponseEntity.ok(camino);
    }
    
    @GetMapping("/dfs/{origenId}/{destinoId}")
    public ResponseEntity<List<Equipo>> buscarCaminoDFS(
            @PathVariable Long origenId,
            @PathVariable Long destinoId) {
        List<Equipo> camino = ligaService.encontrarCaminoDFS(origenId, destinoId);
        return ResponseEntity.ok(camino);
    }
    
    // ========== Dijkstra ==========
    
    @GetMapping("/dijkstra/equipos/{origenId}/{destinoId}")
    public ResponseEntity<DijkstraAlgorithm.ResultadoDijkstra<Equipo>> rutaMasCorta(
            @PathVariable Long origenId,
            @PathVariable Long destinoId) {
        DijkstraAlgorithm.ResultadoDijkstra<Equipo> resultado = ligaService.encontrarRutaMasCorta(origenId, destinoId);
        return ResponseEntity.ok(resultado);
    }
    
    @GetMapping("/dijkstra/estadios/{origenId}/{destinoId}")
    public ResponseEntity<DijkstraAlgorithm.ResultadoDijkstra<Estadio>> rutaMasCortaEstadios(
            @PathVariable Long origenId,
            @PathVariable Long destinoId) {
        DijkstraAlgorithm.ResultadoDijkstra<Estadio> resultado = ligaService.encontrarRutaMasCortaEstadios(origenId, destinoId);
        return ResponseEntity.ok(resultado);
    }
    
    // ========== MST (Prim/Kruskal) ==========
    
    @GetMapping("/mst/prim")
    public ResponseEntity<MSTAlgorithm.ResultadoMST> calcularMSTPrim() {
        MSTAlgorithm.ResultadoMST resultado = ligaService.calcularMSTConPrim();
        return ResponseEntity.ok(resultado);
    }
    
    @GetMapping("/mst/kruskal")
    public ResponseEntity<MSTAlgorithm.ResultadoMST> calcularMSTKruskal() {
        MSTAlgorithm.ResultadoMST resultado = ligaService.calcularMSTConKruskal();
        return ResponseEntity.ok(resultado);
    }
    
    // ========== Greedy ==========
    
    @PostMapping("/greedy/emparejar")
    public ResponseEntity<List<GreedyAlgorithm.EmparejamientoPartido>> emparejarPartidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio) {
        List<GreedyAlgorithm.EmparejamientoPartido> emparejamientos = ligaService.emparejarPartidosGreedy(fechaInicio);
        return ResponseEntity.ok(emparejamientos);
    }
    
    // ========== Programación Dinámica ==========
    
    @PostMapping("/dp/fixture")
    public ResponseEntity<DynamicProgrammingAlgorithm.FixtureOptimo> planificarFixture(
            @RequestBody List<LocalDateTime> fechas) {
        DynamicProgrammingAlgorithm.FixtureOptimo fixture = ligaService.planificarFixture(fechas);
        return ResponseEntity.ok(fixture);
    }
    
    // ========== Backtracking ==========
    
    @GetMapping("/backtracking/fixture-completo")
    public ResponseEntity<List<BacktrackingAlgorithm.Jornada>> generarFixtureCompleto() {
        List<BacktrackingAlgorithm.Jornada> fixture = ligaService.generarFixtureCompleto();
        return ResponseEntity.ok(fixture);
    }
    
    // ========== Branch & Bound ==========
    
    @PostMapping("/branch-bound/calendario")
    public ResponseEntity<BranchBoundAlgorithm.ResultadoCalendario> optimizarCalendario(
            @RequestBody List<LocalDateTime> fechas,
            @RequestParam double presupuesto) {
        BranchBoundAlgorithm.ResultadoCalendario resultado = ligaService.optimizarCalendario(fechas, presupuesto);
        return ResponseEntity.ok(resultado);
    }
}
