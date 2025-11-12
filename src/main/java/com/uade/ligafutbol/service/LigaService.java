package com.uade.ligafutbol.service;

import com.uade.ligafutbol.algorithm.*;
import com.uade.ligafutbol.model.*;
import com.uade.ligafutbol.repository.EquipoRepository;
import com.uade.ligafutbol.repository.EstadioRepository;
import com.uade.ligafutbol.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class LigaService {

    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private EstadioRepository estadioRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private GrafoAlgorithm grafoAlgorithm;
    
    @Autowired
    private DijkstraAlgorithm dijkstraAlgorithm;
    
    @Autowired
    private MSTAlgorithm mstAlgorithm;
    
    @Autowired
    private GreedyAlgorithm greedyAlgorithm;
    
    @Autowired
    private DivideConquerAlgorithm divideConquerAlgorithm;
    
    @Autowired
    private DynamicProgrammingAlgorithm dynamicProgrammingAlgorithm;
    
    @Autowired
    private BacktrackingAlgorithm backtrackingAlgorithm;
    
    @Autowired
    private BranchBoundAlgorithm branchBoundAlgorithm;
    
    // ========== Gestión de Equipos ==========
    
    public Equipo crearEquipo(Equipo equipo) {
        return equipoRepository.save(equipo);
    }
    
    public List<Equipo> obtenerTodosLosEquipos() {
        return equipoRepository.findAll();
    }
    
    public Equipo obtenerEquipoPorId(String id) {
        return equipoRepository.findById(id).orElse(null);
    }
    
    public Equipo obtenerEquipoPorId(Long id) {
        return equipoRepository.findById(id.toString()).orElse(null);
    }
    
    public List<Equipo> obtenerEquiposPorIds(List<Long> ids) {
        return ids.stream()
                .map(id -> equipoRepository.findById(id.toString()).orElse(null))
                .filter(equipo -> equipo != null)
                .toList();
    }
    
    // ========== Gestión de Estadios ==========
    
    public Estadio crearEstadio(Estadio estadio) {
        return estadioRepository.save(estadio);
    }
    
    public List<Estadio> obtenerTodosLosEstadios() {
        return estadioRepository.findAll();
    }
    
    public Estadio obtenerEstadioPorId(Long id) {
        return estadioRepository.findById(id.toString()).orElse(null);
    }
    
    public Estadio obtenerEstadioPorId(String id) {
        return estadioRepository.findById(id).orElse(null);
    }
    
    public List<Estadio> obtenerEstadiosPorIds(List<Long> ids) {
        return ids.stream()
                .map(id -> estadioRepository.findById(id.toString()).orElse(null))
                .filter(estadio -> estadio != null)
                .toList();
    }
    
    // ========== Gestión de Partidos ==========
    
    public Partido crearPartido(Partido partido) {
        return partidoRepository.save(partido);
    }
    
    public List<Partido> obtenerTodosLosPartidos() {
        return partidoRepository.findAllOrderedByFecha();
    }
    
    public Partido registrarResultado(String partidoId, Integer golesLocal, Integer golesVisitante) {
        Partido partido = partidoRepository.findById(partidoId).orElse(null);
        if (partido != null) {
            partido.registrarResultado(golesLocal, golesVisitante);
            equipoRepository.save(partido.getEquipoLocal());
            equipoRepository.save(partido.getEquipoVisitante());
            return partidoRepository.save(partido);
        }
        return null;
    }
    
    // ========== Tabla de Posiciones ==========
    
    public List<Equipo> obtenerTablaPosiciones() {
        List<Equipo> equipos = equipoRepository.findAll();
        return divideConquerAlgorithm.ordenarTablaPosiciones(equipos);
    }
    
    // ========== Algoritmos de Grafos (BFS/DFS) ==========
    
    public List<Equipo> encontrarCaminoBFS(String equipoOrigenId, String equipoDestinoId) {
        Equipo origen = equipoRepository.findById(equipoOrigenId).orElse(null);
        Equipo destino = equipoRepository.findById(equipoDestinoId).orElse(null);
        
        if (origen != null && destino != null) {
            return grafoAlgorithm.bfs(origen, destino);
        }
        return List.of();
    }
    
    public List<Equipo> encontrarCaminoDFS(String equipoOrigenId, String equipoDestinoId) {
        Equipo origen = equipoRepository.findById(equipoOrigenId).orElse(null);
        Equipo destino = equipoRepository.findById(equipoDestinoId).orElse(null);
        
        if (origen != null && destino != null) {
            return grafoAlgorithm.dfs(origen, destino);
        }
        return List.of();
    }
    
    // ========== Dijkstra ==========
    
    public DijkstraAlgorithm.ResultadoDijkstra<Equipo> encontrarRutaMasCorta(String equipoOrigenId, String equipoDestinoId) {
        Equipo origen = equipoRepository.findById(equipoOrigenId).orElse(null);
        Equipo destino = equipoRepository.findById(equipoDestinoId).orElse(null);
        
        if (origen != null && destino != null) {
            return dijkstraAlgorithm.caminoMasCortoEquipos(origen, destino);
        }
        return null;
    }
    
    public DijkstraAlgorithm.ResultadoDijkstra<Estadio> encontrarRutaMasCortaEstadios(String estadioOrigenId, String estadioDestinoId) {
        Estadio origen = estadioRepository.findById(estadioOrigenId).orElse(null);
        Estadio destino = estadioRepository.findById(estadioDestinoId).orElse(null);
        
        if (origen != null && destino != null) {
            return dijkstraAlgorithm.caminoMasCortoEstadios(origen, destino);
        }
        return null;
    }
    
    // ========== MST (Prim/Kruskal) ==========
    
    public MSTAlgorithm.ResultadoMST calcularMSTConPrim() {
        List<Equipo> equipos = equipoRepository.findAll();
        return mstAlgorithm.algoritmoPrim(equipos);
    }
    
    public MSTAlgorithm.ResultadoMST calcularMSTConKruskal() {
        List<Equipo> equipos = equipoRepository.findAll();
        return mstAlgorithm.algoritmoKruskal(equipos);
    }
    
    // ========== Greedy ==========
    
    public List<GreedyAlgorithm.EmparejamientoPartido> emparejarPartidosGreedy(LocalDateTime fechaInicio) {
        List<Equipo> equipos = equipoRepository.findAll();
        return greedyAlgorithm.emparejarPartidosMinimizandoDistancia(equipos, fechaInicio);
    }
    
    // ========== Programación Dinámica ==========
    
    public DynamicProgrammingAlgorithm.FixtureOptimo planificarFixture(List<LocalDateTime> fechas) {
        List<Equipo> equipos = equipoRepository.findAll();
        return dynamicProgrammingAlgorithm.planificarFixtureOptimo(equipos, fechas);
    }
    
    // ========== Backtracking ==========
    
    public List<BacktrackingAlgorithm.Jornada> generarFixtureCompleto() {
        List<Equipo> equipos = equipoRepository.findAll();
        return backtrackingAlgorithm.generarFixtureCompleto(equipos);
    }
    
    // ========== Branch & Bound ==========
    
    public BranchBoundAlgorithm.ResultadoCalendario optimizarCalendario(List<LocalDateTime> fechas, double presupuesto) {
        List<Equipo> equipos = equipoRepository.findAll();
        return branchBoundAlgorithm.optimizarCalendario(equipos, fechas, presupuesto);
    }
}