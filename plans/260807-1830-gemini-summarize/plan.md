# Plan: Gemini News Summarization & Dual-Button Main UI

Created: 2026-08-07
Status: 🟡 In Progress

## Overview
This plan adds a "Tóm tắt" (Summarize) button alongside the existing "AI text" button on the main screen (`activity_main.xml`). The UI is updated to split the top action bar into a 50/50 balanced layout containing "AI Text" on the left and "Tóm tắt" on the right. When the user taps "Tóm tắt", the text content in the main text area is sent to the Gemini API with a specialized driver-focused news summarization prompt to produce numbered key takeaways without greetings or conversational filler.

## UI/UX Design Strategy
- **Balanced Dual-Button Row**: Convert the single full-width card into a horizontal container with two equal-width Material 3 cards/buttons (50/50 split).
- **Icons & Visual Feedback**: Include `@drawable/ic_auto_fix` for AI Text and a dedicated `@drawable/ic_summarize` (or list icon) for Tóm tắt.
- **State Management**: Disable both buttons during API processing and display real-time status updates ("Gemini đang tóm tắt...").

## Tech Stack & Architecture
- **Language**: Kotlin
- **UI**: Android Material 3 Components, ViewBinding
- **API Client**: `GeminiApiClient` with automatic key rotation and model fallback
- **Testing**: Robolectric, JUnit 4, MockK

## Phases

| Phase | Name | Status | Description |
|-------|------|--------|-------------|
| 01 | UI Layout & View Binding | ⬜ Pending | Split top button row into 50/50 "AI Text" and "Tóm tắt" buttons with Material 3 styling. |
| 02 | Gemini Summarize API Logic | ⬜ Pending | Extend `GeminiApiClient` with `summarizeTextWithGemini` method using driver-focused prompt. |
| 03 | Integration & Verification | ⬜ Pending | Wire up click handlers, loading states, error handling, status updates, and unit test suites. |

## Quick Commands
- Run Phase 1 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.SummarizeButtonLayoutTest"`
- Run Phase 2 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.data.GeminiSummarizeApiTest"`
- Run Phase 3 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.SummarizeIntegrationTest"`
- Run All Tests: `./gradlew test`
