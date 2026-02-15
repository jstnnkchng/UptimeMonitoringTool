# Uptime Monitoring Tool

A REST API service that allows users to register endpoints for uptime monitoring. Users provide a URL, expected response criteria, and a check frequency — the system will periodically verify endpoint health and notify users via email when checks fail.

## Future Work:
- REST API for Endpoint Registration
- Health Check Scheduler 
- Email Notifications
- Check History & Observability

## Tech Stack

- **Language:** Kotlin 2.1
- **Framework:** Spring Boot 3.4
- **Database:** PostgreSQL 17
- **Build Tool:** Gradle (Kotlin DSL)
- **Java:** 21 (Temurin)
- **Containerization:** Docker / Docker Compose

## Requirements

- [Docker](https://www.docker.com/products/docker-desktop) (for containerized setup)
- JDK 21 (for local development)

## Getting Started

### 1. Create a `.env` file

Create a `.env` file in the project root with your database credentials:

```
POSTGRES_DB={YOUR_DB_NAME}
POSTGRES_USER={YOUR_DB_USER}
POSTGRES_PASSWORD={YOUR_DB_PASSWORD}
```

### 2. Run with Docker Compose

```bash
docker compose up --build
```

This starts the Spring Boot app on port `8080` and PostgreSQL on port `5432`. The app waits for Postgres to be healthy before starting.

### 3. Create the database table

Connect to the running Postgres instance and execute the SQL script:

```bash
psql -h localhost -U {YOUR_DB_USER} -d {YOUR_DB_PASSWORD} -f sql/create_monitored_endpoints.sql
```

### 4. Verify

Swagger UI is available at: http://localhost:8080/swagger-ui/index.html

## Local Development (without Docker)

1. Start only the database container:
   ```bash
   docker compose up db
   ```

2. Run the application with the local profile:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=e0'
   ```

   Or configure your IDE run configuration with VM option: `-Dspring.profiles.active=e0`

## API Endpoints

### Register a new endpoint

`POST /api/v1/monitorEndpoints`

**Request body:**
```json
{
  "email": "user@example.com",
  "expectedResponse": null,
  "expectedStatusCode": 200,
  "frequency": "DAILY",
  "url": "https://example.com/health",
  "userId": "user-1"
}
```

**Frequency values:** `DAILY`, `HOURLY`, `EVERY_15_MINUTES`

## Running Tests

```bash
./gradlew test
```

Run a single test class:
```bash
./gradlew test --tests "org.jc.uptimemonitor.dao.MonitoredEndpointDaoImplTest"
```

## Shutting Down

```bash
docker compose down
```

To also remove the database volume:
```bash
docker compose down -v
```