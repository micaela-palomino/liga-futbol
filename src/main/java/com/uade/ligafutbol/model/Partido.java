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

    // Getters/setters adicionales para que funcione sin Lombok
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    
    public Equipo getEquipoLocal() { return this.equipoLocal; }
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }
    
    public Equipo getEquipoVisitante() { return this.equipoVisitante; }
    public void setEquipoVisitante(Equipo equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    
    public Estadio getEstadio() { return this.estadio; }
    public void setEstadio(Estadio estadio) { this.estadio = estadio; }
    
    public LocalDateTime getFecha() { return this.fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public Integer getGolesLocal() { return this.golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }
    
    public Integer getGolesVisitante() { return this.golesVisitante; }
    public void setGolesVisitante(Integer golesVisitante) { this.golesVisitante = golesVisitante; }
    
    public Boolean getJugado() { return this.jugado; }
    public void setJugado(Boolean jugado) { this.jugado = jugado; }
    
    public Integer getJornada() { return this.jornada; }
    public void setJornada(Integer jornada) { this.jornada = jornada; }
}
