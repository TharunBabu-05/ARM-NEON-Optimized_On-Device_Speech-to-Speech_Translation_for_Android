<p align="center">
  <img src="assets/hero.svg" width="1000" alt="Voice Translator banner" />
</p>

<h1 align="center">Voice Translator</h1>
<p align="center"><b>English &lt;-&gt; Hindi voice translation for Android, built with Kotlin and ML Kit</b></p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/ML%20Kit-Translation-4285F4?style=flat-square&logo=google&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Material%203-6750A4?style=flat-square&logo=materialdesign&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-0EA5E9?style=flat-square" />
</p>

<p align="center">
  <a href="#-overview">Overview</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-installation-guide">Install</a> •
  <a href="#-usage-instructions">Usage</a> •
  <a href="#-project-structure">Structure</a> •
  <a href="#-roadmap">Roadmap</a>
</p>

<p align="center">
  <img src="https://github.com/Anmol-Baranwal/Cool-GIFs-For-GitHub/assets/74038190/9be4d344-6782-461a-b5a6-32a07bf7b34e" width="520" alt="Animated hello" />
</p>

---

## 🚀 Overview

A polished Android voice translator that combines speech input, ML Kit translation, and text-to-speech playback in a single on-device experience. Translation models are cached locally after first download, so the app works offline for translation. Voice input uses Android SpeechRecognizer and may depend on device speech services.

> **Why it matters:** Private, fast, and practical translation helps people communicate in real-world settings without relying on cloud translation APIs.

---

## ✨ Key Features

- **Bidirectional translation** between English and Hindi
- **Voice input** with partial results and listening overlay
- **Text-to-speech playback** with language-aware locale switching
- **Swap languages** with instant UI updates
- **Copy & share** translated text
- **Material 3 UI** with light/dark theming and Lottie animation

---

## 🧠 How It Works

1. User speaks or types in the source language.
2. SpeechRecognizer produces text (partial + final results).
3. TranslatorViewModel triggers ML Kit translation.
4. UI updates in real time with loading and final output.
5. TextToSpeech reads the translation aloud.

> **Offline note:** Translation works offline after the model download completes. Speech recognition behavior can vary by device and may require network access.

---

## 🏗️ Architecture

<p align="center">
  <img src="https://via.placeholder.com/1200x520/0F172A/94A3B8?text=Architecture+Diagram+Placeholder" width="900" alt="Architecture diagram placeholder" />
</p>

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

---

## 🛠️ Tech Stack

| Layer | Tools |
|------|------|
| Language | Kotlin, Coroutines, StateFlow |
| UI | Material 3, View Binding, Lottie |
| ML | Google ML Kit Translation |
| Speech | SpeechRecognizer, TextToSpeech |
| Build | Gradle (KTS), Android Studio |

---

## 📸 Demo / Screenshots

<p align="center">
  <img src="https://user-images.githubusercontent.com/74038190/215768208-3bf3dda8-eeea-40ee-a58b-f5ac529685bf.gif" width="720" alt="Android demo placeholder" />
</p>

<p align="center">
  <img src="assets/ui-demo.gif" width="720" alt="UI demo animation" />
</p>

<p align="center">
  <img src="assets/ui-light.svg" width="420" alt="Light theme UI" />
  <img src="assets/ui-dark.svg" width="420" alt="Dark theme UI" />
</p>

---

## ⚙️ Installation Guide

1. Clone the repository:

```bash
git clone https://github.com/TharunBabu-05/ARM-NEON-Optimized_On-Device_Speech-to-Speech_Translation_for_Android.git
```

2. Open in Android Studio (Hedgehog or newer)
3. Sync Gradle and run on an Android device

### Build Debug APK

```bash
./gradlew assembleDebug
```

APK output:
- app/build/outputs/apk/debug/app-debug.apk
- VoiceTranslator-debug.apk (repo root)

---

## ▶️ Usage Instructions

- Tap **Voice** to start listening
- Speak in the selected language
- Tap **Translate** to get results
- Tap **Speaker** to listen to the translation
- Use **Swap** to switch languages instantly

---

## 📂 Project Structure

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

## 🔌 API / Modules

| Module | Responsibility |
|--------|----------------|
| TranslationRepository | Model download + translation calls |
| TranslatorViewModel | UI state, translation flow, model readiness |
| TranslatorUiState | StateFlow-backed UI state |
| MainActivity | Speech input, UI actions, TTS output |
| SplashActivity | Branded splash and entry flow |

---

## 📊 Performance / Results

| Metric | Status |
|--------|--------|
| Translation latency | TBD (varies by device) |
| Model download size | ML Kit models (~30MB each, once) |
| Offline translation | Supported after download |

---

## 🧭 Comparison (On-device vs Cloud)

| Capability | This App | Typical Cloud Translator |
|------------|----------|--------------------------|
| Privacy | On-device after download | Server-side processing |
| Offline translation | Yes | No |
| Network dependency | Limited | Required |
| Latency | Low after download | Variable |

---

## 🌍 Real-world Applications

- Travel and navigation
- Education and language learning
- Workplace collaboration
- Customer support kiosks
- Accessibility and assisted communication

---

## 💼 Recruiter-Friendly Highlights

- MVVM architecture with StateFlow-driven UI
- Integration of Android SpeechRecognizer + TextToSpeech
- ML Kit translation with model lifecycle management
- Clean Material 3 UI with custom components
- Coroutines for async translation pipeline

---

## 🧪 Roadmap

- Add more languages through ML Kit selection
- Improve offline speech recognition support
- Add waveform visualization while listening
- Explore device-specific acceleration (NNAPI / ARM optimizations)

---

## 🤝 Contributing Guidelines

1. Fork the repo
2. Create a feature branch
3. Make changes with clear commit messages
4. Submit a pull request with screenshots or notes

---

## 📜 License

MIT License. See LICENSE.

---

## 🙌 Acknowledgements

- Google ML Kit Translation
- Material Design 3
- Lottie animations

---

## 📬 Contact / Author

- GitHub: https://github.com/TharunBabu-05

---

## 📈 GitHub Stats

<p align="center">
  <img src="https://github-readme-stats.vercel.app/api?username=TharunBabu-05&show_icons=true&theme=transparent" />
  <img src="https://github-readme-stats.vercel.app/api/top-langs/?username=TharunBabu-05&layout=compact&theme=transparent" />
</p>

<p align="center">
  <img src="https://github-readme-activity-graph.vercel.app/graph?username=TharunBabu-05&theme=github-compact" />
</p>

---

<details>
  <summary><b>Implementation Notes</b></summary>
  <br />
  <ul>
    <li>Translation models are downloaded on first launch and cached locally.</li>
    <li>Voice input uses Android SpeechRecognizer and may rely on device services.</li>
    <li>The listening overlay uses a Lottie animation in res/raw.</li>
  </ul>
</details>

<details>
  <summary><b>Why This Project Matters</b></summary>
  <br />
  <ul>
    <li>Shows end-to-end on-device ML integration.</li>
    <li>Demonstrates speech UX design and state management.</li>
    <li>Highlights Android best practices in a production-style app.</li>
  </ul>
</details>
