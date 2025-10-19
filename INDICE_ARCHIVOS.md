# 📑 Índice de Archivos del Proyecto

## 📂 Estructura Completa

```
liga-futbol/
│
├── 📄 Archivos de Configuración
│   ├── pom.xml                          # Configuración Maven y dependencias
│   ├── docker-compose.yml               # Configuración Docker para Neo4j
│   ├── .gitignore                       # Archivos ignorados por Git
│   └── start.sh                         # Script de inicio rápido
│
├── 📚 Documentación
│   ├── README.md                        # Documentación principal
│   ├── GUIA_INSTALACION.md             # Guía de instalación paso a paso
│   ├── INICIO_RAPIDO.md                # Inicio rápido en 3 pasos
│   ├── EJEMPLOS_ALGORITMOS.md          # Ejemplos detallados de cada algoritmo
│   ├── ARQUITECTURA.md                 # Diseño y arquitectura del sistema
│   ├── RESUMEN_PROYECTO.md             # Resumen ejecutivo
│   ├── INDICE_ARCHIVOS.md              # Este archivo
│   └── POSTMAN_COLLECTION.json         # Colección de endpoints para Postman
│
├── 📦 src/main/java/com/uade/ligafutbol/
│   │
│   ├── 🧮 algorithm/                    # Algoritmos implementados (8 archivos)
│   │   ├── BacktrackingAlgorithm.java   # Backtracking para fixtures
│   │   ├── BranchBoundAlgorithm.java    # Branch & Bound para optimización
│   │   ├── DijkstraAlgorithm.java       # Dijkstra para rutas mínimas
│   │   ├── DivideConquerAlgorithm.java  # Divide y Conquista (MergeSort)
│   │   ├── DynamicProgrammingAlgorithm.java  # Programación Dinámica
│   │   ├── GrafoAlgorithm.java          # BFS y DFS
│   │   ├── GreedyAlgorithm.java         # Algoritmo Greedy
│   │   └── MSTAlgorithm.java            # Prim y Kruskal (MST)
│   │
│   ├── 🎮 controller/                   # Controladores REST (4 archivos)
│   │   ├── AlgorithmController.java     # Endpoints de algoritmos
│   │   ├── EquipoController.java        # Endpoints de equipos
│   │   ├── EstadioController.java       # Endpoints de estadios
│   │   └── PartidoController.java       # Endpoints de partidos
│   │
│   ├── 📊 model/                        # Modelos de dominio (5 archivos)
│   │   ├── ConexionEquipo.java          # Relación entre equipos
│   │   ├── ConexionEstadio.java         # Relación entre estadios
│   │   ├── Equipo.java                  # Entidad Equipo
│   │   ├── Estadio.java                 # Entidad Estadio
│   │   └── Partido.java                 # Entidad Partido
│   │
│   ├── 💾 repository/                   # Repositorios Neo4j (3 archivos)
│   │   ├── EquipoRepository.java        # Repositorio de equipos
│   │   ├── EstadioRepository.java       # Repositorio de estadios
│   │   └── PartidoRepository.java       # Repositorio de partidos
│   │
│   ├── 🔧 service/                      # Capa de servicios (1 archivo)
│   │   └── LigaService.java             # Servicio principal de la liga
│   │
│   ├── ⚙️ config/                       # Configuración (1 archivo)
│   │   └── DataInitializer.java         # Inicialización de datos de ejemplo
│   │
│   └── 🚀 LigaFutbolApplication.java    # Clase principal de Spring Boot
│
└── 📦 src/main/resources/
    └── application.properties           # Configuración de la aplicación
```

---

## 📊 Estadísticas del Proyecto

### Archivos por Categoría

| Categoría | Cantidad | Archivos |
|-----------|----------|----------|
| **Algoritmos** | 8 | BFS/DFS, Dijkstra, Prim/Kruskal, Greedy, Divide&Conquer, DP, Backtracking, Branch&Bound |
| **Controladores** | 4 | Equipos, Estadios, Partidos, Algoritmos |
| **Modelos** | 5 | Equipo, Estadio, Partido, ConexionEquipo, ConexionEstadio |
| **Repositorios** | 3 | Equipos, Estadios, Partidos |
| **Servicios** | 1 | LigaService |
| **Configuración** | 1 | DataInitializer |
| **Documentación** | 8 | README, Guías, Ejemplos, Arquitectura, etc. |
| **Total Archivos Java** | 22 | - |
| **Total Archivos Proyecto** | 35+ | - |

---

## 🗂️ Descripción de Archivos Principales

### 📄 Archivos de Configuración

