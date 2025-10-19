# Liga de Fútbol - Sistema de Gestión con Algoritmos Avanzados

Sistema completo de gestión de liga de fútbol implementado con **Spring Boot** y **Neo4j**, que incluye algoritmos avanzados para optimización de rutas, calendarios y torneos.

## 🚀 Tecnologías

- **Spring Boot 3.1.5**
- **Spring Data Neo4j**
- **Spring Web**
- **Spring Boot DevTools**
- **Neo4j Database**
- **Maven**
- **Java 17**

## 📋 Algoritmos Implementados

### 1. **Grafos (BFS/DFS)**
- Búsqueda en anchura (BFS) para encontrar conexiones entre equipos
- Búsqueda en profundidad (DFS) para explorar rutas entre estadios
- Verificación de conectividad del grafo

### 2. **Dijkstra**
- Cálculo de la ruta más corta entre equipos
- Optimización de trayectos entre estadios
- Minimización de costos de traslado

### 3. **Prim y Kruskal (MST)**
- Árbol de expansión mínima para minimizar costos totales de traslados
- Dos implementaciones: Prim y Kruskal
- Optimización de red de conexiones entre equipos

### 4. **Greedy**
- Emparejamiento de partidos minimizando distancias
- Selección óptima de estadios
- Asignación de fechas optimizadas

### 5. **Divide y Conquista**
- Ordenamiento de tabla de posiciones (MergeSort)
- Búsqueda del líder de la liga
- Cálculo de estadísticas agregadas

### 6. **Programación Dinámica**
- Planificación de fixture óptimo según fechas disponibles
- Optimización de distribución de partidos
- Cálculo de jornadas óptimas

### 7. **Backtracking**
- Generación de combinaciones válidas de cruces sin repetición
- Fixture completo (todos contra todos)
- Validación de configuraciones

### 8. **Branch & Bound**
- Optimización de calendario minimizando viajes
- Poda por presupuesto y mejor solución conocida
- Cálculo de cotas inferiores

## 🏗️ Estructura del Proyecto

```
liga-futbol/
├── src/main/java/com/uade/ligafutbol/
│   ├── algorithm/              # Algoritmos implementados
│   │   ├── GrafoAlgorithm.java
│   │   ├── DijkstraAlgorithm.java
│   │   ├── MSTAlgorithm.java
│   │   ├── GreedyAlgorithm.java
│   │   ├── DivideConquerAlgorithm.java
│   │   ├── DynamicProgrammingAlgorithm.java
│   │   ├── BacktrackingAlgorithm.java
│   │   └── BranchBoundAlgorithm.java
│   ├── controller/             # Controladores REST
│   │   ├── EquipoController.java
│   │   ├── EstadioController.java
│   │   ├── PartidoController.java
│   │   └── AlgorithmController.java
│   ├── model/                  # Modelos de dominio
│   │   ├── Equipo.java
│   │   ├── Estadio.java
│   │   ├── Partido.java
│   │   ├── ConexionEquipo.java
│   │   └── ConexionEstadio.java
│   ├── repository/             # Repositorios Neo4j
│   │   ├── EquipoRepository.java
│   │   ├── EstadioRepository.java
│   │   └── PartidoRepository.java
│   ├── service/                # Servicios
│   │   └── LigaService.java
│   └── LigaFutbolApplication.java
└── src/main/resources/
    └── application.properties
```

## 🔧 Configuración

### 1. Instalar Neo4j

```bash
# Con Docker
docker run -d \
  --name neo4j \
  -p 7474:7474 -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/password \
  neo4j:latest

# O descargar desde: https://neo4j.com/download/
```

### 2. Configurar application.properties

```properties
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=password
```

### 3. Compilar y ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📡 API Endpoints

### Equipos

- `POST /api/equipos` - Crear equipo
- `GET /api/equipos` - Obtener todos los equipos
- `GET /api/equipos/{id}` - Obtener equipo por ID
- `GET /api/equipos/tabla-posiciones` - Tabla de posiciones ordenada

### Estadios

- `POST /api/estadios` - Crear estadio
- `GET /api/estadios` - Obtener todos los estadios

### Partidos

