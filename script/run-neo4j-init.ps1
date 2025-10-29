# Run Neo4j init cypher script
# Usage: Set environment variables NEO4J_USER and NEO4J_PASSWORD or pass parameters
param(
    [string]$user = $env:NEO4J_USER,
    [string]$password = $env:NEO4J_PASSWORD,
    [string]$neo4jHost = "localhost",
    [int]$port = 7687,
    [string]$file = "src/main/resources/neo4j/init.cypher"
)

if (-not $user -or -not $password) {
    Write-Host "NEO4J credentials not provided. Please set NEO4J_USER and NEO4J_PASSWORD env vars or supply parameters." -ForegroundColor Yellow
    exit 1
}

# Try to run cypher-shell. Assumes cypher-shell is in PATH.
# Use ${} around host/port to avoid PowerShell parsing issues with ':' inside the string
$cmd = "cypher-shell -u $user -p $password -a bolt://${neo4jHost}:${port} -f $file"
Write-Host "Running: $cmd"
Invoke-Expression $cmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "Neo4j init script applied successfully." -ForegroundColor Green
} else {
    Write-Host "Neo4j init script failed with exit code $LASTEXITCODE." -ForegroundColor Red
}