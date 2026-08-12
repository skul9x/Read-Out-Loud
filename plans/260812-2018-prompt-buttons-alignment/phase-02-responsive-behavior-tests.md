# Phase 02: Responsive Behavior & Dynamic State Testing

Status: ✅ Completed
Dependencies: Phase 01

## Objective
Verify that the alignment, geometry, single-line constraints, and visual balance of `makePromptButton` and `searchNowButton` remain completely invariant during all UI state transitions (typing, empty input, loading state, error state, and result display).

---

## Requirements

### Functional Requirements
- **State Synchronicity**: Both buttons must enable and disable simultaneously as the user types or clears text in `promptTopicInput`.
- **Loading State Invariance**: When "Search Now" is pressed, both buttons become disabled and retain their exact dimensions (no layout shift or jumping).
- **Result & Error Recovery**: When search completes (Success or Error), both buttons are restored to their active state without altering typography, padding, or alignment.
- **Orientation & Re-creation**: If activity is recreated or resized, button weights (50/50 split) and single-line constraints remain preserved.

### Non-Functional Requirements
- **Robustness**: 0 UI jitter or baseline misalignment during animations or asynchronous coroutine updates.

---

## Implementation Steps

1. **Review `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt`**:
   - Ensure `updateButtonStates(enabled: Boolean)` and `showLoading()`, `showResult()`, `showError()` methods update button states cleanly without mutating layout parameters at runtime.
   
2. **Extend Test Suite in `PromptButtonsLayoutAlignmentTest.kt`**:
   - Add state invariance tests:
     - `testButtonStateSynchronizationOnTextChange()`
     - `testButtonDimensionsInvariantDuringLoadingState()`
     - `testButtonDimensionsInvariantAfterErrorState()`
     - `testButtonDimensionsInvariantAfterResultDisplay()`

---

## Files to Create / Modify
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` — [VERIFY / MODIFY if needed]
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptButtonsLayoutAlignmentTest.kt` — [MODIFY] Add state transition layout invariance tests.

---

## Detailed File-Based Test Specification
Add to `PromptButtonsLayoutAlignmentTest.kt`:

```kotlin
    @Test
    fun testButtonStateSynchronizationOnTextChange() {
        val promptTopicInput = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.promptTopicInput)
        
        // Initial state: empty -> both disabled
        assertFalse(makePromptButton.isEnabled)
        assertFalse(searchNowButton.isEnabled)

        // Type text -> both enabled
        promptTopicInput.setText("AI News 2026")
        assertTrue(makePromptButton.isEnabled)
        assertTrue(searchNowButton.isEnabled)

        // Clear text -> both disabled
        promptTopicInput.setText("")
        assertFalse(makePromptButton.isEnabled)
        assertFalse(searchNowButton.isEnabled)
    }

    @Test
    fun testButtonDimensionsInvariantDuringAndAfterStateTransitions() {
        val promptTopicInput = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.promptTopicInput)
        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (56 * density).toInt()

        promptTopicInput.setText("Quantum Computing")
        
        // Check params before click
        var makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        var searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams
        assertEquals(expectedHeightPx, makeParams.height)
        assertEquals(expectedHeightPx, searchParams.height)
        assertEquals(1, makePromptButton.maxLines)
        assertEquals(1, searchNowButton.maxLines)

        // Perform click on Make Prompt
        makePromptButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // Verify params remain unchanged
        makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams
        assertEquals(expectedHeightPx, makeParams.height)
        assertEquals(expectedHeightPx, searchParams.height)
        assertEquals(1, makePromptButton.maxLines)
        assertEquals(1, searchNowButton.maxLines)
    }
```

---

## Test Criteria
- [x] `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptButtonsLayoutAlignmentTest"` passes with all test cases green.
- [x] `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabMakePromptTest"` passes.
- [x] `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabSearchNowTest"` passes.

---
Next Phase: [Phase 03 — End-to-End Build & Device Visual Verification](./phase-03-e2e-visual-verification.md)
