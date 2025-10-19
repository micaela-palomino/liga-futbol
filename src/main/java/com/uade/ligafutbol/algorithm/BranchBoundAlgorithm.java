package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Algoritmo Branch & Bound para optimizar el calendario minimizando viajes
 */
@Component
public class BranchBoundAlgorithm {
    
    private double mejorCostoGlobal;
    private List<PartidoCalendario> mejorSolucion;
    
    /**
     * Optimiza el calendario de partidos minimizando el costo total de viajes
     */
    public ResultadoCalendario optimizarCalendario(
            List<Equipo> equipos,
            List<LocalDateTime> fechasDisponibles,
            double limitePresupuesto) {
        
        mejorCostoGlobal = Double.MAX_VALUE;
        mejorSolucion = new ArrayList<>();
        
        List<ParPartidos> todosLosPares = generarTodosLosPares(equipos);
        List<PartidoCalendario> solucionActual = new ArrayList<>();
        Set<String> partidosAsignados = new HashSet<>();
        
        branchAndBound(
            todosLosPares,
            fechasDisponibles,
            solucionActual,
            partidosAsignados,
            0.0,
            0,
            limitePresupuesto
        );
        
        return new ResultadoCalendario(mejorSolucion, mejorCostoGlobal);
    }
    
    private void branchAndBound(
            List<ParPartidos> todosLosPares,
            List<LocalDateTime> fechasDisponibles,
            List<PartidoCalendario> solucionActual,
            Set<String> partidosAsignados,
            double costoActual,
            int nivelFecha,
            double limitePresupuesto) {
        
        // Poda por límite de presupuesto
        if (costoActual > limitePresupuesto) {
            return;
        }
        
        // Poda por mejor solución conocida
        if (costoActual >= mejorCostoGlobal) {
            return;
        }
        
        // Caso base: todas las fechas asignadas o todos los partidos programados
        if (nivelFecha >= fechasDisponibles.size() || partidosAsignados.size() >= todosLosPares.size()) {
            if (costoActual < mejorCostoGlobal) {
                mejorCostoGlobal = costoActual;
                mejorSolucion = new ArrayList<>(solucionActual);
            }
            return;
        }
        
        LocalDateTime fechaActual = fechasDisponibles.get(nivelFecha);
        Set<Equipo> equiposUsadosEnFecha = new HashSet<>();
        
        // Probar asignar diferentes partidos a esta fecha
        for (ParPartidos par : todosLosPares) {
            String keyPartido = generarKeyPartido(par.getEquipo1(), par.getEquipo2());
            
            // Verificar que el partido no haya sido asignado
            if (!partidosAsignados.contains(keyPartido)) {
                // Verificar que los equipos no jueguen dos veces en la misma fecha
                if (!equiposUsadosEnFecha.contains(par.getEquipo1()) && 
                    !equiposUsadosEnFecha.contains(par.getEquipo2())) {
                    
                    // Calcular costo del partido
                    double costoPartido = calcularCostoPartido(par);
                    double nuevoCosto = costoActual + costoPartido;
                    
                    // Calcular cota inferior (lower bound)
                    double cotaInferior = calcularCotaInferior(
                        todosLosPares,
                        partidosAsignados,
                        keyPartido,
                        nuevoCosto
                    );
                    
                    // Poda: si la cota inferior supera la mejor solución, no explorar
                    if (cotaInferior < mejorCostoGlobal) {
                        // Asignar partido
                        PartidoCalendario partido = new PartidoCalendario(
                            par.getEquipo1(),
                            par.getEquipo2(),
                            fechaActual,
                            costoPartido
                        );
                        
                        solucionActual.add(partido);
                        partidosAsignados.add(keyPartido);
                        equiposUsadosEnFecha.add(par.getEquipo1());
                        equiposUsadosEnFecha.add(par.getEquipo2());
                        
                        // Recursión
                        branchAndBound(
                            todosLosPares,
                            fechasDisponibles,
                            solucionActual,
                            partidosAsignados,
                            nuevoCosto,
                            nivelFecha,
                            limitePresupuesto
                        );
                        
                        // Backtrack
                        solucionActual.remove(solucionActual.size() - 1);
                        partidosAsignados.remove(keyPartido);
                        equiposUsadosEnFecha.remove(par.getEquipo1());
                        equiposUsadosEnFecha.remove(par.getEquipo2());
                    }
                }
            }
        }
        
        // También probar pasar a la siguiente fecha sin asignar más partidos
        branchAndBound(
            todosLosPares,
            fechasDisponibles,
            solucionActual,
            partidosAsignados,
            costoActual,
            nivelFecha + 1,
            limitePresupuesto
        );
    }
    
