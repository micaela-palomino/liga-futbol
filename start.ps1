# Script para ejecutar la aplicación Spring Boot sin Maven instalado
Write-Host "🚀 Iniciando Liga de Fútbol..." -ForegroundColor Green
Write-Host ""

# Verificar si Java está instalado
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java encontrado: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "❌ Java no está instalado. Por favor instala Java 17 o superior." -ForegroundColor Red
    exit 1
}

# Verificar si Maven está disponible
try {
    $mvnVersion = mvn -version 2>&1
    Write-Host "✅ Maven encontrado: $($mvnVersion[0])" -ForegroundColor Green
    $mavenAvailable = $true
} catch {
    Write-Host "⚠️  Maven no está disponible en el PATH" -ForegroundColor Yellow
    $mavenAvailable = $false
}

if (-not $mavenAvailable) {
    Write-Host "📥 Descargando Maven temporalmente..." -ForegroundColor Yellow
    
    # Crear directorio temporal para Maven
    $mavenDir = "$env:TEMP\maven-temp"
    if (Test-Path $mavenDir) {
        Remove-Item $mavenDir -Recurse -Force
    }
    New-Item -ItemType Directory -Path $mavenDir -Force | Out-Null
    
    # Descargar Maven
    $mavenUrl = "https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
    $mavenZip = "$mavenDir\maven.zip"
    
    try {
        Write-Host "Descargando Maven desde $mavenUrl..." -ForegroundColor Yellow
        Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing
        
        # Extraer Maven
        Write-Host "Extrayendo Maven..." -ForegroundColor Yellow
        Expand-Archive -Path $mavenZip -DestinationPath $mavenDir -Force
        
        # Configurar PATH temporal
        $mavenBin = "$mavenDir\apache-maven-3.9.6\bin"
        $env:PATH = "$mavenBin;$env:PATH"
        
        Write-Host "✅ Maven configurado temporalmente" -ForegroundColor Green
    } catch {
        Write-Host "❌ Error al descargar Maven: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Por favor instala Maven manualmente o usa Docker." -ForegroundColor Yellow
        exit 1
    }
}

Write-Host ""
Write-Host "🔨 Compilando el proyecto..." -ForegroundColor Yellow

try {
    # Compilar el proyecto
    mvn clean compile -q
    if ($LASTEXITCODE -ne 0) {
        throw "Error en la compilación"
    }
    Write-Host "✅ Proyecto compilado correctamente" -ForegroundColor Green
} catch {
    Write-Host "❌ Error al compilar el proyecto: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "⚠️  IMPORTANTE: Neo4j debe estar ejecutándose en localhost:7687" -ForegroundColor Yellow
Write-Host "   Usuario: neo4j" -ForegroundColor Yellow
Write-Host "   Contraseña: password" -ForegroundColor Yellow
Write-Host ""
Write-Host "🎯 Iniciando la aplicación Spring Boot..." -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "La aplicación estará disponible en:" -ForegroundColor White
Write-Host "http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "Neo4j Browser disponible en:" -ForegroundColor White
Write-Host "http://localhost:7474" -ForegroundColor Cyan
Write-Host "Usuario: neo4j" -ForegroundColor White
Write-Host "Contraseña: password" -ForegroundColor White
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Ejecutar la aplicación
try {
    mvn spring-boot:run
} catch {
    Write-Host "❌ Error al ejecutar la aplicación: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Asegúrate de que Neo4j esté ejecutándose en localhost:7687" -ForegroundColor Yellow
}
