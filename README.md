# NexusBooking

Full-stack booking platform with REST API backend, database infrastructure, and mobile + desktop clients. A complete solution for managing facility bookings, groups, and incidents.

| Folder      | Stack                                | Description                    |
|-------------|--------------------------------------|--------------------------------|
| `backend/`  | Java 21 · Spring Boot 3.2 · Maven    | REST API with JWT auth         |
| `database/` | Docker · PostgreSQL 16 · pgAdmin 4   | Database infrastructure        |
| `desktop/`  | Java 17 · JavaFX · Maven             | Desktop client (multi-role UI) |
| `mobile/`   | Kotlin · Jetpack Compose · Retrofit  | Android client                 |

---

## Prerequisites

| Tool           | Version   | Purpose                             |
|----------------|-----------|-------------------------------------|
| Docker Desktop | any       | Runs Postgres + pgAdmin containers  |
| Java           | 21+       | Compiles and runs the backend       |
| Maven          | 3.8+      | Builds and tests the backend        |
| Android SDK    | API 26+   | Builds and runs the mobile client   |

---

## Quick Start: Database + Backend

```powershell
# 1. Start the database (Docker containers)
cd database
.\db.cmd start

# 2. (Optional) Load demo data
.\db.cmd seed

# 3. Start the backend (Flyway runs migrations automatically)
cd ..\backend
mvn spring-boot:run
```

| Service    | URL                                          |
|------------|----------------------------------------------|
| API        | http://localhost:8080                        |
| Swagger UI | http://localhost:8080/swagger-ui.html        |
| pgAdmin    | http://localhost:5050                        |

---

## Database

PostgreSQL 16 running in Docker with pgAdmin web interface for management.

### Quick Commands

| Command | Purpose |
|---------|---------|
| `.\db.cmd start` | Start Postgres + pgAdmin |
| `.\db.cmd stop` | Stop containers (data persisted) |
| `.\db.cmd reset` | Drop schema + recreate from scratch |
| `.\db.cmd clear` | Delete all rows, keep schema |
| `.\db.cmd seed` | Load complete demo dataset |
| `.\db.cmd psql` | Open interactive SQL shell |
| `.\db.cmd logs` | Tail Postgres logs |

### Features

- **Migrations:** Flyway automates schema versioning in `backend/src/main/resources/db/migration/`
- **Seed Data:** 15 users, 12 facilities, 9 groups, 31 bookings, 5 incidents
- **Admin UI:** pgAdmin at `http://localhost:5050` for visual database management
- **Scripted:** Full control via `db.cmd` — no manual Docker commands needed

See [`database/README.md`](database/README.md) for complete database documentation.

---

## Backend

Spring Boot 3.2 REST API with JWT authentication, OpenAPI (Swagger), and comprehensive booking management features.

### Key Features

- **Authentication:** Login + register with JWT tokens
- **Resource Management:** Users, facilities, groups, bookings, incidents
- **Role-Based Access:** ADMIN and USER roles with separate endpoints
- **Validation:** Input constraints, date/time validation for bookings
- **Error Handling:** Structured JSON error responses
- **API Documentation:** Interactive Swagger UI at `/swagger-ui.html`

### Running Tests

```powershell
cd backend
mvn test
```

Uses H2 in-memory database — no running containers required.

### Configuration

Edit [`backend/src/main/resources/application.properties`](backend/src/main/resources/application.properties):
- Database credentials (username, password)
- JWT secret key
- Server port
- Swagger base path

### API Endpoints

| Resource | Methods | Role Required |
|----------|---------|---------------|
| `/api/auth/*` | POST | Public |
| `/api/facilities` | GET | User |
| `/api/bookings/*` | GET, POST | User |
| `/api/groups/*` | GET, POST | User |
| `/api/incidents/*` | GET, POST | User |
| `/api/admin/*` | POST, PUT, DELETE | Admin |

---

## Desktop Client

JavaFX desktop application with separate interfaces for regular users and administrators. Built with Java 17, Maven, and FXML.

### Features

**User Interface:**
- Dashboard with metrics and upcoming bookings
- My Bookings — create and manage reservations
- My Groups — browse and create/join groups
- Profile — view and edit user information
- Incident reporting from past bookings

**Admin Backoffice:**
- Dashboard with statistics and recent activity
- Users management — list, search, filter, toggle active status
- Facilities management — create, update, list facilities
- Bookings management — view all bookings, manage status
- Groups management — view and administer groups
- Incidents management — create, track, and resolve incidents
- Calendar view — year-based facility booking calendar

### Running

```bash
cd desktop
mvn javafx:run
```

### Configuration

Update the server URL in [`config.properties`](config.properties):

```properties
api.base.url=http://<server-ip>:8080
```

### Architecture

- **Controller-based:** HomeController (users), BackofficeController (admins)
- **Async operations:** Non-blocking API calls with async execution
- **Responsive UI:** Sidebar collapses on narrow screens
- **Dark theme:** NexusBooking custom styling with FontAwesome icons

See [`desktop/README.md`](desktop/README.md) for detailed desktop documentation.

---

## Mobile Client

Kotlin + Jetpack Compose Android application with responsive Material Design 3 UI. Features both user and admin interfaces.

### Features

**User Dashboard:**
- Login/register with JWT authentication
- Browse facilities (name, type, capacity, location)
- Create and manage bookings with date/time picker
- View personal booking history
- Browse and join collaborative groups
- Report incidents from past bookings

**Admin Backoffice:**
- User management with active/inactive toggling
- Facility creation and management
- Full booking administration
- Group management and oversight
- Incident tracking and resolution

