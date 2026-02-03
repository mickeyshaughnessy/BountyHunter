# Bounty Hunter Android App

Native Android application for the Bounty Hunter fitness tracking platform.

## Features

- User registration and login
- Personalized workout suggestions
- Manual workout logging
- Workout history with detailed cards
- Recurring workout management
- Material Design 3 UI
- Offline authentication storage

## Tech Stack

- **Language**: Kotlin
- **UI**: Material Design 3, ViewBinding
- **Networking**: Retrofit2, OkHttp3
- **Architecture**: MVVM-lite with Fragments
- **Async**: Kotlin Coroutines
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Setup

1. Open the `android` folder in Android Studio
2. Sync Gradle files
3. Make sure your backend server is running on `localhost:5000`
4. Run the app on an emulator or device

**Note**: The app uses `10.0.2.2:5000` to connect to localhost from the Android emulator.

## Project Structure

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/bountyhunter/
│   │   │   ├── models/          # Data models
│   │   │   ├── api/             # Retrofit API interface
│   │   │   ├── utils/           # SharedPreferences helper
│   │   │   ├── adapters/        # RecyclerView adapters
│   │   │   ├── fragments/       # Dashboard fragments
│   │   │   └── *.kt             # Activities
│   │   └── res/                 # Resources (layouts, strings, etc.)
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## API Integration

The app integrates with all backend endpoints:
- POST `/api/register` - User registration
- POST `/api/login` - User login
- GET `/api/suggestions` - Get workout suggestions
- POST `/api/workouts` - Log workout
- GET `/api/workouts` - Get workout history
- POST `/api/recurring` - Create recurring workout
- GET `/api/recurring` - Get recurring workouts

## Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

## Screenshots

The app includes:
- Landing page with hero section
- Registration with full profile setup
- Login screen
- Dashboard with 4 tabs:
  - Suggestions (personalized workouts)
  - Log Workout (manual entry)
  - History (past workouts)
  - Recurring (scheduled workouts)
