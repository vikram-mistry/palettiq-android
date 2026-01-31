# Palettiq 🎨

**Palettiq** is a modern, AI-powered wallpaper generator app for Android. Generate stunning, unique wallpapers on-device with beautiful color palettes and patterns — no internet required.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

---

## ✨ Features

### 🖼️ Wallpaper Generation
- **10+ Generation Algorithms**: Gradient, Geometric, Noise, Bokeh, Fluid, Topographic, AMOLED, Shape, Pastel, and Cartoon styles
- **On-Device Generation**: All wallpapers are generated locally — no server calls, complete privacy
- **High Resolution Output**: Wallpapers generated at device-native resolution for crisp display

### 🎨 Color Palette System
- **Smart Color Palettes**: Choose from curated palettes like Sunset, Ocean, Forest, Neon, Pastel, and more
- **Palette Lock**: Lock your favorite colors and generate wallpapers using only those colors
- **Random Mode**: Let the app surprise you with random color combinations

### 🌙 Dark Mode & AMOLED
- **Dedicated Dark Section**: Explore wallpapers optimized for dark themes
- **True AMOLED Blacks**: Special AMOLED generator creates wallpapers with pure black backgrounds for OLED screens
- **System Theme Support**: Automatically adapts to your device's light/dark theme

### ❤️ Favorites & Downloads
- **Save Favorites**: Keep your best wallpapers in a personal collection
- **Download to Gallery**: Save wallpapers to your device's Downloads folder
- **Quick Apply**: Set wallpapers to home screen, lock screen, or both with one tap

### ⚙️ Customization
- **Auto Wallpaper Change**: Schedule automatic wallpaper changes (hourly, daily, weekly)
- **Generation Preferences**: Control generation frequency and style preferences
- **Material You**: Dynamic theming support on Android 12+

---

## 🛠️ Tech Stack

### Language
- **Kotlin** (100%) — Modern, concise, and safe

### UI Framework
- **Jetpack Compose** — Declarative UI toolkit for native Android
- **Material 3 (Material You)** — Latest Material Design components with dynamic color support

### Architecture
- **MVVM** (Model-View-ViewModel) — Clean separation of concerns
- **Clean Architecture** — Domain, Data, and Presentation layers
- **Unidirectional Data Flow** — Predictable state management

### Libraries & Frameworks

| Category | Library |
|----------|---------|
| **Dependency Injection** | Hilt (Dagger) |
| **Local Database** | Room |
| **Preferences** | DataStore |
| **Async/Concurrency** | Kotlin Coroutines & Flow |
| **Image Loading** | Coil |
| **Background Tasks** | WorkManager |
| **Navigation** | Navigation Compose |
| **Splash Screen** | AndroidX Splash Screen API |

### Graphics & Rendering
- **Android Canvas API** — Custom bitmap generation
- **RadialGradient, LinearGradient, SweepGradient** — Smooth color transitions
- **Path & Paint APIs** — Vector shape rendering
- **Perlin/Simplex Noise** — Procedural texture generation

---

## 📁 Project Structure

```
app/
├── src/main/java/com/wallcraft/app/
│   ├── data/                    # Data layer
│   │   ├── local/               # Room database, DAOs, entities
│   │   └── repository/          # Repository implementations
│   ├── di/                      # Hilt dependency injection modules
│   ├── domain/                  # Domain layer
│   │   ├── model/               # Domain models
│   │   └── repository/          # Repository interfaces
│   ├── generation/              # Wallpaper generation algorithms
│   │   ├── GradientGenerator.kt
│   │   ├── GeometricGenerator.kt
│   │   ├── NoiseGenerator.kt
│   │   ├── BokehGenerator.kt
│   │   ├── FluidGenerator.kt
│   │   ├── TopographicGenerator.kt
│   │   ├── AMOLEDGenerator.kt
│   │   ├── ShapeGenerator.kt
│   │   ├── PastelGenerator.kt
│   │   ├── CartoonGenerator.kt
│   │   └── WallpaperGenerator.kt
│   ├── ui/                      # Presentation layer
│   │   ├── components/          # Reusable Compose components
│   │   ├── theme/               # Material theme configuration
│   │   ├── discover/            # Discover screen
│   │   ├── dark/                # Dark wallpapers screen
│   │   ├── favourites/          # Favorites screen
│   │   ├── detail/              # Wallpaper detail screen
│   │   ├── settings/            # Settings screen
│   │   └── navigation/          # Navigation graph
│   └── util/                    # Utility classes
└── src/main/res/                # Resources (layouts, strings, etc.)
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Minimum SDK: 24 (Android 7.0)

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Palettiq-android.git
   cd Palettiq-android
   ```

2. **Open in Android Studio**
   - File → Open → Select the project folder

3. **Sync Gradle**
   - Click "Sync Project with Gradle Files"

4. **Run the app**
   - Select a device/emulator
   - Click Run (▶️)

---

## 📱 Screenshots

*Coming soon*

---

## 📄 License

This project is proprietary software. All rights reserved.

---

## 👨‍💻 Author

Developed with ❤️ using Kotlin and Jetpack Compose.

---

## 🙏 Acknowledgments

- Material Design 3 Guidelines
- Android Developers Documentation
- Jetpack Compose Samples
