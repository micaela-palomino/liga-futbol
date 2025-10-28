package com.uade.ligafutbol.repository;

import com.uade.ligafutbol.model.Estadio;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadioRepository extends Neo4jRepository<Estadio, String> {
    
    java.util.List<Estadio> findByNombre(String nombre);
    
    List<Estadio> findByCiudad(String ciudad);
    
    @Query("MATCH path = (e1:Estadio)-[:CONECTADO_CON*]-(e2:Estadio) " +
        "WHERE e1.id = $estadioOrigenId AND e2.id = $estadioDestinoId " +
        "RETURN path")
    List<Object> findAllPathsBetween(String estadioOrigenId, String estadioDestinoId);
}
