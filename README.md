# 📚 Read-Out-Loud (Gemini Powered)

## Overview
A powerful Android text-to-speech (TTS) tool enhanced with Google Gemini AI for intelligent text processing and a premium listening experience.

## ✨ Key Features
- **🧠 Smart AI Content Cleaning:** Uses Gemini to strip away web garbage, markdown notation, and tables, leaving only clean text for the TTS engine.
- **🔊 Intuitive Volume Controls:** Automatically sets volume to 80% on startup. A dedicated button cycles through **80% -> 85% -> 90%** for the perfect volume level.
- **♻️ Professional API Key Rotation:** Inspired by high-reliability systems, it rotates through multiple Gemini API keys and models (Gemini-2.0-Flash-Lite, etc.) to handle quota limits gracefully.
- **🎤 Vietnamese Voice Selection:** Ability to choose from multiple high-quality Google TTS Vietnamese voices.
- **💎 Premium Dark Mode UI:** Modern, high-contrast interface designed for focus and ease of use.

## 🛠 Tech Stack
- **Language:** Kotlin
- **Environment:** Android Studio Ladybug+
- **Minimum SDK:** 24 | **Target SDK:** 35
- **AI Engine:** Google AI SDK (via manual HTTP integration for rotation logic)
- **Libraries:**
  - **Network:** OkHttp 4.12.0
  - **Serialization:** Kotlinx Serialization
  - **Security:** EncryptedSharedPreferences (Jetpack Security Crypto)
  - **Image Loading:** Material Components

## ⚙️ Setup & Hardware Requirements
1. **Clone the repo:** `git clone https://github.com/skul9x/Read-Out-Loud.git`
2. **Build:** Open in Android Studio and let Gradle sync.
3. **API Keys:** Obtain one or more API keys from [Google AI Studio](https://aistudio.google.com/) and paste them into the "Settings" screen of the app.

---
© 2026 Nguyễn Duy Trường
