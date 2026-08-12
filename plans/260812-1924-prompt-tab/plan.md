# Plan: Prompt Tab — TabLayout + ViewPager2 Multi-Screen Feature

Created: 2026-08-12
Status: ⬜ Pending

## Overview
Add a two-tab navigation system to `MainActivity` using Material 3 `TabLayout` + `ViewPager2` + `FragmentStateAdapter`. The existing main screen becomes the **"Read"** tab (left). A new **"Prompt"** tab (right) provides a text input field, two action buttons ("Make Prompt" and "Search Now"), and Gemini AI integration for multi-language news research. Inter-tab communication uses a Shared `ViewModel` scoped to the Activity.

### Feature Summary
1. **Read Tab**: The entire current `activity_main.xml` content (text area, AI buttons, reading controls, progress bar) wrapped inside a Fragment.
2. **Prompt Tab**: New screen with:
   - A `TextInputEditText` for the user to type a search topic.
   - **"Make Prompt"** button: reads the template from `res/raw/prompt_template.txt` (copied from `1.txt`), replaces the placeholder `{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}` with the user's input, and copies the result to the system clipboard.
   - **"Search Now"** button: performs the same template substitution and sends the full prompt to the Gemini API. Displays the result in a scrollable text area. After the result loads, a **"Tóm tắt"** button appears at the top. Tapping it switches to the "Read" tab and triggers the existing summarize flow on the Gemini response.

## Architecture Decisions
- **TabLayout + ViewPager2 + FragmentStateAdapter**: The standard Android approach for tab-based navigation in 2026.
- **Shared ViewModel (`MainSharedViewModel`)**: Activity-scoped ViewModel using `activityViewModels()` delegate for decoupled inter-fragment communication.
- **`res/raw/prompt_template.txt`**: The `1.txt` content is bundled as a raw resource.
- **Reuse `GeminiApiClient`**: The existing API client with key rotation and model fallback is reused.

## UI/UX Design Highlights
- **Tab Icons + Text Labels**: Both tabs display Material Symbols icons alongside text labels (M3 guideline: never mix icon-only and text-only tabs).
- **Content-Width Tab Indicator**: `tabIndicatorFullWidth="false"` for clean M3 style.
- **Input UX**: Clear button (✕), keyboard "Done" action, auto-hide keyboard, button state tied to TextWatcher.
- **Snackbar over Toast**: All user feedback uses Material 3 Snackbar (clipboard copy, empty input, cross-tab transition).
- **Inline Loading**: Prompt tab uses a local loading card (not full-screen overlay) with CircularProgressIndicator.
- **Error Card with Retry**: API errors display in a `colorErrorContainer` card with retry button.
- **Smooth Animations**: Result card fade-in (300ms), "Tóm tắt" button slide-down (200ms), button press scale (150ms).
- **Empty State**: Centered icon + hint text shown before first search.
- **Cross-Tab Transition**: Smooth ViewPager2 scroll animation + Snackbar confirmation on Read tab.

## Tech Stack
- **Language**: Kotlin
- **UI**: Material 3 TabLayout, ViewPager2, ViewBinding, FragmentStateAdapter
- **State Management**: Shared ViewModel with LiveData
- **API Client**: `GeminiApiClient` (existing, with key rotation)
- **Testing**: Robolectric, JUnit 4, MockK

## Phases

| Phase | Name | Status | Description |
|-------|------|--------|-------------|
| 01 | Infrastructure & Tab Shell | ⬜ Pending | Add ViewPager2 dep, create TabLayout + ViewPager2 layout with icons + M3 styling, build FragmentStateAdapter, extract Read tab into ReadFragment, create empty PromptFragment with empty state. |
| 02 | Prompt Tab UI & Make Prompt | ⬜ Pending | Build PromptFragment layout (input with clear btn, dual buttons, result card, empty state, error card), bundle `1.txt` as raw resource, implement "Make Prompt" clipboard + Snackbar + keyboard handling. |
| 03 | Search Now & Gemini Integration | ⬜ Pending | Implement "Search Now" with inline loading card, Gemini API call, result display with fade-in animation, error card with retry, model badge. |
| 04 | Summarize Cross-Tab Flow | ⬜ Pending | "Tóm tắt" button with slide-down animation, SharedViewModel, smooth tab switch, cross-tab Snackbar, button press scale animation, state persistence. |
| 05 | Integration & Verification | ⬜ Pending | E2E verification, full test suite, APK build, ADB visual testing with 7 screenshot states. |

## Quick Commands
- Run Phase 1 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.TabLayoutInfrastructureTest"`
- Run Phase 2 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabMakePromptTest"`
- Run Phase 3 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabSearchNowTest"`
- Run Phase 4 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.SummarizeCrossTabFlowTest"`
- Run Phase 5 Test: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.PromptTabIntegrationTest"`
- Run All Tests: `./gradlew test`
