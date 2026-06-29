# Task Manager API

A containerized REST API for managing Tasks, built with Java 21 and Spring Boot 4.

---
### Stack:
Spring Boot 4.1.0, Java 21,
Spring Web, Spring Data JPA, Jakarta Bean Validation,
PostgreSQL, Liquibase, MapStruct, Lombok,
Spring Boot Actuator, Docker

### Endpoints
- `POST   /api/v1/tasks`        create a task
- `GET    /api/v1/tasks`        list tasks (filter by `status`, `priority`; paginated)
- `GET    /api/v1/tasks/{id}`   get one
- `PUT    /api/v1/tasks/{id}`   update
- `DELETE /api/v1/tasks/{id}`   delete

### Setting up the app:
The integration tests are using Testcontainers so they need Docker running.
Once Docker is up, run:
```bash
mvn clean verify
```

### Setting up database connection:
(used DBeaver)
- New connection: PostgreSQL 
- Host: localhost
- Port: 5432
- Database: taskdb
- Username: task
- Password: task

### Running the application with Docker
To spin up the database and build the Spring Boot application, run the following command in the root directory:

```bash
docker compose up --build
```

### Running the application on Kubernetes

Prerequisites: Docker, [kind](https://kind.sigs.k8s.io/), and kubectl.

```bash
# 1. Create a local cluster
kind create cluster

# 2. Build the app image and load it into the cluster
docker build -t task-manager-api:latest .
kind load docker-image task-manager-api:latest

# 3. Deploy Postgres + the app
kubectl apply -f k8s/

# 4. Wait until the pods are READY 1/1
kubectl get pods -w

# 5. Forward ports
kubectl port-forward svc/task-manager-api 8080:8080
kubectl port-forward svc/postgres 5432:5432
```

Then: 
Swagger UI at http://localhost:8080/swagger-ui/index.html#/

API at http://localhost:8080/api/v1/tasks

Tear down: `kind delete cluster`


### API Documentation
This project uses OpenAPI to automatically generate interactive documentation. 
Once the application is running, you can view the endpoints, schemas, and send test requests via Swagger UI:

Swagger UI: http://localhost:8080/swagger-ui/index.html#/

### Limitations:

This API is currently a foundational implementation. Not implemented(out of scope):

    Security: Endpoints are public. There is no authentication or authorization (Spring Security, JWT, OAuth2) implemented.
    
    Observability: Centralized logging, tracing, and metric scraping are not configured.
    
    Traffic Management: There is no rate limiting This is intentional because it belongs to the gateway/ingress not per instance.
    
    DevOps: No CI/CD pipelines are set up for automated testing or deployment.
