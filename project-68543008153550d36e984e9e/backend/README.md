# Project Management System - Modernized Java Backend

A modern, scalable project management system built with Spring Boot 3.x and Java 21, featuring MongoDB integration, comprehensive security, and RESTful APIs.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## Overview

This project represents a complete modernization of a legacy Java application, transforming it into a contemporary, cloud-native system with enhanced security, performance, and maintainability. The system manages users, organizations, projects, tasks, and documents with comprehensive role-based access control.

## Features

### Core Functionality
- **User Management**: Complete user lifecycle management with profile management and authentication
- **Organization Management**: Multi-tenant organization support with hierarchical structures
- **Project Management**: Comprehensive project tracking and management capabilities
- **Task Management**: Advanced task assignment, tracking, and workflow management
- **Document Management**: Secure document storage and retrieval with version control

### Technical Features
- **Modern Architecture**: Clean architecture with separation of concerns
- **RESTful APIs**: Well-designed REST endpoints following best practices
- **Security**: JWT-based authentication with role-based access control (RBAC)
- **Data Validation**: Comprehensive input validation and error handling
- **Logging & Monitoring**: Structured logging with performance monitoring
- **Caching**: Redis-based caching for improved performance
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Testing**: Comprehensive unit and integration test coverage

## Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← REST endpoints
├─────────────────────────────────────┤
│          Service Layer              │  ← Business logic
├─────────────────────────────────────┤
│        Repository Layer             │  ← Data access
├─────────────────────────────────────┤
│         MongoDB Database            │  ← Data persistence
└─────────────────────────────────────┘
```

### Design Patterns
- **Dependency Injection**: Spring IoC container
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer objects for API contracts
- **Builder Pattern**: Complex object construction
- **Strategy Pattern**: Flexible algorithm selection
- **Factory Pattern**: Object creation abstraction

## Tech Stack

### Core Technologies
- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.2.x**: Application framework
- **Spring Data MongoDB**: MongoDB integration
- **Spring Security**: Authentication and authorization
- **Spring Validation**: Input validation
- **Gradle 8.x**: Build automation

### Additional Libraries
- **Lombok**: Boilerplate code reduction
- **MapStruct**: Object mapping
- **JWT (jjwt)**: JSON Web Token implementation
- **Springdoc OpenAPI**: API documentation
- **Logback**: Logging framework
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **TestContainers**: Integration testing

## Prerequisites

- Java 21 or higher
- Gradle 8.x or higher
- MongoDB 6.0 or higher
- Redis 7.x (optional, for caching)
- Docker & Docker Compose (for containerized deployment)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd project-management-system
```

### 2. Configure Application Properties

Copy the example configuration and update with your settings:

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

Update the following properties:
- MongoDB connection string
- JWT secret key
- Redis configuration (if using)
- CORS allowed origins

### 3. Build the Application

```bash
./gradlew clean build
```

### 4. Run Tests

```bash
./gradlew test
```

### 5. Start the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 6. Access API Documentation

Navigate to `http://localhost:8080/swagger-ui.html` for interactive API documentation.

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/projectmanagement/
│   │       ├── config/              # Configuration classes
│   │       │   ├── MongoConfig.java
│   │       │   ├── SecurityConfig.java
│   │       │   ├── CacheConfig.java
│   │       │   └── OpenApiConfig.java
│   │       ├── controller/          # REST controllers
│   │       │   ├── UserController.java
│   │       │   ├── OrganizationController.java
│   │       │   ├── ProjectController.java
│   │       │   ├── TaskController.java
│   │       │   └── DocumentController.java
│   │       ├── dto/                 # Data Transfer Objects
│   │       │   ├── request/
│   │       │   └── response/
│   │       ├── exception/           # Custom exceptions
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── custom/
│   │       ├── model/               # Domain models
│   │       │   ├── User.java
│   │       │   ├── Organization.java
│   │       │   ├── Project.java
│   │       │   ├── Task.java
│   │       │   └── Document.java
│   │       ├── repository/          # Data repositories
│   │       │   ├── UserRepository.java
│   │       │   ├── OrganizationRepository.java
│   │       │   ├── ProjectRepository.java
│   │       │   ├── TaskRepository.java
│   │       │   └── DocumentRepository.java
│   │       ├── security/            # Security components
│   │       │   ├── JwtTokenProvider.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── UserDetailsServiceImpl.java
│   │       ├── service/             # Business logic
│   │       │   ├── UserService.java
│   │       │   ├── OrganizationService.java
│   │       │   ├── ProjectService.java
│   │       │   ├── TaskService.java
│   │       │   └── DocumentService.java
│   │       ├── util/                # Utility classes
│   │       └── validation/          # Custom validators
│   └── resources/
│       ├── application.yml          # Main configuration
│       ├── application-dev.yml      # Development profile
│       ├── application-prod.yml     # Production profile
│       └── logback-spring.xml       # Logging configuration
└── test/
    ├── java/
    │   └── com/projectmanagement/
    │       ├── controller/          # Controller tests
    │       ├── service/             # Service tests
    │       ├── repository/          # Repository tests
    │       └── integration/         # Integration tests
    └── resources/
        └── application-test.yml     # Test configuration
