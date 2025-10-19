package com.uade.ligafutbol.repository;

import com.uade.ligafutbol.model.Partido;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends Neo4jRepository<Partido, Long> {
    
    List<Partido> findByJornada(Integer jornada);
    
    List<Partido> findByJugado(Boolean jugado);
    
    @Query("MATCH (p:Partido)-[:EQUIPO_LOCAL]->(el:Equipo), " +
           "(p)-[:EQUIPO_VISITANTE]->(ev:Equipo) " +
           "WHERE id(el) = $equipoId OR id(ev) = $equipoId " +
           "RETURN p ORDER BY p.fecha")
    List<Partido> findByEquipo(Long equipoId);
    
    @Query("MATCH (p:Partido) RETURN p ORDER BY p.fecha")
    List<Partido> findAllOrderedByFecha();
}
