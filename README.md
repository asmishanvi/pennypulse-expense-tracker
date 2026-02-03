# PennyPulse – Expense Tracker (Android)

An aesthetic, offline-first expense tracker built with Kotlin, Jetpack Compose, and Room.

## Screenshots
<p align="center">
  <img src="https://github.com/user-attachments/assets/84cc118c-3d7a-49c5-92f0-ba77c1365661" width="280" />
  <img src="https://github.com/user-attachments/assets/3f4753ef-05a3-493d-bbd3-2ac29ddd1eea" width="280" />
  <img src="https://github.com/user-attachments/assets/5597c0da-1f07-478e-899b-329f4f363575" width="280" />
</p>

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
