package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Algoritmo Divide y Conquista para ordenar la tabla de posiciones
 * Implementa MergeSort para ordenar equipos por múltiples criterios
 */
@Component
public class DivideConquerAlgorithm {
    
    /**
     * Ordena la tabla de posiciones usando MergeSort
     * Criterios: 1) Puntos, 2) Diferencia de goles, 3) Goles a favor
     */
    public List<Equipo> ordenarTablaPosiciones(List<Equipo> equipos) {
        if (equipos.size() <= 1) {
            return new ArrayList<>(equipos);
        }
        
        return mergeSort(new ArrayList<>(equipos));
    }
    
    private List<Equipo> mergeSort(List<Equipo> equipos) {
        if (equipos.size() <= 1) {
            return equipos;
        }
        
        int medio = equipos.size() / 2;
        
        // Dividir
        List<Equipo> izquierda = new ArrayList<>(equipos.subList(0, medio));
        List<Equipo> derecha = new ArrayList<>(equipos.subList(medio, equipos.size()));
        
        // Conquistar
        izquierda = mergeSort(izquierda);
        derecha = mergeSort(derecha);
        
        // Combinar
        return merge(izquierda, derecha);
    }
    
    private List<Equipo> merge(List<Equipo> izquierda, List<Equipo> derecha) {
        List<Equipo> resultado = new ArrayList<>();
        int i = 0, j = 0;
        
        while (i < izquierda.size() && j < derecha.size()) {
            if (compararEquipos(izquierda.get(i), derecha.get(j)) >= 0) {
                resultado.add(izquierda.get(i));
                i++;
            } else {
                resultado.add(derecha.get(j));
                j++;
            }
        }
        
        while (i < izquierda.size()) {
            resultado.add(izquierda.get(i));
            i++;
        }
        
        while (j < derecha.size()) {
            resultado.add(derecha.get(j));
            j++;
        }
        
        return resultado;
    }
    
    /**
     * Compara dos equipos según los criterios de la tabla de posiciones
     * Retorna: positivo si equipo1 > equipo2, negativo si equipo1 < equipo2, 0 si son iguales
     */
    private int compararEquipos(Equipo equipo1, Equipo equipo2) {
        // 1. Comparar por puntos
        if (!equipo1.getPuntos().equals(equipo2.getPuntos())) {
            return equipo1.getPuntos().compareTo(equipo2.getPuntos());
        }
        
        // 2. Comparar por diferencia de goles
        int difGoles1 = equipo1.getDiferenciaGoles();
        int difGoles2 = equipo2.getDiferenciaGoles();
        
        if (difGoles1 != difGoles2) {
            return Integer.compare(difGoles1, difGoles2);
        }
        
        // 3. Comparar por goles a favor
        if (!equipo1.getGolesAFavor().equals(equipo2.getGolesAFavor())) {
            return equipo1.getGolesAFavor().compareTo(equipo2.getGolesAFavor());
        }
        
        // 4. Si todo es igual, comparar por nombre (alfabéticamente)
        return equipo1.getNombre().compareTo(equipo2.getNombre());
    }
    
    /**
     * Encuentra el equipo con más puntos usando divide y conquista
     */
    public Equipo encontrarLider(List<Equipo> equipos) {
        if (equipos.isEmpty()) {
            return null;
        }
        
        return encontrarLiderRecursivo(equipos, 0, equipos.size() - 1);
    }
    
    private Equipo encontrarLiderRecursivo(List<Equipo> equipos, int inicio, int fin) {
        if (inicio == fin) {
            return equipos.get(inicio);
        }
        
        int medio = (inicio + fin) / 2;
        
        Equipo liderIzq = encontrarLiderRecursivo(equipos, inicio, medio);
        Equipo liderDer = encontrarLiderRecursivo(equipos, medio + 1, fin);
        
        return compararEquipos(liderIzq, liderDer) >= 0 ? liderIzq : liderDer;
    }
    
    /**
     * Calcula estadísticas agregadas usando divide y conquista
     */
    public EstadisticasLiga calcularEstadisticas(List<Equipo> equipos) {
        if (equipos.isEmpty()) {
            return new EstadisticasLiga(0, 0, 0.0);
        }
        
        return calcularEstadisticasRecursivo(equipos, 0, equipos.size() - 1);
    }
    
    private EstadisticasLiga calcularEstadisticasRecursivo(List<Equipo> equipos, int inicio, int fin) {
        if (inicio == fin) {
            Equipo equipo = equipos.get(inicio);
            return new EstadisticasLiga(
                equipo.getGolesAFavor(),
                equipo.getGolesEnContra(),
                equipo.getPuntos().doubleValue()
            );
        }
        
        int medio = (inicio + fin) / 2;
        
        EstadisticasLiga statsIzq = calcularEstadisticasRecursivo(equipos, inicio, medio);
        EstadisticasLiga statsDer = calcularEstadisticasRecursivo(equipos, medio + 1, fin);
        
        return new EstadisticasLiga(
            statsIzq.getTotalGolesAFavor() + statsDer.getTotalGolesAFavor(),
            statsIzq.getTotalGolesEnContra() + statsDer.getTotalGolesEnContra(),
            statsIzq.getTotalPuntos() + statsDer.getTotalPuntos()
        );
    }
    
    // Clase auxiliar
    public static class EstadisticasLiga {
        private int totalGolesAFavor;
        private int totalGolesEnContra;
        private double totalPuntos;
        
        public EstadisticasLiga(int totalGolesAFavor, int totalGolesEnContra, double totalPuntos) {
            this.totalGolesAFavor = totalGolesAFavor;
            this.totalGolesEnContra = totalGolesEnContra;
            this.totalPuntos = totalPuntos;
        }
        
        public int getTotalGolesAFavor() {
            return totalGolesAFavor;
        }
        
        public int getTotalGolesEnContra() {
            return totalGolesEnContra;
        }
        
        public double getTotalPuntos() {
            return totalPuntos;
        }
    }
}
