# Souscription Microservices (Spring Boot)

This folder contains Spring Boot microservices replacing the JS mocks.

Services and ports:
- Agilab: `http://localhost:5206` — `/getgrille`, `/calculette`
- DQE: `http://localhost:5207` — `/address/validate`, `/email/validate`, `/phone/validate`
- GRD: `http://localhost:5208` — `/meter/search`, `/consent`, `/meter/read`, `/meter/upsert`, `/installation/upsert`
- EM: `http://localhost:5209` — `/estimate`
- Salesforce: `http://localhost:5205` — `/saves/1`, `/cases`

## Prerequisites
- Java 17+
- Maven 3.9+

## Build and run (each service)
```powershell
# Agilab
Push-Location "c:\_dev\souscription\services\agilab"; mvn spring-boot:run

# DQE
Push-Location "c:\_dev\souscription\services\dqe"; mvn spring-boot:run

# GRD
Push-Location "c:\_dev\souscription\services\grd"; mvn spring-boot:run

# EM
Push-Location "c:\_dev\souscription\services\em"; mvn spring-boot:run

# Salesforce
Push-Location "c:\_dev\souscription\services\salesforce"; mvn spring-boot:run
```

Run them in separate terminals. Update your `.env` or session environment variables to use these base URLs.

## Build all services at once (aggregator)
```powershell
# From repo root or the services folder
mvn -f "c:\_dev\souscription\services\pom.xml" clean package -DskipTests
```

## Start all (helper script)
```powershell
& "c:\_dev\souscription\services\start-services.ps1"
```

## Testing with n8n Save 1
Follow the main README to import the Save 1 workflow, then POST the sample payloads from `c:\_dev\souscription\workflows`.
