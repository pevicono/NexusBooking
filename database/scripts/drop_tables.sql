-- =============================================================
-- NexusBooking – Drop All Tables
-- Drops every application table and supporting objects.
-- WARNING: this is destructive and irreversible.
-- =============================================================

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
DROP TRIGGER IF EXISTS trg_facilities_updated_at ON facilities;
DROP TRIGGER IF EXISTS trg_groups_updated_at ON groups;
DROP TRIGGER IF EXISTS trg_bookings_updated_at ON bookings;
DROP TRIGGER IF EXISTS trg_incidents_updated_at ON incidents;

DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS group_members CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS incidents CASCADE;
DROP TABLE IF EXISTS facilities CASCADE;
DROP FUNCTION IF EXISTS set_updated_at();
DROP TABLE IF EXISTS users CASCADE;
