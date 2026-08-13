# Plan: Prompt Result Action Bar ("Tóm tắt", "Read", "Show") & Fullscreen Reader

Created: 2026-08-13
Status: ✅ Completed

## Problem Statement & Objective
When users run "Search Now" on the Prompt tab, Gemini returns AI research results formatted in Markdown. Currently, the Result Card only provides a single "Tóm tắt" (Summarize) button.
Users need a richer, multi-action reading experience directly from the Search Now result card:
1. **3-Button Action Row**: Align "Tóm tắt", "Read", and "Show" buttons side-by-side in a horizontal row above the result view with anti-text-clipping optimizations (`layout_width="0dp"`, `layout_weight="1"`, zero insets, `minWidth="0dp"`, compact padding, and `app:autoSizeTextType="uniform"`).
2. **Auto-Read Flow ("Read" Button)**: Clicking "Read" transfers the Gemini result content to the "Read" tab's text area (`editText`) and immediately triggers the TTS reader to read the content aloud.
3. **Fullscreen Reader Screen ("Show" Button)**: Clicking "Show" opens a dedicated fullscreen reader with vertical scrollbars, rich Markdown styling, and swipe-back gesture navigation to seamlessly return to the Prompt tab.

---

## Phase Overview

| Phase | Name | Status | Description |
|---|---|---|---|
| 01 | 3-Button Action Row UI in Prompt Tab | ✅ Completed | Build a 3-button horizontal row ("Tóm tắt", "Read", "Show") in `fragment_prompt.xml` with zero-insets & auto-sizing to eliminate text clipping, bind views in `PromptFragment.kt`, and add UI layout unit tests. |
| 02 | Cross-Tab Auto-Read Flow | ✅ Completed | Extend `MainSharedViewModel.kt` with auto-read event, wire `PromptFragment.kt` "Read" button to switch to Read tab and trigger TTS reading immediately in `ReadFragment.kt`, with test suite. |
| 03 | Fullscreen Reader Activity with Swipe-Back & Scrollbar | ✅ Completed | Create `FullScreenReaderActivity` with Material 3 edge-to-edge layout, vertical scrollbars, `Markwon` Markdown rendering, and swipe-back gesture detection to return to Prompt tab. |
| 04 | Integration & Full Test Suite Verification | ✅ Completed | Update existing unit tests, run comprehensive cross-tab tests, execute full `./gradlew testDebugUnitTest` and `./gradlew assembleDebug`. |

---

## Phase Details

### [Phase 01: 3-Button Action Row UI in Prompt Tab](./phase-01-action-buttons-ui.md)
- **Files to Modify**: `app/src/main/res/layout/fragment_prompt.xml`, `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt`
- **Files to Create**: `app/src/main/res/drawable/ic_fullscreen.xml`, `app/src/test/java/com/skul9x/readoutloud/ui/PromptResultActionButtonsLayoutTest.kt`
- **Goal**: Replace the single summarize button with a 3-button horizontal container ("Tóm tắt", "Read", "Show") with equal visual weight, anti-clipping attributes (`minWidth="0dp"`, zero insets, `app:autoSizeTextType="uniform"`, `includeFontPadding="true"`), icons, and smooth slide-down animation upon result arrival.

### [Phase 02: Cross-Tab Auto-Read Flow](./phase-02-cross-tab-auto-read.md)
- **Files to Modify**: `app/src/main/java/com/skul9x/readoutloud/ui/MainSharedViewModel.kt`, `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt`, `app/src/main/java/com/skul9x/readoutloud/ui/ReadFragment.kt`
- **Files to Create**: `app/src/test/java/com/skul9x/readoutloud/ui/CrossTabAutoReadFlowTest.kt`
- **Goal**: Enable the "Read" button to post content to `MainSharedViewModel`, navigate to the Read tab, populate `binding.editText`, and automatically start TTS playback.

### [Phase 03: Fullscreen Reader Activity with Swipe-Back & Scrollbar](./phase-03-fullscreen-reader.md)
- **Files to Modify**: `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt`
- **Files to Create**: `app/src/main/java/com/skul9x/readoutloud/ui/FullScreenReaderActivity.kt`, `app/src/main/res/layout/activity_fullscreen_reader.xml`, `app/src/test/java/com/skul9x/readoutloud/ui/FullScreenReaderActivityTest.kt`
- **Goal**: Implement a distraction-free fullscreen reader with Markwon Markdown rendering, vertical scrollbars, copy action, and swipe-back / back gesture support to return to Prompt tab.

### [Phase 04: Integration & Full Test Suite Verification](./phase-04-integration-verification.md)
- **Files to Modify**: Existing test suites if required (`PromptTabSearchNowTest.kt`, `SummarizeCrossTabFlowTest.kt`)
- **Verification Commands**:
  - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptResultActionButtonsLayoutTest"`
  - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.CrossTabAutoReadFlowTest"`
  - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.FullScreenReaderActivityTest"`
  - `./gradlew testDebugUnitTest`
  - `./gradlew assembleDebug`

---

## Quick Commands
- Run Action Buttons Tests: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptResultActionButtonsLayoutTest"`
- Run Cross-Tab Auto-Read Tests: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.CrossTabAutoReadFlowTest"`
- Run Fullscreen Reader Tests: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.FullScreenReaderActivityTest"`
- Run All Tests: `./gradlew test`
