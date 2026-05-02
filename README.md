# EPTI Backend API

Backend API for EPTI project with Angular integration, built with Spring Boot 3.3.0 and Java 21.

## 🚀 Features

- **Spring Boot 3.3.0** with Java 21
- **Spring Security** with JWT authentication
- **Spring Data JPA** with PostgreSQL
- **Flyway** for database migrations
- **OpenAPI/Swagger** documentation
- **Validation** with Jakarta Bean Validation
- **Testcontainers** for integration testing
- **Lombok** for reducing boilerplate code
- **MapStruct** for object mapping
- **CORS** configuration for Angular integration
- **Actuator** for monitoring and health checks

## 📋 Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+
- Docker (optional, for containerized deployment)

## 🛠️ Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd EPTI/BackEnd
   ```

2. **Configure database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE epti_db;
   CREATE USER epti_user WITH PASSWORD 'epti_password';
   GRANT ALL PRIVILEGES ON DATABASE epti_db TO epti_user;
   ```

3. **Environment variables**
   
   Create `application-local.yml` or set environment variables:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/epti_db
       username: epti_user
       password: epti_password
   
   jwt:
     secret: your-super-secret-jwt-key-here
     expiration: 86400000
   ```

4. **Run the application**
   ```bash
   # Development mode (H2 database)
   mvn spring-boot:run -Dspring-boot.run.profiles=development
   
   # Production mode (PostgreSQL)
   mvn spring-boot:run -Dspring-boot.run.profiles=production
   ```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/epti/backend/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Custom exceptions
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Spring Data repositories
│   │   ├── security/       # Security configuration
│   │   ├── service/        # Business logic
│   │   └── util/           # Utility classes
│   └── resources/
│       ├── db/migration/   # Flyway migrations
│       ├── application.yml # Main configuration
│       └── application-dev.yml
└── test/                   # Test classes
```

## 🔐 Authentication

The API uses JWT (JSON Web Token) for authentication:

1. **Register a new user**
   ```http
   POST /api/auth/register
   Content-Type: application/json
   
   {
     "username": "john.doe",
     "email": "john@example.com",
     "password": "password123",
     "firstName": "John",
     "lastName": "Doe"
   }
   ```

2. **Login**
   ```http
   POST /api/auth/login
   Content-Type: application/json
   
   {
     "username": "john.doe",
     "password": "password123"
   }
   ```

3. **Use the token**
   ```http
   GET /api/users/profile
   Authorization: Bearer <your-jwt-token>
   ```

## 📚 API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs
- **H2 Console (dev)**: http://localhost:8080/api/h2-console

## 🧪 Testing

Run all tests:
```bash
mvn test
```

Run tests with coverage:
```bash
mvn jacoco:report
```

## 🔧 Configuration

### Profiles

- **development**: Uses H2 in-memory database, enables console
- **test**: Uses H2 for testing
- **production**: Uses PostgreSQL, optimized settings

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | `epti_user` |
| `DB_PASSWORD` | Database password | `epti_password` |
| `JWT_SECRET` | JWT signing secret | `mySecretKey...` |
| `JWT_EXPIRATION` | JWT expiration (ms) | `86400000` |
| `CORS_ALLOWED_ORIGINS` | CORS allowed origins | `http://localhost:4200,http://localhost:3000` |

## 🐳 Docker

Build and run with Docker:
```bash
# Build image
docker build -t epti-backend .

# Run container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/epti_db \
  -e DB_USERNAME=epti_user \
  -e DB_PASSWORD=epti_password \
  epti-backend
```

## 🚀 Deployment

### Production Build

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/backend-api-1.0.0.jar --spring.profiles.active=production
```

### Health Check

```bash
curl http://localhost:8080/api/public/health
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support, please contact the development team or create an issue in the repository.

## 🔗 Angular Integration

This backend is designed to work seamlessly with Angular frontend. Key integration points:

1. **CORS**: Configured for Angular development server (localhost:4200)
2. **JWT**: Angular can store and send JWT tokens for authentication
3. **RESTful API**: Standard REST endpoints for easy consumption
4. **Error Handling**: Consistent error response format
5. **Validation**: Server-side validation with meaningful error messages

Example Angular service integration:
```typescript
// Example API service
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials);
  }
  
  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/profile`, {
      headers: { Authorization: `Bearer ${this.getToken()}` }
    });
  }
}
```
