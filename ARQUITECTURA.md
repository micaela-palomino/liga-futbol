# 🏗️ Arquitectura del Sistema - Liga de Fútbol

## Visión General

El sistema está diseñado siguiendo una arquitectura en capas con Spring Boot y Neo4j como base de datos de grafos.

```
┌─────────────────────────────────────────────────────────┐
│                    API REST Layer                        │
│              (Controllers - HTTP Endpoints)              │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                          │
│         (Business Logic & Algorithm Orchestration)       │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  Algorithm Layer                         │
│    (BFS, DFS, Dijkstra, MST, Greedy, DP, etc.)         │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                 Repository Layer                         │
│           (Spring Data Neo4j Repositories)               │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    Neo4j Database                        │
│              (Graph Database - Nodes & Relationships)    │
└─────────────────────────────────────────────────────────┘
```

## Capas del Sistema

### 1. **API REST Layer** (Controllers)

**Responsabilidad**: Exponer endpoints HTTP y manejar requests/responses.

**Componentes**:
- `EquipoController`: Gestión de equipos y tabla de posiciones
- `EstadioController`: Gestión de estadios
- `PartidoController`: Gestión de partidos y resultados
- `AlgorithmController`: Endpoints para ejecutar algoritmos

**Tecnologías**: Spring Web, REST, JSON

---

### 2. **Service Layer**

**Responsabilidad**: Lógica de negocio y orquestación de algoritmos.

**Componente Principal**: `LigaService`

**Funciones**:
- Coordinar operaciones CRUD
- Invocar algoritmos según necesidad
- Validar datos de negocio
- Transformar datos entre capas

---

### 3. **Algorithm Layer**

**Responsabilidad**: Implementación de algoritmos avanzados.

**Componentes**:

#### 3.1 Grafos
- **`GrafoAlgorithm`**: BFS, DFS, verificación de conectividad
- **Complejidad**: O(V + E)
- **Uso**: Búsqueda de caminos, exploración de grafos

#### 3.2 Caminos Mínimos
- **`DijkstraAlgorithm`**: Rutas más cortas
- **Complejidad**: O((V + E) log V)
- **Uso**: Optimización de rutas de viaje

#### 3.3 Árboles de Expansión Mínima
- **`MSTAlgorithm`**: Prim y Kruskal
- **Complejidad**: O(E log V)
- **Uso**: Minimización de costos de red

#### 3.4 Algoritmos Voraces
- **`GreedyAlgorithm`**: Emparejamiento de partidos
- **Complejidad**: O(n²)
- **Uso**: Soluciones rápidas aproximadas

#### 3.5 Divide y Conquista
- **`DivideConquerAlgorithm`**: MergeSort para tabla
- **Complejidad**: O(n log n)
- **Uso**: Ordenamiento eficiente

#### 3.6 Programación Dinámica
- **`DynamicProgrammingAlgorithm`**: Fixture óptimo
- **Complejidad**: O(n² × 2ⁿ)
- **Uso**: Optimización global

#### 3.7 Backtracking
- **`BacktrackingAlgorithm`**: Generación de fixtures
- **Complejidad**: O(n!)
- **Uso**: Soluciones exhaustivas

#### 3.8 Branch & Bound
- **`BranchBoundAlgorithm`**: Optimización con poda
- **Complejidad**: O(2ⁿ) con poda
- **Uso**: Optimización con restricciones

---

### 4. **Repository Layer**

**Responsabilidad**: Acceso a datos y persistencia.

**Componentes**:
- `EquipoRepository`: CRUD de equipos + queries personalizadas
- `EstadioRepository`: CRUD de estadios + búsquedas
- `PartidoRepository`: CRUD de partidos + filtros

**Tecnología**: Spring Data Neo4j

---

### 5. **Model Layer**

**Responsabilidad**: Representación de entidades del dominio.

**Entidades Principales**:

#### Nodos (Nodes)
- **`Equipo`**: Representa un equipo de fútbol
- **`Estadio`**: Representa un estadio
- **`Partido`**: Representa un partido

#### Relaciones (Relationships)
- **`ConexionEquipo`**: Conexión entre equipos (distancia, costo)
- **`ConexionEstadio`**: Conexión entre estadios

---

## Modelo de Datos en Neo4j

### Diagrama de Grafos

```
    (Equipo)─[JUEGA_EN]→(Estadio)
       │
       │
   [CONECTADO_CON]
       │
       ↓
    (Equipo)

    (Partido)─[EQUIPO_LOCAL]→(Equipo)
       │
       └─[EQUIPO_VISITANTE]→(Equipo)
       │
       └─[SE_JUEGA_EN]→(Estadio)
```

### Propiedades de Nodos

**Equipo**:
```
- id: Long
- nombre: String
- ciudad: String
- puntos: Integer
- partidosJugados: Integer
- partidosGanados: Integer
- partidosEmpatados: Integer
- partidosPerdidos: Integer
- golesAFavor: Integer
- golesEnContra: Integer
```

**Estadio**:
```
- id: Long
- nombre: String
- ciudad: String
- capacidad: Integer
- latitud: Double
- longitud: Double
```

**Partido**:
```
- id: Long
- fecha: LocalDateTime
- golesLocal: Integer
- golesVisitante: Integer
- jugado: Boolean
- jornada: Integer
```

### Propiedades de Relaciones

