# NexusBooking – Database Management Scripts
# Helper PowerShell scripts for working with the Postgres container.

param(
    [Parameter(Position=0, Mandatory=$true)]
    [ValidateSet("start","stop","reset","clear","seed","psql","logs","status")]
    [string]$Command
)

$DB_HOST     = "localhost"
$DB_PORT     = "5432"
$DB_NAME     = "nexusbooking"
$DB_USER     = "postgres"
$DB_PASSWORD = "postgres"
$SCRIPTS_DIR = Join-Path $PSScriptRoot "scripts"

function RunSql([string]$file) {
    # Using docker exec to run SQL inside the container
    Get-Content $file | docker exec -i $(docker compose ps -q postgres) psql -U $DB_USER -d $DB_NAME
}

switch ($Command) {

    "start" {
        Write-Host "Starting Postgres and pgAdmin containers..." -ForegroundColor Cyan
        docker compose up -d
        Write-Host ""
        Write-Host "  Postgres  -> localhost:5432  (db: $DB_NAME, user: $DB_USER)" -ForegroundColor Green
        Write-Host "  pgAdmin   -> http://localhost:5050  (admin@nexusbooking.com / admin)" -ForegroundColor Green
    }

    "stop" {
        Write-Host "Stopping containers..." -ForegroundColor Cyan
        docker compose stop
    }

    "reset" {
        Write-Host "Resetting database (drop + recreate schema)..." -ForegroundColor Yellow
        RunSql (Join-Path $SCRIPTS_DIR "drop_tables.sql")
        RunSql (Join-Path $SCRIPTS_DIR "V1__create_users_table.sql")
        Write-Host "Database reset complete." -ForegroundColor Green
    }

    "clear" {
        Write-Host "Clearing all data (schema preserved)..." -ForegroundColor Yellow
        RunSql (Join-Path $SCRIPTS_DIR "clear_data.sql")
        Write-Host "All rows removed." -ForegroundColor Green
    }

    "seed" {
        Write-Host "Seeding database with sample data..." -ForegroundColor Cyan
        RunSql (Join-Path $SCRIPTS_DIR "seed_data.sql")
        Write-Host "Seed data inserted." -ForegroundColor Green
    }

    "psql" {
        Write-Host "Opening psql session for database '$DB_NAME'..." -ForegroundColor Cyan
        #Open interactive psql inside the container
        docker exec -it $(docker compose ps -q postgres) psql -U $DB_USER -d $DB_NAME
    }

    "logs" {
        docker compose logs -f postgres
    }

    "status" {
        docker compose ps
    }
}