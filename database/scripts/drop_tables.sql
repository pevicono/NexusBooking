-- =============================================================
-- NexusBooking – Drop All Tables
-- Drops every application table and supporting objects.
-- WARNING: this is destructive and irreversible.
-- =============================================================

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
DROP FUNCTION IF EXISTS set_updated_at();
DROP TABLE IF EXISTS users CASCADE;
