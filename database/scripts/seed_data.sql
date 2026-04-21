-- =============================================================
-- NexusBooking – Seed Data
-- Inserts sample data for the full domain model.
--
-- Passwords are BCrypt hashes (cost 10):
--   admin@nexusbooking.com → Admin1234!
--   user@nexusbooking.com  → User1234!
-- =============================================================

INSERT INTO users (email, password, role, active) VALUES
    ('admin@nexusbooking.com',
    '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS',
     'ADMIN',
     TRUE),
    ('user@nexusbooking.com',
    '$2a$10$ikJnqUb5MRmkH2jxy8FS.OVJ1WekguxQ4OCJD7.kxLVgKXOFqsSE.',
     'USER',
     TRUE),
    ('user2@nexusbooking.com',
    '$2a$10$ikJnqUb5MRmkH2jxy8FS.OVJ1WekguxQ4OCJD7.kxLVgKXOFqsSE.',
     'USER',
     TRUE)
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
    role = EXCLUDED.role,
    active = EXCLUDED.active;

INSERT INTO facilities (name, description, type, capacity, location, status)
SELECT * FROM (
    VALUES
        ('Pista Padel 1', 'Pista exterior de padel', 'PADEL', 4, 'Zona Esportiva A', 'ACTIVE'),
        ('Pista Futbol 5', 'Camp de futbol 5 amb gespa artificial', 'FUTBOL', 10, 'Zona Esportiva B', 'ACTIVE'),
        ('Sala Gimnas', 'Sala d''entrenament funcional', 'GIMNAS', 25, 'Edifici Principal', 'ACTIVE'),
        ('Sala Conferencies', 'Sala equipada per presentacions', 'CONFERENCIA', 60, 'Edifici Principal', 'MAINTENANCE')
) AS data(name, description, type, capacity, location, status)
WHERE NOT EXISTS (
    SELECT 1 FROM facilities f WHERE f.name = data.name
);

INSERT INTO groups (name, description, owner_id)
SELECT
    'Padel Dilluns',
    'Grup setmanal per jugar a padel',
    u.id
FROM users u
WHERE u.email = 'user@nexusbooking.com'
AND NOT EXISTS (
    SELECT 1 FROM groups g WHERE g.name = 'Padel Dilluns' AND g.owner_id = u.id
);

INSERT INTO group_members (group_id, user_id, role)
SELECT g.id, u.id,
       CASE WHEN u.email = 'user@nexusbooking.com' THEN 'OWNER' ELSE 'MEMBER' END
FROM groups g
JOIN users u ON u.email IN ('user@nexusbooking.com', 'user2@nexusbooking.com')
WHERE g.name = 'Padel Dilluns'
AND NOT EXISTS (
    SELECT 1 FROM group_members gm WHERE gm.group_id = g.id AND gm.user_id = u.id
);

INSERT INTO bookings (user_id, facility_id, group_id, start_time, end_time, status, notes)
SELECT
    u.id,
    f.id,
    g.id,
    TIMESTAMP '2030-05-01 18:00:00',
    TIMESTAMP '2030-05-01 19:00:00',
    'CONFIRMED',
    'Partit de padel setmanal'
FROM users u
JOIN facilities f ON f.name = 'Pista Padel 1'
LEFT JOIN groups g ON g.name = 'Padel Dilluns'
WHERE u.email = 'user@nexusbooking.com'
AND NOT EXISTS (
    SELECT 1
    FROM bookings b
    WHERE b.user_id = u.id
      AND b.facility_id = f.id
      AND b.start_time = TIMESTAMP '2030-05-01 18:00:00'
);

INSERT INTO incidents (facility_id, reported_by, title, description, status)
SELECT
    f.id,
    a.id,
    'Llum vestidors fosa',
    'Cal reemplaçar la llum principal dels vestidors',
    'OPEN'
FROM facilities f
JOIN users a ON a.email = 'admin@nexusbooking.com'
WHERE f.name = 'Pista Futbol 5'
AND NOT EXISTS (
    SELECT 1 FROM incidents i WHERE i.title = 'Llum vestidors fosa'
);
