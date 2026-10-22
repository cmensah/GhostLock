# GhostLock — Open Source Publication & GitHub Release Infrastructure

GhostLock is being prepared for public open-source release on GitHub with an Apache 2.0 license, automated CI/CD APK releases via GitHub Actions, high-visibility SEO documentation covering multifaceted privacy use cases, and in-app open-source attribution.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed during the planning clarification phase:
> - **Open Source License**: Apache License 2.0 (Permissive with patent grant and corporate contributor protections).
> - **GitHub Actions Release Trigger**: Automated APK build and GitHub Release generation on every push to the `main` branch, plus manual `workflow_dispatch` trigger.
> - **SEO & Positioning Priority**: Privacy and stealth anti-tamper security tool, highlighting deception defenses and real-world protective scenarios.

---

## 1. Overview & Core Concept

- **What It Does**: Transforms any Android device into an indistinguishable "powered-off" screen with zero backlight bleed, suppresses hardware volume and back buttons, catches touches silently, and only unlocks upon executing a secret unistroke symbol, rhythmic tap cadence, or quadrant pattern—while deterring snoops with simulated 0% empty battery flashes.
- **Target Audience & Persona**: Privacy-conscious individuals, professionals handling sensitive data in public spaces, commuters avoiding shoulder-surfing, journalists/activists safeguarding unattended hardware, and digital detox enthusiasts seeking tamper-resistant downtime.
- **Key Value**: Provides an unbreakable layer of optical stealth and social deception without requiring root, custom ROMs, or third-party cloud infrastructure.

### Multi-Faceted Use Cases

1. **Public Transit & Commuting Privacy**: Leave your phone on a table or seat next to you without anyone knowing it is running active background tasks or monitoring notifications.
2. **Anti-Shoulder-Surfing in High-Density Spaces**: In crowded cafes, libraries, or planes, bystanders cannot see PIN prompts or lock screens because the screen appears dead.
3. **Defense Against Device Grab-and-Snoop**: If someone picks up your device and presses the Volume or Power buttons, the screen flashes a realistic 🪫 depleted battery icon, convincing them the phone is completely out of power.
4. **Meeting & Classroom Stealth**: Keep the device running silently on a desk without attracting attention from supervisors, teachers, or colleagues.
5. **Deterrent for Snooping Friends & Family**: Discourage unwarranted browsing attempts with simulated low-battery shutdown and pitch-black unresponsive feedback.
6. **Zero-Distraction Focus & Digital Detox**: Put the phone into a state where you are not tempted by lock screen notifications or visual ambient displays.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Arm & Engage**: User selects their desired arming delay (0s instant up to 10s), taps **ARM & ENGAGE STEALTH LOCK**, and the screen smoothly fades into pitch-black OLED stillness.
2. **Stealth Unlock**: User inputs their secret continuous gesture, taps their rhythm, or taps the secret quadrants. A subtle haptic vibration confirms unlock without emitting any light, returning to the console.
3. **Deceptive Tamper Reaction**: If an unauthorized party draws random strokes or mashes physical volume/back buttons, a realistic 🪫 depleted battery warning displays for 1.8 seconds before returning to pure black.
4. **Emergency Recovery**: If the user forgets their gesture, pressing and holding the secret corner for 3 seconds summons a high-contrast emergency master recovery PIN keypad.
5. **Open Source & Settings Inspection**: Users can inspect the Apache 2.0 open-source terms, GitHub repository links, and release version details directly from the in-app Settings tab.

### Visual Identity & Theme
- **Theme**: Stealth Cyber Console & Pure OLED Black (`#000000`).
- **Color Tokens**:
  - Background: Deep Obsidian Dark (`#0A0E14`)
  - Elevated Cards: Stealth Surface (`#131922` / `#1A2332`)
  - Accent Primary: Electric Cyan (`#00E5FF`)
  - Status Armed: Matrix Green (`#00E676`)
  - Tamper & Battery Alert: Crimson Red (`#FF3333`)
  - Warning & Recovery: Amber (`#FFB300`)
- **Typography & Layout**: Standard 8dp grid, Material 3 bottom navigation with active pill indicators, monospace indicators for digits and timers, and accessible 48dp minimum touch targets.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Apache License 2.0**
  - *Chosen Approach*: Apache 2.0 with full copyright notices and standard grant of patent rights.
  - *Why*: Allows open community contribution, commercial usage, and forks while offering explicit patent protection for the author.
