# Project Management System - Java Backend

A modern, scalable project management system built with Java 21 and Spring Boot, featuring MongoDB integration for flexible data management.

## Overview

This application provides a comprehensive project management solution with support for organizations, projects, tasks, and document management. Built on modern Java architecture principles, it offers robust authentication, role-based access control, and enterprise-grade security features.

## Features

- **User Management**: Complete user authentication and authorization with role-based access control
- **Organization Management**: Multi-tenant organization support with hierarchical structures
- **Project Management**: Comprehensive project lifecycle management with team collaboration
- **Task Management**: Advanced task tracking with assignments, priorities, and status management
- **Document Management**: Secure document storage and retrieval with version control
- **Security**: Modern authentication framework with JWT tokens and multi-factor authentication support
- **Performance Monitoring**: Built-in monitoring and metrics collection
- **RESTful API**: Well-documented REST endpoints following OpenAPI specifications

## Technology Stack

- **Java**: 21 (LTS)
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle 8.x
- **Database**: MongoDB 7.x
- **Security**: Spring Security with JWT
- **Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, Mockito, TestContainers
- **Logging**: SLF4J with Logback
- **Monitoring**: Spring Boot Actuator, Micrometer

## Prerequisites

- JDK 21 or higher
- MongoDB 7.0 or higher
- Gradle 8.x (or use included wrapper)
- Docker (optional, for containerized deployment)

## Getting Started

### Clone the Repository

```bash
git clone <repository-url>
cd <project-directory>
```

### Configuration

Create an `application.yml` file in `src/main/resources` or set environment variables:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/projectmanagement
      database: projectmanagement
  
server:
  port: 8080

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400000

logging:
  level:
    root: INFO
    com.projectmanagement: DEBUG
```

### Environment Variables

- `MONGODB_URI`: MongoDB connection string
- `MONGODB_DATABASE`: Database name
- `JWT_SECRET`: Secret key for JWT token generation
- `JWT_EXPIRATION`: Token expiration time in milliseconds
- `SERVER_PORT`: Application server port (default: 8080)

### Build the Application

```bash
./gradlew clean build
```

### Run the Application

```bash
./gradlew bootRun
```

Or run the JAR directly:

```bash
java -jar build/libs/project-management-system-0.0.1-SNAPSHOT.jar
```

### Run with Docker

```bash
docker build -t project-management-system .
docker run -p 8080:8080 -e MONGODB_URI=mongodb://host.docker.internal:27017/projectmanagement project-management-system
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/projectmanagement/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── exception/       # Custom exceptions and handlers
│   │       ├── model/           # Domain models (MongoDB documents)
│   │       ├── repository/      # MongoDB repositories
│   │       ├── security/        # Security configuration and filters
│   │       ├── service/         # Business logic services
│   │       └── util/            # Utility classes
│   └── resources/
│       ├── application.yml      # Application configuration
│       └── application-*.yml    # Profile-specific configurations
└── test/
    └── java/
        └── com/projectmanagement/
            ├── integration/     # Integration tests
            ├── service/         # Service unit tests
            └── controller/      # Controller tests
```

## API Documentation

Once the application is running, access the API documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Key Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout

### Users
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Organizations
- `POST /api/organizations` - Create organization
- `GET /api/organizations` - List organizations
- `GET /api/organizations/{id}` - Get organization details
- `PUT /api/organizations/{id}` - Update organization
- `DELETE /api/organizations/{id}` - Delete organization

### Projects
- `POST /api/projects` - Create project
- `GET /api/projects` - List projects
- `GET /api/projects/{id}` - Get project details
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Tasks
- `POST /api/tasks` - Create task
- `GET /api/tasks` - List tasks
- `GET /api/tasks/{id}` - Get task details
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Documents
- `POST /api/documents` - Upload document
- `GET /api/documents` - List documents
- `GET /api/documents/{id}` - Get document details
- `GET /api/documents/{id}/download` - Download document
- `DELETE /api/documents/{id}` - Delete document

## Testing

### Run All Tests

```bash
./gradlew test
```

### Run Integration Tests

```bash
./gradlew integrationTest
```

### Run with Coverage

```bash
./gradlew test jacocoTestReport
```

Coverage reports will be available at `build/reports/jacoco/test/html/index.html`

## Security

### Authentication

The application uses JWT (JSON Web Tokens) for authentication. To access protected endpoints:

1. Register or login to obtain a JWT token
2. Include the token in the Authorization header: `Authorization: Bearer <token>`

### Role-Based Access Control

The system supports the following roles:
- `ADMIN`: Full system access
- `MANAGER`: Organization and project management
- `USER`: Basic user access
- `GUEST`: Read-only access

## Performance Monitoring

Access actuator endpoints for monitoring:

- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`

## Database Schema

### Collections

- **users**: User accounts and profiles
- **organizations**: Business organizations
- **projects**: Project entities
- **tasks**: Task management
- **documents**: Document metadata and storage

### Indexes

The application automatically creates indexes for:
- User email (unique)
- User username (unique)
- Organization name
- Project organizationId
- Task projectId
- Document projectId and organizationId

## Development

### Code Style

The project follows standard Java conventions:
- Use camelCase for variables and methods
- Use PascalCase for classes
- Use UPPER_SNAKE_CASE for constants
- Maximum line length: 120 characters
- Use meaningful variable names

### Logging

Use SLF4J for logging:

```java
private static final Logger logger = LoggerFactory.getLogger(ClassName.class);

logger.debug("Debug message");
logger.info("Info message");
logger.warn("Warning message");
logger.error("Error message", exception);
```

### Error Handling

The application uses a global exception handler for consistent error responses:

```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users"
}
```

## Deployment

### Production Build

```bash
./gradlew clean build -Pprod
```

### Docker Deployment

```bash
docker-compose up -d
```

### Environment-Specific Configuration

Use Spring profiles for different environments:
- `dev`: Development environment
- `test`: Testing environment
- `prod`: Production environment

Activate a profile:
```bash
java -jar app.jar --spring.profiles.active=prod
```

## Troubleshooting

### Common Issues

1. **MongoDB Connection Failed**
   - Verify MongoDB is running
   - Check connection string in configuration
   - Ensure network connectivity

2. **Port Already in Use**
   - Change server port in application.yml
   - Or set SERVER_PORT environment variable

3. **JWT Token Invalid**
   - Verify JWT_SECRET is configured
   - Check token expiration time
   - Ensure clock synchronization

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Write unit tests for new features
- Maintain test coverage above 80%
- Follow existing code style
- Update documentation as needed
- Add meaningful commit messages

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or contributions, please:
- Open an issue on GitHub
- Contact the development team
- Check the documentation wiki

## Roadmap

- [ ] GraphQL API support
- [ ] Real-time notifications with WebSocket
- [ ] Advanced reporting and analytics
- [ ] Mobile application support
- [ ] Third-party integrations (Slack, JIRA, etc.)
- [ ] AI-powered task recommendations
- [ ] Advanced document collaboration features
- [ ] Multi-language support

## Acknowledgments

- Spring Boot team for the excellent framework
- MongoDB team for the flexible database
- All contributors and maintainers

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Maintained By**: Development Team