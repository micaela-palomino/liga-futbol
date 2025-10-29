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
@EqualsAndHashCode(exclude = "equipoDestino")
@NoArgsConstructor
@AllArgsConstructor
public class ConexionEquipo {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @TargetNode
    private Equipo equipoDestino;
    
    private Double distancia; // en kilómetros
    private Double costo; // costo de traslado
    private Integer tiempoViaje; // en minutos
    
    public ConexionEquipo(Equipo equipoDestino, Double distancia, Double costo, Integer tiempoViaje) {
        this.equipoDestino = equipoDestino;
        this.distancia = distancia;
        this.costo = costo;
        this.tiempoViaje = tiempoViaje;
    }
}
