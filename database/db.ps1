# NexusBooking – Database Management Scripts
# Helper PowerShell scripts for working with the Postgres container.

param(
    [Parameter(Position=0, Mandatory=$true)]
    [ValidateSet("start","stop","reset","clear","seed","psql","logs","status")]
    [string]$Command
)

$DB_NAME     = "nexusbooking"
$DB_USER     = "postgres"
$SCRIPTS_DIR = Join-Path $PSScriptRoot "scripts"
$BACKEND_MIGRATION_DIR = Join-Path $PSScriptRoot "..\backend\src\main\resources\db\migration"

function EnsureDockerAvailable() {
    docker info *> $null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker daemon is not running. Start Docker Desktop and retry."
    }
}

function GetPostgresContainerId() {
    $containerId = docker compose ps -q postgres
    if (-not $containerId) {
        throw "Postgres container is not running. Run '.\\db.cmd start' first."
    }
    return $containerId.Trim()
}

function RunSql([string]$file) {
    EnsureDockerAvailable
    $containerId = GetPostgresContainerId

    # Using docker exec to run SQL inside the container
    Get-Content $file | docker exec -i $containerId psql -v ON_ERROR_STOP=1 -U $DB_USER -d $DB_NAME
    if ($LASTEXITCODE -ne 0) {
        throw "SQL execution failed for file: $file"
    }
}

switch ($Command) {

    "start" {
        EnsureDockerAvailable
        Write-Host "Starting Postgres and pgAdmin containers..." -ForegroundColor Cyan
        docker compose up -d
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to start containers."
        }
        Write-Host ""
        Write-Host "  Postgres  -> localhost:5432  (db: $DB_NAME, user: $DB_USER)" -ForegroundColor Green
        Write-Host "  pgAdmin   -> http://localhost:5050  (admin@nexusbooking.com / admin)" -ForegroundColor Green
    }

    "stop" {
        EnsureDockerAvailable
        Write-Host "Stopping containers..." -ForegroundColor Cyan
        docker compose stop
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to stop containers."
        }
    }

    "reset" {
        Write-Host "Resetting database (drop + recreate schema)..." -ForegroundColor Yellow
        RunSql (Join-Path $SCRIPTS_DIR "drop_tables.sql")

        if (-not (Test-Path $BACKEND_MIGRATION_DIR)) {
            throw "Could not find backend migrations folder: $BACKEND_MIGRATION_DIR"
        }

        $migrations = Get-ChildItem -Path $BACKEND_MIGRATION_DIR -Filter "V*.sql" |
            Sort-Object Name

        foreach ($migration in $migrations) {
            Write-Host "Applying migration $($migration.Name)..." -ForegroundColor DarkGray
            RunSql $migration.FullName
        }

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
        EnsureDockerAvailable
        Write-Host "Opening psql session for database '$DB_NAME'..." -ForegroundColor Cyan
        #Open interactive psql inside the container
        docker exec -it $(GetPostgresContainerId) psql -U $DB_USER -d $DB_NAME
    }

    "logs" {
        EnsureDockerAvailable
        docker compose logs -f postgres
    }

    "status" {
        EnsureDockerAvailable
        docker compose ps
    }
}