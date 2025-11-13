package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.ConexionEquipo;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Algoritmos de Árbol de Expansión Mínima (MST): Prim y Kruskal
 * Para minimizar costos de traslados entre equipos
 */
@Component
public class MSTAlgorithm {
    
    /**
     * Algoritmo de Prim para encontrar el MST
     * Minimiza el costo total de conexiones entre todos los equipos
     */
    public ResultadoMST algoritmoPrim(List<Equipo> equipos) {
        if (equipos.isEmpty()) {
            return new ResultadoMST(new ArrayList<>(), 0.0);
        }
        
        Set<Equipo> visitados = new HashSet<>();
        List<AristaConexion> aristasSeleccionadas = new ArrayList<>();
        PriorityQueue<AristaConexion> cola = new PriorityQueue<>();
        double costoTotal = 0.0;
        
        // Comenzar con el primer equipo
        Equipo inicio = equipos.get(0);
        visitados.add(inicio);
        
        // Agregar todas las conexiones del equipo inicial
        for (ConexionEquipo conexion : inicio.getConexiones()) {
            cola.offer(new AristaConexion(inicio, conexion.getEquipoDestino(), conexion.getCosto()));
        }
        
        while (!cola.isEmpty() && visitados.size() < equipos.size()) {
            AristaConexion arista = cola.poll();
            
            if (visitados.contains(arista.destino)) {
                continue;
            }
            
            // Agregar la arista al MST
            aristasSeleccionadas.add(arista);
            costoTotal += arista.costo;
            visitados.add(arista.destino);
            
            // Agregar las conexiones del nuevo equipo visitado
            for (ConexionEquipo conexion : arista.destino.getConexiones()) {
                if (!visitados.contains(conexion.getEquipoDestino())) {
                    cola.offer(new AristaConexion(arista.destino, conexion.getEquipoDestino(), conexion.getCosto()));
                }
            }
        }
        
        return new ResultadoMST(aristasSeleccionadas, costoTotal);
    }
    
    /**
     * Algoritmo de Kruskal para encontrar el MST
     * Usa Union-Find para detectar ciclos
     */
    public ResultadoMST algoritmoKruskal(List<Equipo> equipos) {
        List<AristaConexion> todasLasAristas = new ArrayList<>();
        
        // Recolectar todas las aristas
        for (Equipo equipo : equipos) {
            for (ConexionEquipo conexion : equipo.getConexiones()) {
                todasLasAristas.add(new AristaConexion(equipo, conexion.getEquipoDestino(), conexion.getCosto()));
            }
        }
        
        // Ordenar aristas por costo
        Collections.sort(todasLasAristas);
        
        UnionFind uf = new UnionFind(equipos);
        List<AristaConexion> aristasSeleccionadas = new ArrayList<>();
        double costoTotal = 0.0;
        
        for (AristaConexion arista : todasLasAristas) {
            if (uf.find(arista.origen) != uf.find(arista.destino)) {
                uf.union(arista.origen, arista.destino);
                aristasSeleccionadas.add(arista);
                costoTotal += arista.costo;
                
                if (aristasSeleccionadas.size() == equipos.size() - 1) {
                    break;
                }
            }
        }
        
        return new ResultadoMST(aristasSeleccionadas, costoTotal);
    }
    
    // Clase Union-Find para Kruskal
    private static class UnionFind {
        private Map<Equipo, Equipo> padre;
        private Map<Equipo, Integer> rango;
        
        UnionFind(List<Equipo> equipos) {
            padre = new HashMap<>();
            rango = new HashMap<>();
            
            for (Equipo equipo : equipos) {
                padre.put(equipo, equipo);
                rango.put(equipo, 0);
            }
        }
        
        Equipo find(Equipo equipo) {
            if (!padre.get(equipo).equals(equipo)) {
                padre.put(equipo, find(padre.get(equipo))); // Compresión de camino
            }
            return padre.get(equipo);
        }
        
        void union(Equipo equipo1, Equipo equipo2) {
            Equipo raiz1 = find(equipo1);
            Equipo raiz2 = find(equipo2);
            
            if (!raiz1.equals(raiz2)) {
                // Union por rango
                if (rango.get(raiz1) < rango.get(raiz2)) {
                    padre.put(raiz1, raiz2);
                } else if (rango.get(raiz1) > rango.get(raiz2)) {
                    padre.put(raiz2, raiz1);
                } else {
                    padre.put(raiz2, raiz1);
                    rango.put(raiz1, rango.get(raiz1) + 1);
                }
            }
        }
    }
    
