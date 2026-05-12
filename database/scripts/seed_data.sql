-- =============================================================
-- NexusBooking – Demo Seed Data
-- Demonstrates real-world use cases for investor pitch
-- =============================================================

-- USERS: 15 users across different organizations
-- Password for all users except user@nexusbooking.com: Admin1234! (BCrypt strength 12)
-- Password for user@nexusbooking.com: User1234! (BCrypt strength 12)
INSERT INTO users (email, password, role, created_at) VALUES
  ('admin@nexusbooking.com', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'ADMIN', NOW()),
  ('maria.torres@univ.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('joan.ferran@univ.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('silvia.gomez@univ.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('carles.ruiz@tech-corp.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('anna.vidal@tech-corp.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('pau.lopez@tech-corp.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('xavier.sole@coworking.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('clara.munoz@coworking.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('andreu.carrio@coworking.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('helena.costa@learning.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('miguel.santos@learning.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('user@nexusbooking.com', '$2a$10$ikJnqUb5MRmkH2jxy8FS.OVJ1WekguxQ4OCJD7.kxLVgKXOFqsSE.', 'USER', NOW()),
  ('laia.puig@alumni.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW()),
  ('oriol.planes@startup.local', '$2a$10$lARW1cCCRaFhwaUy/GOG9eQjYcOzDWdkRpcEUNoi.huefG4oW8JdS', 'USER', NOW());

-- FACILITIES: Real-world resource types
-- University classrooms
INSERT INTO facilities (name, description, type, capacity, location, status) VALUES
  ('Aula 101 – Informàtica', 'Sala amb 25 ordinadors, projectió HD, connexió dual-screen', 'CLASSROOM', 25, 'Edifici Principal, Planta 1', 'ACTIVE'),
  ('Aula 201 – Seminari', 'Sala de discussió amb taula ronda, capacitat íntima', 'CLASSROOM', 15, 'Edifici Principal, Planta 2', 'ACTIVE'),
  ('Laboratori de Bio', 'Laboratori equipat amb microscopis i materials diversos', 'LABORATORY', 30, 'Edifici de Ciències, Planta 0', 'ACTIVE'),
  ('Sala d''Estudi Col·lectiu', 'Espai quiet amb pissarres blanques per col·laboració', 'STUDY_SPACE', 10, 'Biblioteca Central', 'ACTIVE'),

-- Tech corporation meeting rooms
  ('Sala Turing', 'Sala de reunió premium, taula de vidre, videoconferència full HD', 'MEETING_ROOM', 12, 'Tech Campus, Edifici A, Planta 3', 'ACTIVE'),
  ('Sala de Desenvolupament', 'Sala tècnica per dailies i planning, TV interactiva', 'MEETING_ROOM', 20, 'Tech Campus, Edifici B, Planta 2', 'ACTIVE'),
  ('Focus Room – Desenvolupament Backend', 'Sala sense distraccions per pair programming', 'FOCUS_ROOM', 4, 'Tech Campus, Edifici B, Planta 1', 'ACTIVE'),

-- Coworking spaces
  ('Estació de Treball – Flex Open', 'Open space amb 50 PCs, velocitat de connexió 1Gbps', 'HOT_DESK', 50, 'CoWork Center, Planta 1', 'ACTIVE'),
  ('Private Office – Suite A', 'Oficina privada amb 6 llocs de treball, clima controlat', 'PRIVATE_OFFICE', 6, 'CoWork Center, Planta 2', 'ACTIVE'),
  ('Event Space – Grand', 'Espai multiusos per pitches, presentacions, networking', 'EVENT_SPACE', 100, 'CoWork Center, Planta 3', 'ACTIVE'),

-- Learning center
  ('Aula de Formació – Python Basics', 'Aula per curs de programació, 20 ordinadors Intel i7', 'TRAINING_ROOM', 20, 'Learning Hub, Zona Est', 'ACTIVE'),
  ('Aula de Formació – Cloud DevOps', 'Aula amb accés a servidors cloud, certificada AWS', 'TRAINING_ROOM', 15, 'Learning Hub, Zona Oest', 'ACTIVE');

-- GROUPS: Real collaborative teams
-- University group
INSERT INTO groups (name, description, owner_id, created_at) VALUES
  ('Equip de Projecte AI', 'Grup de recerca en intel·ligència artificial, Universitat de Barcelona', 1, NOW() - INTERVAL '3 months'),
  ('Seminari de Bioinformàtica', 'Grups de 3-4 alumnes estudiant seqüenciació genètica', 1, NOW() - INTERVAL '2 months'),

-- Tech company group
  ('Backend Squad – Booking Engine', 'Equip de 8 developers treballant en NexusBooking', 2, NOW() - INTERVAL '4 months'),
  ('Frontend Team – Mobile & Desktop', 'Equip de UI/UX i mobile, 5 persones', 2, NOW() - INTERVAL '4 months'),

-- Coworking group
  ('Tech Startup Founders Network', 'Xarxa de entrepreneurs i fundadors basats al coworking', 3, NOW() - INTERVAL '1 month'),
  ('Freelancers Collaboration Hub', 'Community de freelancers que col·laboren en projectes', 3, NOW() - INTERVAL '3 weeks'),

-- Learning center group
  ('Bootcamp Python 2026 – Cohort A', 'Grup de 20 estudiants d''un bootcamp intensiu de Python', 4, NOW() - INTERVAL '2 weeks'),
  ('Bootcamp Cloud DevOps – Cohort B', 'Grup de 15 estudiants de formació en cloud i DevOps', 4, NOW() - INTERVAL '1 week'),

-- user@nexusbooking.com personal group
  ('Grup Padel Dimarts', 'Grup setmanal de padel al CoWork Center', 13, NOW() - INTERVAL '5 weeks');

-- GROUP MEMBERS: Add users to groups
INSERT INTO group_members (group_id, user_id, role, joined_at) VALUES
  -- Equip AI (owner: Maria Torres)
  (1, 2, 'OWNER', NOW() - INTERVAL '3 months'),
  (1, 3, 'MEMBER', NOW() - INTERVAL '3 months'),
  (1, 4, 'MEMBER', NOW() - INTERVAL '2.5 months'),
  
  -- Seminari Bioinfor (owner: Joan Ferran)
  (2, 3, 'OWNER', NOW() - INTERVAL '2 months'),
  (2, 4, 'MEMBER', NOW() - INTERVAL '2 months'),
  
  -- Backend Squad (owner: Carles Ruiz)
  (3, 5, 'OWNER', NOW() - INTERVAL '4 months'),
  (3, 6, 'MEMBER', NOW() - INTERVAL '4 months'),
  (3, 7, 'MEMBER', NOW() - INTERVAL '3.5 months'),
  
  -- Frontend Team (owner: Anna Vidal)
  (4, 6, 'OWNER', NOW() - INTERVAL '4 months'),
  (4, 7, 'MEMBER', NOW() - INTERVAL '4 months'),
  (4, 5, 'MEMBER', NOW() - INTERVAL '3 months'),
  
  -- Tech Startup Founders (owner: Xavier Sole)
  (5, 8, 'OWNER', NOW() - INTERVAL '1 month'),
  (5, 9, 'MEMBER', NOW() - INTERVAL '3 weeks'),
  (5, 10, 'MEMBER', NOW() - INTERVAL '2 weeks'),
  (5, 6, 'MEMBER', NOW() - INTERVAL '2 weeks'),
  
  -- Freelancers Hub (owner: Clara Munoz)
  (6, 9, 'OWNER', NOW() - INTERVAL '3 weeks'),
  (6, 10, 'MEMBER', NOW() - INTERVAL '3 weeks'),
  (6, 8, 'MEMBER', NOW() - INTERVAL '2 weeks'),
  
  -- Bootcamp Python (owner: Helena Costa)
  (7, 11, 'OWNER', NOW() - INTERVAL '2 weeks'),
  (7, 12, 'MEMBER', NOW() - INTERVAL '2 weeks'),
  
  -- Bootcamp Cloud (owner: Miguel Santos)
  (8, 12, 'OWNER', NOW() - INTERVAL '1 week'),
  (8, 11, 'MEMBER', NOW() - INTERVAL '1 week'),

  -- Grup Padel Dimarts (owner: user@nexusbooking.com = id 13)
  (9, 13, 'OWNER', NOW() - INTERVAL '5 weeks'),
  (9, 8,  'MEMBER', NOW() - INTERVAL '4 weeks'),
  (9, 9,  'MEMBER', NOW() - INTERVAL '3 weeks'),

  -- Also a member of Tech Startup Founders Network
  (5, 13, 'MEMBER', NOW() - INTERVAL '2 weeks');

-- BOOKINGS: Real reservation patterns
-- University bookings
INSERT INTO bookings (user_id, facility_id, group_id, start_time, end_time, status, notes) VALUES
  -- AI Research group using classrooms
  (2, 1, 1, NOW() + INTERVAL '1 day' + INTERVAL '09:00', 
           NOW() + INTERVAL '1 day' + INTERVAL '12:00', 'CONFIRMED', 'Session de treball col·lectiva setmanal'),
  (2, 4, 1, NOW() + INTERVAL '2 days' + INTERVAL '10:00',
           NOW() + INTERVAL '2 days' + INTERVAL '12:00', 'CONFIRMED', 'Discussió de resultats preliminars'),
  (3, 3, 2, NOW() + INTERVAL '1 day' + INTERVAL '14:00',
           NOW() + INTERVAL '1 day' + INTERVAL '17:00', 'CONFIRMED', 'Laboratori pràctic de seqüenciació'),
  
  -- Tech company bookings
  (5, 6, 3, NOW() + INTERVAL '1 day' + INTERVAL '09:30',
           NOW() + INTERVAL '1 day' + INTERVAL '10:30', 'CONFIRMED', 'Daily scrum – Backend Squad'),
  (5, 6, 3, NOW() + INTERVAL '3 days' + INTERVAL '15:00',
           NOW() + INTERVAL '3 days' + INTERVAL '16:30', 'CONFIRMED', 'Reunió de planning per sprint 12'),
  (6, 7, 4, NOW() + INTERVAL '2 days' + INTERVAL '11:00',
           NOW() + INTERVAL '2 days' + INTERVAL '12:00', 'CONFIRMED', 'Design review – sesió de feedback UI'),
  (7, 5, 3, NOW() + INTERVAL '5 days' + INTERVAL '14:00',
           NOW() + INTERVAL '5 days' + INTERVAL '16:00', 'CONFIRMED', 'Sessió de pair programming – implementació nova API'),
  
  -- Coworking bookings
  (8, 9, 5, NOW() + INTERVAL '4 days' + INTERVAL '18:00',
           NOW() + INTERVAL '4 days' + INTERVAL '20:00', 'CONFIRMED', 'Tech Founders Meetup mensual'),
  (9, 8, 6, NOW() + INTERVAL '1 day' + INTERVAL '09:00',
           NOW() + INTERVAL '1 day' + INTERVAL '17:00', 'CONFIRMED', 'Dia complet de co-working amb projectes creuats'),
  (10, 9, 5, NOW() + INTERVAL '7 days' + INTERVAL '19:00',
           NOW() + INTERVAL '7 days' + INTERVAL '21:00', 'CONFIRMED', 'Presentació de startup en pitch night'),
  
  -- Learning center bookings
  (11, 12, 7, NOW() + INTERVAL '1 day' + INTERVAL '09:00',
            NOW() + INTERVAL '1 day' + INTERVAL '12:30', 'CONFIRMED', 'Bootcamp Python – Classes dia 1 (variables, loops, funcions)'),
  (12, 12, 7, NOW() + INTERVAL '2 days' + INTERVAL '09:00',
            NOW() + INTERVAL '2 days' + INTERVAL '12:30', 'CONFIRMED', 'Bootcamp Python – Classes dia 2 (OOP i decoradors)'),
  (11, 12, 8, NOW() + INTERVAL '3 days' + INTERVAL '09:00',
            NOW() + INTERVAL '3 days' + INTERVAL '13:00', 'CONFIRMED', 'Bootcamp Cloud – Kubernetes basics i deployment'),
  (12, 12, 8, NOW() + INTERVAL '4 days' + INTERVAL '09:00',
            NOW() + INTERVAL '4 days' + INTERVAL '13:00', 'CONFIRMED', 'Bootcamp Cloud – Terraform i Infrastructure as Code'),
  
  -- Completed bookings (historical)
  (2, 1, 1, NOW() - INTERVAL '3 days' + INTERVAL '10:00',
           NOW() - INTERVAL '3 days' + INTERVAL '11:30', 'COMPLETED', 'Sessió anterior completada'),
  (5, 6, 3, NOW() - INTERVAL '2 days' + INTERVAL '09:30',
           NOW() - INTERVAL '2 days' + INTERVAL '10:30', 'COMPLETED', 'Daily anterior completat'),
  (9, 8, 6, NOW() - INTERVAL '1 day' + INTERVAL '08:00',
           NOW() - INTERVAL '1 day' + INTERVAL '18:00', 'COMPLETED', 'Session de co-working productiu'),

  -- user@nexusbooking.com bookings
  (13, 8, 9, NOW() + INTERVAL '2 days' + INTERVAL '10:00',
            NOW() + INTERVAL '2 days' + INTERVAL '11:00', 'CONFIRMED', 'Reserva de hot desk per treballar en projecte personal'),
  (13, 9, 9, NOW() + INTERVAL '9 days' + INTERVAL '18:00',
            NOW() + INTERVAL '9 days' + INTERVAL '20:00', 'CONFIRMED', 'Partida de padel setmanal – grup dimarts'),
  (13, 4, NULL, NOW() + INTERVAL '3 days' + INTERVAL '16:00',
            NOW() + INTERVAL '3 days' + INTERVAL '18:00', 'CONFIRMED', 'Estudi personal per preparar entrevista tecnica'),
  (13, 8, 9, NOW() - INTERVAL '5 days' + INTERVAL '10:00',
            NOW() - INTERVAL '5 days' + INTERVAL '11:00', 'COMPLETED', 'Sessió de treball completada'),
  (13, 9, 9, NOW() - INTERVAL '7 days' + INTERVAL '18:00',
            NOW() - INTERVAL '7 days' + INTERVAL '20:00', 'COMPLETED', 'Partida de padel completada'),
  (13, 5, 9, NOW() + INTERVAL '12 days' + INTERVAL '19:00',
            NOW() + INTERVAL '12 days' + INTERVAL '21:00', 'CANCELLED', 'Cancel·lada per indisponibilitat de la pista'),

  -- Cancelled bookings
  (6, 5, 4, NOW() + INTERVAL '2 days' + INTERVAL '16:00',
           NOW() + INTERVAL '2 days' + INTERVAL '17:00', 'CANCELLED', 'Cancel·lada per indisponibilitat del ponent'),
  (8, 10, 5, NOW() + INTERVAL '6 days' + INTERVAL '19:00',
           NOW() + INTERVAL '6 days' + INTERVAL '21:00', 'CANCELLED', 'Esdeveniment ajornat per baixa assistència'),
  (11, 11, 7, NOW() + INTERVAL '5 days' + INTERVAL '09:00',
            NOW() + INTERVAL '5 days' + INTERVAL '12:00', 'CANCELLED', 'Sessió reprogramada per manteniment d''aula');

-- INCIDENTS: Mixed operational states for demos
INSERT INTO incidents (facility_id, reported_by, title, description, status, created_at) VALUES
  (3, 2, 'Microscopi fora de servei', 'Un microscopi no enfoca correctament a 40x.', 'OPEN', NOW() - INTERVAL '6 hours'),
  (6, 5, 'Pantalla de videoconferència intermitent', 'La pantalla principal perd senyal cada 10-15 minuts.', 'IN_PROGRESS', NOW() - INTERVAL '1 day'),
  (8, 9, 'Soroll excessiu a l''open space', 'S''ha reportat soroll constant durant la franja de matí.', 'RESOLVED', NOW() - INTERVAL '2 days'),
  (12, 11, 'Projector amb llum baixa', 'El projector de l''aula mostra baixa intensitat.', 'CLOSED', NOW() - INTERVAL '4 days'),
  (8,  13, 'Cadira trencada a l''open space', 'Una cadira del hot desk te una pota despresa, perill de caiguda.', 'OPEN', NOW() - INTERVAL '3 hours');

-- Mark users as active
UPDATE users SET active = true WHERE id >= 1;

-- Two inactive demo users
UPDATE users
SET active = false
WHERE email IN ('laia.puig@alumni.local', 'oriol.planes@startup.local');

-- Summary for demo
-- Created: 15 users (2 inactive), 12 facilities, 9 groups, 31 bookings, 5 incidents
-- Demonstrates: University labs, Corporate tech teams, Coworking/Freelance, Learning/Bootcamps
-- Time span: Past bookings (historical), Current bookings (this week), Future bookings (1-7 days ahead)
-- Also includes: Incident lifecycle states and cancelled booking scenarios
