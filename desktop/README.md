# NexusBooking Desktop

JavaFX desktop client for the NexusBooking application.

## Requirements

- Java 21+
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

## Features

- **Login** with email and password
- **Register** a new account
- **View profile** (email, role, ID)
- **Edit email**
- **Logout**
