# NexusBooking

Full-stack booking platform. This repository contains two independently runnable modules:

| Folder      | Stack                              | Description                    |
|-------------|------------------------------------|--------------------------------|
| `backend/`  | Java 21 · Spring Boot 3.2 · Maven  | REST API with JWT auth         |
| `database/` | Docker · PostgreSQL 16 · pgAdmin 4 | Database container & scripts   |
| `mobile/`   | Kotlin · Jetpack Compose · Retrofit | Android client                |

---

## Prerequisites

| Tool           | Version   | Purpose                             |
|----------------|-----------|-------------------------------------|
| Docker Desktop | any       | Runs Postgres + pgAdmin containers  |
| Java           | 21+       | Compiles and runs the backend       |
| Maven          | 3.8+      | Builds and tests the backend        |
| Android SDK    | API 26+   | Builds and runs the mobile client   |

---

## Quick start

```powershell
# 1. Start the database
cd database
.\db.ps1 start

# 2. (Optional) Seed sample users
.\db.ps1 seed

# 3. Start the backend  (Flyway runs migrations automatically on boot)
cd ..\backend
mvn spring-boot:run
```

| Service    | URL                                          |
|------------|----------------------------------------------|
| API        | http://localhost:8080                        |
| Swagger UI | http://localhost:8080/swagger-ui.html        |
| pgAdmin    | http://localhost:5050  (admin@nexusbooking.com / admin) |

---

## Database credentials

Both the Docker Compose file and `application.properties` use the same defaults:

| Setting  | Value          |
|----------|----------------|
| Host     | `localhost`    |
| Port     | `5432`         |
| Database | `nexusbooking` |
| Username | `postgres`     |
| Password | `postgres`     |

To change credentials, update **both** files consistently:
- `database/docker-compose.yml` → `POSTGRES_USER` / `POSTGRES_PASSWORD`
- `backend/src/main/resources/application.properties` → `spring.datasource.username` / `spring.datasource.password`

---

## Project structure

```
NexusBooking/
├── backend/                          # Spring Boot application
│   ├── src/main/resources/
│   │   ├── application.properties    # DB + JWT + Swagger config
│   │   └── db/migration/             # Flyway versioned SQL migrations
│   └── pom.xml
├── database/                         # Database infrastructure
│   ├── docker-compose.yml            # Postgres + pgAdmin services
│   ├── db.ps1                        # DB management script
│   └── scripts/
│       ├── V1__create_users_table.sql  # Schema DDL
│       ├── seed_data.sql               # Sample data
│       ├── clear_data.sql              # Truncate all tables
│       └── drop_tables.sql             # Drop all tables
└── mobile/                           # Android client
    └── app/
        └── src/main/java/.../
            ├── data/
            │   ├── local/            # DataStore (JWT token)
            │   ├── remote/           # Retrofit API + DTOs
            │   └── repository/       # Auth & User repositories
            ├── di/                   # Hilt dependency injection
            ├── ui/
            │   ├── login/            # Login & register screen
            │   ├── profile/          # Profile screen
            │   └── navigation/       # Nav graph
            └── util/                 # Shared utilities
```

---

## Useful database commands

```powershell
cd database

.\db.ps1 start    # start containers
.\db.ps1 stop     # stop containers (data persisted)
.\db.ps1 reset    # drop schema + recreate from scratch
.\db.ps1 clear    # delete all rows, keep schema
.\db.ps1 seed     # insert sample admin + user
.\db.ps1 psql     # open interactive psql shell
.\db.ps1 logs     # tail Postgres logs
.\db.ps1 status   # show container health
```

---

## Run tests

```powershell
cd backend
mvn test
```

Tests use an H2 in-memory database — no running container required. Flyway is disabled in the test profile.

---

## Mobile client

Android app built with Kotlin and Jetpack Compose. Connects to the backend REST API using Retrofit and stores the JWT token with DataStore.

**Requirements:** Android SDK (API 26+) and an emulator or physical device running Android 8+

**Setup:**
The app points to `http://10.0.2.2:8080` by default, which is the standard Android emulator alias for `localhost`. Make sure the backend is running before launching the app. If using a physical device, update the base URL in `mobile/app/src/main/java/.../data/remote/ApiService.kt`.

**Run:**
```bash
cd mobile
./gradlew installDebug
```

---

## Seed accounts

Created by `.\db.ps1 seed` or running `database/scripts/seed_data.sql`:

| Email                      | Password   | Role  |
|----------------------------|------------|-------|
| admin@nexusbooking.com     | Admin1234! | ADMIN |
| user@nexusbooking.com      | User1234!  | USER  |
