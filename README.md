# 📚 Library Management System API

A professional and robust REST API designed for book inventory management, user registration control, and the dynamic processing of book loans and returns within a corporate library. This project is built following industry best practices, ensuring a highly scalable architecture, clean code, and an automated test suite with comprehensive coverage.

---

## 🛠️ Tech Stack

The project's modern ecosystem leverages the following technologies from the Java and Cloud environments:

*   **Java 17 & Spring Boot 3.4+**: The core framework driving structured and rapid enterprise web application development.
*   **Spring Security & JWT (JSON Web Tokens)**: A stateless security architecture providing cryptographic user authentication and Role-Based Access Control (RBAC).
*   **Spring Data JPA & Hibernate**: The data persistence abstraction layer utilizing an advanced Object-Relational Mapper (ORM).
*   **PostgreSQL**: A robust relational database engine used both in production (hosted on **Supabase Cloud**) and in local containers.
*   **Docker & Testcontainers**: Infrastructure virtualization enabling automated integration tests coupled with a live, isolated PostgreSQL container.
*   **JaCoCo (Java Code Coverage)**: Static code analysis tool ensuring the integrity and metrics of software testing.
*   **Springdoc OpenAPI (Swagger UI)**: A live, interactive, self-generated web documentation interface for rapid API endpoint exploration.
*   **Lombok**: Automation library to keep the codebase free of boilerplate code (Getters, Setters, Constructors).

---

## 📐 Architectural and Design Patterns

To ensure maximum maintainability and clear decoupling, the system strictly enforces the following patterns:

### 1. Layered Architecture
Code is strictly segregated into logical layers with unidirectional responsibilities:
*   **Presentation Layer (Controllers/DTOs)**: Manages the HTTP protocol, validates incoming payloads (`jakarta.validation`), and formats hypermedia responses.
*   **Business Layer (Services)**: Centralizes library domain logic (stock control, return expiration calculation, cryptographic payload validation, and login flows).
*   **Persistence Layer (Repositories)**: Abstraes raw SQL executions via clean repository interfaces extending `JpaRepository`.
*   **Model Layer (Entities)**: Defines the physical database schema structure utilizing standard JPA annotations.

### 2. Single Responsibility (SRP) & Dependency Injection (DI)
Heavy authentication and orchestration logics were decoupled from web controllers and migrated into dedicated service classes (`AuthService`). Communication is handled via **Constructor-based Dependency Injection** assisted by Lombok's `@RequiredArgsConstructor`, eliminating rigid coupling and paving the way for pure unit testing.

### 3. DTO Pattern using Java Records
Native, immutable Java `Records` are employed for secure data transfer between client and server (e.g., `LoginRequest`, `AuthResponse`, `LoanRequest`), shielding JPA entities from unnecessary public exposures.

### 4. Intercepting Filter Pattern (OncePerRequestFilter)
The application perimeter security enforces a custom `JwtAuthenticationFilter`. It asynchronously intercepts every incoming HTTP request exactly once to decode and cryptographically verify the JWT signature, encapsulating security away from business controller routines.

---

## 🔒 Security Architecture

The API perimetral network is armored through the following defense layers:

*   **One-Way Hashing with BCrypt**: User passwords are never stored in plain text. They are securely processed through an irreversible key derivation function before persisting to PostgreSQL.
*   **Stateless Authentication (JWT)**: Upon successful authentication, the API issues a cryptographic token digitally signed with a symmetric secret key managed through system environment variables.
*   **Role-Based Access Control (RBAC)**: Critical endpoints are restricted using fine-grained Spring expression parameters via security annotations:
    *   `ROLE_MEMBER`: Standard consumers authorized to view catalogs or request personal book loans.
    *   `ROLE_LIBRARIAN`: Administrative superusers granted exclusive permissions (`@PreAuthorize`) to create books, manage user entities, or clear loan returns.

---

## 🧪 Testing Strategy and Coverage

The application implements a robust testing suite divided strategically to safeguard against software regressions:

### Isolated Unit Tests (JUnit 5 & Mockito)
Core application fragments are tested independently by mocking external runtime dependencies:
*   `JwtServiceTest`: Native verification of JWT generation, token parsing, and custom claims extraction.
*   `AuthServiceTest`: Validates the business login flow orchestration by mocking user repository lookups.
*   `JwtAuthenticationFilterTest`: Validates incoming HTTP header interception and mock thread `SecurityContextHolder` injections.
*   `SecurityConfigTest`: Employs web slice testing (`@WebMvcTest`) to certify that anonymous route deflections and granular role validations enforce actual access parameters.

### Live Integration Tests (Testcontainers & Docker)
An abstract base architecture (`BaseIntegrationTest`) unifies the lifecycle of a real Docker container spinning up **PostgreSQL**. Test classes (`BookControllerIT`, `LoanControllerIT`) execute physical database interactions via a fluid `WebTestClient`, ensuring foreign key constraints and JPA custom queries behave identically to production.

---

## 🚀 Local Configuration and Deployment

### 1. Application Local Properties
To boot the application natively or via container runtimes, create an `application-local.properties` file in your src/main/resource dictionary containing the following secure keys:

```properties
# Supabase Cloud Database Production Credentials
CLOUD_DB_URL=jdbc:postgresql://your-supabase-url:5432/postgres
CLOUD_DB_USER=your_db_user
CLOUD_DB_PASSWORD=your_db_password

# Cryptographic Security Configuration
JWT_SECRET=YourSuperSecureBase64EncodedSecretStringOfAtLeast256BitsToSignTokens
JWT_EXPIRATION=86400000
```

### 2. Compilation and Coverage Reporting
To run all automated test suites concurrently and assemble the JaCoCo coverage report, open your terminal workspace and execute:

```bash
./gradlew clean test jacocoTestReport
```
*The detailed interactive HTML report will be generated inside the workspace directory at: `build/reports/jacoco/test/html/index.html`.*
