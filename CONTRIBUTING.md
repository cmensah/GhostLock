# Contributing to GhostLock

Thank you for your interest in contributing to GhostLock! We welcome contributions, bug reports, and enhancements from developers of all skill levels.

## How Can I Contribute?

### Reporting Bugs
- Check existing issues to see if the bug has already been reported.
- If not, create a new issue detailing:
  - Device model and Android OS version.
  - Steps to reproduce the unexpected behavior.
  - Expected vs actual outcome.

### Suggesting Enhancements
- Open a feature request issue describing the enhancement and its security or stealth benefits.

### Submitting Pull Requests
1. Fork the repository and create your branch from `main`:
   ```bash
   git checkout -b feature/my-new-feature
   ```
2. Write clean Kotlin code following modern Jetpack Compose and Material 3 best practices.
3. Ensure all Robolectric unit tests pass:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Commit your changes with clear, descriptive commit messages.
5. Push to your fork and submit a Pull Request targeting `main`.

## Code Style & Architecture
- Follow official Kotlin coding conventions.
- State management must adhere to unidirectional data flow (UDF) via `ViewModel` and `StateFlow`.
- Maintain 100% offline security: Never introduce network dependencies or permissions.
