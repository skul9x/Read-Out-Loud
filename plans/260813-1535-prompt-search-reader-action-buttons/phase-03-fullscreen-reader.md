# Phase 03: Fullscreen Reader Activity with Swipe-Back & Scrollbar

Status: ✅ Completed
Dependencies: [Phase 01: 3-Button Action Row UI in Prompt Tab](./phase-01-action-buttons-ui.md)

## Objective
Build a distraction-free, full-screen rich text reader (`FullScreenReaderActivity`) launched when clicking the "Show" button on the Prompt tab's result card. The reader renders rich Markdown via `Markwon`, features a vertical scrollbar, and supports swipe-back gesture navigation (and back button/toolbar arrow) to return directly to the Prompt tab.

## Requirements

### Functional Requirements
- [x] Create `FullScreenReaderActivity.kt` and `res/layout/activity_fullscreen_reader.xml`:
  - Material 3 Edge-to-Edge display with `ViewCompat.setOnApplyWindowInsetsListener` handling status bar and navigation bar insets.
  - Sleek top toolbar (`MaterialToolbar`) containing:
    - Back button (arrow back icon) that finishes the activity.
    - Title text (e.g., search topic or "Trình đọc toàn màn hình").
    - Action button: Copy to clipboard (`ic_content_copy`).
  - Full-screen `NestedScrollView` with vertical scrollbars explicitly enabled:
    - `android:scrollbars="vertical"`
    - `android:fadeScrollbars="false"`
    - `android:scrollbarThumbVertical` / system scrollbar thumb for visible scrolling progress.
  - `TextView` (`fullScreenTextView`) styled with high-readability typography (20sp text size, lineSpacingExtra 6dp, padding 20dp).
  - Rich Markdown rendering using `Markwon.create(this)` to parse headers, bold, italics, and lists.
- [x] Swipe-Back Navigation:
  - Integrate `OnBackPressedCallback` via `onBackPressedDispatcher` for system edge back swipe gesture support.
  - Implement a touch gesture detector (`GestureDetector.SimpleOnGestureListener`) detecting left-to-right horizontal swipe flings across the screen to dismiss/finish the activity.
  - Smooth activity transition animations when entering and exiting.
- [x] Register `FullScreenReaderActivity` in `AndroidManifest.xml`.
- [x] In `PromptFragment.kt`:
  - Attach click listener to `showResultButton`.
  - Launch `FullScreenReaderActivity` with `Intent` extras:
    - `EXTRA_CONTENT`: Gemini search result text.
    - `EXTRA_TOPIC`: Current search topic for the header title.

### Non-Functional Requirements
- [x] Readability: Optimized line spacing, high contrast dark-theme color tokens (`colorSurface`, `colorOnSurface`), and text selection support.
- [x] Responsiveness: Fast instant rendering of long markdown articles without UI stutter.

## Implementation Steps
1. [x] Create layout `app/src/main/res/layout/activity_fullscreen_reader.xml`.
2. [x] Create `app/src/main/java/com/skul9x/readoutloud/ui/FullScreenReaderActivity.kt`:
   - Setup Markwon rendering, insets padding, copy button, vertical scrollbars, and swipe-back gesture detector.
3. [x] Register `FullScreenReaderActivity` in `app/src/main/AndroidManifest.xml`.
4. [x] Wire `showResultButton.setOnClickListener` in `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` to start `FullScreenReaderActivity`.
5. [x] Create unit test `app/src/test/java/com/skul9x/readoutloud/ui/FullScreenReaderActivityTest.kt` verifying layout, Markwon text rendering, vertical scrollbar configuration, and swipe-back finish behavior.

## Files to Create/Modify
- `app/src/main/res/layout/activity_fullscreen_reader.xml` - [NEW] Fullscreen reader layout with vertical scrollbar.
- `app/src/main/java/com/skul9x/readoutloud/ui/FullScreenReaderActivity.kt` - [NEW] Fullscreen reader activity implementation.
- `app/src/main/AndroidManifest.xml` - [MODIFY] Register `FullScreenReaderActivity`.
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` - [MODIFY] Wire `showResultButton` to launch activity.
- `app/src/test/java/com/skul9x/readoutloud/ui/FullScreenReaderActivityTest.kt` - [NEW] Robolectric test suite for fullscreen reader.

## Test Criteria
- [x] `testFullScreenReaderActivityLayoutElements`: Verifies toolbar, scrollview, scrollbar attribute, and textview exist.
- [x] `testFullScreenReaderRendersMarkdownContent`: Verifies `Markwon` renders markdown passed via Intent `EXTRA_CONTENT`.
- [x] `testToolbarBackNavigationFinishesActivity`: Verifies clicking toolbar back arrow finishes activity.
- [x] `testSwipeBackGestureTriggersFinish`: Verifies horizontal left-to-right swipe fling triggers activity finish.
- [x] `testPromptFragmentShowButtonClickLaunchesActivity`: Verifies clicking `showResultButton` starts `FullScreenReaderActivity` with correct extras.

---
Next Phase: [Phase 04: Integration & Full Test Suite Verification](./phase-04-integration-verification.md)
