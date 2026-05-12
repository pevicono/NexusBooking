# NexusBooking Desktop

JavaFX desktop client for the NexusBooking application. Multi-role application with separate interfaces for regular users and administrators.

## Requirements

- Java 17+
- Maven 3.8+

## Configuration

Before running, update the server URL in [`config.properties`](../config.properties) at the repo root:

```properties
api.base.url=http://<server-ip>:8080
```

## Running

```bash
cd desktop
mvn javafx:run
```

---

## Features

### Authentication
- **Login** with email and password
- **Register** a new account
- **Forgot password** recovery

### User Dashboard
- **Dashboard** overview with upcoming bookings, groups, and available facilities
- **My Bookings** – create and manage reservations
  - Pick facility, group, date, time, and add notes
  - View all personal bookings with status
- **My Groups** – browse and manage groups
  - Join groups with join codes
  - Create new groups
- **Create Incidents** – report issues from past bookings
- **Profile** management – view and edit user information

### Admin Backoffice
*Available to users with ADMIN role*

- **Dashboard** with statistics and recent activity
- **Users** management – list, search, sort, and toggle active status
- **Facilities** management – create, edit, list available facilities
- **Bookings** management – view all bookings, manage status, search and filter
- **Groups** management – create, view, and manage collaborative groups
- **Incidents** management – create, track, and resolve incidents
- **Calendar** view – year-based facility booking calendar

### UI Experience
- Responsive sidebar (collapsible for compact view)
- Dark theme with NexusBooking branding
- Real-time data refresh
- Search and filtering across all resources
- Sortable lists with multiple columns
