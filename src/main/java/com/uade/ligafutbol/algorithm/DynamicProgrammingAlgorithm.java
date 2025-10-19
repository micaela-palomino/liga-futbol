package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Algoritmo de Programación Dinámica para planificar fixture óptimo
 */
@Component
public class DynamicProgrammingAlgorithm {
    
    /**
     * Planifica el fixture óptimo minimizando distancias totales
     * Usa programación dinámica para encontrar la mejor secuencia de partidos
     */
    public FixtureOptimo planificarFixtureOptimo(List<Equipo> equipos, List<LocalDateTime> fechasDisponibles) {
        int n = equipos.size();
        
        if (n < 2 || fechasDisponibles.isEmpty()) {
            return new FixtureOptimo(new ArrayList<>(), 0.0);
        }
        
        // Generar todos los posibles emparejamientos
        List<Emparejamiento> emparejamientos = generarEmparejamientos(equipos);
        
        // DP: dp[i][mask] = costo mínimo para programar i partidos con equipos usados en mask
        Map<String, Double> dp = new HashMap<>();
        Map<String, List<Emparejamiento>> caminos = new HashMap<>();
        
        // Caso base
        dp.put("0_0", 0.0);
        caminos.put("0_0", new ArrayList<>());
        
        // Llenar la tabla DP
        for (int i = 0; i < fechasDisponibles.size() && i < emparejamientos.size(); i++) {
            Map<String, Double> nuevoDp = new HashMap<>(dp);
            Map<String, List<Emparejamiento>> nuevosCaminos = new HashMap<>(caminos);
            
            for (Emparejamiento emp : emparejamientos) {
                int maskEquipo1 = 1 << equipos.indexOf(emp.getEquipo1());
                int maskEquipo2 = 1 << equipos.indexOf(emp.getEquipo2());
                
                for (Map.Entry<String, Double> entry : dp.entrySet()) {
                    String[] partes = entry.getKey().split("_");
                    int partidosAnteriores = Integer.parseInt(partes[0]);
                    int maskAnterior = Integer.parseInt(partes[1]);
                    
                    // Verificar que los equipos no estén ya usados en esta ronda
                    if ((maskAnterior & maskEquipo1) == 0 && (maskAnterior & maskEquipo2) == 0) {
                        int nuevoMask = maskAnterior | maskEquipo1 | maskEquipo2;
                        String nuevaClave = (partidosAnteriores + 1) + "_" + nuevoMask;
                        
                        double nuevoCosto = entry.getValue() + emp.getDistancia();
                        
                        if (!nuevoDp.containsKey(nuevaClave) || nuevoCosto < nuevoDp.get(nuevaClave)) {
                            nuevoDp.put(nuevaClave, nuevoCosto);
                            
                            List<Emparejamiento> nuevoCamino = new ArrayList<>(caminos.get(entry.getKey()));
                            nuevoCamino.add(emp);
                            nuevosCaminos.put(nuevaClave, nuevoCamino);
                        }
                    }
                }
            }
            
            dp = nuevoDp;
            caminos = nuevosCaminos;
        }
        
        // Encontrar la mejor solución
        double mejorCosto = Double.MAX_VALUE;
        List<Emparejamiento> mejorCamino = new ArrayList<>();
        
        for (Map.Entry<String, Double> entry : dp.entrySet()) {
            if (entry.getValue() < mejorCosto) {
                mejorCosto = entry.getValue();
                mejorCamino = caminos.get(entry.getKey());
            }
        }
        
        return new FixtureOptimo(mejorCamino, mejorCosto);
    }
    
    /**
     * Optimiza la distribución de partidos en fechas disponibles
     * Minimiza el costo total considerando restricciones de fechas
     */
    public FixtureOptimo optimizarDistribucionPartidos(List<Emparejamiento> partidos, List<LocalDateTime> fechas) {
        if (partidos.isEmpty() || fechas.isEmpty()) {
            return new FixtureOptimo(new ArrayList<>(), 0.0);
        }
        
        int n = partidos.size();
        int m = fechas.size();
        
        // dp[i][j] = costo mínimo para asignar los primeros i partidos en las primeras j fechas
        double[][] dp = new double[n + 1][m + 1];
        
        // Inicializar con valores altos
        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], Double.MAX_VALUE / 2);
        }
        dp[0][0] = 0;
        
        // Llenar la tabla DP
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                // No asignar el partido i a la fecha j
                dp[i][j] = dp[i][j - 1];
                
                // Asignar el partido i a la fecha j
                double costoPartido = partidos.get(i - 1).getDistancia();
                dp[i][j] = Math.min(dp[i][j], dp[i - 1][j - 1] + costoPartido);
            }
        }
        
        return new FixtureOptimo(partidos, dp[n][m]);
    }
    
    /**
     * Calcula el número óptimo de jornadas necesarias
     */
    public int calcularJornadasOptimas(int numeroEquipos) {
        // En un torneo todos contra todos, cada equipo juega n-1 partidos
        // Se pueden jugar n/2 partidos por jornada
        if (numeroEquipos % 2 == 0) {
            return numeroEquipos - 1;
        } else {
            return numeroEquipos; // Un equipo descansa cada jornada
        }
    }
    
    private List<Emparejamiento> generarEmparejamientos(List<Equipo> equipos) {
        List<Emparejamiento> emparejamientos = new ArrayList<>();
        
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                Equipo eq1 = equipos.get(i);
                Equipo eq2 = equipos.get(j);
                
                double distancia = calcularDistancia(eq1, eq2);
                emparejamientos.add(new Emparejamiento(eq1, eq2, distancia));
            }
        }
        
        return emparejamientos;
    }
    
    private double calcularDistancia(Equipo eq1, Equipo eq2) {
        // Buscar conexión directa
        for (var conexion : eq1.getConexiones()) {
            if (conexion.getEquipoDestino().equals(eq2)) {
                return conexion.getDistancia();
            }
        }
        return 100.0; // Distancia por defecto
    }
    
    // Clases auxiliares
    public static class Emparejamiento {
        private Equipo equipo1;
        private Equipo equipo2;
        private double distancia;
        
        public Emparejamiento(Equipo equipo1, Equipo equipo2, double distancia) {
            this.equipo1 = equipo1;
            this.equipo2 = equipo2;
            this.distancia = distancia;
        }
        
        public Equipo getEquipo1() {
            return equipo1;
        }
        
        public Equipo getEquipo2() {
            return equipo2;
        }
        
        public double getDistancia() {
            return distancia;
        }
    }
    
    public static class FixtureOptimo {
        private List<Emparejamiento> partidos;
        private double costoTotal;
        
        public FixtureOptimo(List<Emparejamiento> partidos, double costoTotal) {
            this.partidos = partidos;
            this.costoTotal = costoTotal;
        }
        
        public List<Emparejamiento> getPartidos() {
            return partidos;
        }
        
        public double getCostoTotal() {
            return costoTotal;
        }
    }
}