**ConexionEquipo / ConexionEstadio**:
```
- distancia: Double (km)
- costo: Double ($)
- tiempoViaje: Integer (minutos)
```

---

## Flujo de Datos

### Ejemplo: Calcular Ruta Más Corta

```
1. Cliente → GET /api/algoritmos/dijkstra/equipos/0/1

2. AlgorithmController recibe request
   ↓
3. Llama a LigaService.encontrarRutaMasCorta(0, 1)
   ↓
4. LigaService obtiene equipos desde EquipoRepository
   ↓
5. LigaService invoca DijkstraAlgorithm.caminoMasCortoEquipos()
   ↓
6. DijkstraAlgorithm ejecuta el algoritmo sobre el grafo
   ↓
7. Retorna ResultadoDijkstra con camino y distancia
   ↓
8. LigaService retorna resultado a Controller
   ↓
9. Controller serializa a JSON y responde al cliente
```

---

## Patrones de Diseño Utilizados

### 1. **Repository Pattern**
- Abstracción del acceso a datos
- Implementado por Spring Data Neo4j

### 2. **Service Layer Pattern**
- Separación de lógica de negocio
- Orquestación de operaciones

### 3. **Strategy Pattern**
- Diferentes algoritmos intercambiables
- Cada algoritmo en su propia clase

### 4. **DTO Pattern** (implícito)
- Clases de resultado específicas por algoritmo
- Ej: `ResultadoDijkstra`, `ResultadoMST`

### 5. **Dependency Injection**
- Spring IoC Container
- `@Autowired` para inyección de dependencias

---

## Decisiones de Arquitectura

### ¿Por qué Neo4j?

1. **Modelo de Grafos Natural**: Equipos y estadios forman naturalmente un grafo
2. **Consultas Eficientes**: Cypher optimizado para traversals
3. **Relaciones Explícitas**: Las conexiones tienen propiedades (distancia, costo)
4. **Visualización**: Neo4j Browser permite visualizar el grafo

### ¿Por qué Spring Boot?

1. **Productividad**: Configuración automática
2. **Ecosistema**: Spring Data Neo4j integrado
3. **REST**: Spring Web facilita creación de APIs
4. **DevTools**: Hot reload durante desarrollo

### ¿Por qué Separar Algoritmos?

1. **Single Responsibility**: Cada clase tiene una responsabilidad
2. **Testabilidad**: Fácil de probar individualmente
3. **Mantenibilidad**: Cambios aislados
4. **Reutilización**: Algoritmos independientes del dominio

---

## Escalabilidad

### Horizontal

- **Neo4j Cluster**: Para alta disponibilidad
- **Load Balancer**: Distribuir requests entre instancias
- **Caché**: Redis para resultados frecuentes

### Vertical

- **Índices Neo4j**: Optimizar búsquedas
- **Paginación**: Limitar resultados grandes
- **Async Processing**: Para algoritmos costosos

---

## Seguridad

### Implementaciones Recomendadas

1. **Autenticación**: Spring Security + JWT
2. **Autorización**: Role-based access control
3. **Validación**: Bean Validation en DTOs
4. **HTTPS**: Certificados SSL/TLS
5. **Rate Limiting**: Prevenir abuso de API

---

## Monitoreo y Logging

### Herramientas

- **Spring Boot Actuator**: Métricas y health checks
- **SLF4J + Logback**: Logging estructurado
- **Neo4j Monitoring**: Query performance

### Logs Importantes

```java
@Slf4j
public class LigaService {
    public List<Equipo> obtenerTablaPosiciones() {
        log.info("Calculando tabla de posiciones");
        // ...
        log.debug("Tabla calculada con {} equipos", equipos.size());
    }
}
```

---

## Testing

### Estrategia de Testing

1. **Unit Tests**: Algoritmos individuales
2. **Integration Tests**: Repositories + Neo4j
3. **API Tests**: Controllers + MockMvc
4. **Performance Tests**: Algoritmos con datasets grandes

### Ejemplo

```java
@SpringBootTest
class DijkstraAlgorithmTest {
    @Test
    void testCaminoMasCorto() {
        // Given
        Equipo origen = new Equipo("A", "Ciudad A");
        Equipo destino = new Equipo("B", "Ciudad B");
        
        // When
        var resultado = dijkstra.caminoMasCortoEquipos(origen, destino);
        
        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getDistanciaTotal() > 0);
    }
}
```

---

## Mejoras Futuras

1. **GraphQL API**: Alternativa a REST
2. **WebSockets**: Updates en tiempo real
3. **Machine Learning**: Predicción de resultados
4. **Frontend**: React/Angular dashboard
5. **Microservicios**: Separar algoritmos en servicios independientes
6. **Event Sourcing**: Historial completo de cambios
7. **CQRS**: Separar lecturas y escrituras

---

## Conclusión

La arquitectura está diseñada para ser:

- ✅ **Modular**: Componentes independientes
- ✅ **Escalable**: Preparada para crecer
- ✅ **Mantenible**: Código limpio y organizado
- ✅ **Testeable**: Fácil de probar
- ✅ **Extensible**: Fácil agregar nuevos algoritmos

El uso de Neo4j como base de datos de grafos es ideal para representar las relaciones entre equipos, estadios y partidos, permitiendo ejecutar algoritmos de grafos de manera eficiente.
