# Bounty Hunter iOS App

Native iOS application for the Bounty Hunter fitness tracking platform built with SwiftUI.

## Features

- User registration and login
- Personalized workout suggestions
- Manual workout logging
- Workout history with detailed cards
- Recurring workout management
- Modern iOS design with SwiftUI
- UserDefaults for auth persistence

## Tech Stack

- **Language**: Swift 5.9+
- **UI Framework**: SwiftUI
- **Networking**: URLSession with async/await
- **Storage**: UserDefaults
- **Min iOS**: 16.0+
- **Architecture**: MVVM with ObservableObject

## Setup

1. Open `BountyHunter.xcodeproj` in Xcode
2. Make sure your backend server is running on `localhost:5000`
3. Build and run on simulator or device

**Note**: For physical devices, update the `baseURL` in `APIService.swift` to your computer's local IP address.

## Project Structure

```
ios/
├── BountyHunter/
│   ├── BountyHunterApp.swift       # App entry point
│   ├── Models/
│   │   └── Models.swift            # All data models
│   ├── Services/
│   │   └── APIService.swift        # API networking layer
│   ├── Utils/
│   │   └── AuthManager.swift       # Auth state management
│   └── Views/
│       ├── LandingView.swift       # Welcome screen
│       ├── LoginView.swift         # Login screen
│       ├── RegisterView.swift      # Registration form
│       ├── DashboardView.swift     # Tab container
│       ├── SuggestionsView.swift   # Workout suggestions
│       ├── LogWorkoutView.swift    # Log workout form
│       ├── HistoryView.swift       # Workout history list
│       └── RecurringView.swift     # Recurring workouts
└── BountyHunter.xcodeproj/
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

## Features Breakdown

### Landing View
- Hero section with app branding
- Navigation to login or registration

### Authentication
- Login with username/password
- Full registration form with:
  - Username, email, password (required)
  - Full name, location, age, weight (optional)
  - Activity type preferences (chips)
  - Payment info
- Auto-login after registration
- Persistent auth with UserDefaults

### Dashboard
- Tab-based navigation with 4 tabs:
  1. **Suggestions** - Personalized workout cards with "Use This" button
  2. **Log Workout** - Form to manually log workouts
  3. **History** - Past workouts with formatted dates
  4. **Recurring** - Scheduled recurring workouts

### UI/UX Features
- Pull-to-refresh on history
- Loading states with ProgressView
- Empty states with icons and messages
- Form validation
- Error handling
- Modern card-based layouts
- Color-coded workout type badges

## Building

### Debug
1. Select target in Xcode
2. Choose simulator or device
3. Press ⌘+R to build and run

### Release
1. Archive the app (Product → Archive)
2. Distribute to TestFlight or App Store

## Requirements

- Xcode 15.0+
- iOS 16.0+ deployment target
- Swift 5.9+
- Backend server running on localhost:5000

## SwiftUI Components

The app uses modern SwiftUI features:
- `@StateObject` and `@EnvironmentObject` for state management
- `async/await` for networking
- `Task` for concurrent operations
- Custom layouts (FlowLayout for chips)
- Sheet presentations for modals
- TabView for main navigation
- NavigationView for hierarchical navigation
