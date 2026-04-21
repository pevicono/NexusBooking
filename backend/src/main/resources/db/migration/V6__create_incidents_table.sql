-- =============================================================
-- NexusBooking – Flyway Migration V6
-- Creates incidents table.
-- =============================================================

CREATE TABLE IF NOT EXISTS incidents (
    id           BIGSERIAL    PRIMARY KEY,
    facility_id  BIGINT       REFERENCES facilities(id) ON DELETE SET NULL,
    reported_by  BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN'
                              CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

DROP TRIGGER IF EXISTS trg_incidents_updated_at ON incidents;
CREATE TRIGGER trg_incidents_updated_at
    BEFORE UPDATE ON incidents
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
