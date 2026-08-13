# Phase 04: Integration & Full Test Suite Verification

Status: ✅ Completed
Dependencies: [Phase 01: 3-Button Action Row UI in Prompt Tab](./phase-01-action-buttons-ui.md), [Phase 02: Cross-Tab Auto-Read Flow](./phase-02-cross-tab-auto-read.md), [Phase 03: Fullscreen Reader Activity with Swipe-Back & Scrollbar](./phase-03-fullscreen-reader.md)

## Objective
Verify the end-to-end integration of all 3 action buttons ("Tóm tắt", "Read", "Show") in the Prompt tab's result view, ensure all existing and new unit/integration tests pass cleanly, and validate the debug build.

## Requirements

### Functional Requirements
- [x] Verify that after Gemini returns results in Prompt tab:
  - "Tóm tắt" button performs cross-tab summarization to Read tab.
  - "Read" button performs cross-tab auto-read aloud to Read tab and starts TTS playback immediately.
  - "Show" button opens `FullScreenReaderActivity` with complete formatted Markdown and vertical scrollbar.
  - Swiping back or pressing back from `FullScreenReaderActivity` returns cleanly to the Prompt tab with full state preserved.
- [x] Update any existing test assertions in `PromptTabSearchNowTest.kt` or `SummarizeCrossTabFlowTest.kt` if button container structure changed.
- [x] Run complete Robolectric test suite across all modules.

### Non-Functional Requirements
- [x] 100% test pass rate across all unit tests in the repository.
- [x] Zero lint/build warnings or errors during `./gradlew assembleDebug`.

## Implementation Steps
1. [x] Review and update existing test cases in `PromptTabSearchNowTest.kt` and `SummarizeCrossTabFlowTest.kt` to accommodate the 3-button layout.
2. [x] Execute unit tests for each new phase:
   - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptResultActionButtonsLayoutTest"`
   - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.CrossTabAutoReadFlowTest"`
   - `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.FullScreenReaderActivityTest"`
3. [x] Execute full unit test suite:
   - `./gradlew testDebugUnitTest`
4. [x] Build the debug APK to verify compilation and resource packaging:
   - `./gradlew assembleDebug`

## Files to Create/Modify
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabSearchNowTest.kt` - [MODIFY] If needed for layout structure adjustments.
- `app/src/test/java/com/skul9x/readoutloud/ui/SummarizeCrossTabFlowTest.kt` - [MODIFY] If needed for 3-button layout checks.

## Test Criteria
- [x] All new tests pass: `PromptResultActionButtonsLayoutTest`, `CrossTabAutoReadFlowTest`, `FullScreenReaderActivityTest`.
- [x] All existing test suites pass: `PromptTabIntegrationTest`, `PromptResultMarkdownFormattingTest`, `TabSwipeDisabledTest`, `ReadFragmentTest`, etc.
- [x] Gradle build finishes with `BUILD SUCCESSFUL`.
