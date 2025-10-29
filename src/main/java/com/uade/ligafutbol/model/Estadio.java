package com.uade.ligafutbol.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node
@Data
@EqualsAndHashCode(exclude = "conexiones")
@NoArgsConstructor
@AllArgsConstructor
public class Estadio {
    
    @Id
    private String id = UUID.randomUUID().toString();
    
    private String nombre;
    private String ciudad;
    private Integer capacidad;
    private Double latitud;
    private Double longitud;
    
    @Relationship(type = "CONECTADO_CON", direction = Relationship.Direction.OUTGOING)
    private Set<ConexionEstadio> conexiones = new HashSet<>();
    
    public Estadio(String nombre, String ciudad, Integer capacidad, Double latitud, Double longitud) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.capacidad = capacidad;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
