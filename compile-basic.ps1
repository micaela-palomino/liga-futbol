# Script para compilar solo las clases básicas sin algoritmos
Write-Host "Compilando solo las clases básicas..." -ForegroundColor Green
Write-Host ""

# Configurar variables de entorno
$env:PATH = "$env:USERPROFILE\Apache\maven\bin;$env:PATH"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"

# Crear directorio de clases temporal
$tempClassesDir = "target\temp-classes"
if (Test-Path $tempClassesDir) {
    Remove-Item $tempClassesDir -Recurse -Force
}
New-Item -ItemType Directory -Path $tempClassesDir -Force | Out-Null

Write-Host "Compilando clases básicas..." -ForegroundColor Yellow

# Compilar solo las clases básicas sin algoritmos
$sourceFiles = @(
    "src\main\java\com\uade\ligafutbol\model\*.java",
    "src\main\java\com\uade\ligafutbol\config\DataLoader.java",
    "src\main\java\com\uade\ligafutbol\repository\*.java",
    "src\main\java\com\uade\ligafutbol\controller\EquipoController.java",
    "src\main\java\com\uade\ligafutbol\controller\EstadioController.java",
    "src\main\java\com\uade\ligafutbol\controller\PartidoController.java",
    "src\main\java\com\uade\ligafutbol\LigaFutbolApplication.java"
)

# Obtener todas las dependencias de Maven
Write-Host "Descargando dependencias..." -ForegroundColor Yellow
mvn dependency:copy-dependencies -DoutputDirectory=target\dependency -q

# Compilar con javac directamente
$classpath = "target\dependency\*"
$javacArgs = @(
    "-cp", $classpath,
    "-d", $tempClassesDir,
    "-source", "24",
    "-target", "24"
)

# Agregar archivos fuente
foreach ($pattern in $sourceFiles) {
    $files = Get-ChildItem -Path $pattern -Recurse -ErrorAction SilentlyContinue
    foreach ($file in $files) {
        $javacArgs += $file.FullName
    }
}

Write-Host "Ejecutando javac..." -ForegroundColor Yellow
try {
    & javac @javacArgs
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Compilación exitosa!" -ForegroundColor Green
        
        # Copiar recursos
        Copy-Item "src\main\resources\*" -Destination $tempClassesDir -Recurse -Force
        
        Write-Host ""
        Write-Host "🚀 Ejecutando la aplicación..." -ForegroundColor Green
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host "La aplicación estará disponible en:" -ForegroundColor White
        Write-Host "http://localhost:8080" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "⚠️  IMPORTANTE: Neo4j debe estar ejecutándose en localhost:7687" -ForegroundColor Yellow
        Write-Host "   Usuario: neo4j" -ForegroundColor Yellow
        Write-Host "   Contraseña: password" -ForegroundColor Yellow
        Write-Host "==========================================" -ForegroundColor Cyan
        Write-Host ""
        
        # Ejecutar la aplicación
        $runArgs = @(
            "-cp", "$tempClassesDir;target\dependency\*",
            "com.uade.ligafutbol.LigaFutbolApplication"
        )
        
        & java @runArgs
        
    } else {
        Write-Host "❌ Error en la compilación" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error al ejecutar javac: $($_.Exception.Message)" -ForegroundColor Red
}
