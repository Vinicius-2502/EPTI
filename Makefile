.PHONY: help build test run clean docker-build docker-run docker-stop docker-clean

# Default target
help:
	@echo "Available targets:"
	@echo "  build          - Build the project"
	@echo "  test           - Run tests"
	@echo "  run            - Run the application (development)"
	@echo "  run-prod       - Run the application (production)"
	@echo "  clean          - Clean build artifacts"
	@echo "  docker-build   - Build Docker image"
	@echo "  docker-run     - Run with Docker Compose"
	@echo "  docker-stop    - Stop Docker containers"
	@echo "  docker-clean   - Clean Docker resources"
	@echo "  format         - Format code with spotless"
	@echo "  check          - Run code quality checks"

# Build the project
build:
	./mvnw clean compile

# Run tests
test:
	./mvnw test

# Run tests with coverage
test-coverage:
	./mvnw clean verify jacoco:report

# Run the application in development mode
run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=development

# Run the application in production mode
run-prod:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=production

# Clean build artifacts
clean:
	./mvnw clean

# Package the application
package:
	./mvnw clean package -DskipTests

# Build Docker image
docker-build:
	docker build -t epti-backend .

# Run with Docker Compose
docker-run:
	docker-compose up -d

# Stop Docker containers
docker-stop:
	docker-compose down

# Clean Docker resources
docker-clean:
	docker-compose down -v
	docker system prune -f

# Format code
format:
	./mvnw spotless:apply

# Check code quality
check:
	./mvnw spotless:check
	./mvnw checkstyle:check

# Generate API documentation
docs:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=development &
	@sleep 10
	@echo "API Documentation available at: http://localhost:8080/api/swagger-ui.html"

# Database migration
migrate:
	./mvnw flyway:migrate

# Database migration rollback
rollback:
	./mvnw flyway:undo

# Install dependencies
deps:
	./mvnw dependency:resolve

# Update dependencies
update-deps:
	./mvnw versions:display-dependency-updates
	./mvnw versions:display-plugin-updates

# Security check
security:
	./mvnw org.owasp:dependency-check-maven:check

# Full CI pipeline
ci: clean test-coverage check security package
