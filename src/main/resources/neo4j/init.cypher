/ Neo4j initialization / constraints for liga-futbol
// Run with: cypher-shell -u <user> -p <password> -f init.cypher

// Make team and stadium names unique
CREATE CONSTRAINT IF NOT EXISTS FOR (e:Estadio) REQUIRE e.nombre IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS FOR (t:Equipo) REQUIRE t.nombre IS UNIQUE;

// Optional: create indexes for fast lookups
CREATE INDEX IF NOT EXISTS FOR (p:Partido) ON (p.fecha);
CREATE INDEX IF NOT EXISTS FOR (e:Estadio) ON (e.ciudad);

// You can add more constraints/indexes here as needed