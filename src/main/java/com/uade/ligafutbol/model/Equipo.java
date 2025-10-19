package com.uade.ligafutbol.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String nombre;
    private String ciudad;
    private Integer puntos;
    private Integer partidosJugados;
    private Integer partidosGanados;
    private Integer partidosEmpatados;
    private Integer partidosPerdidos;
    private Integer golesAFavor;
    private Integer golesEnContra;
    
    @Relationship(type = "JUEGA_EN", direction = Relationship.Direction.OUTGOING)
    private Estadio estadio;
    
    @Relationship(type = "CONECTADO_CON", direction = Relationship.Direction.OUTGOING)
    private Set<ConexionEquipo> conexiones = new HashSet<>();
    
    public Equipo(String nombre, String ciudad) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.puntos = 0;
        this.partidosJugados = 0;
        this.partidosGanados = 0;
        this.partidosEmpatados = 0;
        this.partidosPerdidos = 0;
        this.golesAFavor = 0;
        this.golesEnContra = 0;
    }
    
    public Integer getDiferenciaGoles() {
        return golesAFavor - golesEnContra;
    }
    
    public void registrarVictoria(int golesFavor, int golesContra) {
        this.partidosJugados++;
        this.partidosGanados++;
        this.puntos += 3;
        this.golesAFavor += golesFavor;
        this.golesEnContra += golesContra;
    }
    
    public void registrarEmpate(int golesFavor, int golesContra) {
        this.partidosJugados++;
        this.partidosEmpatados++;
        this.puntos += 1;
        this.golesAFavor += golesFavor;
        this.golesEnContra += golesContra;
    }
    
    public void registrarDerrota(int golesFavor, int golesContra) {
        this.partidosJugados++;
        this.partidosPerdidos++;
        this.golesAFavor += golesFavor;
        this.golesEnContra += golesContra;
    }
}
