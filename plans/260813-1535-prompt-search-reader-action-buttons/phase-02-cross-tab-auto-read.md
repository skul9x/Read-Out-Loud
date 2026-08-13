# Phase 02: Cross-Tab Auto-Read Flow

Status: ✅ Completed
Dependencies: [Phase 01: 3-Button Action Row UI in Prompt Tab](./phase-01-action-buttons-ui.md)

## Objective
Implement the "Read" action on the Prompt tab: when clicked, it pastes the Gemini API search result text into the Read tab's text editor (`binding.editText`) and immediately triggers the TTS engine to start reading aloud automatically.

## Requirements

### Functional Requirements
- [x] Extend `MainSharedViewModel.kt`:
  - Add `readAloudEvent: LiveData<String?>` and `requestReadAloud(text: String)`.
  - Add `clearReadAloudEvent()` to reset state after processing.
- [x] In `PromptFragment.kt`:
  - Attach click listener to `readResultButton`.
  - Trigger haptic feedback and scale micro-animation.
  - Disable `readResultButton` temporarily on click to prevent double-tap race conditions.
  - Post result text to `sharedViewModel.requestReadAloud(resultText)`.
  - Call `(requireActivity() as? MainActivity)?.switchToTab(0)` to switch to the Read tab.
- [x] In `ReadFragment.kt`:
  - Observe `sharedViewModel.readAloudEvent`.
  - When non-blank text is received:
    - Set the text into `binding.editText`: `binding.editText.setText(text)`.
    - Scroll `binding.editText` to top (`scrollTo(0, 0)`).
    - Show informative `Snackbar` (e.g., "🔊 Đang đọc kết quả tìm kiếm...").
    - Call `checkPermissionsAndRead()` to trigger TTS playback.
    - Clear the event via `sharedViewModel.clearReadAloudEvent()`.
- [x] Ensure `PromptFragment.onResume()` re-enables `readResultButton` when navigating back.

### Non-Functional Requirements
- [x] Concurrency & LifeCycle safety: Clean event handling using LiveData so no duplicate audio triggers occur on orientation change or tab re-selection.
- [x] Seamless transition: Instantaneous text paste with automatic focus reset and immediate voice playback.

## Implementation Steps
1. [x] Update `app/src/main/java/com/skul9x/readoutloud/ui/MainSharedViewModel.kt`:
   - Add `_readAloudEvent`, `readAloudEvent`, `requestReadAloud()`, `clearReadAloudEvent()`.
2. [x] Update `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt`:
   - Implement `readResultButton.setOnClickListener` to request auto-read and switch to tab index 0.
3. [x] Update `app/src/main/java/com/skul9x/readoutloud/ui/ReadFragment.kt`:
   - Add observer for `sharedViewModel.readAloudEvent` that sets text, scrolls to top, and calls `checkPermissionsAndRead()`.
4. [x] Create unit test `app/src/test/java/com/skul9x/readoutloud/ui/CrossTabAutoReadFlowTest.kt` verifying the full click-to-read pipeline.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/ui/MainSharedViewModel.kt` - [MODIFY] Add auto-read LiveData event.
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` - [MODIFY] Wire `readResultButton` click handler.
- `app/src/main/java/com/skul9x/readoutloud/ui/ReadFragment.kt` - [MODIFY] Handle auto-read event and trigger TTS.
- `app/src/test/java/com/skul9x/readoutloud/ui/CrossTabAutoReadFlowTest.kt` - [NEW] Robolectric cross-tab auto-read tests.

## Test Criteria
- [x] `testSharedViewModelReadAloudEvent`: Tests posting, getting, and clearing `readAloudEvent`.
- [x] `testPromptFragmentReadButtonClick_SwitchesTabAndPostsEvent`: Verifies clicking `readResultButton` switches `ViewPager2` to tab 0 and passes text to `MainSharedViewModel`.
- [x] `testReadFragmentReceivesReadAloudEvent_PopulatesEditTextAndTriggersPlayback`: Verifies `ReadFragment` sets text in `binding.editText` and initiates TTS playback intent.
- [x] `testReadButtonReEnabledOnResume`: Verifies `readResultButton` is enabled after returning to Prompt tab.

---
Next Phase: [Phase 03: Fullscreen Reader Activity with Swipe-Back & Scrollbar](./phase-03-fullscreen-reader.md)
