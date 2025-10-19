# ⚡ Inicio Rápido - Liga de Fútbol

## 🚀 Ejecutar en 3 Pasos

### 1️⃣ Iniciar Neo4j
```bash
cd liga-futbol
docker-compose up -d
```

### 2️⃣ Ejecutar la aplicación
```bash
./start.sh
```

### 3️⃣ Probar la API
```bash
# Ver todos los equipos
curl http://localhost:8080/api/equipos

# Ver tabla de posiciones
curl http://localhost:8080/api/equipos/tabla-posiciones
```

---

## 🎯 Endpoints Más Usados

### Equipos y Tabla
```bash
# Listar equipos
curl http://localhost:8080/api/equipos

# Tabla de posiciones (Divide y Conquista)
curl http://localhost:8080/api/equipos/tabla-posiciones

# Crear equipo
curl -X POST http://localhost:8080/api/equipos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Vélez", "ciudad": "Buenos Aires"}'
```

### Algoritmos de Grafos
```bash
# BFS - Buscar camino
curl http://localhost:8080/api/algoritmos/bfs/0/3

# DFS - Buscar camino
curl http://localhost:8080/api/algoritmos/dfs/0/3
```

### Dijkstra - Ruta Más Corta
```bash
# Entre equipos
curl http://localhost:8080/api/algoritmos/dijkstra/equipos/0/1

# Entre estadios
curl http://localhost:8080/api/algoritmos/dijkstra/estadios/0/2
```

### MST - Minimizar Costos
```bash
# Algoritmo de Prim
curl http://localhost:8080/api/algoritmos/mst/prim

# Algoritmo de Kruskal
curl http://localhost:8080/api/algoritmos/mst/kruskal
```

### Greedy - Emparejar Partidos
```bash
curl -X POST "http://localhost:8080/api/algoritmos/greedy/emparejar?fechaInicio=2024-12-01T15:00:00"
```

### Backtracking - Fixture Completo
```bash
curl http://localhost:8080/api/algoritmos/backtracking/fixture-completo
```

### Branch & Bound - Optimizar Calendario
```bash
curl -X POST http://localhost:8080/api/algoritmos/branch-bound/calendario?presupuesto=5000 \
  -H "Content-Type: application/json" \
  -d '["2024-12-01T15:00:00", "2024-12-08T15:00:00", "2024-12-15T15:00:00"]'
```

---

## 🌐 Interfaces Web

### Aplicación Spring Boot
```
http://localhost:8080
```

### Neo4j Browser
```
http://localhost:7474
Usuario: neo4j
Contraseña: password
```

---

## 🔍 Consultas Neo4j Útiles

