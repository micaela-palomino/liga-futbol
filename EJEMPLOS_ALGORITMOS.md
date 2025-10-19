# 🎯 Ejemplos de Uso de Algoritmos

Este documento contiene ejemplos prácticos de cómo usar cada algoritmo implementado en el sistema.

## 1. 🔍 Grafos: BFS y DFS

### Búsqueda en Anchura (BFS)

**Propósito**: Encontrar el camino más corto en términos de número de saltos entre dos equipos.

**Endpoint**: `GET /api/algoritmos/bfs/{origenId}/{destinoId}`

**Ejemplo**:
```bash
curl http://localhost:8080/api/algoritmos/bfs/0/3
```

**Respuesta**:
```json
[
  {
    "id": 0,
    "nombre": "Boca Juniors",
    "ciudad": "Buenos Aires"
  },
  {
    "id": 2,
    "nombre": "Racing Club",
    "ciudad": "Avellaneda"
  },
  {
    "id": 3,
    "nombre": "Independiente",
    "ciudad": "Avellaneda"
  }
]
```

**Uso práctico**: Determinar la ruta de conexión más directa entre dos equipos para planificar traslados.

---

### Búsqueda en Profundidad (DFS)

**Propósito**: Explorar todas las rutas posibles entre equipos.

**Endpoint**: `GET /api/algoritmos/dfs/{origenId}/{destinoId}`

**Ejemplo**:
```bash
curl http://localhost:8080/api/algoritmos/dfs/0/1
```

**Uso práctico**: Verificar conectividad y explorar rutas alternativas.

---

## 2. 🛣️ Dijkstra: Ruta Más Corta

### Ruta más corta entre equipos

**Propósito**: Encontrar la ruta con menor distancia total entre dos equipos.

**Endpoint**: `GET /api/algoritmos/dijkstra/equipos/{origenId}/{destinoId}`

**Ejemplo**:
```bash
curl http://localhost:8080/api/algoritmos/dijkstra/equipos/0/1
```

**Respuesta**:
```json
{
  "camino": [
    {
      "id": 0,
      "nombre": "Boca Juniors",
      "ciudad": "Buenos Aires"
    },
    {
      "id": 1,
      "nombre": "River Plate",
      "ciudad": "Buenos Aires"
    }
  ],
  "distanciaTotal": 12.5
}
```

**Uso práctico**: Calcular la ruta más corta para minimizar distancias de viaje entre estadios.

---

### Ruta más corta entre estadios

**Endpoint**: `GET /api/algoritmos/dijkstra/estadios/{origenId}/{destinoId}`

**Ejemplo**:
```bash
curl http://localhost:8080/api/algoritmos/dijkstra/estadios/0/2
```

**Uso práctico**: Optimizar rutas de transporte de equipamiento entre estadios.

---

## 3. 🌲 MST: Árbol de Expansión Mínima

### Algoritmo de Prim

**Propósito**: Minimizar el costo total de conexiones entre todos los equipos.

**Endpoint**: `GET /api/algoritmos/mst/prim`

**Ejemplo**:
```bash
curl http://localhost:8080/api/algoritmos/mst/prim
```

**Respuesta**:
```json
{
  "aristas": [
    {
      "origen": {
        "id": 0,
        "nombre": "Boca Juniors"
      },
      "destino": {
        "id": 2,
        "nombre": "Racing Club"
      },
      "costo": 350.0
    },
    {
      "origen": {
        "id": 2,
        "nombre": "Racing Club"
      },
      "destino": {
        "id": 3,
        "nombre": "Independiente"
      },
      "costo": 100.0
    },
    {
      "origen": {
        "id": 0,
        "nombre": "Boca Juniors"
      },
      "destino": {
        "id": 1,
        "nombre": "River Plate"
      },
      "costo": 500.0
    }
  ],
  "costoTotal": 950.0
}
```

**Uso práctico**: Determinar la red óptima de conexiones para minimizar costos de infraestructura de transporte.

---

### Algoritmo de Kruskal

**Endpoint**: `GET /api/algoritmos/mst/kruskal`

**Ejemplo**:
```bash
curl http://localhost:8080/api/algoritmos/mst/kruskal
```

**Uso práctico**: Alternativa a Prim para calcular el MST, útil para grafos dispersos.

---

## 4. 💰 Greedy: Emparejamiento de Partidos

**Propósito**: Emparejar equipos para partidos minimizando distancias totales.

**Endpoint**: `POST /api/algoritmos/greedy/emparejar?fechaInicio={fecha}`

**Ejemplo**:
```bash
curl -X POST "http://localhost:8080/api/algoritmos/greedy/emparejar?fechaInicio=2024-12-01T15:00:00"
```

