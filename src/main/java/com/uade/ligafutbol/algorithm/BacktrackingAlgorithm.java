package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.Equipo;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Algoritmo de Backtracking para generar combinaciones válidas de cruces sin repetición
 */
@Component
public class BacktrackingAlgorithm {
    
    /**
     * Genera todas las combinaciones válidas de partidos para una jornada
     * Sin repetir equipos en la misma jornada
     */
    public List<List<Cruce>> generarCombinacionesCruces(List<Equipo> equipos) {
        List<List<Cruce>> todasLasCombinaciones = new ArrayList<>();
        List<Cruce> combinacionActual = new ArrayList<>();
        Set<Equipo> equiposUsados = new HashSet<>();
        
        generarCombinacionesRecursivo(equipos, 0, combinacionActual, equiposUsados, todasLasCombinaciones);
        
        return todasLasCombinaciones;
    }
    
    private void generarCombinacionesRecursivo(
            List<Equipo> equipos,
            int indice,
            List<Cruce> combinacionActual,
            Set<Equipo> equiposUsados,
            List<List<Cruce>> todasLasCombinaciones) {
        
        // Caso base: todos los equipos han sido emparejados
        if (equiposUsados.size() == equipos.size()) {
            todasLasCombinaciones.add(new ArrayList<>(combinacionActual));
            return;
        }
        
        // Si quedan equipos impares y solo queda uno, también es válido
        if (equiposUsados.size() == equipos.size() - 1) {
            todasLasCombinaciones.add(new ArrayList<>(combinacionActual));
            return;
        }
        
        // Encontrar el primer equipo no usado
        Equipo equipoLocal = null;
        for (Equipo eq : equipos) {
            if (!equiposUsados.contains(eq)) {
                equipoLocal = eq;
                break;
            }
        }
        
        if (equipoLocal == null) {
            return;
        }
        
        // Probar emparejar con cada equipo restante
        for (Equipo equipoVisitante : equipos) {
            if (!equiposUsados.contains(equipoVisitante) && !equipoVisitante.equals(equipoLocal)) {
                // Hacer el emparejamiento
                Cruce cruce = new Cruce(equipoLocal, equipoVisitante);
                combinacionActual.add(cruce);
                equiposUsados.add(equipoLocal);
                equiposUsados.add(equipoVisitante);
                
                // Recursión
                generarCombinacionesRecursivo(equipos, indice + 1, combinacionActual, equiposUsados, todasLasCombinaciones);
                
                // Backtrack
                combinacionActual.remove(combinacionActual.size() - 1);
                equiposUsados.remove(equipoLocal);
                equiposUsados.remove(equipoVisitante);
            }
        }
    }
    
    /**
     * Genera un fixture completo (todos contra todos) sin repeticiones
     */
    public List<Jornada> generarFixtureCompleto(List<Equipo> equipos) {
        List<Jornada> fixture = new ArrayList<>();
        int n = equipos.size();
        
        // Si el número de equipos es impar, agregar un equipo "fantasma"
        List<Equipo> equiposAux = new ArrayList<>(equipos);
        if (n % 2 != 0) {
            equiposAux.add(null); // null representa "descansa"
            n++;
        }
        
        // Algoritmo de round-robin
        for (int jornada = 0; jornada < n - 1; jornada++) {
            List<Cruce> cruces = new ArrayList<>();
            
            for (int i = 0; i < n / 2; i++) {
                Equipo equipo1 = equiposAux.get(i);
                Equipo equipo2 = equiposAux.get(n - 1 - i);
                
                // Solo agregar si ninguno es null (descansa)
                if (equipo1 != null && equipo2 != null) {
                    cruces.add(new Cruce(equipo1, equipo2));
                }
            }
            
            fixture.add(new Jornada(jornada + 1, cruces));
            
            // Rotar equipos (excepto el primero)
            Equipo temp = equiposAux.get(n - 1);
            for (int i = n - 1; i > 1; i--) {
                equiposAux.set(i, equiposAux.get(i - 1));
            }
            equiposAux.set(1, temp);
        }
        
        return fixture;
    }
    
