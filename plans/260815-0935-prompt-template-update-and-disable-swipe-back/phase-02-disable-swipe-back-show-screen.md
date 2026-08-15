# Phase 02: Disable Swipe Back on Show Screen

Status: ✅ Completed
Dependencies: None

## Objective
Remove the swipe-to-back gesture handler from `FullScreenReaderActivity` (the screen opened via the "Show" button in the Prompt tab). Users will navigate back exclusively via the Toolbar back button or the system back button / gesture.

## Requirements
### Functional
- [x] Remove `GestureDetector` setup and `dispatchTouchEvent` interception from `FullScreenReaderActivity.kt`.
- [x] Remove unused swipe gesture constants (`SWIPE_MIN_DISTANCE`, `SWIPE_THRESHOLD_VELOCITY`).
- [x] Retain toolbar navigation click listener (`binding.toolbar.setNavigationOnClickListener { finish() }`).
- [x] Retain `onBackPressedDispatcher` callback for system back button navigation.
- [x] Ensure horizontal swipes/flings across markdown content or scrollview do NOT close the activity.

### Non-Functional
- [x] Eliminate touch event overhead during reading and vertical scrolling.
- [x] Maintain smooth transition animations on `finish()`.

## Implementation Steps
1. [x] In `app/src/main/java/com/skul9x/readoutloud/ui/FullScreenReaderActivity.kt`:
   - Remove `setupSwipeBackGesture()` method.
   - Remove `override fun dispatchTouchEvent(ev: MotionEvent): Boolean`.
   - Remove `gestureDetector` property and companion constants `SWIPE_MIN_DISTANCE`, `SWIPE_THRESHOLD_VELOCITY`.
   - Keep `setupOnBackPressed()`, `setupUI()`, `renderContent()`, `copyContentToClipboard()`, and `finish()`.
2. [x] In `app/src/test/java/com/skul9x/readoutloud/ui/FullScreenReaderActivityTest.kt`:
   - Replace `testSwipeBackGestureTriggersFinish()` with `testSwipeBackGestureIsDisabled()`:
     - Simulate horizontal swipe/fling touch events (ACTION_DOWN, ACTION_MOVE, ACTION_UP).
     - Assert that `activity.isFinishing` remains `false`.
   - Verify `testToolbarBackNavigationFinishesActivity()`:
     - Asserts clicking toolbar back navigation finishes activity.
   - Add `testSystemBackPressedFinishesActivity()`:
     - Asserts `activity.onBackPressedDispatcher.onBackPressed()` finishes activity.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/ui/FullScreenReaderActivity.kt` — [MODIFY] Remove swipe back gesture listener and touch event interception.
- `app/src/test/java/com/skul9x/readoutloud/ui/FullScreenReaderActivityTest.kt` — [MODIFY] Update tests to assert swipe-back is disabled while toolbar/system back remain functional.

## Detailed File-Based Test Specifications

### `FullScreenReaderActivityTest.kt`
- `testFullScreenReaderActivityLayoutElements()`:
  - Verifies existence and visibility of `toolbar`, `copyButton`, `scrollView`, `fullScreenTextView`.
  - Verifies text selection and scrollbar properties.
- `testFullScreenReaderRendersMarkdownContent()`:
  - Verifies markdown content renders properly in `fullScreenTextView`.
- `testFullScreenReaderDefaultTitleWhenTopicMissing()`:
  - Verifies fallback toolbar title when topic is null/empty.
- `testToolbarBackNavigationFinishesActivity()`:
  - Simulates toolbar back button click.
  - Asserts `activity.isFinishing` is `true`.
- `testSystemBackPressedFinishesActivity()`:
  - Triggers `activity.onBackPressedDispatcher.onBackPressed()`.
  - Asserts `activity.isFinishing` is `true`.
- `testSwipeBackGestureIsDisabled()`:
  - Simulates horizontal drag/swipe events (down at x=10, move to x=300, up at x=300).
  - Asserts `activity.isFinishing` is `false`.
- `testCopyButtonCopiesContentToClipboard()`:
  - Simulates clicking copy button and verifies clipboard contents.
- `testPromptFragmentShowButtonClickLaunchesActivity()`:
  - Verifies "Show" button in `PromptFragment` launches `FullScreenReaderActivity` with content and topic extras.

---
Next Phase: [Phase 03: Full Regression & Verification](file:///d:/skul9x/Read-Out-Loud-main/plans/260815-0935-prompt-template-update-and-disable-swipe-back/phase-03-regression-and-verification.md)
