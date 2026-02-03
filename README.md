# 🦅 Bounty Hunter - Physical Fitness Quest Platform

A simple platform for tracking physical fitness goals and rewards.

## 🚀 Features

- **Signup**: Create an account with your personal info, fitness preferences, and payment details.
- **Workouts**: Get personalized workout suggestions or manually input your own workout data.
- **Recurring Tasks**: Set up recurring workouts to stay consistent.
- **History**: View your complete workout history and progress.
- **Honor System**: No proof required - we trust your manual inputs or automatic app tracking.

## 🏗️ Architecture

- **Backend**: Flask API server
- **Storage**: JSON-based storage (S3 or Local)
- **Clients**: Web and Mobile (coming soon)

## 🛠️ Setup

1. Install dependencies:
   ```bash
   cd server
   pip install -r requirements.txt
   ```

2. Run the server:
   ```bash
   python app.py
   ```

3. Open the web client:
   ```bash
   cd web
   python -m http.server 8080
   ```
   
   Then visit `http://localhost:8080` in your browser.

The API will be available at `http://localhost:5000`

## 📡 API Endpoints

### Authentication
- **POST** `/api/register` - Create new account
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string",
    "full_name": "string",
    "location": "string",
    "age": number,
    "weight": number,
    "activity_types": ["group", "individual", "gym", "sports", "free"],
    "payment_info": "string"
  }
  ```

- **POST** `/api/login` - Login to account
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
  Returns: `{"token": "...", "user": {...}}`

### Workouts
- **GET** `/api/suggestions` - Get personalized workout suggestions (requires auth)
- **POST** `/api/workouts` - Log a workout (requires auth)
  ```json
  {
    "title": "Morning Run",
    "type": "individual",
    "duration": "30 mins",
    "data": {}
  }
  ```
- **GET** `/api/workouts` - View workout history (requires auth)

### Recurring Workouts
- **POST** `/api/recurring` - Create recurring workout (requires auth)
  ```json
  {
    "title": "Daily Morning Yoga",
    "schedule": "daily"
  }
  ```
- **GET** `/api/recurring` - View recurring workouts (requires auth)

**Authentication**: Include `Authorization: Bearer <token>` header for protected endpoints.

## 💻 Web Client Features

The single-page web application includes:

- **Landing Page**: Clean hero section with call-to-action
- **Registration**: Complete signup with personal info and fitness preferences
- **Login**: Secure authentication with token management
- **Dashboard Tabs**:
  - **Suggestions**: Personalized workout recommendations based on your preferences
  - **Log Workout**: Manual workout tracking with notes
  - **History**: Complete workout history with visual cards
  - **Recurring**: Create and manage recurring workout schedules
- **Professional UI**: Modern design with smooth transitions and responsive layout
- **Local Storage**: Persistent authentication across sessions
