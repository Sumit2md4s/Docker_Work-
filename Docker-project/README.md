# Docker 3-Tier Application

A containerized 3-tier web application built using Docker, Docker Compose, Java, Maven, Nginx, and MySQL.

This project demonstrates frontend, backend, and database containerization using Docker networking, Docker Compose, health checks, volumes, and Docker Secrets.

## Architecture

```text
                    User / Browser
                          |
                          | HTTP :8080
                          v
                 +------------------+
                 |     Frontend     |
                 |      Nginx       |
                 |      :80         |
                 +------------------+
                          |
                          | /api
                          v
                 +------------------+
                 |     Backend      |
                 |      Java        |
                 |     :8080        |
                 +------------------+
                          |
                          | JDBC
                          v
                 +------------------+
                 |      MySQL       |
                 |      :3306       |
                 +------------------+
```

## Technologies Used

- Docker
- Docker Compose
- Java 17
- Maven
- Nginx
- MySQL 8.0
- JDBC
- Docker Networks
- Docker Volumes
- Docker Secrets
- Git & GitHub

## Project Structure

```text
docker-3-tier_project/
│
├── backend/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── Main.java
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── index.html
│   ├── nginx.conf
│   └── Dockerfile
│
├── docker-compose.yml
├── mysql_root_password.txt
├── .gitignore
└── README.md
```

## Application Flow

```text
Browser
   |
   | http://localhost:8080
   v
Nginx Frontend
   |
   | /api
   v
Java Backend
   |
   | JDBC
   v
MySQL
```

The frontend calls `/api`. Nginx forwards the request to the Java backend. The Java backend connects to MySQL using JDBC and executes `SELECT 1`.

Example response:

```text
Hello from Docker Backend!
MySQL Status: Connected
MySQL Response: 1
```

## Docker Networking

All services communicate through a Docker Compose network.

The backend connects to MySQL using the Docker service name:

```text
jdbc:mysql://mysql:3306/myapp
```

Docker's internal DNS resolves `mysql` to the MySQL container.

## Docker Secrets

The MySQL root password is stored locally in:

```text
mysql_root_password.txt
```

The file is excluded from Git using `.gitignore`.

Docker mounts the secret inside the container at:

```text
/run/secrets/mysql_root_password
```

The Java application reads the password from this file instead of hardcoding it.

Never commit real passwords, API keys, tokens, or credentials to GitHub.

## Multi-Stage Docker Build

The backend uses a multi-stage Docker build.

### Builder stage

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
```

The builder compiles the Java application and creates the JAR.

### Runtime stage

```dockerfile
FROM eclipse-temurin:17-jre
```

Only the JRE is included in the final runtime image.

```text
Maven + JDK
     |
     | Build
     v
   JAR
     |
     v
Java 17 JRE
     |
     v
Runtime Container
```

## Docker Compose

Start the application:

```bash
docker compose up -d
```

Stop the application:

```bash
docker compose down
```

Check services:

```bash
docker compose ps
```

View all logs:

```bash
docker compose logs
```

Backend logs:

```bash
docker compose logs backend
```

MySQL logs:

```bash
docker compose logs mysql
```

## Health Check

MySQL has a Docker health check. The backend waits for MySQL to become healthy before starting.

```yaml
depends_on:
  mysql:
    condition: service_healthy
```

## Running the Project

### Prerequisites

Install:

- Docker Desktop
- Git

Verify Docker:

```bash
docker --version
```

Verify Docker Compose:

```bash
docker compose version
```

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/docker-3-tier-project.git
cd docker-3-tier-project
```

### Create Docker Secret

Create this file in the project root:

```text
mysql_root_password.txt
```

Put your local MySQL root password inside it.

Do not commit this file.

### Build Backend

```bash
docker build -t my-backend:v7 ./backend
```

### Build Frontend

```bash
docker build -t my-frontend:v3 ./frontend
```

### Start Application

```bash
docker compose up -d
```

Check:

```bash
docker compose ps
```

Expected:

```text
frontend   Up
backend    Up
mysql      Up (healthy)
```

## Testing

### Test Frontend

Open:

```text
http://localhost:8080
```

### Test Backend Through Nginx

```bash
curl http://localhost:8080/api
```

Expected:

```text
Hello from Docker Backend!
MySQL Status: Connected
MySQL Response: 1
```

## Useful Docker Commands

List running containers:

```bash
docker ps
```

List all containers:

```bash
docker ps -a
```

View images:

```bash
docker images
```

View backend logs:

```bash
docker logs backend
```

Follow logs:

```bash
docker logs -f backend
```

Inspect container:

```bash
docker inspect backend
```

List networks:

```bash
docker network ls
```

Inspect network:

```bash
docker network inspect app-network
```

Check MySQL:

```bash
docker exec mysql mysql -uroot -p -e "SHOW DATABASES;"
```

## Troubleshooting

### Container exits immediately

Check:

```bash
docker compose ps -a
docker compose logs backend
```

`Exited (0)` means the application terminated successfully. For a server application, the process must remain running and listen for requests.

### Port already allocated

If you see:

```text
Bind for 0.0.0.0:8080 failed: port is already allocated
```

check:

```bash
docker ps
```

Find and stop the container using the port:

```bash
docker stop <container-name>
```

### Backend returns 404

Check the Nginx API routing:

```nginx
location /api {
    proxy_pass http://backend:8080;
}
```

The trailing slash behavior matters because it affects the path received by the backend.

### Backend cannot connect to MySQL

Check:

```bash
docker compose ps
docker compose logs mysql
docker compose logs backend
```

Make sure MySQL is healthy and both services are on the same Docker network.

## Security

Recommended `.gitignore`:

```gitignore
mysql_root_password.txt
.env
target/
```

Before pushing to GitHub:

```bash
git status
```

Verify the password file is ignored:

```bash
git check-ignore -v mysql_root_password.txt
```

Do not commit secrets.

## Learning Outcomes

This project provided hands-on practice with:

### Docker

- Images
- Containers
- Dockerfiles
- Multi-stage builds
- Port mapping
- Docker networking
- Docker DNS
- Volumes
- Secrets
- Container troubleshooting
- Logs and inspection

### Docker Compose

- Services
- Networks
- Volumes
- Secrets
- Health checks
- Service dependencies
- Container orchestration

### Java / Maven

- Java 17
- Maven
- JAR packaging
- JDBC
- MySQL connectivity
- HTTP server

### Nginx

- Static file serving
- Reverse proxy
- API routing

## Future Improvements

- Kubernetes deployment
- Jenkins CI/CD pipeline
- GitHub Actions
- Terraform infrastructure
- AWS deployment
- Non-root containers
- Resource limits
- Application health endpoints
- Prometheus monitoring
- Grafana dashboards
- Centralized logging
- HTTPS/TLS

## Author

**Sumit Zarkhande**

DevOps / Cloud Engineer


