<div align="center">

# 👻 GhostLock
### Stealth OLED Black Screen Lock & Anti-Tamper Security for Android

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B%20(API%2024%2B)-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline%20%7C%20Zero%20Telemetry-success.svg)](#privacy--threat-model)
[![GitHub Actions CI](https://img.shields.io/badge/GitHub%20Actions-Automated%20APK%20Release-orange.svg)](.github/workflows/release.yml)

<p align="center">
  <b>GhostLock disguises your active Android device as a completely powered-down, unresponsive phone.</b><br>
  No backlight bleed, no system volume toasts, no lockscreen notifications—unlocked exclusively via invisible gestures, rhythmic taps, or spatial quadrant patterns.
</p>

[Key Features](#-key-features) •
[Real-World Use Cases](#-real-world-use-cases) •
[How It Works](#-how-it-works) •
[Download APK](#-download--installation) •
[Building from Source](#-building-from-source) •
[Privacy Model](#-privacy--threat-model) •
[License](#-license)

</div>

---

## 🌟 Why GhostLock?

Standard lock screens **announce your phone is on**: they glow, show notification clocks, display biometric icons, and reveal volume sliders when buttons are accidentally pressed. 

**GhostLock eliminates every visual and acoustic trace of device activity:**
- **Zero Backlight Bleed (True OLED `#000000`)**: Individual OLED pixels turn completely off, rendering the screen optically identical to a phone with a dead battery.
- **Physical Hardware Button Suppression**: Intercepts `Volume Up`, `Volume Down`, `Mute`, and `Back` buttons at the window layer—preventing volume sliders or audio interruptions from exposing you.
- **Deceptive 🪫 Dead Battery Reaction**: When unauthorized bystanders mash physical buttons or attempt invalid gestures, GhostLock briefly flashes a realistic **0% Depleted Battery indicator** before returning to pitch black, convincing them the phone is completely out of power.
- **Invisible Biometric-Free Unlocking**: Unlock using continuous unistroke symbols, secret rhythmic tap counts, or invisible 4-quadrant screen patterns known only to you.
- **Zero Cloud / 100% Offline**: No network permissions, no account requirements, zero analytics, zero keystroke logging.

---

## 🛡️ Key Features

| Capability | Technical Mechanism | Stealth Advantage |
| :--- | :--- | :--- |
| **OLED Pure Black** | `#000000` Compose canvas + minimum brightness override | Zero illumination; screen looks 100% powered off. |
| **Hardware Key Lock** | Native `onKeyDown` / `onKeyUp` override | Pressing volume keys produces no sound or screen overlay. |
| **Home Button Lockdown** | Android Lock Task Mode (`startLockTask()`) | OS-level screen pinning prevents exiting to home launcher. |
| **Lifecycle Anti-Home** | Intercepts `onUserLeaveHint()` & `onWindowFocusChanged()` | Instantly recaptures foreground if home gesture is attempted. |
| **Deceptive Shutdown** | High-fidelity Android power-off simulation | Authentically fakes a low-battery shutdown over 2.5 seconds. |
| **🪫 0% Tamper Flash** | Depleted 1-cell battery warning animation | Convinces snoops the battery is dead and stops further tampering. |
| **Continuous Symbol** | Resampling + centroid normalization distance & angle vectors | Draw unistroke letters, shapes, or sigils anywhere on screen. |
| **Rhythmic Tap Cadence** | Timestamp-based consecutive tap accumulator | Unlock by tapping a secret number of times (2–10 taps). |
| **Quadrant Pattern** | 4-zone spatial touch matrix ($Q1 \to Q2 \to Q4 \to Q3$) | Invisible coordinate order check with prefix validation. |
| **Emergency Fail-Safe** | 3-second sustained hold in secret corner | Reveals master recovery PIN keypad if you forget your code. |

---

## 📱 Real-World Use Cases

### 1. 🚆 Commuting & Public Transit
Keep long downloads, navigation, audio feeds, or hotspot sharing active while resting your phone face-up on a train or bus seat. Curious onlookers will assume your phone is powered off.

### 2. 👥 High-Density Anti-Shoulder-Surfing
In crowded cafes, elevators, airplanes, or libraries, bystanders frequently glance at lock screens to see PINs, pattern lines, or private notifications. GhostLock reveals no keypad or stroke visualizers.

### 3. 🚨 Defense Against Grab-and-Snoop
If an acquaintance, sibling, or stranger grabs your phone and mashes the power or volume buttons to check notifications, GhostLock displays the **🪫 0% Depleted Battery** warning, convincing them that pressing buttons is useless.

### 4. 💼 Executive Meetings & Quiet Classrooms
Keep the device on your desk without attracting attention from colleagues, managers, or professors. No accidental ambient display activations or glowing notification bursts.

### 5. 🧘 Tamper-Resistant Digital Detox
Place yourself into uninterrupted focus mode. Because the phone looks dead and resists effortless casual checking, you break the unconscious habit of checking notifications.

---

## ⚙️ How It Works

```
                               ┌────────────────────────────────┐
                               │  User Initiates Stealth Lock   │
                               └───────────────┬────────────────┘
                                               │
                                               ▼
                              ┌──────────────────────────────────┐
                              │  Optional: Simulated Shutdown    │
                              │  • "Shutting down... Battery 1%" │
                              └────────────────┬─────────────────┘
                                               │
                                               ▼
                              ┌──────────────────────────────────┐
                              │  LockScreenOverlay (OLED #000)   │
                              │  • Full Immersive Sticky Mode    │
                              │  • Screen Brightness to Minimum  │
                              │  • Lock Task Mode (Screen Pin)   │
                              └────────────────┬─────────────────┘
                                               │
                 ┌─────────────────────────────┼─────────────────────────────┐
                 │ (Tamper / Invalid Input)    │ (Valid Secret Unlock)       │ (Emergency Recovery)
                 ▼                             ▼                             ▼
   ┌───────────────────────────┐ ┌───────────────────────────┐ ┌───────────────────────────┐
   │  🪫 0% Dead Battery Flash │ │     Haptic Confirmation   │ │   3s Corner Long-Press    │
   │  • Displays for 1.8s      │ │  • Subtle silent vibration│ │  • Master PIN Keypad      │
   │  • Fades back to black    │ │  • Screen restored        │ │  • Emergency lockout exit │
   └───────────────────────────┘ └───────────────────────────┘ └───────────────────────────┘
```

---

## 📥 Download & Installation

### Option 1: GitHub Releases (Pre-built APK)
Every push to the `main` branch automatically generates tested, ready-to-install Android APKs via GitHub Actions:
1. Navigate to the **[Releases](https://github.com/your-username/GhostLock/releases)** tab in this repository.
2. Download the latest `GhostLock-release.apk` (or `GhostLock-debug.apk`).
3. On your Android device, open the downloaded file and select **Install** (ensure *Install from unknown sources* is enabled for your browser/file manager).

### Option 2: Build from Source
Clone the repository and build locally using Gradle:

```bash
# Clone the repository
git clone https://github.com/your-username/GhostLock.git
cd GhostLock

# Build debug APK
./gradlew assembleDebug

# Output APK location:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔒 Privacy & Threat Model

GhostLock is designed with zero-trust privacy:
- **No Internet Access**: The app does not request or possess `android.permission.INTERNET`. It cannot communicate with any remote server.
- **Zero Telemetry**: No Google Analytics, Firebase Analytics, Crashlytics, or telemetry SDKs.
- **Local Enclave**: Gestures, tap patterns, and emergency PINs are stored in private on-device shared preferences inaccessible to other applications.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0
- **UI Toolkit**: Jetpack Compose with Material Design 3
- **Architecture**: Model-View-ViewModel (MVVM) + Unidirectional Data Flow (`StateFlow`)
- **System Integration**:
  - `WindowInsetsControllerCompat` (Sticky immersive system bars)
  - `Activity.startLockTask()` (Native Android screen pinning)
  - `onUserLeaveHint()` (Lifecycle anti-home recapture)
- **Testing**: Robolectric JVM unit tests verifying gesture matching algorithms and repository persistence.

---

## 🤝 Contributing

Contributions, bug reports, and feature suggestions are welcome!
1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

Please review [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) before submitting.

---

## 📄 License

GhostLock is open-source software licensed under the **[Apache License, Version 2.0](LICENSE)**.

```
Copyright 2026 GhostLock Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
