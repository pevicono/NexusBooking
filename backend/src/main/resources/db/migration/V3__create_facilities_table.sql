-- =============================================================
-- NexusBooking – Flyway Migration V3
-- Creates facilities table.
-- =============================================================

CREATE TABLE IF NOT EXISTS facilities (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    description TEXT,
    type        VARCHAR(50)     NOT NULL,
    capacity    INT,
    location    VARCHAR(200),
    status      VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE'
                                CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')),
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

DROP TRIGGER IF EXISTS trg_facilities_updated_at ON facilities;
CREATE TRIGGER trg_facilities_updated_at
    BEFORE UPDATE ON facilities
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
