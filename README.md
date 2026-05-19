# JAT - Job Application Tracker

> A full-stack web app for tracking job applications.
> Track your applications' progress, change statuses, add notes.<br>
> Built in Java 25 with Spring Boot 4, Spring Security, Spring Data JPA, JWT authentication (HttpOnly cookies), Flyway migration, Docker, PostgreSQL, and an Angular 21 frontend.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Architecture & Packages](#architecture--packages)
- [Tech Stack & Requirements](#tech-stack--requirements)
- [Environment Configuration](#environment-configuration)
- [Local Setup](#local-setup)
- [Running the App](#running-the-app)
- [Database](#database)
- [API Endpoints](#api-endpoints)
- [Application Status Flow](#application-status-flow)
- [Exception Handling & Responses](#exception-handling--responses)
- [Security Notes](#security-notes)
- [License](#license)

---

## Project Overview

**JAT** (Job Application Tracker) is a full-stack web app for managing your job search from a single dashboard.

- The **backend** exposes a secured REST API (JWT in HttpOnly cookies) built with Spring Boot 4 and PostgreSQL, with schema migrations managed by Flyway.
- The **frontend** is an Angular 21 SPA that consumes the API, featuring a PrimeNG UI, protected routes (auth/guest guards), OTP email verification, and automatic token handling via HTTP interceptors.

---

## Features

- User registration, login, JWT access/refresh token flow via HttpOnly SameSite cookies
- Email OTP verification on registration (via Resend)
- Full CRUD for job applications
- Archive / unarchive applications
- Status lifecycle management (`PENDING → SCREENING → INTERVIEW → OFFER / GHOSTED / REJECTED`)
- Company, location, position level, and contract type tracking per application
- Salary range (lowest / highest) per application
- Job posting URL per application
- Notes field for custom comments on each application
- Application and closing date tracking
- Filter applications by status, position, contract type, company name, or date range; toggle archived visibility
- Role-based access control (each user sees only their own applications)
- Global exception handling with consistent JSON error responses
- Actuator endpoints exposed: `health`, `info`, `metrics`

---

## Architecture & Packages

```
com.wardiusz.jat
├── config/           # SecurityConfig, DatabaseConfig
├── controller/       # AuthController, JobController, UserController
├── dto/              # JobDTO, JobFilter, UserDTO
│   ├── request/      # LoginRequest, RegisterRequest, OtpRequest, CreateJobRequest, ...
│   └── response/
├── entity/           # Job, User, RefreshToken, OtpToken
├── enums/            # JobStatus, JobPosition, JobContract, UserType
├── exception/        # GlobalExceptionHandler, ErrorResponse, ResourceNotFoundException
├── mapper/           # JobMapper, UserMapper
├── repository/       # JobRepository, UserRepository, RefreshTokenRepository, OtpRepository
├── security/         # JwtTokenProvider, JwtAuthFilter, JwtAuthEntryPoint, CookieUtil
└── service/          # AuthService, JobService, OtpTokenService, RefreshTokenService, UserService
    └── implementation/
```

Frontend structure (`frontend/src/app`):

```
app
├── core/
│   ├── guards/       # authGuard, guestGuard
│   ├── interceptors/ # jwt.interceptor (attaches cookies)
│   ├── models/       # auth.model, job.model
│   └── services/     # auth.service, job.service
└── features/
    ├── auth/         # login, register, otp-verify
    ├── dashboard/    # dashboard, job-table, job-modal, filter-bar, stat-card
    └── settings/
```

---

## Tech Stack & Requirements

| Layer        | Technology                               |
|--------------|------------------------------------------|
| Language     | Java 25                                  |
| Framework    | Spring Boot 4.0.3                        |
| Security     | Spring Security 6.x + JWT (jjwt 0.12.6)  |
| Build tool   | Maven 3.x (Maven Wrapper included)       |
| Database     | PostgreSQL                               |
| Migrations   | Flyway                                   |
| ORM          | Spring Data JPA / Hibernate              |
| API style    | REST                                     |
| Email        | Resend (resend-java)                     |
| Testing      | JUnit 5, Spring Boot Test (H2 in-memory) |
| Frontend     | Angular 21                               |
| HTTP         | Angular HttpClient + JWT Interceptor     |
| Reactivity   | RxJS / Observables                       |

**Minimum requirements:** Java 17, Node.js 20+, Docker (for the database)

---

## Environment Configuration

Secrets are stored locally in `src/main/resources/env.properties`, which is loaded via `spring.config.import: optional:env.properties`.

Create this file before running the app:

```properties
DB_SECRET=your-base64-or-plain-jwt-secret-minimum-32-chars
DB_URL=jdbc:postgresql://localhost:5432/postgres-jat-db
DB_USERNAME=postgres
DB_PASSWORD=postgres
MAIL_ADDRESS=noreply@yourdomain.com
RESEND_SECRET=re_your_resend_api_key
```

The `COOKIE_SECURE` flag defaults to `false` in development. Set `COOKIE_SECURE=true` in production (requires HTTPS).

---

## Local Setup

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Wardiusz/JobApplicationTracker.git
   cd JobApplicationTracker
   ```

2. **Create the local config:**

   A template is provided. Copy it and fill in your values:

   ```bash
   cp src/main/resources/templates/application.yml \
      src/main/resources/application.yml
   # Then create env.properties as described above
   ```

3. **Start PostgreSQL with Docker:**

   ```bash
   docker compose -f docker/docker-compose.yaml up -d
   ```

   This starts a `postgres:latest` container named `jat-postgres` with:
    - Database: `postgres-jat-db`
    - User: `postgres` / Password: `postgres`
    - Port: `5432`

   Flyway will automatically create and migrate the schema on first startup.

4. **Build the backend:**

   ```bash
   ./mvnw clean package -DskipTests
   # Windows:
   mvnw.cmd clean package -DskipTests
   ```

5. **Install frontend dependencies:**

   ```bash
   cd frontend
   npm install
   ```

---

## Running the App

### Backend

```bash
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080/api/v1`

### Frontend (development)

```bash
cd frontend
ng serve
# or: npm start
```

The Angular dev server will be available at: `http://localhost:4200`

### Frontend (production build)

```bash
cd frontend
npm run build
# Output: frontend/dist/frontend/browser/
```

Serve the built `dist/` folder via Nginx or let Spring Boot serve it as static content.

---

## Database

- Schema is managed by **Flyway** — migrations run automatically on startup.
- `ddl-auto` is set to `validate` (Flyway is the source of truth, not Hibernate).
- In-memory **H2** is used for tests (`application-test.properties`).
- Main entities: `users`, `jobs`, `refresh_tokens`, `otp_tokens`

---

## API Endpoints

Tokens are transmitted via **HttpOnly cookies** (`access_token`, `refresh_token`), not Authorization headers. All requests to protected endpoints must include cookies (the Angular interceptor handles this automatically).

### Authentication — `/api/v1/auth`

```http
# Register (sends OTP email)
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "jan@example.com",
  "password": "strongpassword"
}
```

```http
# Verify OTP after registration
POST /api/v1/auth/otp-verify
Content-Type: application/json

{
  "email": "jan@example.com",
  "otp": "123456"
}
```

```http
# Resend OTP
POST /api/v1/auth/otp-resend
Content-Type: application/json

{
  "email": "jan@example.com"
}
```

```http
# Login — sets access_token and refresh_token cookies
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "jan@example.com",
  "password": "strongpassword"
}
```

```http
# Refresh access token — reads refresh_token cookie, sets new access_token cookie
POST /api/v1/auth/refresh
```

```http
# Logout — clears both cookies and deletes refresh token from DB
POST /api/v1/auth/logout
```

---

### Job Applications — `/api/v1/jobs`

All job endpoints require an authenticated session (valid `access_token` cookie).

```http
# List all jobs (supports filtering via query params)
GET /api/v1/jobs
GET /api/v1/jobs?status=INTERVIEW
GET /api/v1/jobs?position=JUNIOR&contract=B2B
GET /api/v1/jobs?companyName=Acme&dateFrom=2026-01-01&dateTo=2026-06-01
GET /api/v1/jobs?includeArchived=true
```

Filter parameters:

| Parameter        | Type        | Values                                              |
|------------------|-------------|-----------------------------------------------------|
| `status`         | enum        | `PENDING`, `SCREENING`, `INTERVIEW`, `OFFER`, `GHOSTED`, `REJECTED` |
| `position`       | enum        | `INTERN`, `JUNIOR`, `MID`, `SENIOR`                 |
| `contract`       | enum        | `B2B`, `UOP`, `UOZ`, `UOD`, `INTERN`               |
| `companyName`    | string      | partial or full company name                        |
| `dateFrom`       | date        | `YYYY-MM-DD`                                        |
| `dateTo`         | date        | `YYYY-MM-DD`                                        |
| `includeArchived`| boolean     | `true` / `false` (default: `false`)                 |

```http
# Create a new job application
POST /api/v1/jobs
Content-Type: application/json

{
  "company": "Acme Corp",
  "location": "Warsaw",
  "status": "PENDING",
  "position": "JUNIOR",
  "contract": "B2B",
  "url": "https://example.com/job/123",
  "dateApplied": "2026-04-01",
  "dateClosing": "2026-05-01",
  "salaryLowest": 8000,
  "salaryHighest": 12000,
  "notes": "Applied via LinkedIn"
}
```

```http
# Update a job application
PUT /api/v1/jobs/{id}/update
Content-Type: application/json

{
  "company": "Acme Corp",
  "location": "Warsaw",
  "status": "INTERVIEW",
  "position": "JUNIOR",
  "contract": "B2B",
  "url": "https://example.com/job/123",
  "dateApplied": "2026-04-01",
  "notes": "Phone screen scheduled"
}
```

```http
# Update notes only
PATCH /api/v1/jobs/{id}/notes
Content-Type: text/plain

Phone screen moved to next week
```

```http
# Archive a job
PATCH /api/v1/jobs/{id}/archive
```

```http
# Unarchive a job
PATCH /api/v1/jobs/{id}/unarchive
```

```http
# Delete a job
DELETE /api/v1/jobs/{id}/delete
```

---

## Application Status Flow

```
PENDING → SCREENING → INTERVIEW → OFFER
                    ↘            ↘
                   GHOSTED      REJECTED
```

| Status      | Meaning                                      |
|-------------|----------------------------------------------|
| `PENDING`   | Application submitted, awaiting response     |
| `SCREENING` | Initial HR screening / phone call            |
| `INTERVIEW` | Technical or on-site interview stage         |
| `OFFER`     | Offer received                               |
| `GHOSTED`   | No response after follow-up attempts         |
| `REJECTED`  | Formally rejected                            |

---

## Exception Handling & Responses

All errors return a consistent JSON body:

```json
{
  "status": 404,
  "timestamp": "2026-04-01T12:00:00",
  "message": "Resource not found with id: 42",
  "error": "Not Found"
}
```

Common HTTP response codes:

| Code | Meaning                                      |
|------|----------------------------------------------|
| 200  | Success                                      |
| 201  | Resource created                             |
| 204  | Success, no content (delete, archive, logout)|
| 400  | Validation or bad request                    |
| 401  | Unauthorized (missing/invalid token)         |
| 403  | Forbidden (resource belongs to another user) |
| 404  | Resource not found                           |
| 408  | OTP expired or invalid                       |
| 500  | Internal server error                        |

---

## Security Notes

- JWT tokens are stored in **HttpOnly, SameSite cookies** — they are not accessible via JavaScript.
- Access token expiry: **15 minutes** (`900000 ms`). Refresh token expiry: **24 hours** (`86400000 ms`).
- Each user can only access their own applications — enforced at the service layer.
- For production:
    - Set `COOKIE_SECURE=true` (requires HTTPS / TLS termination via Nginx)
    - Store all secrets in environment variables or a secrets manager (Vault, AWS Secrets Manager)
    - Use a strong, randomly generated `DB_SECRET` (256+ bits for HS256)
    - Configure CORS to only allow your frontend origin
    - Consider rate-limiting the `/auth` endpoints to prevent brute-force attacks

---

## Developer Notes

- **Add Swagger/OpenAPI** — use `springdoc-openapi` for auto-generated API docs
- **Add pagination** — wrap list endpoints
- **Add statistics endpoint** — `GET /api/v1/application/stats` returning counts by status
- ~~**Add email OTP verification** — verify accounts~~
- ~~**Write integration and unit tests** — cover auth flow, application CRUD, status transitions~~

---

## License

This project is licensed under the MIT License.