    /**
     * Calcula la cota inferior para la poda
     * Estima el costo mínimo posible para completar la solución
     */
    private double calcularCotaInferior(
            List<ParPartidos> todosLosPares,
            Set<String> partidosAsignados,
            String partidoActual,
            double costoActual) {
        
        double costoMinimoPendiente = 0.0;
        int partidosPendientes = 0;
        
        for (ParPartidos par : todosLosPares) {
            String key = generarKeyPartido(par.getEquipo1(), par.getEquipo2());
            
            if (!partidosAsignados.contains(key) && !key.equals(partidoActual)) {
                // Usar el costo mínimo posible (distancia mínima)
                costoMinimoPendiente += calcularCostoMinimo(par);
                partidosPendientes++;
            }
        }
        
        return costoActual + costoMinimoPendiente;
    }
    
    private double calcularCostoPartido(ParPartidos par) {
        // Buscar la conexión entre equipos
        for (var conexion : par.getEquipo1().getConexiones()) {
            if (conexion.getEquipoDestino().equals(par.getEquipo2())) {
                return conexion.getCosto();
            }
        }
        return 100.0; // Costo por defecto
    }
    
    private double calcularCostoMinimo(ParPartidos par) {
        // Retorna el costo mínimo estimado para un partido
        return calcularCostoPartido(par) * 0.8; // Estimación optimista
    }
    
    private List<ParPartidos> generarTodosLosPares(List<Equipo> equipos) {
        List<ParPartidos> pares = new ArrayList<>();
        
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                pares.add(new ParPartidos(equipos.get(i), equipos.get(j)));
            }
        }
        
        return pares;
    }
    
    private String generarKeyPartido(Equipo eq1, Equipo eq2) {
        long id1 = eq1.getId() != null ? eq1.getId() : eq1.hashCode();
        long id2 = eq2.getId() != null ? eq2.getId() : eq2.hashCode();
        
        if (id1 < id2) {
            return id1 + "-" + id2;
        } else {
            return id2 + "-" + id1;
        }
    }
    
    // Clases auxiliares
    public static class ParPartidos {
        private Equipo equipo1;
        private Equipo equipo2;
        
        public ParPartidos(Equipo equipo1, Equipo equipo2) {
            this.equipo1 = equipo1;
            this.equipo2 = equipo2;
        }
        
        public Equipo getEquipo1() {
            return equipo1;
        }
        
        public Equipo getEquipo2() {
            return equipo2;
        }
    }
    
    public static class PartidoCalendario {
        private Equipo equipoLocal;
        private Equipo equipoVisitante;
        private LocalDateTime fecha;
        private double costo;
        
        public PartidoCalendario(Equipo equipoLocal, Equipo equipoVisitante, LocalDateTime fecha, double costo) {
            this.equipoLocal = equipoLocal;
            this.equipoVisitante = equipoVisitante;
            this.fecha = fecha;
            this.costo = costo;
        }
        
        public Equipo getEquipoLocal() {
            return equipoLocal;
        }
        
        public Equipo getEquipoVisitante() {
            return equipoVisitante;
        }
        
        public LocalDateTime getFecha() {
            return fecha;
        }
        
        public double getCosto() {
            return costo;
        }
    }
    
    public static class ResultadoCalendario {
        private List<PartidoCalendario> partidos;
        private double costoTotal;
        
        public ResultadoCalendario(List<PartidoCalendario> partidos, double costoTotal) {
            this.partidos = partidos;
            this.costoTotal = costoTotal;
        }
        
        public List<PartidoCalendario> getPartidos() {
            return partidos;
        }
        
        public double getCostoTotal() {
            return costoTotal;
        }
    }
}