#### `pom.xml`
- **Propósito**: Configuración de Maven
- **Contenido**: Dependencias (Spring Boot, Neo4j, Lombok)
- **Versión Java**: 17
- **Spring Boot**: 3.1.5

#### `docker-compose.yml`
- **Propósito**: Configuración de Neo4j con Docker
- **Puertos**: 7474 (HTTP), 7687 (Bolt)
- **Credenciales**: neo4j/password
- **Volúmenes**: Persistencia de datos

#### `application.properties`
- **Propósito**: Configuración de Spring Boot
- **Contenido**: Conexión a Neo4j, puerto del servidor, logging

#### `start.sh`
- **Propósito**: Script de inicio automático
- **Funciones**: Verifica dependencias, inicia Neo4j, compila y ejecuta

---

### 📚 Archivos de Documentación

#### `README.md` (6.7 KB)
- Documentación principal del proyecto
- Características, tecnologías, estructura
- API endpoints y ejemplos de uso

#### `GUIA_INSTALACION.md` (4.8 KB)
- Instalación paso a paso
- Configuración de Neo4j
- Solución de problemas comunes

#### `INICIO_RAPIDO.md` (6.9 KB)
- Inicio en 3 pasos
- Endpoints más usados
- Comandos útiles

#### `EJEMPLOS_ALGORITMOS.md` (11.6 KB)
- Ejemplos detallados de cada algoritmo
- Casos de uso prácticos
- Complejidad temporal y espacial

#### `ARQUITECTURA.md` (11.1 KB)
- Diseño del sistema
- Patrones de diseño
- Flujo de datos
- Decisiones arquitectónicas

#### `RESUMEN_PROYECTO.md` (9.6 KB)
- Resumen ejecutivo
- Métricas del proyecto
- Objetivos cumplidos

#### `POSTMAN_COLLECTION.json` (10.9 KB)
- Colección completa de endpoints
- Ejemplos de requests
- Listo para importar en Postman

---

### 🧮 Algoritmos (8 archivos)

#### `GrafoAlgorithm.java`
- **Algoritmos**: BFS, DFS
- **Complejidad**: O(V + E)
- **Uso**: Búsqueda de caminos en grafos

#### `DijkstraAlgorithm.java`
- **Algoritmo**: Dijkstra
- **Complejidad**: O((V + E) log V)
- **Uso**: Caminos más cortos

#### `MSTAlgorithm.java`
- **Algoritmos**: Prim, Kruskal
- **Complejidad**: O(E log V)
- **Uso**: Árbol de expansión mínima

#### `GreedyAlgorithm.java`
- **Algoritmo**: Greedy
- **Complejidad**: O(n²)
- **Uso**: Emparejamiento de partidos

#### `DivideConquerAlgorithm.java`
- **Algoritmo**: MergeSort
- **Complejidad**: O(n log n)
- **Uso**: Ordenar tabla de posiciones

#### `DynamicProgrammingAlgorithm.java`
- **Algoritmo**: Programación Dinámica
- **Complejidad**: O(n² × 2ⁿ)
- **Uso**: Fixture óptimo

#### `BacktrackingAlgorithm.java`
- **Algoritmo**: Backtracking
- **Complejidad**: O(n!)
- **Uso**: Generar fixtures completos

#### `BranchBoundAlgorithm.java`
- **Algoritmo**: Branch & Bound
- **Complejidad**: O(2ⁿ) con poda
- **Uso**: Optimizar calendario

---

### 🎮 Controladores (4 archivos)

#### `EquipoController.java`
- **Endpoints**: 4
- **Funciones**: CRUD equipos, tabla de posiciones

#### `EstadioController.java`
- **Endpoints**: 2
- **Funciones**: CRUD estadios

#### `PartidoController.java`
- **Endpoints**: 3
- **Funciones**: CRUD partidos, registrar resultados

#### `AlgorithmController.java`
- **Endpoints**: 11+
- **Funciones**: Ejecutar todos los algoritmos

---

### 📊 Modelos (5 archivos)

#### `Equipo.java`
- **Tipo**: Nodo (@Node)
- **Propiedades**: nombre, ciudad, puntos, estadísticas
- **Relaciones**: JUEGA_EN, CONECTADO_CON

#### `Estadio.java`
- **Tipo**: Nodo (@Node)
- **Propiedades**: nombre, ciudad, capacidad, coordenadas
- **Relaciones**: CONECTADO_CON

#### `Partido.java`
- **Tipo**: Nodo (@Node)
- **Propiedades**: fecha, goles, jornada
- **Relaciones**: EQUIPO_LOCAL, EQUIPO_VISITANTE, SE_JUEGA_EN

#### `ConexionEquipo.java`
- **Tipo**: Relación (@RelationshipProperties)
- **Propiedades**: distancia, costo, tiempoViaje

