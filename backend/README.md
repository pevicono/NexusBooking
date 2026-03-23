# NexusBooking Backend (Spring Boot)

Simple backend API for:
- Register user
- Login user (JWT)
- Logout user
- Get current user
- Edit current user

Includes:
- Basic security with password encryption using BCrypt
- JWT token authentication
- Swagger/OpenAPI docs generation
- PostgreSQL database for runtime persistence

### Prerequisites
- Java 21+
- Maven 3.8+

### Start
```bash
mvn spring-boot:run
```

Default URL:
- App: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

> Note: this repository currently does not include a Maven wrapper (`mvnw`), so Maven must be installed globally.

## PostgreSQL setup
The database is managed via Docker Compose in the `../database/` folder.

```powershell
# From the project root — start Postgres before running the app
cd ../database
.\db.cmd start
```

This spins up a `postgres:16-alpine` container with:

| Setting  | Value                                    |
|----------|------------------------------------------|
| URL      | `jdbc:postgresql://localhost:5432/nexusbooking` |
| Username | `postgres`                               |
| Password | `postgres`                               |
| DB name  | `nexusbooking`                           |

These values are already configured in [src/main/resources/application.properties](src/main/resources/application.properties). If you need different credentials, update **both** that file and `../database/docker-compose.yml`.

On startup, **Flyway** automatically applies any pending migrations from `src/main/resources/db/migration/`, so the schema is created without any manual SQL execution.

## API Endpoints

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`

### User
- `GET /api/users/me` (requires `Authorization: Bearer <token>`)
- `PUT /api/users/me` (requires `Authorization: Bearer <token>`)

## Example requests

### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "secret123"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "secret123"
}
```

### Get current user
```http
GET /api/users/me
Authorization: Bearer <token>
```

### Update current user
```http
PUT /api/users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "john_new@example.com"
}
```

## Run tests
```bash
mvn test
```
