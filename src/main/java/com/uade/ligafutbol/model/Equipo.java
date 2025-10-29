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
public class Equipo {
    
    @Id
    private String id = UUID.randomUUID().toString();
    
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

    // Explicit getters/setters for puntos in case Lombok annotation processing
    // is not active in the build/IDE. Having these avoids "cannot find symbol"
    // errors when calling getPuntos()/setPuntos() from other classes.
    public Integer getPuntos() {
        return this.puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    // Explicit getters/setters for partidosJugados in case Lombok annotation
    // processing is not active in the IDE/build. This avoids "cannot find symbol"
    // errors when other classes call getPartidosJugados()/setPartidosJugados().
    public Integer getPartidosJugados() {
        return this.partidosJugados;
    }

    public void setPartidosJugados(Integer partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    // Getters/setters adicionales para que funcione sin Lombok
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCiudad() { return this.ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public Integer getPartidosGanados() { return this.partidosGanados; }
    public void setPartidosGanados(Integer partidosGanados) { this.partidosGanados = partidosGanados; }
    
    public Integer getPartidosEmpatados() { return this.partidosEmpatados; }
    public void setPartidosEmpatados(Integer partidosEmpatados) { this.partidosEmpatados = partidosEmpatados; }
    
    public Integer getPartidosPerdidos() { return this.partidosPerdidos; }
    public void setPartidosPerdidos(Integer partidosPerdidos) { this.partidosPerdidos = partidosPerdidos; }
    
    public Integer getGolesAFavor() { return this.golesAFavor; }
    public void setGolesAFavor(Integer golesAFavor) { this.golesAFavor = golesAFavor; }
    
    public Integer getGolesEnContra() { return this.golesEnContra; }
    public void setGolesEnContra(Integer golesEnContra) { this.golesEnContra = golesEnContra; }
    
    public Estadio getEstadio() { return this.estadio; }
    public void setEstadio(Estadio estadio) { this.estadio = estadio; }
    
    public Set<ConexionEquipo> getConexiones() { return this.conexiones; }
    public void setConexiones(Set<ConexionEquipo> conexiones) { this.conexiones = conexiones; }
}