Abre Neo4j Browser (http://localhost:7474) y ejecuta:

```cypher
// Ver todos los equipos
MATCH (e:Equipo) RETURN e

// Ver conexiones entre equipos
MATCH (e1:Equipo)-[c:CONECTADO_CON]->(e2:Equipo) 
RETURN e1.nombre, c.distancia, e2.nombre

// Ver todos los partidos
MATCH (p:Partido)-[:EQUIPO_LOCAL]->(el:Equipo),
      (p)-[:EQUIPO_VISITANTE]->(ev:Equipo)
RETURN el.nombre as Local, 
       p.golesLocal + '-' + p.golesVisitante as Resultado,
       ev.nombre as Visitante,
       p.fecha

// Ver tabla de posiciones
MATCH (e:Equipo) 
RETURN e.nombre, e.puntos, e.partidosJugados, 
       e.golesAFavor, e.golesEnContra
ORDER BY e.puntos DESC

// Visualizar el grafo completo
MATCH (n) RETURN n LIMIT 100
```

---

## 📊 Flujo de Trabajo Típico

### Escenario 1: Organizar un Torneo

```bash
# 1. Ver equipos disponibles
curl http://localhost:8080/api/equipos

# 2. Generar fixture completo
curl http://localhost:8080/api/algoritmos/backtracking/fixture-completo

# 3. Optimizar calendario
curl -X POST http://localhost:8080/api/algoritmos/branch-bound/calendario?presupuesto=10000 \
  -H "Content-Type: application/json" \
  -d '["2024-12-01T15:00:00", "2024-12-08T15:00:00", "2024-12-15T15:00:00"]'

# 4. Ver tabla de posiciones
curl http://localhost:8080/api/equipos/tabla-posiciones
```

### Escenario 2: Optimizar Rutas

```bash
# 1. Calcular MST (red óptima)
curl http://localhost:8080/api/algoritmos/mst/prim

# 2. Ruta más corta entre dos equipos
curl http://localhost:8080/api/algoritmos/dijkstra/equipos/0/3

# 3. Emparejar partidos minimizando distancias
curl -X POST "http://localhost:8080/api/algoritmos/greedy/emparejar?fechaInicio=2024-12-01T15:00:00"
```

### Escenario 3: Registrar Resultados

```bash
# 1. Ver partidos programados
curl http://localhost:8080/api/partidos

# 2. Registrar resultado de un partido
curl -X PUT "http://localhost:8080/api/partidos/0/resultado?golesLocal=3&golesVisitante=1"

# 3. Ver tabla actualizada
curl http://localhost:8080/api/equipos/tabla-posiciones
```

---

## 🛠️ Comandos Útiles

### Docker
```bash
# Ver contenedores corriendo
docker ps

# Ver logs de Neo4j
docker-compose logs -f neo4j

# Detener Neo4j
docker-compose down

# Reiniciar Neo4j (borra datos)
docker-compose down -v && docker-compose up -d
```

### Maven
```bash
# Compilar sin tests
mvn clean install -DskipTests

# Ejecutar aplicación
mvn spring-boot:run

# Limpiar proyecto
mvn clean
```

---

## 📦 Importar Colección de Postman

1. Abre Postman
2. Click en "Import"
3. Selecciona `POSTMAN_COLLECTION.json`
4. ¡Listo! Todos los endpoints disponibles

---

## 🐛 Solución Rápida de Problemas

### Error: "Connection refused"
```bash
# Verificar que Neo4j esté corriendo
docker ps

# Si no está, iniciarlo
docker-compose up -d
```

### Error: "Port 8080 already in use"
```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error: "Authentication failed"
```bash
# Verificar credenciales en application.properties
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=password
```

---

## 📚 Documentación Completa

- **README.md** - Documentación principal
- **GUIA_INSTALACION.md** - Instalación detallada
- **EJEMPLOS_ALGORITMOS.md** - Ejemplos de cada algoritmo
- **ARQUITECTURA.md** - Diseño del sistema
- **RESUMEN_PROYECTO.md** - Resumen ejecutivo

---

## 🎓 Algoritmos Disponibles

| Algoritmo | Endpoint | Complejidad |
|-----------|----------|-------------|
| BFS | `/api/algoritmos/bfs/{o}/{d}` | O(V+E) |
| DFS | `/api/algoritmos/dfs/{o}/{d}` | O(V+E) |
| Dijkstra | `/api/algoritmos/dijkstra/equipos/{o}/{d}` | O((V+E)logV) |
| Prim | `/api/algoritmos/mst/prim` | O(ElogV) |
| Kruskal | `/api/algoritmos/mst/kruskal` | O(ElogE) |
| Greedy | `/api/algoritmos/greedy/emparejar` | O(n²) |
| MergeSort | `/api/equipos/tabla-posiciones` | O(nlogn) |
| DP | `/api/algoritmos/dp/fixture` | O(n²×2ⁿ) |
| Backtracking | `/api/algoritmos/backtracking/fixture-completo` | O(n!) |
| Branch&Bound | `/api/algoritmos/branch-bound/calendario` | O(2ⁿ) |

---

## ✅ Checklist de Inicio

- [ ] Docker instalado y corriendo
- [ ] Java 17+ instalado
- [ ] Maven instalado
- [ ] Neo4j iniciado (`docker-compose up -d`)
- [ ] Aplicación compilada (`mvn clean install`)
- [ ] Aplicación corriendo (`mvn spring-boot:run`)
- [ ] Probado endpoint de prueba (`curl http://localhost:8080/api/equipos`)
- [ ] Neo4j Browser accesible (http://localhost:7474)

---

## 🎯 Próximo Paso

Una vez que todo esté funcionando, explora:

1. **EJEMPLOS_ALGORITMOS.md** - Para ver ejemplos detallados
2. **Neo4j Browser** - Para visualizar el grafo
3. **Postman Collection** - Para probar todos los endpoints
4. **ARQUITECTURA.md** - Para entender el diseño

---

¡Listo para usar! 🚀⚽
