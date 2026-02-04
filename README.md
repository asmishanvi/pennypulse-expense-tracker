# PennyPulse – Expense Tracker (Android)

An aesthetic, offline-first expense tracker built with Kotlin, Jetpack Compose, and Room.

## Screenshots
<p align="center">
  <img src="https://github.com/user-attachments/assets/5597c0da-1f07-478e-899b-329f4f363575" width="220" />
  <img src="https://github.com/user-attachments/assets/84cc118c-3d7a-49c5-92f0-ba77c1365661" width="220" />
  <img src="https://github.com/user-attachments/assets/3f4753ef-05a3-493d-bbd3-2ac29ddd1eea" width="220" />
</p>

## Features
- Expense tracking with add, edit, delete, notes, and date selection
- Emoji category picker with custom categories
- Search and filters: query, category chips, min/max amount, date range, and clear filters
- Monthly overview with totals, averages, and month switcher
- Budgeting with progress ring, remaining balance, and alerts at 80% and 100%
- Recurring expenses (subscriptions/rent) with monthly scheduling
- Insights charts: category split donut + weekly spend trend
- Daily streak and gamified points
- Smart reminders (daily 8:00 PM notification toggle)
- Appearance settings: light/dark/system mode + accent color picker
- Smooth UI motion: animated FAB, list item transitions, screen transitions

## Data & Privacy
- Offline-first: all data stays on device
- Room database for local persistence

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

## Permissions
- `POST_NOTIFICATIONS` (Android 13+) for budget alerts and smart reminders
