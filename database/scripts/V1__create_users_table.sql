-- =============================================================
-- NexusBooking – Initial Schema
-- Migration: V1 – Create users table
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

-- Trigger to keep updated_at current on every row update
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
