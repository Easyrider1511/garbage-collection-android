# Garbage Collection App - Android

Android client for a civic waste-collection management platform, developed in Kotlin for the DAM 2025/2026 final project.

## Project Status

The Android app currently builds successfully, passes Android Lint, and runs correctly in the emulator.

### Latest validation

```bash
./gradlew clean assembleDebug lintDebug jacocoDebugUnitTestReport installDebug --warning-mode all
```

Validation result:

- `assembleDebug`: passed, debug APK generated
- `lintDebug`: passed, no issues found
- `jacocoDebugUnitTestReport`: passed, with JVM tests under `app/src/test`
- `installDebug`: passed on the Android Emulator

### Code coverage

The project includes a JaCoCo JVM unit-test coverage report task:

```bash
./gradlew jacocoDebugUnitTestReport
```

Current report output:

- HTML: `app/build/reports/jacoco/jacocoDebugUnitTestReport/html/index.html`
- XML: `app/build/reports/jacoco/jacocoDebugUnitTestReport/jacocoDebugUnitTestReport.xml`
- CSV: `app/build/reports/jacoco/jacocoDebugUnitTestReport/jacocoDebugUnitTestReport.csv`

Current coverage snapshot:

- line coverage: 82.45%
- branch coverage: 61.02%
- method coverage: 84.09%
- class coverage: 98.31%

### Emulator smoke test

The app was also installed and manually smoke-tested on an Android Emulator (`emulator-5554`, `Pixel_9_Pro` AVD):

- app launches into the main screen successfully
- OpenStreetMap map screen loads, renders map tiles, and supports zoom controls
- collection-point type filtering works on the map screen
- bottom navigation works for Map, Incidents, Schedules, and Profile
- language switching between English and Portuguese (Portugal) works
- incident creation exposes camera capture and photo preview/removal
- admin back-office can be opened from Profile when the logged-in user has the `ADMIN` role, with selector-based updates and contextual previews
- logout and login work with the backend seed admin user
- no `FATAL EXCEPTION` was found in the app process logs during the smoke test

## Implemented Features

- user registration and login with local session persistence
- app language selection with English and Portuguese (Portugal)
- collection-point map using OpenStreetMap through osmdroid, with pinch and button-based zoom controls
- collection-point category filtering on the map
- explicit map fallback message if the map cannot be loaded
- authenticated user's incident list
- incident creation using the current device location
- incident photo capture, preview, and local URI persistence
- incident detail screen
- "I also see this issue" action to reinforce an existing incident
- authenticated user's pickup schedule list
- pickup request creation
- administration back-office for collection-point creation and newest-first incident/pickup selectors with contextual previews
- basic profile view and profile editing
- logout
- automated JVM and Robolectric tests for utilities, adapters, auth, profile, map, incidents, schedules, and admin flows

## Known Limitations

These product constraints still apply:

- captured incident photos are stored as app-local `content://` URIs and are not uploaded as multipart files to the backend
- the automated test suite is currently focused on JVM unit tests; emulator UI flows are still validated through smoke testing
- admin back-office actions depend on a backend account with the `ADMIN` role and the corresponding REST endpoints enabled

## Tech Stack

- Kotlin 2.2.21
- Android SDK 36
- minSdk 26
- targetSdk 36
- Android Gradle Plugin 8.13.2
- Gradle Wrapper 8.14.4
- Retrofit 3 + Gson
- OkHttp 5 + logging interceptor
- OpenStreetMap via osmdroid
- Material Components
- ViewBinding
- RecyclerView + SwipeRefreshLayout
- SharedPreferences for local session data
- LeakCanary included for leak inspection during development

## Project Structure

```text
app/src/main/java/com/garbagecollection/app/
├── api/           # Retrofit ApiService + client setup
├── model/         # DTOs and request/response models
├── adapter/       # RecyclerView adapters
├── util/          # Session, language, location, photo, map-filter, and UI text helpers
└── ui/
    ├── admin/     # Admin dashboard and operational actions
    ├── auth/      # Login + Register
    ├── map/       # Map and collection points
    ├── incidents/ # Incident list, creation, and detail
    ├── schedules/ # Schedule list and creation
    └── profile/   # Profile view and editing
```

## Local Setup

### Requirements

- recent Android Studio version
- JDK 17 or 21
- Android SDK installed and configured

On macOS, if `JAVA_HOME` is not set, the project Gradle wrapper tries to use Android Studio's bundled JBR automatically.

### `local.properties`

The backend base URL is read from `local.properties`.

Example for Android Emulator:

```properties
sdk.dir=/Users/<your-user>/Library/Android/sdk
GC_BASE_URL=http://10.0.2.2:8080/
```

Notes:

- the map uses OpenStreetMap/osmdroid, so no Google Maps API key is required
- if OSM tiles cannot be loaded, the map screen shows a user-facing fallback message
- the app sets a package-specific osmdroid User-Agent to comply with the [OSM Tile Usage Policy](https://operations.osmfoundation.org/policies/tiles/)
- `10.0.2.2` is the Android Emulator alias for the host machine's `localhost`
- on a physical device, replace `GC_BASE_URL` with your machine's LAN IP or a reachable HTTPS endpoint
- if `GC_BASE_URL` does not end with `/`, the build script appends it automatically

### Development HTTP traffic

The app allows cleartext HTTP in development so it can connect to local backend services more easily.
For non-academic or production usage, prefer HTTPS and tighten the network security configuration.

## Backend

This repository is only the Android client. It expects a compatible backend exposing the authentication, incident, schedule, and collection-point endpoints used by `ApiService`.

If you are running the companion backend with its default seed data, the admin demo account is:

```text
username: admin
password: admin123
```

## How to Run

1. Start the backend service.
2. Configure `local.properties`.
3. Open the project in Android Studio and run the app, or build from the terminal:

```bash
./gradlew assembleDebug
```

The generated debug APK is available at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

To install the debug build on a connected emulator/device:

```bash
./gradlew installDebug
```

## Maintenance Notes

- `.gitignore` excludes local Android Studio/Gradle files and generated build artifacts
- `gradle.properties` sets JVM/Metaspace limits for more stable local Gradle daemon runs
- `lintDebug` is clean in the current project state
- if Android Studio shows stale build-artifact errors, run **Sync Project with Gradle Files**, then **Build > Rebuild Project**