    /**
     * Valida que un fixture no tenga cruces repetidos
     */
    public boolean validarFixtureSinRepeticiones(List<Jornada> fixture) {
        Set<String> crucesVistos = new HashSet<>();
        
        for (Jornada jornada : fixture) {
            for (Cruce cruce : jornada.getCruces()) {
                String idLocal = cruce.getEquipoLocal().getId() != null ? cruce.getEquipoLocal().getId() : String.valueOf(cruce.getEquipoLocal().hashCode());
                String idVisitante = cruce.getEquipoVisitante().getId() != null ? cruce.getEquipoVisitante().getId() : String.valueOf(cruce.getEquipoVisitante().hashCode());
                String key1 = idLocal + "-" + idVisitante;
                String key2 = idVisitante + "-" + idLocal;
                
                if (crucesVistos.contains(key1) || crucesVistos.contains(key2)) {
                    return false; // Cruce repetido
                }
                
                crucesVistos.add(key1);
            }
        }
        
        return true;
    }
    
    /**
     * Encuentra una configuración válida de partidos con restricciones
     */
    public List<Cruce> encontrarConfiguracionValida(
            List<Equipo> equipos,
            Set<String> restricciones) {
        
        List<Cruce> solucion = new ArrayList<>();
        Set<Equipo> equiposUsados = new HashSet<>();
        
        if (encontrarConfiguracionRecursivo(equipos, restricciones, solucion, equiposUsados, 0)) {
            return solucion;
        }
        
        return new ArrayList<>(); // No se encontró solución
    }
    
    private boolean encontrarConfiguracionRecursivo(
            List<Equipo> equipos,
            Set<String> restricciones,
            List<Cruce> solucion,
            Set<Equipo> equiposUsados,
            int nivel) {
        
        if (equiposUsados.size() >= equipos.size() - 1) {
            return true; // Solución encontrada
        }
        
        for (int i = 0; i < equipos.size(); i++) {
            Equipo eq1 = equipos.get(i);
            if (equiposUsados.contains(eq1)) continue;
            
            for (int j = i + 1; j < equipos.size(); j++) {
                Equipo eq2 = equipos.get(j);
                if (equiposUsados.contains(eq2)) continue;
                
                String id1 = eq1.getId() != null ? eq1.getId() : String.valueOf(eq1.hashCode());
                String id2 = eq2.getId() != null ? eq2.getId() : String.valueOf(eq2.hashCode());
                String restriccion = id1 + "-" + id2;
                
                // Verificar restricciones
                if (!restricciones.contains(restriccion)) {
                    Cruce cruce = new Cruce(eq1, eq2);
                    solucion.add(cruce);
                    equiposUsados.add(eq1);
                    equiposUsados.add(eq2);
                    
                    if (encontrarConfiguracionRecursivo(equipos, restricciones, solucion, equiposUsados, nivel + 1)) {
                        return true;
                    }
                    
                    // Backtrack
                    solucion.remove(solucion.size() - 1);
                    equiposUsados.remove(eq1);
                    equiposUsados.remove(eq2);
                }
            }
        }
        
        return false;
    }
    
    // Clases auxiliares
    public static class Cruce {
        private Equipo equipoLocal;
        private Equipo equipoVisitante;
        
        public Cruce(Equipo equipoLocal, Equipo equipoVisitante) {
            this.equipoLocal = equipoLocal;
            this.equipoVisitante = equipoVisitante;
        }
        
        public Equipo getEquipoLocal() {
            return equipoLocal;
        }
        
        public Equipo getEquipoVisitante() {
            return equipoVisitante;
        }
    }
    
    public static class Jornada {
        private int numero;
        private List<Cruce> cruces;
        
        public Jornada(int numero, List<Cruce> cruces) {
            this.numero = numero;
            this.cruces = cruces;
        }
        
        public int getNumero() {
            return numero;
        }
        
        public List<Cruce> getCruces() {
            return cruces;
        }
    }
}