**Respuesta**:
```json
[
  {
    "equipoLocal": {
      "id": 2,
      "nombre": "Racing Club"
    },
    "equipoVisitante": {
      "id": 3,
      "nombre": "Independiente"
    },
    "distancia": 2.1,
    "fecha": "2024-12-01T15:00:00"
  },
  {
    "equipoLocal": {
      "id": 0,
      "nombre": "Boca Juniors"
    },
    "equipoVisitante": {
      "id": 1,
      "nombre": "River Plate"
    },
    "distancia": 12.5,
    "fecha": "2024-12-08T15:00:00"
  }
]
```

**Uso práctico**: Programar partidos priorizando los equipos más cercanos para reducir costos de viaje.

---

## 5. 📊 Divide y Conquista: Tabla de Posiciones

**Propósito**: Ordenar la tabla de posiciones usando MergeSort.

**Endpoint**: `GET /api/equipos/tabla-posiciones`

**Ejemplo**:
```bash
curl http://localhost:8080/api/equipos/tabla-posiciones
```

**Respuesta**:
```json
[
  {
    "id": 0,
    "nombre": "Boca Juniors",
    "puntos": 3,
    "partidosJugados": 1,
    "partidosGanados": 1,
    "partidosEmpatados": 0,
    "partidosPerdidos": 0,
    "golesAFavor": 2,
    "golesEnContra": 1
  },
  {
    "id": 2,
    "nombre": "Racing Club",
    "puntos": 1,
    "partidosJugados": 1,
    "partidosGanados": 0,
    "partidosEmpatados": 1,
    "partidosPerdidos": 0,
    "golesAFavor": 1,
    "golesEnContra": 1
  }
]
```

**Criterios de ordenamiento**:
1. Puntos (descendente)
2. Diferencia de goles (descendente)
3. Goles a favor (descendente)
4. Nombre alfabético

**Uso práctico**: Mostrar la clasificación actual del torneo de forma eficiente.

---

## 6. 🎲 Programación Dinámica: Fixture Óptimo

**Propósito**: Planificar el fixture minimizando distancias totales según fechas disponibles.

**Endpoint**: `POST /api/algoritmos/dp/fixture`

**Ejemplo**:
```bash
curl -X POST http://localhost:8080/api/algoritmos/dp/fixture \
  -H "Content-Type: application/json" \
  -d '[
    "2024-12-01T15:00:00",
    "2024-12-08T15:00:00",
    "2024-12-15T15:00:00",
    "2024-12-22T15:00:00"
  ]'
```

**Respuesta**:
```json
{
  "partidos": [
    {
      "equipo1": {
        "nombre": "Racing Club"
      },
      "equipo2": {
        "nombre": "Independiente"
      },
      "distancia": 2.1
    },
    {
      "equipo1": {
        "nombre": "Boca Juniors"
      },
      "equipo2": {
        "nombre": "Racing Club"
      },
      "distancia": 8.3
    }
  ],
  "costoTotal": 45.7
}
```

**Uso práctico**: Optimizar el calendario completo del torneo considerando restricciones de fechas.

---

## 7. 🔄 Backtracking: Fixture Completo

**Propósito**: Generar todas las combinaciones válidas de partidos sin repetir equipos en la misma jornada.

**Endpoint**: `GET /api/algoritmos/backtracking/fixture-completo`

**Ejemplo**:
```bash
curl http://localhost:8080/api/algoritmos/backtracking/fixture-completo
```

**Respuesta**:
```json
[
  {
    "numero": 1,
    "cruces": [
      {
        "equipoLocal": {
          "nombre": "Boca Juniors"
        },
        "equipoVisitante": {
          "nombre": "River Plate"
        }
      },
      {
        "equipoLocal": {
          "nombre": "Racing Club"
        },
        "equipoVisitante": {
          "nombre": "Independiente"
        }
      }
    ]
  },
  {
    "numero": 2,
    "cruces": [
      {
        "equipoLocal": {
          "nombre": "Boca Juniors"
        },
        "equipoVisitante": {
          "nombre": "Racing Club"
        }
      },
      {
        "equipoLocal": {
          "nombre": "River Plate"
        },
        "equipoVisitante": {
          "nombre": "Independiente"
        }
      }
    ]
  },
  {
    "numero": 3,
    "cruces": [
      {
        "equipoLocal": {
          "nombre": "Boca Juniors"
        },
        "equipoVisitante": {
          "nombre": "Independiente"
        }
      },
      {
        "equipoLocal": {
          "nombre": "River Plate"
        },
        "equipoVisitante": {
          "nombre": "Racing Club"
        }
      }
    ]
  }
]
```

