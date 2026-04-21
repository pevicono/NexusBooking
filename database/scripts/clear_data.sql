-- =============================================================
-- NexusBooking – Clear Data
-- Removes all rows from every table while preserving the schema
-- and resetting auto-increment sequences.
-- =============================================================

TRUNCATE TABLE
	bookings,
	group_members,
	groups,
	incidents,
	facilities,
	users
RESTART IDENTITY CASCADE;
