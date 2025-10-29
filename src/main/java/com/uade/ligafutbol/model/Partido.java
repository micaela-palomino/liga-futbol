package com.uade.ligafutbol.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partido {
    
    @Id
    private String id = UUID.randomUUID().toString();
    
    @Relationship(type = "EQUIPO_LOCAL", direction = Relationship.Direction.OUTGOING)
    private Equipo equipoLocal;
    
    @Relationship(type = "EQUIPO_VISITANTE", direction = Relationship.Direction.OUTGOING)
    private Equipo equipoVisitante;
    
    @Relationship(type = "SE_JUEGA_EN", direction = Relationship.Direction.OUTGOING)
    private Estadio estadio;
    
    private LocalDateTime fecha;
    private Integer golesLocal;
    private Integer golesVisitante;
    private Boolean jugado;
    private Integer jornada;
    
    public Partido(Equipo equipoLocal, Equipo equipoVisitante, Estadio estadio, LocalDateTime fecha, Integer jornada) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.estadio = estadio;
        this.fecha = fecha;
        this.jornada = jornada;
        this.jugado = false;
        this.golesLocal = 0;
        this.golesVisitante = 0;
    }
    
    public void registrarResultado(Integer golesLocal, Integer golesVisitante) {
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.jugado = true;
        
        if (golesLocal > golesVisitante) {
            equipoLocal.registrarVictoria(golesLocal, golesVisitante);
            equipoVisitante.registrarDerrota(golesVisitante, golesLocal);
        } else if (golesLocal < golesVisitante) {
            equipoLocal.registrarDerrota(golesLocal, golesVisitante);
            equipoVisitante.registrarVictoria(golesVisitante, golesLocal);
        } else {
            equipoLocal.registrarEmpate(golesLocal, golesVisitante);
            equipoVisitante.registrarEmpate(golesVisitante, golesLocal);
        }
    }
}
