# Internationalization (i18n) & Dynamic Language Switcher Plan

GhostLock will automatically adapt to each user's device system language by default, while providing an in-app language switcher inside the Settings tab with full support for **English**, **French (Français)**, **Spanish (Español)**, **German (Deutsch)**, and **Portuguese (Português)**.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed during the planning clarification phase:
> - **Supported Languages**: English (`en`), French (`fr`), Spanish (`es`), German (`de`), and Portuguese (`pt`).
> - **Language Selector Placement**: Dedicated Language Selector card inside the **Settings & Safety** tab.
> - **Behavior**: Adapts to the **System Default** by default, with an optional manual in-app override that updates all text immediately without app restart.

---

## 1. Overview & Core Concept

- **What It Does**:
  - Automatically reads the device's system locale and resolves strings from standard Android language qualifiers (`values-fr`, `values-es`, `values-de`, `values-pt`).
  - Allows users to override the system language from the Settings tab by selecting their preferred language from a curated native-name list.
  - Updates the UI dynamically in Jetpack Compose so all labels, hints, dialogs, buttons, and error messages switch language seamlessly.
- **Target Audience**: Global users across Europe, Latin America, North America, and Portuguese-speaking countries who need discreet stealth security in their mother tongue.
- **Key Value**: Makes GhostLock intuitive, accessible, and natural for non-English speakers while preserving full offline privacy.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **System Default Auto-Adaptation**:
   - When a user on a French device installs GhostLock, the app launches in French (*"BOUCLIER FURTIF PRÊT"*, *"ARMER ET VERROUILLER"*, etc.).
2. **Opening Language Selector**:
   - User navigates to the **Settings** tab.
   - User taps the **Language / Langue / Idioma / Sprache** card.
   - A modern Material 3 dialog appears with native language labels and country/region flags or badges:
     - 🌐 *System Default* (*Défaut du système / Predeterminado*)
     - 🇬🇧 *English*
     - 🇫🇷 *Français*
     - 🇪🇸 *Español*
     - 🇩🇪 *Deutsch*
     - 🇵🇹 *Português*
3. **Instant Switching**:
   - Selecting any language instantly re-renders the app's strings, updates the bottom navigation tabs, and saves the preference in local encrypted storage.

### UI Styling & Layout
- Seamless integration with the existing Obsidian Dark (`#0A0E14`) and Electric Cyan (`#00E5FF`) aesthetic.
- Radio list with haptic touch feedback and clear checkmark indicator for the active language.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Standard Android Resource Qualifiers + Compose Dynamic Context**
  - *Chosen Approach*: Use Android's standard resource hierarchy (`res/values/strings.xml`, `res/values-fr/strings.xml`, `res/values-es/strings.xml`, `res/values-de/strings.xml`, `res/values-pt/strings.xml`) paired with dynamic locale context wrapping in Compose (`LocalConfiguration` and `LocalContext`).
  - *Why*: Guarantees that system locale detection works natively out-of-the-box on every Android version, while in-app manual selection updates Compose views immediately without requiring a full activity recreation or process kill.
- **Decision 2: Persistent User Preference in SecurityRepository**
  - *Chosen Approach*: Store the selected language tag (`""` for system default, or `"fr"`, `"es"`, `"de"`, `"pt"`, `"en"`) in `SecurityRepository`.
  - *Why*: Offline, persistent across app reboots, and zero external dependencies.

---

## 4. Technical Architecture & Component Mapping

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Localization Architecture                       │
└────────────────────────────────────────────────────────────────────────┘
                                    │
           ┌────────────────────────┼────────────────────────┐
           ▼                        ▼                        ▼
┌──────────────────────┐ ┌──────────────────────┐ ┌──────────────────────┐
│  SecurityRepository  │ │    LockViewModel     │ │   LocaleContextWrap  │
│ • selectedLanguageTag│ │ • setLanguage(tag)   │ │ • Updates Context    │
│ • SharedPreferences  │ │ • StateFlow<UiState> │ │ • LocalConfiguration │
└──────────────────────┘ └──────────────────────┘ └──────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                    Jetpack Compose UI (String Resources)               │
│                                                                        │
│   res/values/          res/values-fr/       res/values-es/             │
│   (English Default)    (French)             (Spanish)                  │
│                                                                        │
│   res/values-de/       res/values-pt/                                  │
│   (German)             (Portuguese)                                    │
└────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                  LanguageSelectorDialog (Settings Tab)                 │
│  • System Default  • English  • Français  • Español  • Deutsch  • PT   │
└────────────────────────────────────────────────────────────────────────┘
```

### Complete Translation Scope
Every user-facing string will be mapped and localized across all 5 languages:
- Bottom Navigation labels (Arm, Practice, Settings).
- Status card & readiness labels (*"STEALTH SHIELD READY"*, *"ARM & ENGAGE STEALTH LOCK"*, etc.).
- Active unlock descriptions (Continuous Symbol, Rhythmic Taps, Quadrant Matrix).
- Deceptive shutdown & dead battery alerts (*"0% Battery"*, *"Connect charger to power on"*, *"Shutting down..."*).
- Practice sandbox instructions & accuracy match feedback (*"Match confirmed!"*, *"Taps registered"*, etc.).
- Emergency fail-safe dialogs and backup PIN prompts.
- Hardware button lockdown toggles and descriptions.
- About & open-source license attribution notices.