**Technical:**
- Offline token persistence using DataStore
- Reactive StateFlow/Flow for UI updates
- Error handling with Resource wrapper
- Dependency injection with Hilt
- Type-safe API calls with Retrofit

### Running

**Setup base URL:**
- Emulator: `http://10.0.2.2:8080` (default — no changes needed)
- Device: Update in `ApiService.kt` to your server's IP

**Build and run:**

```bash
cd mobile
./gradlew installDebug
# Or from Android Studio: Run > Run 'app'
```

### Architecture

```
app/src/main/java/.../
├── ui/login, home, profile, admin, navigation
├── data/local (DataStore), remote (Retrofit), repository
├── di (Hilt modules)
└── util (helpers, Resource wrapper)
```

See [`mobile/README.md`](mobile/README.md) for complete mobile documentation.

---

## Database Credentials

Both Docker Compose and backend configuration use these defaults:

| Setting  | Value          |
|----------|----------------|
| Host     | `localhost`    |
| Port     | `5432`         |
| Database | `nexusbooking` |
| Username | `postgres`     |
| Password | `postgres`     |

**To change:** Update both `database/docker-compose.yml` and `backend/src/main/resources/application.properties`.

---

## Seed Accounts

Created by `.\db.cmd seed`:

| Email                    | Password   | Role  |
|--------------------------|------------|-------|
| admin@nexusbooking.com   | Admin1234! | ADMIN |
| user@nexusbooking.com    | User1234!  | USER  |

Additional demo users available — see `DEMO_DATA_README.md` or the mobile/desktop clients after login.

---

## Project Structure

---

## Project Structure

```
NexusBooking/
├── backend/                              # Spring Boot REST API
│   ├── src/main/resources/
│   │   ├── application.properties        # Database, JWT, Swagger config
│   │   └── db/migration/                 # Flyway SQL migrations
│   ├── src/main/java/../
│   │   ├── controller/                   # REST endpoints
│   │   ├── service/                      # Business logic
│   │   ├── repository/                   # Data access (JPA)
│   │   └── model/                        # Entities, DTOs
│   └── pom.xml
│
├── database/                             # Database infrastructure
│   ├── docker-compose.yml                # Postgres + pgAdmin services
│   ├── db.cmd / db.ps1                   # Database management scripts
│   └── scripts/
│       ├── V*.sql                        # Flyway schema migrations
│       ├── seed_data.sql                 # Demo dataset
│       ├── clear_data.sql                # Truncate all tables
│       └── drop_tables.sql               # Drop all objects
│
├── desktop/                              # JavaFX desktop client
│   ├── src/main/resources/fxml/          # FXML UI definitions
│   ├── src/main/resources/css/           # Dark theme styling
│   ├── src/main/java/../
│   │   ├── controller/                   # HomeController, BackofficeController
│   │   ├── api/                          # ApiClient for REST calls
│   │   └── model/                        # Response DTOs
│   └── pom.xml
│
└── mobile/                               # Android Jetpack Compose app
    └── app/
        ├── src/main/res/                 # Strings, colors, icons
        ├── src/main/java/.../
        │   ├── ui/
        │   │   ├── login/                # LoginScreen, LoginViewModel
        │   │   ├── home/                 # HomeScreen (user dashboard)
        │   │   ├── profile/              # ProfileScreen
        │   │   ├── admin/                # AdminScreen (backoffice)
        │   │   ├── splash/               # SplashScreen (session resume)
        │   │   ├── navigation/           # NavGraph, navigation logic
        │   │   └── theme/                # Material Design 3 theme
        │   ├── data/
        │   │   ├── local/                # DataStore (JWT token storage)
        │   │   ├── remote/               # Retrofit API service + DTOs
        │   │   └── repository/           # Auth & User repositories
        │   ├── di/                       # Hilt dependency injection
        │   └── util/                     # Resource wrapper, extensions
        └── build.gradle.kts
```

---

## Connecting the Pieces

1. **Start database:** `.\database\db.cmd start` — Postgres + pgAdmin running
2. **Load data:** `.\database\db.cmd seed` — Admin + user accounts created
3. **Run backend:** `cd backend && mvn spring-boot:run` — API listening on port 8080
4. **Run desktop:** `cd desktop && mvn javafx:run` — JavaFX client (use seed accounts to login)
5. **Run mobile:** `cd mobile && ./gradlew installDebug` — Android app on emulator/device

---

## Common Tasks

### View database structure

```powershell
cd database
.\db.cmd psql          # Opens interactive SQL shell
```

Then browse tables:

```sql
\dt                    # List all tables
SELECT * FROM users;   # View users table
SELECT * FROM bookings ORDER BY id DESC LIMIT 10;
```

### Add mock data

```powershell
cd database
.\db.cmd clear         # Remove existing data
.\db.cmd seed          # Load fresh demo dataset
```

### Rebuild everything from scratch

```powershell
cd database
.\db.cmd reset         # Drop all → recreate schema → apply migrations
.\db.cmd seed          # Load demo data
```

### Run only backend tests

```powershell
cd backend
mvn clean test         # Runs on H2 in-memory DB
```

---

## Documentation

- **Backend:** See [`backend/README.md`](backend/README.md)
- **Database:** See [`database/README.md`](database/README.md)
- **Desktop:** See [`desktop/README.md`](desktop/README.md)
- **Mobile:** See [`mobile/README.md`](mobile/README.md)
- **Demo guide:** See [`DEMO_DATA_README.md`](DEMO_DATA_README.md)
