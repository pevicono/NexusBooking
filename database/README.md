# NexusBooking – Database

This folder contains everything needed to spin up and manage the PostgreSQL database for NexusBooking.

## Stack

| Service  | Image                | Port  | Purpose                     |
|----------|----------------------|-------|-----------------------------|
| postgres | postgres:16-alpine   | 5432  | Primary application database|
| pgadmin  | dpage/pgadmin4       | 5050  | Web-based DB admin UI        |

---

## Quick start

> **Prerequisite:** Docker Desktop must be running.

```powershell
# From the /database directory

# 1. Start Postgres + pgAdmin
.\db.cmd start

# 2. The Spring Boot app will run Flyway migrations automatically on startup
#    (backend/src/main/resources/db/migration/)
```

> **Note:** Use `db.cmd` instead of `db.ps1` directly. The `.cmd` wrapper calls the PowerShell script with `-ExecutionPolicy Bypass`, so it works out of the box for all users regardless of their system's PowerShell execution policy. No manual policy changes required.

---

## db.cmd / db.ps1 – all commands

| Command  | Description                                              |
|----------|----------------------------------------------------------|
| `start`  | `docker compose up -d` – starts Postgres and pgAdmin     |
| `stop`   | `docker compose stop` – stops containers (data kept)     |
| `reset`  | Drops all tables then re-runs the create script          |
| `clear`  | Truncates all tables, resets sequences (schema kept)     |
| `seed`   | Inserts the default admin + sample user                  |
| `psql`   | Opens an interactive `psql` session                      |
| `logs`   | Tails the Postgres container logs                        |
| `status` | Shows container health status                            |

```powershell
.\db.cmd reset   # wipe schema and recreate
.\db.cmd clear   # delete rows only
.\db.cmd seed    # load sample data
.\db.cmd psql    # open SQL shell
```

---

## Scripts

| File                             | Purpose                                         |
|----------------------------------|-------------------------------------------------|
| `scripts/V1__create_users_table.sql` | Create the `users` table + trigger         |
| `scripts/seed_data.sql`          | Insert default admin and sample user            |
| `scripts/clear_data.sql`         | TRUNCATE all tables (reset sequences)           |
| `scripts/drop_tables.sql`        | DROP all tables and functions                   |

The `V1__` prefix mirrors Flyway's naming convention so the same file serves both Docker init and manual runs.

---

## Flyway (Java-side migrations)

Flyway is configured in the Spring Boot app. On startup it:
1. Connects to the database.
2. Checks the `flyway_schema_history` table.
3. Applies any pending `V*.sql` scripts from `backend/src/main/resources/db/migration/`.

To add a new migration, create `V2__your_description.sql` in that folder.  
**Never edit or delete an already-applied migration file.**

---

## pgAdmin

Open [http://localhost:5050](http://localhost:5050) after running `.\db.ps1 start`.

| Field    | Value                        |
|----------|------------------------------|
| Email    | admin@nexusbooking.com       |
| Password | admin                        |

Add a new server connection:
- Host: `postgres` (container name) or `host.docker.internal`
- Port: `5432`
- Database: `nexusbooking`
- Username: `postgres`
- Password: `postgres`

---

## Seed users

| Email                       | Password    | Role  |
|-----------------------------|-------------|-------|
| admin@nexusbooking.com      | Admin1234!  | ADMIN |
| user@nexusbooking.com       | User1234!   | USER  |
