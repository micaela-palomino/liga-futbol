package com.uade.ligafutbol.algorithm;

import com.uade.ligafutbol.model.ConexionEstadio;
import com.uade.ligafutbol.model.Estadio;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Algoritmos de Árbol de Expansión Mínima (MST) operando sobre Estadios
 */
@Component
public class MSTEstadioAlgorithm {

    /**
     * Algoritmo de Prim (desde primer nodo)
     */
    public ResultadoMST algoritmoPrim(List<Estadio> estadios) {
        return algoritmoPrimDesdeNodo(estadios, 0);
    }

    /**
     * Algoritmo de Prim comenzando desde un índice específico
     */
    public ResultadoMST algoritmoPrimDesdeNodo(List<Estadio> estadios, int indiceInicio) {
        if (estadios == null || estadios.isEmpty() || indiceInicio < 0 || indiceInicio >= estadios.size()) {
            return new ResultadoMST(new ArrayList<>(), 0.0);
        }

        Set<Estadio> visitados = new HashSet<>();
        List<AristaConexion> aristasSeleccionadas = new ArrayList<>();
        PriorityQueue<AristaConexion> cola = new PriorityQueue<>();
        double costoTotal = 0.0;

        Estadio inicio = estadios.get(indiceInicio);
        visitados.add(inicio);

        // Agregar conexiones del estadio inicial
        for (ConexionEstadio conexion : inicio.getConexiones()) {
            cola.offer(new AristaConexion(inicio, conexion.getEstadioDestino(), conexion.getDistancia()));
        }

        while (!cola.isEmpty() && visitados.size() < estadios.size()) {
            AristaConexion arista = cola.poll();
            if (visitados.contains(arista.destino)) continue;

            aristasSeleccionadas.add(arista);
            costoTotal += arista.costo;
            visitados.add(arista.destino);

            for (ConexionEstadio conexion : arista.destino.getConexiones()) {
                if (!visitados.contains(conexion.getEstadioDestino())) {
                    cola.offer(new AristaConexion(arista.destino, conexion.getEstadioDestino(), conexion.getDistancia()));
                }
            }
        }

        return new ResultadoMST(aristasSeleccionadas, costoTotal);
    }

    /**
     * Algoritmo de Kruskal
     */
    public ResultadoMST algoritmoKruskal(List<Estadio> estadios) {
        List<AristaConexion> todas = new ArrayList<>();
        for (Estadio e : estadios) {
            for (ConexionEstadio c : e.getConexiones()) {
                todas.add(new AristaConexion(e, c.getEstadioDestino(), c.getDistancia()));
            }
        }
        Collections.sort(todas);

        UnionFind uf = new UnionFind(estadios);
        List<AristaConexion> sel = new ArrayList<>();
        double total = 0.0;

        for (AristaConexion a : todas) {
            if (uf.find(a.origen) != uf.find(a.destino)) {
                uf.union(a.origen, a.destino);
                sel.add(a);
                total += a.costo;
                if (sel.size() == estadios.size() - 1) break;
            }
        }
        return new ResultadoMST(sel, total);
    }

    /**
     * Genera múltiples variaciones (varios inicios de Prim + Kruskal) y ordena por costo
     */
    public ResultadoMultiplesMST algoritmoMultiplesMST(List<Estadio> estadios) {
        List<ResultadoMST> variaciones = new ArrayList<>();
        for (int i = 0; i < Math.min(estadios.size(), 3); i++) {
            ResultadoMST r = algoritmoPrimDesdeNodo(estadios, i);
            if (!r.getAristas().isEmpty()) variaciones.add(r);
        }
        ResultadoMST kr = algoritmoKruskal(estadios);
        if (!kr.getAristas().isEmpty()) variaciones.add(kr);
        variaciones.sort(Comparator.comparingDouble(ResultadoMST::getCostoTotal));
        return new ResultadoMultiplesMST(variaciones);
    }

    // ---- Estructuras auxiliares ----
    public static class AristaConexion implements Comparable<AristaConexion> {
        private Estadio origen;
        private Estadio destino;
        private double costo; // distancia

        public AristaConexion(Estadio origen, Estadio destino, double costo) {
            this.origen = origen;
            this.destino = destino;
            this.costo = costo;
        }

        public Estadio getOrigen() { return origen; }
        public Estadio getDestino() { return destino; }
        public double getCosto() { return costo; }

        @Override
        public int compareTo(AristaConexion o) { return Double.compare(this.costo, o.costo); }
    }

    public static class ResultadoMST {
        private List<AristaConexion> aristas;
        private double costoTotal;

        public ResultadoMST(List<AristaConexion> aristas, double costoTotal) {
            this.aristas = aristas;
            this.costoTotal = costoTotal;
        }

        public List<AristaConexion> getAristas() { return aristas; }
        public double getCostoTotal() { return costoTotal; }
    }

    public static class ResultadoMultiplesMST {
        private List<ResultadoMST> variaciones;

        public ResultadoMultiplesMST(List<ResultadoMST> variaciones) { this.variaciones = variaciones; }
        public List<ResultadoMST> getVariaciones() { return variaciones; }
        public ResultadoMST getMejorMST() { return variaciones.isEmpty() ? null : variaciones.get(0); }
        public double getMejorCosto() { return getMejorMST() != null ? getMejorMST().getCostoTotal() : Double.MAX_VALUE; }
    }

    private static class UnionFind {
        private final Map<Estadio, Estadio> parent = new HashMap<>();
        private final Map<Estadio, Integer> rank = new HashMap<>();

        UnionFind(List<Estadio> nodos) {
            for (Estadio e : nodos) { parent.put(e, e); rank.put(e, 0); }
        }
        Estadio find(Estadio x) {
            if (!parent.get(x).equals(x)) parent.put(x, find(parent.get(x)));
            return parent.get(x);
        }
        void union(Estadio a, Estadio b) {
            Estadio ra = find(a), rb = find(b);
            if (ra.equals(rb)) return;
            int raR = rank.get(ra), rbR = rank.get(rb);
            if (raR < rbR) parent.put(ra, rb);
            else if (raR > rbR) parent.put(rb, ra);
            else { parent.put(rb, ra); rank.put(ra, raR + 1); }
        }
    }
}
