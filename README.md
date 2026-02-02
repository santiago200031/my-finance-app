# My Finance App

A personal finance management Android application built with modern Android development practices. Track your income, expenses, contracts, budgets, and get insights into your financial health.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Building the App](#building-the-app)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Configuration](#configuration)

## Features

- **Dashboard**: Overview of your financial status with income, expenses, and net growth summaries
- **Entries**: Track income and expenses with categories and dates
- **Contracts**: Manage recurring payments (rent, subscriptions, salaries) with automatic entry generation
- **Budget Planning**: Set and monitor budgets for different categories
- **Analysis**: Visualize your financial data with charts and statistics
- **Reminders**: Set reminders for important financial tasks and contract payments
- **Data Export**: Export your financial data for backup or analysis
- **Sharing Settings**: Share contract information with other users
- **Notifications**: Get notified about contract payments and reminders

## Architecture

This app follows the **MVVM (Model-View-ViewModel)** architecture pattern with a clean separation of concerns:

- **UI Layer**: Jetpack Compose screens and components
- **ViewModel Layer**: State management and business logic
- **Data Layer**: Repositories and services for data operations
- **Model Layer**: Data classes representing entities (Entry, Contract, User, etc.)

The app supports both local (fake) and cloud (Firebase Firestore) data sources through repository abstractions.

## Tech Stack

- **Language**: Kotlin 2.2.21
- **UI Framework**: Jetpack Compose (Material3)
- **Architecture**: MVVM with StateFlow
- **Dependency Injection**: Manual DI with AppContainer
- **Backend**: Firebase Firestore
- **Navigation**: Navigation Compose
- **Async Operations**: Kotlin Coroutines
- **Local Storage**: DataStore Preferences
- **Build System**: Gradle with Kotlin DSL

### Key Dependencies

| Library | Version |
|---------|---------|
| Android Gradle Plugin | 8.13.2 |
| Kotlin | 2.2.21 |
| Compose BOM | 2025.12.01 |
| Firebase BOM | 34.7.0 |
| Navigation Compose | 2.9.6 |
| Coroutines | 1.10.2 |
| Material3 | Latest (via BOM) |

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio**: Koala Feature Drop (2024.1.2) or later
- **JDK**: Java 11 or higher
- **Android SDK**:
  - Compile SDK: 36
  - Min SDK: 33 (Android 13)
  - Target SDK: 36
- **Git**: For cloning the repository

## Getting Started

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/my-finance-app.git
   cd my-finance-app
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing Android Studio project"
   - Choose the `my-finance-app` folder

3. **Sync Project**
   - Android Studio will automatically sync the Gradle files
   - If not, click "Sync Now" in the notification bar

4. **Firebase Configuration**
   - The app requires Firebase configuration
   - Ensure `google-services.json` is present in the `app/` directory
   - If not, set up a Firebase project and download the config file

5. **Build the project**

   ```bash
   ./gradlew build
   ```

## Building the App

### Debug Build

Build a debug APK for testing:

```bash
./gradlew :app:assembleDebug
```

The APK will be located at:

```
app/build/outputs/apk/debug/app-debug.apk
```

### Install Debug Version

To build and install the debug version directly on a connected device or emulator:

```bash
./gradlew clean :app:installDebug
```

### Release Build

Build a release APK (requires signing configuration):

```bash
./gradlew :app:assembleRelease
```

## Running Tests

### Unit Tests

Run all unit tests:

```bash
./gradlew test
```

Run tests for specific module:

Unit tests are located in:

```
app/src/test/java/com/mobilecomputing/myfinance/
```

#### Test Coverage

The app includes unit tests for:

- **Service Layer**: `ContractService`, `EntryService`, `UserService`
- **ViewModels**: `AddContractViewModel`, `AnalysisViewModel`, `DashboardViewModel`, `ExportViewModel`
- **Utilities**: `DateUtils`

Testing libraries used:

- JUnit
- MockK
- kotlinx-coroutines-test (for coroutine testing)

### Test Reports

After running tests, view the reports at:

- **Unit Tests**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Instrumented Tests**: `app/build/reports/androidTests/connected/index.html`

## Configuration

### Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use an existing one
3. Add an Android app with package name: `com.mobilecomputing.myfinance`
4. Download `google-services.json` and place it in the `app/` directory
5. Enable Firestore Database in the Firebase Console

### Repository Mode

The app can switch between local (fake) and cloud (Firestore) data sources:

- Modify `AppContainer.kt` to change the repository implementation
- Fake repositories are useful for development and testing without Firebase
- Firestore repositories provide real cloud persistence

### Minimum Requirements

- **Android Version**: Android 13 (API 33) or higher
- **Internet**: Required for Firebase Firestore functionality
- **Storage**: Minimal local storage for preferences and caching

## Development

### Code Style

The project follows standard Kotlin coding conventions:

- Use meaningful variable and function names
- Follow MVVM architecture patterns
- Keep composables focused and reusable
- Use StateFlow for state management

### Useful Gradle Commands

## Install on device

./gradlew :app:installDebug

## Run all checks (tests + lint)

./gradlew check

## Troubleshooting

### Build Issues

## License

This project is developed as part of a Mobile Computing course project.

## Acknowledgments

- Built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Powered by [Firebase](https://firebase.google.com/)
- Icons by [Material Design](https://material.io/design/iconography/system-icons.html)
