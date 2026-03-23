-- =============================================================
-- NexusBooking – Seed Data
-- Inserts a default admin user and a sample regular user.
--
-- Passwords are BCrypt hashes (cost 10):
--   admin@nexusbooking.com → Admin1234!
--   user@nexusbooking.com  → User1234!
-- =============================================================

INSERT INTO users (email, password, role) VALUES
    ('admin@nexusbooking.com',
     '$2a$10$7EqJtq98hPqEX7fNZaFWoOa9wPMEZB5i.4Z3sJp3v2k9T8QJvBaW6',
     'ADMIN'),
    ('user@nexusbooking.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
     'USER')
ON CONFLICT (email) DO NOTHING;
