package com.uade.ligafutbol.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@RelationshipProperties
@Data
@EqualsAndHashCode(exclude = "estadioDestino")
@NoArgsConstructor
@AllArgsConstructor
public class ConexionEstadio {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @TargetNode
    private Estadio estadioDestino;
    
    private Double distancia; // en kilómetros
    private Double costo; // costo de traslado
    private Integer tiempoViaje; // en minutos
    
    public ConexionEstadio(Estadio estadioDestino, Double distancia, Double costo, Integer tiempoViaje) {
        this.estadioDestino = estadioDestino;
        this.distancia = distancia;
        this.costo = costo;
        this.tiempoViaje = tiempoViaje;
    }

    // Getters/setters adicionales para que funcione sin Lombok
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    
    public Estadio getEstadioDestino() { return this.estadioDestino; }
    public void setEstadioDestino(Estadio estadioDestino) { this.estadioDestino = estadioDestino; }
    
    public Double getDistancia() { return this.distancia; }
    public void setDistancia(Double distancia) { this.distancia = distancia; }
    
    public Double getCosto() { return this.costo; }
    public void setCosto(Double costo) { this.costo = costo; }
    
    public Integer getTiempoViaje() { return this.tiempoViaje; }
    public void setTiempoViaje(Integer tiempoViaje) { this.tiempoViaje = tiempoViaje; }
}