- `POST /api/partidos` - Crear partido
- `GET /api/partidos` - Obtener todos los partidos
- `PUT /api/partidos/{id}/resultado` - Registrar resultado

### Algoritmos

#### BFS/DFS
- `GET /api/algoritmos/bfs/{origenId}/{destinoId}` - Buscar camino con BFS
- `GET /api/algoritmos/dfs/{origenId}/{destinoId}` - Buscar camino con DFS

#### Dijkstra
- `GET /api/algoritmos/dijkstra/equipos/{origenId}/{destinoId}` - Ruta más corta entre equipos
- `GET /api/algoritmos/dijkstra/estadios/{origenId}/{destinoId}` - Ruta más corta entre estadios

#### MST
- `GET /api/algoritmos/mst/prim` - Calcular MST con Prim
- `GET /api/algoritmos/mst/kruskal` - Calcular MST con Kruskal

#### Greedy
- `POST /api/algoritmos/greedy/emparejar?fechaInicio=2024-01-01T10:00:00` - Emparejar partidos

#### Programación Dinámica
- `POST /api/algoritmos/dp/fixture` - Planificar fixture óptimo

#### Backtracking
- `GET /api/algoritmos/backtracking/fixture-completo` - Generar fixture completo

#### Branch & Bound
- `POST /api/algoritmos/branch-bound/calendario?presupuesto=10000` - Optimizar calendario

## 📊 Ejemplos de Uso

### Crear un equipo

```bash
curl -X POST http://localhost:8080/api/equipos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Boca Juniors",
    "ciudad": "Buenos Aires"
  }'
```

### Obtener tabla de posiciones

```bash
curl http://localhost:8080/api/equipos/tabla-posiciones
```

### Encontrar ruta más corta (Dijkstra)

```bash
curl http://localhost:8080/api/algoritmos/dijkstra/equipos/1/2
```

### Generar fixture completo (Backtracking)

```bash
curl http://localhost:8080/api/algoritmos/backtracking/fixture-completo
```

## 🎯 Características Principales

- ✅ **Gestión completa de equipos, estadios y partidos**
- ✅ **8 algoritmos avanzados implementados**
- ✅ **Base de datos de grafos Neo4j**
- ✅ **API REST completa**
- ✅ **Optimización de rutas y calendarios**
- ✅ **Cálculo automático de tabla de posiciones**
- ✅ **Minimización de costos de traslados**
- ✅ **Generación automática de fixtures**

## 📝 Notas

- El sistema utiliza Neo4j como base de datos de grafos para representar las relaciones entre equipos y estadios
- Todos los algoritmos están optimizados para trabajar con grafos
- La tabla de posiciones se ordena automáticamente usando Divide y Conquista (MergeSort)
- Los fixtures se generan sin repeticiones usando Backtracking

## 📚 Documentación Adicional

El proyecto incluye documentación exhaustiva:

- **[INICIO_RAPIDO.md](INICIO_RAPIDO.md)** - Guía de inicio en 3 pasos
- **[GUIA_INSTALACION.md](GUIA_INSTALACION.md)** - Instalación detallada paso a paso
- **[EJEMPLOS_ALGORITMOS.md](EJEMPLOS_ALGORITMOS.md)** - Ejemplos de uso de cada algoritmo
- **[ARQUITECTURA.md](ARQUITECTURA.md)** - Diseño y arquitectura del sistema
- **[RESUMEN_PROYECTO.md](RESUMEN_PROYECTO.md)** - Resumen ejecutivo del proyecto
- **[INDICE_ARCHIVOS.md](INDICE_ARCHIVOS.md)** - Índice completo de archivos
- **[PRESENTACION.md](PRESENTACION.md)** - Guía para presentar el proyecto
- **[POSTMAN_COLLECTION.json](POSTMAN_COLLECTION.json)** - Colección de endpoints para Postman

## 🚀 Inicio Rápido

```bash
# 1. Iniciar Neo4j
docker-compose up -d

# 2. Ejecutar la aplicación
./start.sh

# 3. Probar
curl http://localhost:8080/api/equipos
```

## 👨‍💻 Autor

Desarrollado para el TP de Programación 3 - UADE

## 📄 Licencia

Este proyecto es de uso académico.
