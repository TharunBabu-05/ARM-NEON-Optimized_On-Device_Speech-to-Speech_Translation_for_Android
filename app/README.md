# Voice Translator - English ↔ Hindi

A professional voice translator Android app built with Kotlin that translates between English and Hindi with both text and voice input/output support.

![Voice Translator](screenshots/app_preview.png)

## Features

- **Bidirectional Translation**: Translate from English to Hindi and Hindi to English
- **Voice Input**: Speak in either language using speech recognition
- **Text-to-Speech**: Listen to translations in the target language
- **Offline Translation**: Uses ML Kit for on-device translation (models downloaded once)
- **Modern UI**: Material Design 3 with light/dark theme support
- **Copy & Share**: Easily copy or share translations

## Screenshots

| Light Mode | Dark Mode |
|------------|-----------|
| Main Screen | Main Screen |
| Voice Input | Voice Input |

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Material Design 3 with View Binding
- **Translation**: Google ML Kit Translation API
- **Speech**: Android Speech Recognition & Text-to-Speech
- **Animation**: Lottie

## Requirements

- Android 7.0 (API 24) or higher
- Internet for initial model download
- Microphone permission for voice input

## Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/VoiceTranslator.git
```

2. Open the project in Android Studio (Hedgehog or later)

3. Sync Gradle and build the project

4. Run on a device or emulator

## Project Structure

```
app/
├── src/main/
│   ├── java/com/voicetranslator/
│   │   ├── data/
│   │   │   ├── model/
│   │   │   │   ├── Language.kt
│   │   │   │   ├── ModelDownloadState.kt
│   │   │   │   └── TranslationResult.kt
│   │   │   └── repository/
│   │   │       └── TranslationRepository.kt
│   │   └── ui/
│   │       ├── MainActivity.kt
│   │       ├── SplashActivity.kt
│   │       ├── state/
│   │       │   └── TranslatorUiState.kt
│   │       └── viewmodel/
│   │           └── TranslatorViewModel.kt
│   └── res/
│       ├── drawable/
│       ├── layout/
│       ├── values/
│       └── raw/
```

## How It Works

### Translation
The app uses Google ML Kit's Translation API which works offline after initial model download. When you first launch the app, it downloads the English-Hindi translation models (~30MB each).

### Voice Input
Speech recognition is handled by Android's built-in `SpeechRecognizer` API. The app automatically selects the correct language based on the source language setting.

### Text-to-Speech
Android's `TextToSpeech` engine reads out translations. Make sure you have the required language packs installed on your device.

## Permissions

- **INTERNET**: Required to download translation models
- **RECORD_AUDIO**: Required for voice input feature

## Building for Release

1. Create a keystore file
2. Configure signing in `app/build.gradle.kts`
3. Build signed APK/Bundle:
```bash
./gradlew assembleRelease
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Google ML Kit for translation
- Material Design for UI components
- Lottie for animations
