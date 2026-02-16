# 🏀 NBA Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-green.svg)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)](https://developer.android.com/about/versions/nougat)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern Android application to explore NBA data, including teams, players, and games. Built with Android architecture best practices and the latest technologies.

## 📱 Features

- ✅ **Team List**: View all NBA teams with sorting by name, city, or conference
- ✅ **Player Search**: Search players with automatic debounce and infinite pagination
- ✅ **Team Games**: View each team's games in a bottom sheet
- ✅ **Efficient Pagination**: Implementation with Paging 3 for long lists
- ✅ **Modern UI**: Interface built with Jetpack Compose and Material Design 3

## 🏗️ Architecture

This project follows **Clean Architecture** principles with clear layer separation:

```
┌─────────────────────────────────────────────────────────┐
│                     Presentation Layer                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │   Features   │  │  ViewModels  │  │  UI/Compose  │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                      Domain Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │  Use Cases   │  │  Repository  │  │    Models    │   │
│  │              │  │  Interfaces  │  │              │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                       Data Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ Repository   │  │   Network    │  │    Mappers   │   │
│  │     Impl     │  │   (Retrofit) │  │              │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Modules

```
app/                    # Main application module
├── core/
│   ├── data/          # Repository implementations and data sources
│   ├── domain/        # Use cases, domain models and interfaces
│   ├── network/       # Network configuration (Retrofit, OkHttp)
│   └── ui/            # Shared UI components
└── features/
    ├── home/          # Team listing feature
    └── players/       # Player search feature
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Hedgehog | 2023.1.1 or higher
- **JDK**: 11 or higher
- **Gradle**: 9.1.0 (included in wrapper)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36

### Project Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/NBA.git
   cd NBA
   ```

2. **Configure the API Key**

   The project uses the [balldontlie API](https://www.balldontlie.io/). You need an API key:

   a. Create an account at https://www.balldontlie.io/

   b. Get your API key

   c. Create a `local.properties` file in the project root (if it doesn't exist):
   ```properties
   sdk.dir=/path/to/your/android/sdk
   API_KEY=your-api-key-here
   ```

   ⚠️ **IMPORTANT**: Never commit the `local.properties` file with your API key!

3. **Project Sync**
   ```bash
   ./gradlew build
   ```

4. **Run the App**
   - Open the project in Android Studio
   - Wait for Gradle synchronization
   - Run on emulator or physical device

## 🧪 Testing

The project has comprehensive unit test coverage:

```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :core:domain:test
./gradlew :features:home:test

# Generate test report
./gradlew test
# Reports at: build/reports/tests/testDebugUnitTest/index.html
```

### Test Coverage

- ✅ **ViewModels**: HomeViewModel, TeamGamesViewModel, PlayersViewModel
- ✅ **Use Cases**: GetTeams, SearchPlayers, SortTeams
- ✅ **Repositories**: TeamRepositoryImpl
- ✅ **Paging**: GenericPagingSource
- ✅ **Utilities**: SafeApiCall

## 🔨 Build

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

⚠️ **Note**: The project is configured with `isMinifyEnabled = false` for development. For production, enable minification and configure proper ProGuard rules.

## 🛠️ Technologies Used

### Core
- **[Kotlin](https://kotlinlang.org/)** - Programming language
- **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming
- **[Flow](https://kotlinlang.org/docs/flow.html)** - Reactive streams

### UI
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Declarative UI
- **[Material 3](https://m3.material.io/)** - Design system
- **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)** - Navigation

### Architecture Components
- **[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)** - UI state management
- **[Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)** - Efficient pagination
- **[Hilt](https://dagger.dev/hilt/)** - Dependency injection

### Network
- **[Retrofit](https://square.github.io/retrofit/)** - HTTP client
- **[OkHttp](https://square.github.io/okhttp/)** - HTTP client
- **[Moshi](https://github.com/square/moshi)** - JSON serialization

### Testing
- **[JUnit](https://junit.org/junit4/)** - Testing framework
- **[MockK](https://mockk.io/)** - Mocking library
- **[Turbine](https://github.com/cashapp/turbine)** - Flow testing
- **[Coroutines Test](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)** - Coroutines testing

## 📂 Project Structure

```
NBA/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/nba/
│   │   │   ├── MainActivity.kt
│   │   │   └── NbaApplication.kt
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── core/
│   ├── data/
│   │   ├── repository/          # Repository implementations
│   │   ├── mapper/              # DTO -> Domain converters
│   │   ├── paging/              # PagingSources
│   │   └── util/                # Utilities (SafeApiCall)
│   │
│   ├── domain/
│   │   ├── model/               # Domain models
│   │   ├── repository/          # Repository interfaces
│   │   ├── usecase/             # Use cases
│   │   └── result/              # AppResult sealed class
│   │
│   ├── network/
│   │   ├── api/                 # Retrofit APIs
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── interceptor/         # HTTP interceptors
│   │
│   └── ui/
│       ├── components/          # Reusable Compose components
│       ├── theme/               # Themes and colors
│       └── viewmodel/           # Shared ViewModels
│
├── features/
│   ├── home/
│   │   ├── ui/                  # Screens and components
│   │   ├── viewmodel/           # HomeViewModel
│   │   └── navigation/          # HomeGraph
│   │
│   └── players/
│       ├── ui/                  # Screens and components
│       ├── viewmodel/           # PlayersViewModel
│       ├── paging/              # PlayersPagingSource
│       └── navigation/          # PlayersGraph
│
├── gradle/
│   └── libs.versions.toml       # Centralized dependency catalog
│
├── build.gradle.kts             # Root build configuration
├── settings.gradle.kts
├── local.properties             # Local settings (not versioned)
└── README.md
```

## 🎨 Design Patterns

### Repository Pattern
Abstraction of data sources with clean interface for the domain layer.

### Use Case Pattern
Encapsulation of business logic in reusable and testable components.

### MVVM (Model-View-ViewModel)
Clear separation between UI and business logic.

### Single Source of Truth
StateFlow as the single source of truth for UI state.

### Unidirectional Data Flow
Predictable data flow: Events → ViewModel → State → UI

## 🔐 Security

- ✅ API Key is not committed to code
- ✅ Uses `local.properties` for local configuration


## 📄 License

This project is licensed under the MIT License

