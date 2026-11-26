# PILOT

**Pilot** is a modular, Turkish-first AI driving assistant for Android.

> Product direction: a premium cockpit UI that brings together a voice assistant, Android navigation, notifications, media controls, dashcam, driver monitoring and ADAS-style assistance. Safety-critical driving features are assistive only and must never be presented as a replacement for the driver's attention.

## Project setup

- Windows 11
- Android Studio (2026)
- Kotlin + Jetpack Compose
- Android SDK: `D:\Android\SDK`
- Gradle user home: `D:\Android\.gradle`
- JDK 17
- Compile / target SDK: 36
- Minimum SDK: 26

Open this repository in Android Studio. Set the SDK location to `D:\Android\SDK` and Gradle user home to `D:\Android\.gradle` in your local environment. Do not commit `local.properties` or API keys.

## Modules

| Module | Responsibility |
|---|---|
| `:app` | Application entry point, permissions and dependency graph |
| `:core:common` | Shared primitives and result types |
| `:core:ui` | Pilot design system |
| `:core:voice` | Wake phrase and voice session contracts |
| `:core:ai` | Provider-neutral AI contracts |
| `:core:navigation` | Navigation contracts |
| `:core:bluetooth` | Bluetooth domain contracts |
| `:core:notification` | Notification access contracts |
| `:data` | Persistence and repository implementations |
| `:feature:assistant` | Main cockpit and assistant experience |
| `:feature:media` | Media controls |
| `:feature:call` | Calling and contacts |
| `:feature:bluetooth` | Vehicle connection mode |
| `:feature:notification` | Notification center |
| `:feature:fatigue` | Driver fatigue detection |
| `:feature:dashcam` | Camera recording |
| `:feature:adas` | Driver assistance experiments |
| `:feature:settings` | Settings and permissions |

## Voice interaction

- Wake phrase: **"Hey Pilot"**
- Close phrase: **"Pilot kapan"**

The initial wake phrase implementation uses Android's speech-recognition service as a development bridge. Android's general speech recognizer is not a production-grade always-on hotword engine: availability, offline support, battery use and background behavior depend on the device and recognition provider. A dedicated on-device wake-word model will be integrated before production release.

Android does not allow an ordinary app to silently force its Activity to the foreground whenever a background wake phrase is detected. Pilot can keep a user-started microphone foreground service active with a persistent notification, respond by voice, and show a notification that the user can tap to open the cockpit. The "Pilot kapan" command closes the active assistant screen/session but leaves the wake listener running. Say "Pilot tamamen kapan" or use the in-app full-stop button to stop the microphone service.

## API key safety

Never commit Gemini Live or Evren API keys. The production design will use a backend-issued short-lived session credential for Gemini Live where supported. Local development secrets belong in an untracked local configuration file.

## Build

Use Android Studio's Gradle sync and run the `app` configuration, or build with Gradle 8.13 and JDK 17:

Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

macOS / Linux:

```bash
./gradlew :app:assembleDebug
```

The GitHub Actions workflow builds the debug APK and publishes it as a workflow artifact.