- **Decision 2: Automated Push-to-Main GitHub Actions Workflow**
  - *Chosen Approach*: A GitHub Actions workflow (`.github/workflows/release.yml`) running on pushes to `main` that builds unsigned release APK and debug APK, then publishes an automated GitHub Release with download links.
  - *Why*: Guarantees that every code update synced from AI Studio or git automatically yields a downloadable, ready-to-install `.apk` without manual builds.
- **Decision 3: Zero-External-Network Privacy Model**
  - *Chosen Approach*: 100% offline, local persistence via encrypted SharedPreferences.
  - *Why*: As a security application, users must have complete assurance that no keystrokes, gestures, or telemetry leave the device.
- **Decision 4: SEO-Optimized README with Rich Documentation**
  - *Chosen Approach*: Full documentation including badges, architecture diagrams, threat model, step-by-step installation, and open-source contribution guidelines.
  - *Why*: Maximizes GitHub discovery, star velocity, and organic search ranking for keywords like "Android stealth lock", "anti-tamper black screen", and "fake dead battery lock".

---

## 4. Technical Architecture & Release Pipeline

```
┌────────────────────────────────────────────────────────────────────────┐
│                        GhostLock Architecture                          │
└────────────────────────────────────────────────────────────────────────┘
                                    │
           ┌────────────────────────┼────────────────────────┐
           ▼                        ▼                        ▼
┌──────────────────────┐ ┌──────────────────────┐ ┌──────────────────────┐
│       Arm Tab        │ │     Practice Tab     │ │     Settings Tab     │
│ • Stealth status     │ │ • Method switcher    │ │ • Emergency corner   │
│ • Arm & lock button  │ │ • Real-time scoring  │ │ • Master PIN config  │
│ • Instant shortcut   │ │ • 🪫 Deception test  │ │ • Anti-tamper toggles│
│ • Active code glance │ │ • Drawing canvas     │ │ • About & Apache 2.0 │
└──────────────────────┘ └──────────────────────┘ └──────────────────────┘
           │                        │                        │
           └────────────────────────┼────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       LockScreenOverlay (OLED #000)                    │
│  • WindowInsetsController: immersive sticky system bars suppression    │
│  • FLAG_KEEP_SCREEN_ON + screenBrightness = 0.005f                     │
│  • KeyEvent interceptor (Volume Up/Down/Mute, Back)                    │
│  • Lock Task Mode (Screen Pinning) + onUserLeaveHint auto-recapture   │
└────────────────────────────────────────────────────────────────────────┘
           │                        │                        │
           ▼                        ▼                        ▼
┌──────────────────────┐ ┌──────────────────────┐ ┌──────────────────────┐
│    GestureMatcher    │ │  DeadBatteryOverlay  │ │  EmergencyPinDialog  │
│ Resampling, centroid │ │ 0% battery animation │ │ 3s secret corner     │
│ vector angle matching│ │ 1.8s deceptive flash │ │ master override      │
└──────────────────────┘ └──────────────────────┘ └──────────────────────┘

┌────────────────────────────────────────────────────────────────────────┐
│                      GitHub Actions CI/CD Pipeline                     │
│                                                                        │
│  git push main ──► Run Robolectric Tests ──► Build APK ──► GH Release │
└────────────────────────────────────────────────────────────────────────┘
```

### GitHub Synchronization & Deliverables Planned
1. **`LICENSE`**: Standard Apache License 2.0.
2. **`README.md`**: High-SEO documentation featuring badges, screenshots placeholder, feature matrix, installation instructions, build guides, and security threat model.
3. **`.github/workflows/release.yml`**: GitHub Actions workflow that executes tests, builds the APK using Gradle, and creates a GitHub Release with attached `.apk` binaries.
4. **`CONTRIBUTING.md` & `SECURITY.md`**: Standard open-source governance guidelines.
5. **In-App "About & Open Source" Section**: Add versioning, Apache 2.0 attribution, and GitHub repository links in `SettingsTabScreen.kt`.
6. **`metadata.json` Refinement**: Update description with focused keywords while preserving critical platform capabilities.
