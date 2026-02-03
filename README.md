# PennyPulse – Expense Tracker (Android)

An aesthetic, offline-first expense tracker built with Kotlin, Jetpack Compose, and Room.

## Screenshots
Add your screenshots here (recommended: 3-4 images).

```
screenshots/
  home.png
  insights.png
  edit.png
  filters.png
```

## Features
- Add, edit, and delete expenses
- Categories, notes, emoji picker, and date selection
- Monthly totals and averages
- Recurring expenses + budget tracking
- Local-only storage with Room

## Tech Stack
- Kotlin
- Jetpack Compose (Material 3)
- Room (SQLite)
- WorkManager

## Architecture
MVVM with Repository pattern and Room for persistence.

## Project Structure
- `app/src/main/java/com/example/expensetracker/data` – Room entities, DAO, repository
- `app/src/main/java/com/example/expensetracker/ui` – ViewModel and Compose screens

## Getting Started
Open the project in Android Studio and let it sync Gradle. If you want to update versions, edit `build.gradle.kts` and `app/build.gradle.kts`.

## Roadmap
- Cloud sync
- iOS version
- Analytics export (CSV)
