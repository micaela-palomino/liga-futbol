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

    // Getters/setters adicionales para que funcione sin Lombok
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCiudad() { return this.ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public Integer getCapacidad() { return this.capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    
    public Double getLatitud() { return this.latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    
    public Double getLongitud() { return this.longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    
    public Set<ConexionEstadio> getConexiones() { return this.conexiones; }
    public void setConexiones(Set<ConexionEstadio> conexiones) { this.conexiones = conexiones; }
}
