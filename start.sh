#!/bin/bash

echo "🚀 Iniciando Liga de Fútbol..."
echo ""

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

# Verificar si Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Por favor instala Maven primero."
    exit 1
fi

# Verificar si Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado. Por favor instala Java 17 o superior."
    exit 1
fi

echo "✅ Verificación de dependencias completada"
echo ""

# Iniciar Neo4j con Docker Compose
echo "📦 Iniciando Neo4j..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "❌ Error al iniciar Neo4j"
    exit 1
fi

echo "✅ Neo4j iniciado correctamente"
echo ""

# Esperar a que Neo4j esté listo
echo "⏳ Esperando a que Neo4j esté listo..."
sleep 10

# Compilar el proyecto
echo "🔨 Compilando el proyecto..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar el proyecto"
    exit 1
fi

echo "✅ Proyecto compilado correctamente"
echo ""

# Iniciar la aplicación
echo "🎯 Iniciando la aplicación Spring Boot..."
echo ""
echo "=========================================="
echo "La aplicación estará disponible en:"
echo "http://localhost:8080"
echo ""
echo "Neo4j Browser disponible en:"
echo "http://localhost:7474"
echo "Usuario: neo4j"
echo "Contraseña: password"
echo "=========================================="
echo ""

mvn spring-boot:run
