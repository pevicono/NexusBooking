# NexusBooking Mobile

Android mobile client for the NexusBooking application. Built with Kotlin and Jetpack Compose, featuring both user and admin interfaces.

## Requirements

- Android SDK API 26+ (Android 8.0+)
- Android Studio (latest recommended)
- Gradle 8.0+
- A running NexusBooking backend server
- Android Emulator or physical device

## Setup

### 1. Configure Backend URL

The app connects to `http://10.0.2.2:8080` by default, which is the Android emulator's alias for `localhost`.

- **For emulator:** No changes needed if your backend runs on `localhost:8080`
- **For physical device:** Update the base URL in [data/remote/ApiService.kt](app/src/main/java/com/example/nexusbooking/mobile/data/remote/ApiService.kt) to your server's IP address

### 2. Start or Configure Backend

Ensure your NexusBooking backend is running:

```bash
# From the backend directory
mvn spring-boot:run
```

Or ensure Docker containers are running:

```bash
# From the database directory
.\db.cmd start
```

### 3. Build and Run

**Using Android Studio:**
- Open the project in Android Studio
- Select Run > Run 'app' (or press Shift+F10)

**Using Gradle:**

```bash
cd mobile

# Build and install on emulator/device
./gradlew installDebug

# Or run directly
./gradlew run
```

---

## Features

### Authentication
- **Login** with email and password
- **Register** a new account
- **Token-based auth** with JWT stored securely using DataStore
- **Splash screen** that checks session on app launch

### User Dashboard (HomeScreen)

**Facilities Tab**
- Browse all available facilities
- View facility details (type, capacity, location, status)

**Bookings Tab**
- Create new bookings by selecting facility, group, date, and time
- View all personal bookings with status and details
- Notes field for additional information

**Groups Tab**
- Create new groups
- Join existing groups
- View group details and members

### User Profile (ProfileScreen)
- View user information (email, role, ID)
- Change password
- Logout

### Admin Backoffice (AdminScreen)
*Available to users with ADMIN role*

- **Users Management** – toggle user active status
- **Facilities Management** – create and update facilities
- **Bookings** – view and manage all bookings
- **Groups** – view and manage collaborative groups
- **Incidents** – create and track incidents

---

## Architecture

The app follows **MVVM + Repository Pattern**:

### Project Structure

```
app/src/main/java/com/example/nexusbooking/mobile/
├── ui/
│   ├── login/          # LoginScreen, LoginViewModel
│   ├── home/           # HomeScreen, HomeViewModel
│   ├── profile/        # ProfileScreen, ProfileViewModel
│   ├── admin/          # AdminScreen, AdminViewModel
│   ├── splash/         # SplashScreen, SplashViewModel
│   ├── navigation/     # NavGraph, Routes
│   └── theme/          # Theme configuration
├── data/
│   ├── remote/         # API service & DTOs
│   ├── local/          # DataStore for token storage
│   └── repository/     # Data repositories
├── di/                 # Dependency injection (Hilt)
└── util/               # Resource wrapper, extensions

app/src/main/res/
├── values/             # Strings, colors, styles
├── mipmap/             # App icons
└── layout/             # (If using XML layouts)
```

### Key Technologies

- **Jetpack Compose** – Modern declarative UI framework
- **Retrofit** – Type-safe HTTP client for REST API calls
- **Hilt** – Dependency injection framework
- **DataStore** – Secure local data storage for JWT tokens
- **Flow/StateFlow** – Reactive data streams
- **Material Design 3** – UI components and theming

---

## API Integration

The app communicates with the backend via REST endpoints:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/auth/login` | POST | User login |
| `/api/auth/register` | POST | Create new account |
| `/api/auth/change-password` | POST | Change password |
| `/api/facilities` | GET | List all facilities |
| `/api/bookings/mine` | GET | Get user's bookings |
| `/api/bookings` | POST | Create new booking |
| `/api/groups` | GET | List all groups |
| `/api/groups` | POST | Create new group |
| `/admin/users` | GET | List all users (admin only) |
| `/admin/users/{id}/active` | PUT | Toggle user status (admin only) |
| `/admin/facilities` | POST | Create facility (admin only) |
| `/admin/bookings` | GET | List all bookings (admin only) |
| `/incidents` | GET | List incidents |
| `/incidents` | POST | Create incident |

---

## Login Credentials

Use these accounts to test the app:

| Email                    | Password   | Role  |
|--------------------------|------------|-------|
| admin@nexusbooking.com   | Admin1234! | ADMIN |
| user@nexusbooking.com    | User1234!  | USER  |

Additional demo users are available—see [`DEMO_DATA_README.md`](../DEMO_DATA_README.md).

---

## Development

### Hot Reload

Jetpack Compose with Android Studio supports instant preview:
- Edit code → Auto-recompile and preview updates on target device

### Testing

Run instrumented tests:

```bash
./gradlew connectedAndroidTest
```

Unit tests:

```bash
./gradlew test
```

### Building Release APK

```bash
./gradlew assembleRelease
```

The release APK will be in `app/build/outputs/apk/release/`.

---

## Troubleshooting

### "Connection refused" or "Network error"

- Verify backend is running: `http://10.0.2.2:8080/api/auth` (from emulator)
- For physical device: Update the base URL to your server's IP address
- Check firewall rules allow port 8080

### Token expired on app restart

- The app uses DataStore to persist JWT tokens
- If storing fails, ensure proper write permissions on device

### Gradle build fails

- Run `./gradlew clean` then `./gradlew build`
- Check Android SDK is installed: `Tools > SDK Manager` in Android Studio

### Layout looks wrong

- Clear app data: `adb shell pm clear com.example.nexusbooking.mobile`
- Rebuild and reinstall: `./gradlew installDebug`

---

## Contributing

When adding new features:
1. Create a new ViewModel in `ui/your-feature/`
2. Create corresponding Repository in `data/repository/`
3. Implement Repository methods using the ApiService
4. Use StateFlow/Flow for reactive data binding in Compose
5. Add error handling with Resource wrapper
6. Write unit and instrumented tests

---

## License

Part of the NexusBooking project.
