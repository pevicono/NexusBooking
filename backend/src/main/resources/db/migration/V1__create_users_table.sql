-- =============================================================
-- NexusBooking – Flyway Migration V1
-- Managed by Flyway; do NOT edit after first deployment.
-- =============================================================

CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL       PRIMARY KEY,
    email       VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(100)    NOT NULL,
    role        VARCHAR(20)     NOT NULL DEFAULT 'USER'
                                CHECK (role IN ('USER', 'ADMIN')),
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
