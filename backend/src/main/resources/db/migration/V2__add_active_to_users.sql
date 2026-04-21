-- =============================================================
-- NexusBooking – Flyway Migration V2
-- Adds active flag to users for admin disable/enable.
-- =============================================================

ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;