#### `ConexionEstadio.java`
- **Tipo**: Relación (@RelationshipProperties)
- **Propiedades**: distancia, costo, tiempoViaje

---

### 💾 Repositorios (3 archivos)

#### `EquipoRepository.java`
- **Extiende**: Neo4jRepository
- **Queries**: findByNombre, findByCiudad, tabla ordenada

#### `EstadioRepository.java`
- **Extiende**: Neo4jRepository
- **Queries**: findByNombre, findByCiudad

#### `PartidoRepository.java`
- **Extiende**: Neo4jRepository
- **Queries**: findByJornada, findByEquipo

---

### 🔧 Servicios y Configuración

#### `LigaService.java`
- **Propósito**: Lógica de negocio
- **Funciones**: Orquestar operaciones, invocar algoritmos
- **Inyecciones**: Todos los algoritmos y repositorios

#### `DataInitializer.java`
- **Propósito**: Inicializar datos de ejemplo
- **Implementa**: CommandLineRunner
- **Datos**: 4 equipos, 4 estadios, 4 partidos

#### `LigaFutbolApplication.java`
- **Propósito**: Clase principal
- **Anotación**: @SpringBootApplication
- **Función**: Iniciar la aplicación

---

## 🎯 Archivos por Funcionalidad

### Gestión de Equipos
- `Equipo.java`
- `EquipoRepository.java`
- `EquipoController.java`
- Parte de `LigaService.java`

### Gestión de Estadios
- `Estadio.java`
- `EstadioRepository.java`
- `EstadioController.java`
- Parte de `LigaService.java`

### Gestión de Partidos
- `Partido.java`
- `PartidoRepository.java`
- `PartidoController.java`
- Parte de `LigaService.java`

### Algoritmos
- 8 archivos en `algorithm/`
- `AlgorithmController.java`
- Parte de `LigaService.java`

---

## 📏 Tamaño de Archivos

| Archivo | Tamaño | Líneas Aprox. |
|---------|--------|---------------|
| BacktrackingAlgorithm.java | ~7 KB | ~200 |
| BranchBoundAlgorithm.java | ~8 KB | ~250 |
| DijkstraAlgorithm.java | ~6 KB | ~180 |
| DivideConquerAlgorithm.java | ~5 KB | ~150 |
| DynamicProgrammingAlgorithm.java | ~6 KB | ~180 |
| GrafoAlgorithm.java | ~4 KB | ~120 |
| GreedyAlgorithm.java | ~5 KB | ~150 |
| MSTAlgorithm.java | ~6 KB | ~180 |
| LigaService.java | ~5 KB | ~150 |
| DataInitializer.java | ~5 KB | ~150 |

**Total estimado**: ~3,500+ líneas de código Java

---

## 🔍 Cómo Navegar el Proyecto

### Para Entender los Algoritmos
1. Leer `EJEMPLOS_ALGORITMOS.md`
2. Ver implementación en `algorithm/`
3. Probar endpoints en `AlgorithmController.java`

### Para Entender la Arquitectura
1. Leer `ARQUITECTURA.md`
2. Ver flujo: Controller → Service → Algorithm/Repository
3. Revisar modelos en `model/`

### Para Usar el Sistema
1. Seguir `INICIO_RAPIDO.md`
2. Importar `POSTMAN_COLLECTION.json`
3. Explorar Neo4j Browser

### Para Instalar
1. Seguir `GUIA_INSTALACION.md`
2. Ejecutar `start.sh`
3. Verificar en http://localhost:8080

---

## ✅ Checklist de Archivos

### Código Fuente
- [x] 8 Algoritmos implementados
- [x] 4 Controladores REST
- [x] 5 Modelos de dominio
- [x] 3 Repositorios Neo4j
- [x] 1 Servicio principal
- [x] 1 Inicializador de datos
- [x] 1 Clase principal

### Configuración
- [x] pom.xml
- [x] application.properties
- [x] docker-compose.yml
- [x] .gitignore
- [x] start.sh

### Documentación
- [x] README.md
- [x] GUIA_INSTALACION.md
- [x] INICIO_RAPIDO.md
- [x] EJEMPLOS_ALGORITMOS.md
- [x] ARQUITECTURA.md
- [x] RESUMEN_PROYECTO.md
- [x] POSTMAN_COLLECTION.json
- [x] INDICE_ARCHIVOS.md

---

## 🎉 Proyecto Completo

**Total de archivos**: 35+  
**Líneas de código**: ~3,500+  
**Algoritmos**: 8  
**Endpoints REST**: 20+  
**Documentación**: 8 archivos  

✅ **El proyecto está completo y listo para usar!**
