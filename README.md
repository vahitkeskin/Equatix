# Equatix: Matrix Math Puzzle 🧩

<div align="center">
  <a href="https://play.google.com/store/apps/details?id=com.vahitkeskin.equatix" target="_blank">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="80">
  </a>
</div>

<p align="center">
  <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/fullbackground.png" alt="Equatix Featured">
</p>

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.0-4285F4.svg?style=flat&logo=jetpack-compose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com)
[![iOS](https://img.shields.io/badge/Platform-iOS-000000.svg?style=flat&logo=apple)](https://www.apple.com/ios/)
[![Desktop](https://img.shields.io/badge/Platform-Desktop-0078D6.svg?style=flat&logo=windows)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Equatix** is a modern, cross-platform brain training game built with **Kotlin Multiplatform (KMP)** and **Jetpack Compose**. It challenges players to complete mathematical matrices by finding the correct numbers to satisfy row and column operations.

Designed with a sleek "Glassmorphism" UI, it offers a premium, minimalist aesthetic while providing rigorous mental exercise.

---

## 🌟 Features

### 🎮 Dynamic Gameplay
- **Procedural Generation**: Every puzzle is unique. No two games are the same.
- **Smart Difficulty**:
    - 🟢 **Easy**: Addition & Subtraction (3x3 Grid)
    - 🟠 **Medium**: Includes Multiplication (4x4 Grid)
    - 🔴 **Hard**: Full Arithmetic with Division (5x5 Grid)
- **Timer & Scoring**: Race against the clock to earn higher scores based on speed and accuracy.

### 🎨 Visual & UX Design
- **Glassmorphism Theme**: Translucent layers, blur effects, and smooth gradients.
- **Dark/Light Mode**: Fully responsive theme switching with automatic system detection.
- **Animations**: Fluid transitions, detailed interaction feedback, and particle effects.
- **Responsive Layout**: Perfectly adapts to Android phones, tablets, iOS devices, and Desktop windows.

### 📊 Progression & Logic
- **History Tracking**: Saves every completed game with detailed stats (Time, Moves, Score).
- **Hint System**: Intelligent hints that reveal cells when you're stuck.
- **Validation**: Real-time checking of rows and columns (Green = Correct, Red = Incorrect).

### 💰 Monetization
- **AdMob Integration**: Support for banner, interstitial, and rewarded video ads.
- **Premium (Upcoming)**: Infrastructure ready for In-App Purchases to remove ads.

---

## 🛠️ Tech Stack & Architecture

Equatix is a showcase of modern **Kotlin Multiplatform** development practices.

| Category | Technology | Description |
| :--- | :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) | 100% Kotlin codebase. |
| **UI Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) | Declarative UI shared across Android, iOS, and Desktop. |
| **Architecture** | MVVM + Clean Architecture | Separation of concerns with Domain, Data, and UI layers. |
| **Navigation** | [Voyager](https://voyager.adriel.cafe/) | Type-safe, multiplatform navigation library. |
| **Dependency Injection** | [Koin](https://insert-koin.io/) | Lightweight dependency injection framework. |
| **Local Database** | [Room (KMP)](https://developer.android.com/kotlin/multiplatform/room) | SQLite abstraction for saving game history and state. |
| **Key-Value Store** | [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) | Asynchronous storage for user preferences (Theme, Sound, etc.). |
| **Asynchronous** | [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html) | Managing background threads and reactive data streams. |
| **Ads** | [Google Mobile Ads SDK](https://developers.google.com/admob) | Monetization via AdMob (Android/iOS). |

---

## 📂 Project Structure

The project follows a standard KMP source set structure:

```
Equatix/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/        # 🧠 Shared Logic & UI (95% of code)
│   │   │   ├── kotlin/
│   │   │   │   ├── domain/    # Models, Repositories, UseCases
│   │   │   │   ├── data/      # Room DB, API, DataSources
│   │   │   │   ├── di/        # Koin Modules
│   │   │   │   ├── ui/        # Compose Screens, Components, Theme
│   │   │   │   └── platform/  # Expect/Actual interfaces
│   │   ├── androidMain/       # 🤖 Android-specific implementations
│   │   ├── iosMain/           # 🍎 iOS-specific implementations
│   │   └── desktopMain/       # 🖥️ Desktop-specific implementations
├── gradle/                    # Build configuration
└── iosApp/                    # iOS Xcode project entry point
```

---

## 🚀 Getting Started

### Prerequisites
- **JDK 17** or higher.
- **Android Studio Ladybug** (or newer) with KMP plugin enabled.
- **Xcode 15+** (for iOS development on macOS).

### Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/vahitkeskin/Equatix.git
    cd Equatix
    ```

2.  **Open in Android Studio**:
    - Wait for Gradle sync to complete.

3.  **Run the App**:

    *   **Android**: Select `composeApp` configuration and click **Run**.
    *   **Desktop**: Run the Gradle task:
        ```bash
        ./gradlew :composeApp:run
        ```
    *   **iOS**: Open `iosApp/iosApp.xcodeproj` in Xcode or run via KMP wizard.

---

## 📸 Screenshots

| <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image1.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image2.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image3.png" width="250"> |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image4.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image5.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image6.png" width="250"> |

---

## 🤝 Contribution

Contributions are welcome! If you'd like to improve Equatix, please follow these steps:

1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

<div align="center">
  <p>Made with ❤️ by <b>Vahit Keskin</b></p>
  <p>
    <a href="https://linkedin.com/in/vahitkeskin">LinkedIn</a> •
    <a href="https://github.com/vahitkeskin">GitHub</a>
  </p>
</div>