$ErrorActionPreference = "Stop"

$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvn) { Write-Error "Maven (mvn) not found on PATH"; exit 1 }

$parentPom = "c:\_dev\souscription\services\pom.xml"
if (-not (Test-Path $parentPom)) { Write-Error "Parent pom not found at $parentPom"; exit 1 }

mvn -f $parentPom clean package -DskipTests
