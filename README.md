# Weather — Android Project

A simple, modern weather app for Android. Built as part of a junior-developer to demonstrate Jetpack Compose, MVVM, networking with Retrofit, and a layered architecture.

## Screenshots

> _Add screenshots here once you run the app: `docs/screenshot-1.png`, etc._

## Features

- Current weather + 7-day forecast based on device location
- Material 3 design with dynamic color (Android 12+)
- Dark theme support
- Loading / error / success states
- Clean separation: `data` / `domain` / `presentation`

## Tech stack

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **Coroutines** + **StateFlow**
- **Retrofit** + **OkHttp** + **kotlinx.serialization**
- **Accompanist Permissions** + **Play Services Location**
- **Open-Meteo API** (no API key required)

## Architecture

```
data/
  remote/      — Retrofit API + DTOs
  repository/  — Maps DTOs to domain models
  location/    — FusedLocationProvider wrapper
domain/
  model/       — Pure Kotlin models used by UI
presentation/
  weather/     — Screen, ViewModel, UiState
  theme/       — Material 3 theme
```

Manual dependency wiring in `MainActivity` for now — will migrate to Hilt in the next project.

## How to run

1. Open the project in Android Studio (Koala or newer)
2. Sync Gradle
3. Run on a device or emulator (minSdk 24)
4. Grant location permission when prompted

## What I learned

- Building a Compose-only UI without XML layouts
- State handling with sealed `UiState` and `StateFlow`
- Mapping network DTOs to domain models in the repository layer
- Working with runtime permissions in Compose

## License

MIT
