# NexusBooking - Demo Data Guide

Guia del dataset de demostracio de NexusBooking per preparar una demo funcional (producte, operativa i backoffice).

## Resum del Dataset

El command `seed` carrega un escenari complet amb:

- 15 usuaris (2 inactius)
- 12 facilities
- 9 grups
- 31 reserves
- 5 incidents

Aquest dataset esta pensat per demostrar:

- col.laboracio entre grups
- planificacio de reserves
- control administratiu
- seguiment d'incidencies

## Com Carregar les Dades

Des del directori `database`:

```powershell
.\db.cmd start
.\db.cmd seed
```

Per reconstruir des de zero:

```powershell
.\db.cmd reset
.\db.cmd seed
```

Notes:

- `seed` ja carrega el dataset complet (ja no hi ha `seed-demo`).
- El script actiu es `database/scripts/seed_data.sql`.

## Credencials de Demo

Comptes base:

| Email | Password | Rol |
|---|---|---|
| admin@nexusbooking.com | Admin1234! | ADMIN |
| user@nexusbooking.com | User1234! | USER |

Usuaris addicionals del dataset:

- maria.torres@univ.local
- joan.ferran@univ.local
- silvia.gomez@univ.local
- carles.ruiz@tech-corp.local
- anna.vidal@tech-corp.local
- pau.lopez@tech-corp.local
- xavier.sole@coworking.local
- clara.munoz@coworking.local
- andreu.carrio@coworking.local
- helena.costa@learning.local
- miguel.santos@learning.local

Password per tots els usuaris del demo: `Admin1234!`

## Storytelling Recomanat (Pitch)

### Escena 1 - Problema

Context: multiples equips comparteixen espais i horaris.

Missatge: sense eina central, apareixen conflictes i perdua de temps.

Demo: mostra `bookings` i el volum real de reserves en paral.lel.

### Escena 2 - Solucio Col.laborativa

Context: grups de diferents organitzacions comparteixen recursos.

Missatge: NexusBooking centralitza grups, membres i reserves.

Demo: consulta `groups`, `group_members` i `bookings` amb joins.

### Escena 3 - Operativa Setmanal

Context: reserves passades, actuals i futures.

Missatge: visibilitat completa de planificacio i ocupacio.

Demo: filtra reserves per dia, facility i grup.

### Escena 4 - Control Admin

Context: decisio basada en dades.

Missatge: l'admin veu us, activitat i incidencies en temps real.

Demo: agregacions per grup, facility i estat.

## Queries Utils per Demo

### Xifres globals

```sql
SELECT
  (SELECT COUNT(*) FROM users) AS total_users,
  (SELECT COUNT(*) FROM groups) AS total_groups,
  (SELECT COUNT(*) FROM facilities) AS total_facilities,
  (SELECT COUNT(*) FROM bookings) AS total_bookings,
  (SELECT COUNT(*) FROM incidents) AS total_incidents;
```

### Grups mes actius

```sql
SELECT g.name, COUNT(b.id) AS num_bookings, COUNT(DISTINCT gm.user_id) AS group_size
FROM groups g
LEFT JOIN bookings b ON g.id = b.group_id
LEFT JOIN group_members gm ON g.id = gm.group_id
GROUP BY g.id, g.name
ORDER BY num_bookings DESC;
```

### Facilities mes utilitzades

```sql
SELECT f.name, f.type, COUNT(b.id) AS bookings, f.capacity
FROM facilities f
LEFT JOIN bookings b ON f.id = b.facility_id
GROUP BY f.id, f.name, f.type, f.capacity
ORDER BY bookings DESC;
```

### Activitat propera (7 dies)

```sql
SELECT
  TO_CHAR(start_time, 'Dy') AS day_of_week,
  COUNT(*) AS reservations,
  COUNT(DISTINCT group_id) AS groups_involved
FROM bookings
WHERE start_time >= NOW()
  AND start_time < NOW() + INTERVAL '7 days'
GROUP BY TO_CHAR(start_time, 'Dy'), EXTRACT(DOW FROM start_time)
ORDER BY EXTRACT(DOW FROM start_time);
```

## Timeline de Dades

- Passat: reserves completades
- Present: activitat actual
- Futur: planificacio setmanal

## Notes Finals

- Si necessites regenerar l'escenari abans d'una demo: `clear` + `seed`.
- Per exploracio visual de dades: pgAdmin a `http://localhost:5050`.

Creacio inicial: 26 abril 2026
Actualitzat: 12 maig 2026