    // Clases auxiliares
    public static class AristaConexion implements Comparable<AristaConexion> {
        private Equipo origen;
        private Equipo destino;
        private double costo;
        
        public AristaConexion(Equipo origen, Equipo destino, double costo) {
            this.origen = origen;
            this.destino = destino;
            this.costo = costo;
        }
        
        public Equipo getOrigen() {
            return origen;
        }
        
        public Equipo getDestino() {
            return destino;
        }
        
        public double getCosto() {
            return costo;
        }
        
        @Override
        public int compareTo(AristaConexion otra) {
            return Double.compare(this.costo, otra.costo);
        }
    }
    
    /**
     * Genera múltiples variaciones de MST para comparar opciones
     */
    public ResultadoMultiplesMST algoritmoMultiplesMST(List<Equipo> equipos) {
        List<ResultadoMST> variaciones = new ArrayList<>();
        
        // MST con Prim (comenzando desde diferentes nodos)
        for (int i = 0; i < Math.min(equipos.size(), 3); i++) {
            ResultadoMST mstPrim = algoritmoPrimDesdeNodo(equipos, i);
            if (!mstPrim.getAristas().isEmpty()) {
                variaciones.add(mstPrim);
            }
        }
        
        // MST con Kruskal
        ResultadoMST mstKruskal = algoritmoKruskal(equipos);
        if (!mstKruskal.getAristas().isEmpty()) {
            variaciones.add(mstKruskal);
        }
        
        // Ordenar por costo (el primero será el óptimo)
        variaciones.sort((a, b) -> Double.compare(a.getCostoTotal(), b.getCostoTotal()));
        
        return new ResultadoMultiplesMST(variaciones);
    }
    
    /**
     * Algoritmo de Prim comenzando desde un nodo específico
     */
    private ResultadoMST algoritmoPrimDesdeNodo(List<Equipo> equipos, int indiceInicio) {
        if (equipos.isEmpty() || indiceInicio >= equipos.size()) {
            return new ResultadoMST(new ArrayList<>(), 0.0);
        }
        
        Set<Equipo> visitados = new HashSet<>();
        List<AristaConexion> aristasSeleccionadas = new ArrayList<>();
        PriorityQueue<AristaConexion> cola = new PriorityQueue<>();
        double costoTotal = 0.0;
        
        // Comenzar con el equipo en el índice especificado
        Equipo inicio = equipos.get(indiceInicio);
        visitados.add(inicio);
        
        // Agregar todas las conexiones del equipo inicial
        for (ConexionEquipo conexion : inicio.getConexiones()) {
            cola.offer(new AristaConexion(inicio, conexion.getEquipoDestino(), conexion.getCosto()));
        }
        
        while (!cola.isEmpty() && visitados.size() < equipos.size()) {
            AristaConexion arista = cola.poll();
            
            if (visitados.contains(arista.destino)) {
                continue;
            }
            
            // Agregar la arista al MST
            aristasSeleccionadas.add(arista);
            costoTotal += arista.costo;
            visitados.add(arista.destino);
            
            // Agregar las conexiones del nuevo equipo visitado
            for (ConexionEquipo conexion : arista.destino.getConexiones()) {
                if (!visitados.contains(conexion.getEquipoDestino())) {
                    cola.offer(new AristaConexion(arista.destino, conexion.getEquipoDestino(), conexion.getCosto()));
                }
            }
        }
        
        return new ResultadoMST(aristasSeleccionadas, costoTotal);
    }
    
    public static class ResultadoMultiplesMST {
        private List<ResultadoMST> variaciones;
        
        public ResultadoMultiplesMST(List<ResultadoMST> variaciones) {
            this.variaciones = variaciones;
        }
        
        public List<ResultadoMST> getVariaciones() {
            return variaciones;
        }
        
        public ResultadoMST getMejorMST() {
            return variaciones.isEmpty() ? null : variaciones.get(0);
        }
        
        public double getMejorCosto() {
            return getMejorMST() != null ? getMejorMST().getCostoTotal() : Double.MAX_VALUE;
        }
    }

    public static class ResultadoMST {
        private List<AristaConexion> aristas;
        private double costoTotal;
        
        public ResultadoMST(List<AristaConexion> aristas, double costoTotal) {
            this.aristas = aristas;
            this.costoTotal = costoTotal;
        }
        
        public List<AristaConexion> getAristas() {
            return aristas;
        }
        
        public double getCostoTotal() {
            return costoTotal;
        }
    }
}
