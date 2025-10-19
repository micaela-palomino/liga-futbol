package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.model.Partido;
import com.uade.ligafutbol.model.ConexionEquipo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Algoritmo Greedy para emparejar partidos minimizando distancias
 */
@Component
public class GreedyAlgorithm {
    
    /**
     * Empareja equipos para partidos minimizando la distancia total
     * Enfoque greedy: siempre selecciona el par de equipos más cercanos disponibles
     */
    public List<EmparejamientoPartido> emparejarPartidosMinimizandoDistancia(List<Equipo> equipos, LocalDateTime fechaInicio) {
        List<EmparejamientoPartido> emparejamientos = new ArrayList<>();
        Set<Equipo> equiposDisponibles = new HashSet<>(equipos);
        LocalDateTime fechaActual = fechaInicio;
        
        while (equiposDisponibles.size() >= 2) {
            EmparejamientoPartido mejorEmparejamiento = null;
            double menorDistancia = Double.MAX_VALUE;
            
            // Buscar el par de equipos con menor distancia
            List<Equipo> listaDisponibles = new ArrayList<>(equiposDisponibles);
            
            for (int i = 0; i < listaDisponibles.size(); i++) {
                Equipo equipo1 = listaDisponibles.get(i);
                
                for (int j = i + 1; j < listaDisponibles.size(); j++) {
                    Equipo equipo2 = listaDisponibles.get(j);
                    
                    double distancia = obtenerDistancia(equipo1, equipo2);
                    
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        mejorEmparejamiento = new EmparejamientoPartido(
                            equipo1, equipo2, distancia, fechaActual
                        );
                    }
                }
            }
            
            if (mejorEmparejamiento != null) {
                emparejamientos.add(mejorEmparejamiento);
                equiposDisponibles.remove(mejorEmparejamiento.getEquipoLocal());
                equiposDisponibles.remove(mejorEmparejamiento.getEquipoVisitante());
                fechaActual = fechaActual.plusDays(7); // Siguiente semana
            }
        }
        
        return emparejamientos;
    }
    
    /**
     * Selecciona el mejor estadio para un partido basándose en criterios greedy
     * Prioriza: menor distancia promedio, mayor capacidad
     */
    public EmparejamientoPartido seleccionarMejorEstadio(Equipo equipo1, Equipo equipo2, LocalDateTime fecha) {
        // Por defecto, usar el estadio del equipo local
        double distancia = obtenerDistancia(equipo1, equipo2);
        return new EmparejamientoPartido(equipo1, equipo2, distancia, fecha);
    }
    
    /**
     * Asigna fechas a partidos minimizando conflictos y distancias
     */
    public List<EmparejamientoPartido> asignarFechasOptimas(List<EmparejamientoPartido> partidos, LocalDateTime fechaInicio) {
        // Ordenar partidos por distancia (menor a mayor)
        List<EmparejamientoPartido> partidosOrdenados = new ArrayList<>(partidos);
        partidosOrdenados.sort(Comparator.comparingDouble(EmparejamientoPartido::getDistancia));
        
        LocalDateTime fechaActual = fechaInicio;
        List<EmparejamientoPartido> partidosConFecha = new ArrayList<>();
        
        for (EmparejamientoPartido partido : partidosOrdenados) {
            EmparejamientoPartido partidoConFecha = new EmparejamientoPartido(
                partido.getEquipoLocal(),
                partido.getEquipoVisitante(),
                partido.getDistancia(),
                fechaActual
            );
            partidosConFecha.add(partidoConFecha);
            fechaActual = fechaActual.plusDays(7);
        }
        
        return partidosConFecha;
    }
    
    /**
     * Obtiene la distancia entre dos equipos
     */
    private double obtenerDistancia(Equipo equipo1, Equipo equipo2) {
        for (ConexionEquipo conexion : equipo1.getConexiones()) {
            if (conexion.getEquipoDestino().equals(equipo2)) {
                return conexion.getDistancia();
            }
        }
        
        // Si no hay conexión directa, retornar una distancia grande
        return 1000.0;
    }
    
    // Clase auxiliar
    public static class EmparejamientoPartido {
        private Equipo equipoLocal;
        private Equipo equipoVisitante;
        private double distancia;
        private LocalDateTime fecha;
        
        public EmparejamientoPartido(Equipo equipoLocal, Equipo equipoVisitante, double distancia, LocalDateTime fecha) {
            this.equipoLocal = equipoLocal;
            this.equipoVisitante = equipoVisitante;
            this.distancia = distancia;
            this.fecha = fecha;
        }
        
        public Equipo getEquipoLocal() {
            return equipoLocal;
        }
        
        public Equipo getEquipoVisitante() {
            return equipoVisitante;
        }
        
        public double getDistancia() {
            return distancia;
        }
        
        public LocalDateTime getFecha() {
            return fecha;
        }
    }
}