```

## Configuration

### Application Properties

Key configuration properties in `application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/projectmanagement}
  
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: 86400000  # 24 hours

  cache:
    type: redis
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}

logging:
  level:
    com.projectmanagement: INFO
    org.springframework: WARN
```

### Environment Variables

- `MONGODB_URI`: MongoDB connection string
- `JWT_SECRET`: Secret key for JWT token generation
- `REDIS_HOST`: Redis server host
- `REDIS_PORT`: Redis server port
- `CORS_ALLOWED_ORIGINS`: Comma-separated list of allowed origins

## API Documentation

### Authentication Endpoints

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout

### User Endpoints

- `GET /api/users` - List all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Organization Endpoints

- `POST /api/organizations` - Create organization
- `GET /api/organizations` - List organizations
- `GET /api/organizations/{id}` - Get organization by ID
- `PUT /api/organizations/{id}` - Update organization
- `DELETE /api/organizations/{id}` - Delete organization

### Project Endpoints

- `POST /api/projects` - Create project
- `GET /api/projects` - List projects
- `GET /api/projects/{id}` - Get project by ID
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Task Endpoints

- `POST /api/tasks` - Create task
- `GET /api/tasks` - List tasks
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/status` - Update task status

### Document Endpoints

- `POST /api/documents` - Upload document
- `GET /api/documents` - List documents
- `GET /api/documents/{id}` - Get document by ID
- `GET /api/documents/{id}/download` - Download document
- `DELETE /api/documents/{id}` - Delete document

## Security

### Authentication

The application uses JWT (JSON Web Token) for stateless authentication:

1. User logs in with credentials
2. Server validates and returns JWT token
3. Client includes token in Authorization header for subsequent requests
4. Server validates token and extracts user information

### Authorization

Role-Based Access Control (RBAC) with the following roles:

- **ADMIN**: Full system access
- **MANAGER**: Organization and project management
- **USER**: Basic user access
- **GUEST**: Read-only access

### Security Best Practices

- Passwords hashed using BCrypt
- JWT tokens with expiration
- CORS configuration for cross-origin requests
- Input validation and sanitization
- SQL injection prevention through parameterized queries
- XSS protection through output encoding
- CSRF protection for state-changing operations

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests UserServiceTest

# Run with coverage report
./gradlew test jacocoTestReport
```

### Test Coverage

The project maintains minimum 80% code coverage across:
- Unit tests for service layer
- Integration tests for repositories
- Controller tests with MockMvc
- End-to-end tests with TestContainers

### Test Structure

```java
@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");
    
    @Autowired
    private UserService userService;
    
    @Test
    void shouldCreateUser() {
        // Test implementation
    }
}
```

## Monitoring

### Health Checks

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Logging

Structured logging with correlation IDs for request tracing:

```java
log.info("User created successfully", 
    kv("userId", user.getId()),
    kv("username", user.getUsername()));
```

### Performance Monitoring

- Response time tracking
- Database query performance
- Cache hit/miss ratios
- Error rate monitoring

## Deployment

### Docker Deployment

Build and run with Docker:

```bash
# Build Docker image
docker build -t project-management-system .

# Run container
docker run -p 8080:8080 \
  -e MONGODB_URI=mongodb://mongo:27017/projectmanagement \
  -e JWT_SECRET=your-secret-key \
  project-management-system
```

### Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Kubernetes Deployment

```bash
# Apply configurations
kubectl apply -f k8s/

# Check deployment status
kubectl get pods
kubectl get services
```

## Contributing

### Development Workflow

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards

- Follow Java naming conventions
- Write comprehensive unit tests
- Document public APIs with Javadoc
- Keep methods focused and concise
- Use meaningful variable names
- Follow SOLID principles

### Commit Message Format

```
type(scope): subject

body

footer
```

Types: feat, fix, docs, style, refactor, test, chore

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or contributions:
- Create an issue in the repository
- Contact the development team
- Check the documentation wiki

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Maintained By**: Development Team