package com.uade.ligafutbol.repository;

import com.uade.ligafutbol.model.Equipo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends Neo4jRepository<Equipo, Long> {
    
    Optional<Equipo> findByNombre(String nombre);
    
    List<Equipo> findByCiudad(String ciudad);
    
    @Query("MATCH (e:Equipo) RETURN e ORDER BY e.puntos DESC, e.golesAFavor - e.golesEnContra DESC, e.golesAFavor DESC")
    List<Equipo> findAllOrderedByPosition();
    
    @Query("MATCH path = (e1:Equipo)-[:CONECTADO_CON*]-(e2:Equipo) " +
           "WHERE id(e1) = $equipoOrigenId AND id(e2) = $equipoDestinoId " +
           "RETURN path")
    List<Object> findAllPathsBetween(Long equipoOrigenId, Long equipoDestinoId);
}
