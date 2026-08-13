# Breach Guard 🛡️

A comprehensive security monitoring and breach protection application built with Spring Boot.

## 📋 Features

- **Real-time Breach Monitoring**: Monitor and detect security breaches
- **System Status Tracking**: Track system health and status
- **API Integration**: RESTful APIs for breach checking and monitoring
- **Web Dashboard**: User-friendly web interface for security monitoring
- **Game Module**: Interactive security awareness game
- **Database Persistence**: H2 database for data storage
- **API Documentation**: Swagger/OpenAPI documentation included
- **Security**: Spring Security integration for protected resources

## 🛠️ Tech Stack

- **Java 21**: Latest Java LTS version
- **Spring Boot 3.2.3**: Modern Spring framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: ORM and database abstraction
- **Thymeleaf**: Server-side templating engine
- **H2 Database**: Embedded SQL database
- **SpringDoc OpenAPI**: API documentation
- **Docker & Docker Compose**: Containerization support

## 📦 Prerequisites

- **Java 21** (or compatible JDK)
- **Maven 3.8+** (for building)
- **Docker & Docker Compose** (optional, for containerized deployment)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/vito-dev-null/breach-guard.git
cd breach-guard
```

### 2. Build the Application

```bash
mvn clean install
```

### 3. Run the Application

#### Using Maven:

```bash
mvn spring-boot:run
```

#### Using Java (after building):

```bash
java -jar target/monitoring-app-0.0.1-SNAPSHOT.jar
```

#### Using Docker Compose:

```bash
docker-compose up --build
```

The application will start on `http://localhost:8080`

## 📚 Usage

### Web Interface

1. Open your browser and navigate to: `http://localhost:8080`
2. Explore the main dashboard for security monitoring
3. Use the navigation menu to access different features:
   - **Dashboard**: Main security overview
   - **Breach Check**: Check for known breaches
   - **Monitoring**: System status and monitoring
   - **Game**: Security awareness interactive game

### API Endpoints

All API endpoints are documented with Swagger/OpenAPI:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Key API Routes

- **GET `/api/breach/check`** - Check for breaches
- **GET `/api/monitoring/status`** - Get system status
- **GET `/game`** - Access the security game
- **GET `/error`** - Custom error page

## 🗂️ Project Structure

```
breach-guard/
├── src/main/java/com/example/monitoring/
│   ├── MonitoringApplication.java          # Spring Boot entry point
│   ├── config/                             # Configuration classes
│   ├── controller/                         # REST controllers & MVC handlers
│   ├── service/                            # Business logic services
│   ├── repository/                         # Data access layer
│   ├── dto/                                # Data transfer objects
│   └── model/                              # Domain models
├── src/main/resources/
│   ├── templates/                          # Thymeleaf HTML templates
│   ├── static/                             # CSS, JavaScript, images
│   └── application.properties              # Application configuration
├── src/test/java/                          # Unit and integration tests
├── pom.xml                                 # Maven configuration
├── Dockerfile                              # Container image definition
├── docker-compose.yml                      # Multi-container orchestration
└── README.md                               # This file
```

## ⚙️ Configuration

### Development Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Server port
server.port=8080

# Database configuration
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb

# Logging levels
logging.level.root=INFO
```

### Environment Variables Setup

Copy the `.env.example` file to `.env` and configure:

```bash
cp .env.example .env
# Edit .env with your settings
```

**Important**: Never commit `.env` file to Git. It's already in `.gitignore`.

### Production Deployment

For production, use the `prod` profile with environment variables:

```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export SECURITY_USER_NAME=admin
export SECURITY_USER_PASSWORD=your_secure_password_here
export SECURITY_USER_ROLE=ADMIN
export SPRING_DATASOURCE_PASSWORD=your_db_password

# Run with production config
mvn spring-boot:run
```

Or with Docker:

```bash
docker run -e SPRING_PROFILES_ACTIVE=prod \
  -e SECURITY_USER_PASSWORD=your_secure_password \
  breach-guard:latest
```

**Production Security Notes:**
- Use strong passwords (at least 12 characters, mixed case, numbers, symbols)
- Disable H2 console (`spring.h2.console.enabled=false` in prod profile)
- Use HTTPS/TLS in production
- Rotate credentials regularly
- Use external database instead of H2 in production
- Enable proper logging and monitoring


## 🧪 Testing

Run the test suite:

```bash
mvn test
```

Tests include:
- Unit tests for services
- Integration tests for API endpoints
- Branch coverage tests for critical components

## 🐳 Docker Deployment

### Build Docker Image

```bash
docker build -t breach-guard:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

Access the application at `http://localhost:8080`

### Stop Containers

```bash
docker-compose down
```

## 📖 API Documentation

### Breach Checking

```bash
curl -X GET "http://localhost:8080/api/breach/check" \
  -H "Content-Type: application/json"
```

### System Monitoring

```bash
curl -X GET "http://localhost:8080/api/monitoring/status" \
  -H "Content-Type: application/json"
```

## 🔐 Security

### Development vs Production

This application includes both development and production configurations:

- **Development**: Uses in-memory database (H2), simplified credentials
- **Production**: Uses persistent database, BCrypt password hashing, environment variable configuration

### Password Security

- Passwords are hashed using **BCrypt** (production-grade)
- Never use default credentials in production
- Set strong passwords via environment variables: `SECURITY_USER_PASSWORD`
- All credentials should be at least 12 characters with mixed case, numbers, and symbols

### Application Security Features

- Spring Security integration for protected resources
- BCryptPasswordEncoder for password hashing
- CSRF protection can be enabled per environment
- API endpoint documentation with Swagger/OpenAPI
- H2 console disabled in production profile

### Security Best Practices

1. **Never commit secrets** to Git (`.env` is in `.gitignore`)
2. **Use environment variables** for sensitive configuration in production
3. **Keep dependencies updated** - run `mvn dependency:check` regularly
4. **Run HTTPS** in production - configure SSL/TLS
5. **Disable H2 console** in production (`spring.h2.console.enabled=false`)
6. **Use external database** - Don't rely on embedded H2 for production data
7. **Implement logging** - Monitor authentication attempts and security events
8. **Regular backups** - Backup your database regularly
9. **Keep Java updated** - Use Java 21 LTS for latest security patches

### CVE and Vulnerability Scanning

Monitor dependencies for vulnerabilities:

```bash
# Check for known vulnerabilities
mvn org.owasp:dependency-check-maven:check

# Update dependencies safely
mvn versions:display-dependency-updates
```


## 🚦 Troubleshooting

### Port 8080 Already in Use

```bash
# Run on a different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Database Issues

The application uses an embedded H2 database. To reset:

1. Delete the database file (if persistent)
2. Restart the application

### Build Errors

```bash
# Clear Maven cache
mvn clean
# Rebuild
mvn clean install
```

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Vito D'Orio**
- Email: user@example.com
- GitHub: [@vito-dev-null](https://github.com/vito-dev-null)

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to open an issue or submit a pull request.

## 📞 Support

For support, open an issue on GitHub: [Issues](https://github.com/vito-dev-null/breach-guard/issues)

---

**Happy coding!** 🚀
