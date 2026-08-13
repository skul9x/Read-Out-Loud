# Plan: Markdown Formatted Results & Tab Swipe Gesture Disabling

Created: 2026-08-13
Status: ✅ Completed

## Problem Statement
1. **Raw Markdown Clutter in "Search Now" Results**: When users search using "Search Now" in the Prompt tab, the Gemini API returns markdown text containing raw formatting symbols (e.g., `#`, `##`, `*`, `**`, `_`). Currently, `PromptFragment` assigns this directly as plain text to `resultTextView`, resulting in poor UI/UX with visible markdown syntax rather than cleanly formatted rich text (bold, italic, headers, bullet points).
2. **Accidental Tab Swiping**: Users swiping horizontally anywhere across the screen inadvertently switch between the "Read" and "Prompt" tabs. The desired UX is strictly tab-click-based navigation (users must tap the target tab directly to switch).

## Objective
1. **Disable ViewPager2 User Swipe Input**: Set `isUserInputEnabled = false` on `binding.viewPager` so switching tabs is only triggered via tab taps or explicit programmatic actions (e.g., Cross-Tab Summarize).
2. **Rich Markdown Rendering**: Integrate the lightweight `Markwon` (`io.noties.markwon:core:4.6.2`) library to parse markdown into Android `Spanned` formatting with bold, italics, headers, and bullet lists on `resultTextView`, eliminating raw markdown syntax characters.
3. **Comprehensive Unit & Integration Test Coverage**: Provide dedicated file-based Robolectric test suites to verify swipe gesture disabling, markdown span rendering, and cross-tab summarization.

---

## Phase Overview

| Phase | Name | Status | Description |
|-------|------|--------|-------------|
| 01 | Disable Tab Swipe Gestures | ✅ Completed | Disable user touch swipe on `ViewPager2` in `MainActivity.kt` (`isUserInputEnabled = false`) and add test suite verifying tab tap navigation remains active while swipe is blocked. |
| 02 | Markdown Rendering for Search Now | ✅ Completed | Add `io.noties.markwon:core:4.6.2` to version catalog and app dependencies, integrate `Markwon` in `PromptFragment.kt` for `showResult`, and add rich text formatting unit tests. |
| 03 | Cross-Tab Integration & Test Suite Verification | ✅ Completed | Verify cross-tab summarize flow with formatted text, update existing tests, and run full test suite with `./gradlew testDebugUnitTest`. |

---

## Phase Details

### [Phase 01: Disable Tab Swipe Gestures](./phase-01-disable-tab-swipe.md)
- **Files to Modify**: `app/src/main/java/com/skul9x/readoutloud/MainActivity.kt`
- **Files to Create/Modify**: `app/src/test/java/com/skul9x/readoutloud/ui/TabLayoutInfrastructureTest.kt`, `app/src/test/java/com/skul9x/readoutloud/ui/TabSwipeDisabledTest.kt`
- **Goal**: Ensure `ViewPager2.isUserInputEnabled` is set to `false`, swiping between tabs is disabled, and clicking tabs or `switchToTab(index)` functions properly.

### [Phase 02: Markdown Rendering for Search Now](./phase-02-markdown-rendering.md)
- **Files to Modify**: `gradle/libs.versions.toml`, `app/build.gradle.kts`, `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt`
- **Files to Create**: `app/src/test/java/com/skul9x/readoutloud/ui/PromptResultMarkdownFormattingTest.kt`
- **Goal**: Render Gemini AI research responses with styled spans (headers, bold, italic, bullet points) without raw markdown syntax characters `#`, `*`, `**`.

### [Phase 03: Cross-Tab Integration & Test Suite Verification](./phase-03-integration-verification.md)
- **Files to Modify**: `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabSearchNowTest.kt`, `app/src/test/java/com/skul9x/readoutloud/ui/SummarizeCrossTabFlowTest.kt`
- **Verification Commands**:
  - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.TabSwipeDisabledTest"`
  - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptResultMarkdownFormattingTest"`
  - `./gradlew testDebugUnitTest`
  - `./gradlew assembleDebug`
- **Goal**: Full automated test verification (100% pass) and clean build output.

---

## Quick Commands
- Run Swipe Disabling Tests: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.*Tab*"`
- Run Markdown Rendering Tests: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptResultMarkdownFormattingTest"`
- Run Entire Test Suite: `./gradlew test`
