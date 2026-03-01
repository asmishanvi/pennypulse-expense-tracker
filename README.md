# PennyPulse – Expense Tracker (Android)

An aesthetic, offline-first expense tracker built with Kotlin, Jetpack Compose, and Room.

## Screenshots
### Light Mode
<p align="center">
  <img src="https://github.com/user-attachments/assets/2748f0ae-eb86-4e6b-88c7-3d6f1c6a3aad" width="200" />
  <img src="https://github.com/user-attachments/assets/4511795e-bddc-45ff-9b60-5ef3d3f4bd57" width="200" />
  <img src="https://github.com/user-attachments/assets/648b72cf-89dd-4ad1-9b80-7453ddb4a365" width="200" />
  <img src="https://github.com/user-attachments/assets/5c185e3c-823d-445e-9ce3-9c2b478d3efa" width="200" />
  <img src="https://github.com/user-attachments/assets/7ccf9697-69ee-4954-84bf-1bbfbc44163e" width="200" />
</p>

### Dark Mode
<p align="center">
  <img src="https://github.com/user-attachments/assets/98cf7f4d-ba7b-4277-852f-006833863b72" width="200" />
  <img src="https://github.com/user-attachments/assets/fa546fd9-c162-49ff-ab64-92f9a2a6b80f" width="200" />
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
