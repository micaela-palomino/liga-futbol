package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.ConexionEquipo;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Algoritmos de grafos: BFS y DFS para encontrar conexiones entre equipos
 */
@Component
public class GrafoAlgorithm {
    
    /**
     * BFS - Búsqueda en Anchura
     * Encuentra el camino más corto en términos de número de saltos entre equipos
     */
    public List<Equipo> bfs(Equipo origen, Equipo destino) {
        if (origen.equals(destino)) {
            return Collections.singletonList(origen);
        }
        
        Queue<Equipo> cola = new LinkedList<>();
        Map<Equipo, Equipo> padres = new HashMap<>();
        Set<Equipo> visitados = new HashSet<>();
        
        cola.offer(origen);
        visitados.add(origen);
        padres.put(origen, null);
        
        while (!cola.isEmpty()) {
            Equipo actual = cola.poll();
            
            if (actual.equals(destino)) {
                return reconstruirCamino(padres, destino);
            }
            
            // Explorar vecinos
            for (ConexionEquipo conexion : actual.getConexiones()) {
                Equipo vecino = conexion.getEquipoDestino();
                
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padres.put(vecino, actual);
                    cola.offer(vecino);
                }
            }
        }
        
        return Collections.emptyList(); // No hay camino
    }
    
    /**
     * DFS - Búsqueda en Profundidad
     * Encuentra un camino entre equipos explorando en profundidad
     */
    public List<Equipo> dfs(Equipo origen, Equipo destino) {
        Set<Equipo> visitados = new HashSet<>();
        List<Equipo> camino = new ArrayList<>();
        
        if (dfsRecursivo(origen, destino, visitados, camino)) {
            return camino;
        }
        
        return Collections.emptyList(); // No hay camino
    }
    
    private boolean dfsRecursivo(Equipo actual, Equipo destino, Set<Equipo> visitados, List<Equipo> camino) {
        visitados.add(actual);
        camino.add(actual);
        
        if (actual.equals(destino)) {
            return true;
        }
        
        for (ConexionEquipo conexion : actual.getConexiones()) {
            Equipo vecino = conexion.getEquipoDestino();
            
            if (!visitados.contains(vecino)) {
                if (dfsRecursivo(vecino, destino, visitados, camino)) {
                    return true;
                }
            }
        }
        
        camino.remove(camino.size() - 1); // Backtrack
        return false;
    }
    
    /**
     * Verifica si todos los equipos están conectados (grafo conexo)
     */
    public boolean esGrafoConexo(List<Equipo> equipos) {
        if (equipos.isEmpty()) {
            return true;
        }
        
        Set<Equipo> visitados = new HashSet<>();
        Queue<Equipo> cola = new LinkedList<>();
        
        Equipo inicio = equipos.get(0);
        cola.offer(inicio);
        visitados.add(inicio);
        
        while (!cola.isEmpty()) {
            Equipo actual = cola.poll();
            
            for (ConexionEquipo conexion : actual.getConexiones()) {
                Equipo vecino = conexion.getEquipoDestino();
                
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.offer(vecino);
                }
            }
        }
        
        return visitados.size() == equipos.size();
    }
    
    private List<Equipo> reconstruirCamino(Map<Equipo, Equipo> padres, Equipo destino) {
        List<Equipo> camino = new ArrayList<>();
        Equipo actual = destino;
        
        while (actual != null) {
            camino.add(0, actual);
            actual = padres.get(actual);
        }
        
        return camino;
    }
}
