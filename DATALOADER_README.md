# DataLoader - Carga Inicial de Datos

## Descripción

El `DataLoader` es una clase que implementa `CommandLineRunner` y se ejecuta automáticamente al iniciar la aplicación Spring Boot. Su propósito es cargar datos iniciales en la base de datos Neo4j para poblar el sistema con información de equipos, estadios y partidos de la Primera División Argentina.

## Características

- **Ejecución automática**: Se ejecuta al iniciar la aplicación
- **Verificación de datos existentes**: Solo carga datos si la base está vacía
- **Datos reales**: Incluye equipos, estadios y partidos de la Primera División Argentina
- **Logging detallado**: Registra el progreso de la carga de datos
- **Manejo de errores**: Captura y registra errores durante la carga

## Datos Cargados

### Equipos (10 equipos)
- **River Plate** (Buenos Aires) - Estadio Monumental
- **Boca Juniors** (Buenos Aires) - La Bombonera
- **Racing Club** (Avellaneda) - Estadio Libertadores de América
- **Independiente** (Avellaneda) - Estadio Presidente Perón
- **San Lorenzo** (Buenos Aires) - Estadio Monumental (compartido)
- **Huracán** (Buenos Aires) - La Bombonera (compartido)
- **Talleres** (Córdoba) - Estadio Mario Alberto Kempes
- **Central Córdoba** (Santiago del Estero) - Estadio Único Madre de Ciudades
- **Colón** (Santa Fe) - Estadio Brigadier General Estanislao López
- **Godoy Cruz** (Mendoza) - Estadio Malvinas Argentinas

### Estadios (10 estadios)
Cada estadio incluye:
- Nombre real del estadio
- Ciudad donde se encuentra
- Capacidad aproximada
- Coordenadas GPS (latitud y longitud)

### Partidos (15 partidos de ejemplo)
- **3 jornadas** con 5 partidos cada una
- Fechas programadas para febrero 2024
- Algunos resultados ya registrados para demostrar funcionalidad
- Relaciones correctas entre equipos locales, visitantes y estadios

## Uso

### Ejecución Automática
El DataLoader se ejecuta automáticamente al iniciar la aplicación:

```bash
mvn spring-boot:run
```

### Verificación de Carga
Los logs mostrarán el progreso de la carga:

```
INFO  - Iniciando carga de datos iniciales...
INFO  - Cargando estadios...
INFO  - Estadios cargados: 10
INFO  - Cargando equipos...
INFO  - Equipos cargados: 10
INFO  - Cargando partidos...
INFO  - Partidos cargados: 15
INFO  - Carga de datos iniciales completada exitosamente.
```

### Prevención de Duplicados
Si ya existen datos en la base, el DataLoader se saltará la carga:

```
INFO  - Los datos ya existen, saltando la carga inicial.
```

## Configuración

### Propiedades de Aplicación
Asegúrate de que `application.properties` tenga la configuración correcta de Neo4j:

```properties
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=password
```

### Dependencias
El DataLoader requiere las siguientes dependencias (ya incluidas en el proyecto):

- `spring-boot-starter-data-neo4j`
- `spring-boot-starter-web`
- `lombok`

## Estructura del Código

### Métodos Principales

1. **`run(String... args)`**: Punto de entrada principal que implementa CommandLineRunner
2. **`cargarEstadios()`**: Crea y guarda los estadios en la base de datos
3. **`cargarEquipos()`**: Crea equipos y los asocia con sus estadios correspondientes
4. **`cargarPartidos()`**: Crea partidos con equipos, estadios y fechas

### Manejo de Relaciones
- Los equipos se asocian con sus estadios usando la relación `JUEGA_EN`
- Los partidos se relacionan con equipos locales, visitantes y estadios
- Las estadísticas de equipos se actualizan automáticamente cuando se registran resultados

## Personalización

### Agregar Más Equipos
Para agregar más equipos, modifica el método `cargarEquipos()`:

```java
Equipo nuevoEquipo = new Equipo("Nombre Equipo", "Ciudad");
nuevoEquipo.estadio = estadios.get(index);
equipos.add(nuevoEquipo);
```

### Agregar Más Partidos
Para agregar más partidos, modifica el método `cargarPartidos()`:

```java
Partido nuevoPartido = new Partido(equipoLocal, equipoVisitante, estadio, fecha, jornada);
partidos.add(nuevoPartido);
```

### Modificar Fechas
Las fechas están hardcodeadas en el método `cargarPartidos()`. Puedes cambiarlas según tus necesidades:

```java
LocalDateTime.of(2024, 2, 10, 16, 0) // Año, mes, día, hora, minuto
```

## Troubleshooting

### Error de Conexión a Neo4j
- Verifica que Neo4j esté ejecutándose
- Confirma las credenciales en `application.properties`
- Asegúrate de que el puerto 7687 esté disponible

### Error de Lombok
Si hay problemas con los métodos generados por Lombok:
- Verifica que Lombok esté instalado en tu IDE
- Reinicia el IDE después de agregar Lombok
- Usa acceso directo a campos como alternativa (como se hace en el código actual)

### Datos No Se Cargan
- Verifica los logs para errores específicos
- Confirma que la base de datos esté vacía
- Revisa que los repositorios estén correctamente configurados