**Características**:
- Todos contra todos (round-robin)
- Sin repeticiones de cruces
- Cada equipo juega una vez por jornada
- Fixture balanceado

**Uso práctico**: Generar el calendario completo de un torneo de liga.

---

## 8. 🌿 Branch & Bound: Optimización de Calendario

**Propósito**: Optimizar el calendario completo minimizando costos de viaje con restricción de presupuesto.

**Endpoint**: `POST /api/algoritmos/branch-bound/calendario?presupuesto={monto}`

**Ejemplo**:
```bash
curl -X POST http://localhost:8080/api/algoritmos/branch-bound/calendario?presupuesto=5000 \
  -H "Content-Type: application/json" \
  -d '[
    "2024-12-01T15:00:00",
    "2024-12-08T15:00:00",
    "2024-12-15T15:00:00"
  ]'
```

**Respuesta**:
```json
{
  "partidos": [
    {
      "equipoLocal": {
        "nombre": "Racing Club"
      },
      "equipoVisitante": {
        "nombre": "Independiente"
      },
      "fecha": "2024-12-01T15:00:00",
      "costo": 100.0
    },
    {
      "equipoLocal": {
        "nombre": "Boca Juniors"
      },
      "equipoVisitante": {
        "nombre": "Racing Club"
      },
      "fecha": "2024-12-08T15:00:00",
      "costo": 350.0
    }
  ],
  "costoTotal": 450.0
}
```

**Características**:
- Poda por presupuesto
- Poda por mejor solución conocida
- Optimización global del calendario
- Respeta restricciones de fechas

**Uso práctico**: Planificar el calendario del torneo con un presupuesto limitado de traslados.

---

## 📝 Casos de Uso Combinados

### Caso 1: Planificar un Torneo Completo

```bash
# 1. Generar fixture completo
curl http://localhost:8080/api/algoritmos/backtracking/fixture-completo

# 2. Optimizar calendario con presupuesto
curl -X POST http://localhost:8080/api/algoritmos/branch-bound/calendario?presupuesto=10000 \
  -H "Content-Type: application/json" \
  -d '["2024-12-01T15:00:00", "2024-12-08T15:00:00", "2024-12-15T15:00:00"]'

# 3. Ver tabla de posiciones
curl http://localhost:8080/api/equipos/tabla-posiciones
```

### Caso 2: Optimizar Costos de Traslado

```bash
# 1. Calcular MST para red óptima
curl http://localhost:8080/api/algoritmos/mst/prim

# 2. Encontrar rutas más cortas
curl http://localhost:8080/api/algoritmos/dijkstra/equipos/0/3

# 3. Emparejar partidos minimizando distancias
curl -X POST "http://localhost:8080/api/algoritmos/greedy/emparejar?fechaInicio=2024-12-01T15:00:00"
```

### Caso 3: Análisis de Conectividad

```bash
# 1. Verificar camino con BFS
curl http://localhost:8080/api/algoritmos/bfs/0/3

# 2. Explorar rutas con DFS
curl http://localhost:8080/api/algoritmos/dfs/0/3

# 3. Calcular distancia mínima
curl http://localhost:8080/api/algoritmos/dijkstra/equipos/0/3
```

---

## 🎓 Complejidad de los Algoritmos

| Algoritmo | Complejidad Temporal | Complejidad Espacial |
|-----------|---------------------|---------------------|
| BFS | O(V + E) | O(V) |
| DFS | O(V + E) | O(V) |
| Dijkstra | O((V + E) log V) | O(V) |
| Prim | O(E log V) | O(V) |
| Kruskal | O(E log E) | O(V) |
| Greedy | O(n²) | O(n) |
| MergeSort | O(n log n) | O(n) |
| DP Fixture | O(n² × 2ⁿ) | O(n × 2ⁿ) |
| Backtracking | O(n!) | O(n) |
| Branch & Bound | O(2ⁿ) con poda | O(n) |

Donde:
- V = número de vértices (equipos/estadios)
- E = número de aristas (conexiones)
- n = número de elementos a procesar

---

## 💡 Tips de Uso

1. **BFS vs DFS**: Usa BFS para caminos más cortos, DFS para exploración completa
2. **Prim vs Kruskal**: Prim es mejor para grafos densos, Kruskal para grafos dispersos
3. **Greedy**: Rápido pero no siempre óptimo, ideal para soluciones aproximadas
4. **DP**: Óptimo pero costoso, usa solo cuando necesites la mejor solución
5. **Backtracking**: Genera todas las soluciones, útil para fixtures completos
6. **Branch & Bound**: Mejor que backtracking puro gracias a la poda

---

¡Explora estos algoritmos y optimiza tu liga de fútbol! ⚽🏆
