<img width="1080" height="2400" alt="Screenshot_20260204_234053" src="https://github.com/user-attachments/assets/a10d8a7e-85ee-41a0-9491-d0cbcce67d47" /><img width="1080" height="2400" alt="Screenshot_20260204_234053" src="https://github.com/user-attachments/assets/e64db7e2-82e8-4916-b695-63c612ac6f8f" /># PennyPulse – Expense Tracker (Android)

An aesthetic, offline-first expense tracker built with Kotlin, Jetpack Compose, and Room.

## Screenshots
<p align="center">
  <img width="1080" height="2400" alt="Screenshot_20260204_234212" src="https://github.com/user-attachments/assets/ee41a922-06ce-42a4-a181-fa8a984329a0" />
<img width="1080" height="2400" alt="Screenshot_20260204_234226" src="https://github.com/user-attachments/assets/31e16f64-453a-480e-b175-21ea1b882e47" /><img width="1080" height="2400" alt="Screenshot_20260204_234242" src="https://github.com/user-attachments/assets/0c34e1bd-d1e6-4322-ae39-17db0afc98a7" <img width="1080" height="2400" alt="Screenshot_20260204_234256" src="https://github.com/user-attachments/assets/280e4cde-fc84-45<img width="1080" height="2400" alt="Screenshot_20260204_234249" src="https://github.com/user-attachments/assets/85503d87-0a39-4f0c-9ddb-2e82c734ce1a" />
1c-92b8-2f1179a2f507" />
/>

<img width="1080" height="2400" alt="Screenshot_20260204_234231" src="https://github.com/user-attachments/assets/b9067f79-349f-4c5b-9e23-4e700012411d" />

  <img width="1080" height="2400" alt="Screenshot_20260204_234053" src="https://github.com/user-attachments/assets/60ff8257-3c2d-41c5-badf-1a1fa1b26621" />

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
