<p align="center">
  <img src="assets/hero.svg" width="1000" alt="Voice Translator banner" />
</p>

<h1 align="center">Voice Translator (English &lt;-&gt; Hindi)</h1>

<p align="center">
  On-device speech translation for Android with speech input, instant text translation, and voice playback.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/ML%20Kit-Translation-4285F4?style=flat-square&logo=google&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Material%203-6750A4?style=flat-square&logo=materialdesign&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-0EA5E9?style=flat-square" />
</p>

---

## Highlights

- On-device translation with Google ML Kit (offline after initial model download)
- Speech input via Android SpeechRecognizer with partial results
- Text-to-speech playback with language-aware locale switching
- MVVM with StateFlow-based UI state
- Material 3 UI, Lottie listening animation, and quick actions (swap, copy, share)

---

## UI Preview

<p align="center">
  <img src="assets/ui-demo.gif" width="720" alt="UI demo animation" />
</p>

<p align="center">
  <img src="assets/ui-light.svg" width="420" alt="Light theme UI" />
  <img src="assets/ui-dark.svg" width="420" alt="Dark theme UI" />
</p>

---

## Architecture

This app follows MVVM and keeps the translation logic inside a repository. The UI layer (MainActivity) listens to a StateFlow, triggers translation, and controls speech input/output.

```mermaid
flowchart LR
    Mic[Microphone] --> SR[SpeechRecognizer]
    SR --> UI[MainActivity]
    UI --> VM[TranslatorViewModel]
    VM --> Repo[TranslationRepository]
    Repo --> ML[ML Kit Translator]
    ML --> VM
    VM --> TTS[TextToSpeech]
    TTS --> Speaker[Speaker]
```

### Key Components

- MainActivity: speech input, text input, and UI actions (swap, copy, share, speak)
- TranslatorViewModel: owns UI state and translation lifecycle
- TranslationRepository: ML Kit model download and translation calls
- Lottie overlay: listening animation during voice input

---

## App Flow

1. App starts and downloads English and Hindi translation models (Wi-Fi required on first run).
2. User types or speaks; source text updates the StateFlow.
3. Translate triggers ML Kit, updates UI with loading and results.
4. Text-to-speech plays the translated result.

---

## Tech Stack

- Kotlin, Coroutines, StateFlow
- AndroidX, Material 3, View Binding
- ML Kit Translation API
- Android SpeechRecognizer and TextToSpeech
- Lottie animations

---

## Requirements

- Android 7.0 (API 24) or higher
- Internet connection for initial model download
- Microphone permission for voice input

---

## Permissions

| Permission | Purpose |
|------------|---------|
| INTERNET | Download translation models (first run) |
| RECORD_AUDIO | Voice input |

---

## Build and Run

1. Clone the repository:

```bash
git clone https://github.com/TharunBabu-05/ARM-NEON-Optimized_On-Device_Speech-to-Speech_Translation_for_Android.git
```

2. Open in Android Studio (Hedgehog or newer)
3. Sync Gradle and run the app on a device

### Build Debug APK

```bash
./gradlew assembleDebug
```

APK output:
- app/build/outputs/apk/debug/app-debug.apk
- VoiceTranslator-debug.apk (repo root)

---

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

---

## Notes

- Translation models are downloaded on first launch and cached locally.
- Voice input is optional; text translation works without microphone permission.
- The listening overlay uses a Lottie animation from res/raw.

---

## Acknowledgments

- Google ML Kit Translation
- Material Design 3
- Lottie for animations
