# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project setup with Spring Boot 3.3.0
- JWT authentication and authorization
- User management system
- Role-based access control
- RESTful API with OpenAPI documentation
- Database migrations with Flyway
- Docker and Docker Compose configuration
- Comprehensive testing setup
- Code quality tools and CI/CD pipeline

### Security
- JWT token-based authentication
- Password encryption with BCrypt
- CORS configuration for Angular integration
- Security headers configuration

### Infrastructure
- PostgreSQL database integration
- H2 database for development and testing
- Redis for caching (optional)
- Docker containerization
- Health checks and monitoring

## [1.0.0] - 2024-05-02

### Added
- Complete backend API structure
- User authentication and authorization
- Role management system
- API documentation with Swagger
- Database schema and migrations
- Docker deployment configuration
- Development and testing setup
- Comprehensive documentation

### Features
- **Authentication**: JWT-based secure authentication
- **Authorization**: Role-based access control (USER, ADMIN)
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Database**: PostgreSQL with Flyway migrations
- **Testing**: Unit tests with JUnit 5 and Testcontainers
- **Monitoring**: Spring Boot Actuator with health checks
- **CORS**: Configured for Angular frontend integration
- **Validation**: Input validation with Jakarta Bean Validation
- **Error Handling**: Global exception handling with consistent responses

### Technology Stack
- **Framework**: Spring Boot 3.3.0
- **Language**: Java 21
- **Security**: Spring Security 6
- **Database**: Spring Data JPA with PostgreSQL
- **Documentation**: SpringDoc OpenAPI 3
- **Testing**: JUnit 5, Testcontainers, Mockito
- **Build**: Maven 3.9.6
- **Containerization**: Docker and Docker Compose

### API Endpoints
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register` - User registration
- `GET /api/auth/me` - Get current user
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/profile` - Get current user profile
- `GET /api/public/health` - Health check
- `GET /api/public/info` - API information

### Database Schema
- `users` - User accounts
- `roles` - User roles
- `user_roles` - User-role relationships

### Configuration
- **Development**: H2 in-memory database
- **Test**: H2 database for testing
- **Production**: PostgreSQL database
- **Security**: JWT configuration with customizable secrets
- **CORS**: Configurable allowed origins for frontend integration

### Documentation
- Comprehensive README with setup instructions
- API documentation with Swagger UI
- Docker deployment guide
- Development and testing guidelines
- Architecture and design patterns documentation
