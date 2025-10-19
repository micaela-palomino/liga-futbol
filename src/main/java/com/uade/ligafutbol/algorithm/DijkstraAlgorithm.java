package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.Estadio;
import com.uade.ligafutbol.model.ConexionEquipo;
import com.uade.ligafutbol.model.ConexionEstadio;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Algoritmo de Dijkstra para encontrar el camino más corto entre estadios/equipos
 */
@Component
public class DijkstraAlgorithm {
    
    /**
     * Encuentra el camino más corto entre dos equipos basado en distancia
     */
    public ResultadoDijkstra<Equipo> caminoMasCortoEquipos(Equipo origen, Equipo destino) {
        Map<Equipo, Double> distancias = new HashMap<>();
        Map<Equipo, Equipo> padres = new HashMap<>();
        PriorityQueue<NodoDistancia<Equipo>> cola = new PriorityQueue<>();
        Set<Equipo> visitados = new HashSet<>();
        
        distancias.put(origen, 0.0);
        cola.offer(new NodoDistancia<>(origen, 0.0));
        
        while (!cola.isEmpty()) {
            NodoDistancia<Equipo> actual = cola.poll();
            Equipo equipoActual = actual.nodo;
            
            if (visitados.contains(equipoActual)) {
                continue;
            }
            
            visitados.add(equipoActual);
            
            if (equipoActual.equals(destino)) {
                break;
            }
            
            for (ConexionEquipo conexion : equipoActual.getConexiones()) {
                Equipo vecino = conexion.getEquipoDestino();
                
                if (!visitados.contains(vecino)) {
                    double nuevaDistancia = distancias.get(equipoActual) + conexion.getDistancia();
                    
                    if (nuevaDistancia < distancias.getOrDefault(vecino, Double.MAX_VALUE)) {
                        distancias.put(vecino, nuevaDistancia);
                        padres.put(vecino, equipoActual);
                        cola.offer(new NodoDistancia<>(vecino, nuevaDistancia));
                    }
                }
            }
        }
        
        return new ResultadoDijkstra<>(
            reconstruirCamino(padres, origen, destino),
            distancias.getOrDefault(destino, Double.MAX_VALUE)
        );
    }
    
    /**
     * Encuentra el camino más corto entre dos estadios basado en distancia
     */
    public ResultadoDijkstra<Estadio> caminoMasCortoEstadios(Estadio origen, Estadio destino) {
        Map<Estadio, Double> distancias = new HashMap<>();
        Map<Estadio, Estadio> padres = new HashMap<>();
        PriorityQueue<NodoDistancia<Estadio>> cola = new PriorityQueue<>();
        Set<Estadio> visitados = new HashSet<>();
        
        distancias.put(origen, 0.0);
        cola.offer(new NodoDistancia<>(origen, 0.0));
        
        while (!cola.isEmpty()) {
            NodoDistancia<Estadio> actual = cola.poll();
            Estadio estadioActual = actual.nodo;
            
            if (visitados.contains(estadioActual)) {
                continue;
            }
            
            visitados.add(estadioActual);
            
            if (estadioActual.equals(destino)) {
                break;
            }
            
            for (ConexionEstadio conexion : estadioActual.getConexiones()) {
                Estadio vecino = conexion.getEstadioDestino();
                
                if (!visitados.contains(vecino)) {
                    double nuevaDistancia = distancias.get(estadioActual) + conexion.getDistancia();
                    
                    if (nuevaDistancia < distancias.getOrDefault(vecino, Double.MAX_VALUE)) {
                        distancias.put(vecino, nuevaDistancia);
                        padres.put(vecino, estadioActual);
                        cola.offer(new NodoDistancia<>(vecino, nuevaDistancia));
                    }
                }
            }
        }
        
        return new ResultadoDijkstra<>(
            reconstruirCamino(padres, origen, destino),
            distancias.getOrDefault(destino, Double.MAX_VALUE)
        );
    }
    
    /**
     * Encuentra el camino de menor costo entre equipos
     */
    public ResultadoDijkstra<Equipo> caminoMenorCostoEquipos(Equipo origen, Equipo destino) {
        Map<Equipo, Double> costos = new HashMap<>();
        Map<Equipo, Equipo> padres = new HashMap<>();
        PriorityQueue<NodoDistancia<Equipo>> cola = new PriorityQueue<>();
        Set<Equipo> visitados = new HashSet<>();
        
        costos.put(origen, 0.0);
        cola.offer(new NodoDistancia<>(origen, 0.0));
        
        while (!cola.isEmpty()) {
            NodoDistancia<Equipo> actual = cola.poll();
            Equipo equipoActual = actual.nodo;
            
            if (visitados.contains(equipoActual)) {
                continue;
            }
            
            visitados.add(equipoActual);
            
            if (equipoActual.equals(destino)) {
                break;
            }
            
            for (ConexionEquipo conexion : equipoActual.getConexiones()) {
                Equipo vecino = conexion.getEquipoDestino();
                
                if (!visitados.contains(vecino)) {
                    double nuevoCosto = costos.get(equipoActual) + conexion.getCosto();
                    
                    if (nuevoCosto < costos.getOrDefault(vecino, Double.MAX_VALUE)) {
                        costos.put(vecino, nuevoCosto);
                        padres.put(vecino, equipoActual);
                        cola.offer(new NodoDistancia<>(vecino, nuevoCosto));
                    }
                }
            }
        }
        
        return new ResultadoDijkstra<>(
            reconstruirCamino(padres, origen, destino),
            costos.getOrDefault(destino, Double.MAX_VALUE)
        );
    }
    
    private <T> List<T> reconstruirCamino(Map<T, T> padres, T origen, T destino) {
        List<T> camino = new ArrayList<>();
        T actual = destino;
        
        while (actual != null && !actual.equals(origen)) {
            camino.add(0, actual);
            actual = padres.get(actual);
        }
        
        if (actual != null) {
            camino.add(0, origen);
        }
        
        return camino;
    }
    
    // Clases auxiliares
    private static class NodoDistancia<T> implements Comparable<NodoDistancia<T>> {
        T nodo;
        double distancia;
        
        NodoDistancia(T nodo, double distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }
        
        @Override
        public int compareTo(NodoDistancia<T> otro) {
            return Double.compare(this.distancia, otro.distancia);
        }
    }
    
    public static class ResultadoDijkstra<T> {
        private List<T> camino;
        private double distanciaTotal;
        
        public ResultadoDijkstra(List<T> camino, double distanciaTotal) {
            this.camino = camino;
            this.distanciaTotal = distanciaTotal;
        }
        
        public List<T> getCamino() {
            return camino;
        }
        
        public double getDistanciaTotal() {
            return distanciaTotal;
        }
    }
}
