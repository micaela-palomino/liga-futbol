# Script para instalar Maven en Windows (version usuario)
Write-Host "Instalando Maven en directorio de usuario..." -ForegroundColor Green
Write-Host ""

# Verificar si Java esta instalado
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java encontrado: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "Java no esta instalado. Por favor instala Java 17 o superior primero." -ForegroundColor Red
    exit 1
}

# Crear directorio para Maven en el directorio del usuario
$mavenHome = "$env:USERPROFILE\Apache\maven"
$mavenBin = "$mavenHome\bin"

# Verificar si Maven ya esta instalado
if (Test-Path "$mavenBin\mvn.cmd") {
    Write-Host "Maven ya esta instalado en: $mavenHome" -ForegroundColor Green
    Write-Host "Agregando Maven al PATH..." -ForegroundColor Yellow
    
    # Agregar al PATH del usuario
    $currentPath = [Environment]::GetEnvironmentVariable("PATH", "User")
    if ($currentPath -notlike "*$mavenBin*") {
        [Environment]::SetEnvironmentVariable("PATH", "$currentPath;$mavenBin", "User")
        Write-Host "Maven agregado al PATH del usuario" -ForegroundColor Green
    }
    
    # Agregar al PATH de la sesion actual
    $env:PATH = "$mavenBin;$env:PATH"
    
    Write-Host ""
    Write-Host "Maven instalado correctamente!" -ForegroundColor Green
    
    # Verificar instalacion
    try {
        $mvnVersion = mvn -version 2>&1
        Write-Host ""
        Write-Host "Version de Maven instalada:" -ForegroundColor Cyan
        Write-Host $mvnVersion[0] -ForegroundColor White
    } catch {
        Write-Host "Maven instalado pero no disponible en esta sesion. Reinicia PowerShell." -ForegroundColor Yellow
    }
    
    exit 0
}

Write-Host "Descargando Maven..." -ForegroundColor Yellow

# Crear directorio si no existe
if (-not (Test-Path $mavenHome)) {
    New-Item -ItemType Directory -Path $mavenHome -Force | Out-Null
}

# Descargar Maven (URL corregida)
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
$mavenZip = "$env:TEMP\maven.zip"

try {
    Write-Host "Descargando desde: $mavenUrl" -ForegroundColor Yellow
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing
    
    Write-Host "Extrayendo Maven..." -ForegroundColor Yellow
    Expand-Archive -Path $mavenZip -DestinationPath "$env:USERPROFILE\Apache\" -Force
    
    # Mover contenido a la ubicacion correcta
    $extractedDir = "$env:USERPROFILE\Apache\apache-maven-3.9.6"
    if (Test-Path $extractedDir) {
        Move-Item -Path "$extractedDir\*" -Destination $mavenHome -Force
        Remove-Item $extractedDir -Force
    }
    
    # Limpiar archivo temporal
    Remove-Item $mavenZip -Force
    
    Write-Host "Maven extraido correctamente" -ForegroundColor Green
    
} catch {
    Write-Host "Error al descargar Maven: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Intentando con URL alternativa..." -ForegroundColor Yellow
    
    # URL alternativa
    $mavenUrl2 = "https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip"
    try {
        Invoke-WebRequest -Uri $mavenUrl2 -OutFile $mavenZip -UseBasicParsing
        Expand-Archive -Path $mavenZip -DestinationPath "$env:USERPROFILE\Apache\" -Force
        
        $extractedDir = "$env:USERPROFILE\Apache\apache-maven-3.9.5"
        if (Test-Path $extractedDir) {
            Move-Item -Path "$extractedDir\*" -Destination $mavenHome -Force
            Remove-Item $extractedDir -Force
        }
        
        Remove-Item $mavenZip -Force
        Write-Host "Maven extraido correctamente (version 3.9.5)" -ForegroundColor Green
        
    } catch {
        Write-Host "Error al descargar Maven: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Por favor instala Maven manualmente desde: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
        exit 1
    }
}

# Configurar variables de entorno
Write-Host "Configurando variables de entorno..." -ForegroundColor Yellow

# Agregar Maven al PATH del usuario
$currentPath = [Environment]::GetEnvironmentVariable("PATH", "User")
if ($currentPath -notlike "*$mavenBin*") {
    [Environment]::SetEnvironmentVariable("PATH", "$currentPath;$mavenBin", "User")
    Write-Host "Maven agregado al PATH del usuario" -ForegroundColor Green
}

# Configurar MAVEN_HOME
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, "User")
Write-Host "MAVEN_HOME configurado: $mavenHome" -ForegroundColor Green

# Agregar al PATH de la sesion actual
$env:PATH = "$mavenBin;$env:PATH"
$env:MAVEN_HOME = $mavenHome

Write-Host ""
Write-Host "Maven instalado correctamente!" -ForegroundColor Green
Write-Host ""
Write-Host "Detalles de la instalacion:" -ForegroundColor Cyan
Write-Host "   Ubicacion: $mavenHome" -ForegroundColor White
Write-Host "   Binarios: $mavenBin" -ForegroundColor White
Write-Host "   MAVEN_HOME: $mavenHome" -ForegroundColor White
Write-Host ""

# Verificar instalacion
try {
    $mvnVersion = mvn -version 2>&1
    Write-Host "Verificacion exitosa:" -ForegroundColor Green
    Write-Host $mvnVersion[0] -ForegroundColor White
    Write-Host ""
    Write-Host "Ya puedes usar Maven!" -ForegroundColor Green
    Write-Host "Ejecuta 'mvn --version' para verificar la instalacion." -ForegroundColor Yellow
} catch {
    Write-Host "Maven instalado pero no disponible en esta sesion." -ForegroundColor Yellow
    Write-Host "Reinicia PowerShell o CMD para usar Maven." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Proximos pasos:" -ForegroundColor Cyan
Write-Host "   1. Reinicia PowerShell/CMD" -ForegroundColor White
Write-Host "   2. Ejecuta 'mvn --version' para verificar" -ForegroundColor White
Write-Host "   3. Ejecuta 'mvn spring-boot:run' en tu proyecto" -ForegroundColor White
