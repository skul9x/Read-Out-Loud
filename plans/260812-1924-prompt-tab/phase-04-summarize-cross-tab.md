# Phase 04: Summarize Cross-Tab Flow — Prompt → Read (with UI/UX Polish)

Status: ✅ Completed
Dependencies: Phase 03

## Objective
After a successful Gemini search result is displayed in the Prompt tab, show a "Tóm tắt" button at the top of the results area. When the user taps this button, the app:
1. Switches to the "Read" tab with a **smooth transition**.
2. Pastes the Gemini search result into the Read tab's main text area.
3. Automatically triggers the existing `processSummarizeWithAI()` function in `ReadFragment` to summarize the content using Gemini.

This cross-tab communication is implemented via a **Shared ViewModel** (`MainSharedViewModel`) scoped to the Activity.

## UI/UX Design Specification

### "Tóm tắt" Button Appearance Animation
```
Before search result:              After search result (200ms delay):
┌──────────────────────┐           ┌──────────────────────┐
║ (empty / loading)    ║           ║ ┌──────────────────┐ ║  ← slide-down
║                      ║   ──→    ║ │ 📄 Tóm tắt       │ ║    translateY: -20→0
║ Result text...       ║           ║ └──────────────────┘ ║    alpha: 0→1
║                      ║           ║ Result text...       ║    duration: 200ms
└──────────────────────┘           └──────────────────────┘
```

### Cross-Tab Transition UX Flow
```
Prompt Tab                           Read Tab
┌────────────────┐                  ┌────────────────┐
│ Result: "..."  │  ─── Tap ──→    │                │
│                │  "Tóm tắt"      │ (text area     │
│ [📄 Tóm tắt]  │                  │  gets filled)  │
│                │  1. ViewModel    │                │
│                │     posts text   │ auto-triggers  │
│                │  2. Tab switches │ summarize()    │
│                │  3. Smooth       │                │
│                │     animation    │ Loading...     │
└────────────────┘                  └────────────────┘

Expected user perception:
"Tap → Smooth slide → Read tab appears → Text already there → Summarizing..."
Total perceived latency: <500ms
```

### Cross-Tab Snackbar Confirmation
After tapping "Tóm tắt", a brief Snackbar shows on the Read tab:
```
┌──────────────────────────────────────┐
│                                      │
│  [Read tab content...]               │
│                                      │
│  ┌──────────────────────────────┐    │
│  │ 📄 Đang tóm tắt từ Prompt...│    │  ← Snackbar (1.5s, auto-dismiss)
│  └──────────────────────────────┘    │
└──────────────────────────────────────┘
```

## Requirements
### Functional
- [x] **"Tóm tắt" button** (`@+id/summarizeResultButton`):
  - Initially hidden (`GONE`) in the Prompt tab.
  - Becomes visible (`VISIBLE`) only after a successful "Search Now" result is displayed.
  - Positioned at the top of the result card, before the scrollable result text.
  - Uses the same Material 3 tonal style and `@drawable/ic_summarize` icon as the existing "Tóm tắt" button in the Read tab.
- [x] **Create `MainSharedViewModel.kt`**:
  - `val summarizeEvent: LiveData<String?>` — emits the text to be summarized.
  - `fun requestSummarize(text: String)` to post the event.
  - `fun clearSummarizeEvent()` to reset after consumption.
- [x] **PromptFragment** "Tóm tắt" button click:
  1. Get the current result text from `resultTextView`.
  2. Call `sharedViewModel.requestSummarize(resultText)`.
  3. Switch to Read tab via `(requireActivity() as MainActivity).switchToTab(0)`.
- [x] **ReadFragment** observes `summarizeEvent`:
  1. When a non-null event is received, set `editText.setText(text)`.
  2. Call `processSummarizeWithAI(text)` (the same existing summarize flow).
  3. Call `sharedViewModel.clearSummarizeEvent()` to prevent re-triggering.
- [x] **Tab switching**: `ViewPager2.currentItem = 0` via Activity helper.

### UI/UX Requirements (NEW)
- [x] **"Tóm tắt" Button Entrance Animation**:
  - Appears with **slide-down animation**: translateY from -20dp to 0dp, alpha 0 to 1, duration 200ms, `AccelerateDecelerateInterpolator`.
  - Delayed by 100ms after the result card's fade-in (so the result shows first, then the button slides in).
  - Uses the same button style as Read tab's existing "Tóm tắt" for visual consistency.
- [x] **Smooth Tab Transition**:
  - `ViewPager2.setCurrentItem(0, true)` — the `true` parameter enables smooth scroll animation (not instant jump).
  - The transition should feel like a natural swipe, not a jarring switch.
- [x] **Cross-Tab Feedback Snackbar**:
  - When ReadFragment receives the `summarizeEvent`, show a brief Snackbar on the Read tab:
    - "📄 Đang tóm tắt kết quả tìm kiếm..."
    - Duration: `Snackbar.LENGTH_SHORT` (1.5s)
    - This confirms to the user that the action was triggered and context switched.
- [x] **"Tóm tắt" Button Press Feedback**:
  - On button tap: brief scale animation (scale 0.95 → 1.0 over 150ms) for tactile feel.
  - Disable the button after tap (prevent double-tap during tab switch + API call).
  - Re-enable when the user comes back to the Prompt tab.
- [x] **Read Tab Text Injection UX**:
  - When text is injected from Prompt tab:
    - The editText is scrolled to the top (`scrollTo(0, 0)`) so the user sees the beginning.
    - The text is set without triggering edit mode.
    - Loading overlay appears immediately (matching existing summarize flow).
- [x] **State Persistence**:
  - If the user switches back to the Prompt tab after summarization, the original search result should still be visible in the result card (not cleared).
  - The "Tóm tắt" button should be re-enabled.

### Non-Functional
- [x] The cross-tab flow must feel seamless — switching + summarizing should start immediately.
- [x] Memory safety: ViewModel is scoped to Activity lifecycle, no memory leaks.
- [x] Fragments must use `activityViewModels()` delegate to share the ViewModel.
- [x] ViewPager2 `offscreenPageLimit = 1` ensures both fragments stay alive (set in Phase 01).

## Implementation Steps
1. [x] **Create `MainSharedViewModel.kt`** in `ui/` package:
   ```kotlin
   package com.skul9x.readoutloud.ui

   import androidx.lifecycle.LiveData
   import androidx.lifecycle.MutableLiveData
   import androidx.lifecycle.ViewModel

   class MainSharedViewModel : ViewModel() {
       private val _summarizeEvent = MutableLiveData<String?>()
       val summarizeEvent: LiveData<String?> = _summarizeEvent

       fun requestSummarize(text: String) {
           _summarizeEvent.value = text
       }

       fun clearSummarizeEvent() {
           _summarizeEvent.value = null
       }
   }
   ```

2. [x] **Update `PromptFragment.kt`**:
   - Add `private val sharedViewModel: MainSharedViewModel by activityViewModels()`.
   - After successful search result, show "Tóm tắt" button with slide-down animation (delayed 100ms after result card).
   - "Tóm tắt" button click handler:
     ```kotlin
     binding.summarizeResultButton.setOnClickListener {
         val resultText = binding.resultTextView.text.toString()
         if (resultText.isNotBlank()) {
             // Scale animation for feedback
             it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(75).withEndAction {
                 it.animate().scaleX(1f).scaleY(1f).setDuration(75).start()
             }.start()
             
             // Disable to prevent double-tap
             it.isEnabled = false
             
             // Post to shared ViewModel
             sharedViewModel.requestSummarize(resultText)
             
             // Smooth tab switch
             (requireActivity() as MainActivity).switchToTab(0)
         }
     }
     ```
   - Re-enable the button in `onResume()` (when user returns to Prompt tab).

3. [x] **Update `ReadFragment.kt`**:
   - Add `private val sharedViewModel: MainSharedViewModel by activityViewModels()`.
   - In `onViewCreated`, observe `summarizeEvent`:
     ```kotlin
     sharedViewModel.summarizeEvent.observe(viewLifecycleOwner) { text ->
         if (!text.isNullOrBlank()) {
             binding.editText.setText(text)
             binding.editText.scrollTo(0, 0) // Scroll to top
             
             // Show cross-tab feedback Snackbar
             Snackbar.make(binding.root, "📄 Đang tóm tắt kết quả tìm kiếm...", 
                 Snackbar.LENGTH_SHORT).show()
             
             processSummarizeWithAI(text)
             sharedViewModel.clearSummarizeEvent()
         }
     }
     ```

4. [x] **Add `switchToTab(index: Int)` method to `MainActivity.kt`**:
   ```kotlin
   fun switchToTab(index: Int) {
       binding.viewPager.setCurrentItem(index, true) // smooth scroll enabled
   }
   ```

5. [x] **Ensure `fragment-ktx` dependency** is present (from Phase 01) for `activityViewModels()`.

6. [x] **Create verification tests**.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/ui/MainSharedViewModel.kt` — [NEW] Shared ViewModel for cross-tab communication
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` — [MODIFY] Add "Tóm tắt" button animation + click logic + button state management
- `app/src/main/java/com/skul9x/readoutloud/ui/ReadFragment.kt` — [MODIFY] Observe summarize event, inject text, show Snackbar, trigger summarize
- `app/src/main/java/com/skul9x/readoutloud/MainActivity.kt` — [MODIFY] Add `switchToTab()` with smooth scroll
- `app/src/test/java/com/skul9x/readoutloud/ui/SummarizeCrossTabFlowTest.kt` — [NEW] Verification tests

## Verification Test (File-Based)
Create `app/src/test/java/com/skul9x/readoutloud/ui/SummarizeCrossTabFlowTest.kt`:

```kotlin
package com.skul9x.readoutloud.ui

import androidx.lifecycle.MutableLiveData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SummarizeCrossTabFlowTest {

    private lateinit var viewModel: MainSharedViewModel

    @Before
    fun setUp() {
        viewModel = MainSharedViewModel()
    }

    @Test
    fun testSharedViewModelInitialStateIsNull() {
        assertNull("Initial summarize event should be null",
            viewModel.summarizeEvent.value)
    }

    @Test
    fun testRequestSummarizeSetsEvent() {
        val testText = "This is a test article about AI regulations."
        viewModel.requestSummarize(testText)
        assertEquals("summarizeEvent must contain the requested text",
            testText, viewModel.summarizeEvent.value)
    }

    @Test
    fun testClearSummarizeEventResetsToNull() {
        viewModel.requestSummarize("Some text")
        assertNotNull(viewModel.summarizeEvent.value)

        viewModel.clearSummarizeEvent()
        assertNull("After clearing, summarizeEvent must be null",
            viewModel.summarizeEvent.value)
    }

    @Test
    fun testRequestSummarizeOverwritesPreviousValue() {
        viewModel.requestSummarize("First text")
        assertEquals("First text", viewModel.summarizeEvent.value)

        viewModel.requestSummarize("Second text")
        assertEquals("Second text must overwrite first",
            "Second text", viewModel.summarizeEvent.value)
    }

    @Test
    fun testSummarizeEventIsLiveData() {
        val event = viewModel.summarizeEvent
        assertNotNull("summarizeEvent LiveData must not be null", event)
        assertTrue("summarizeEvent must be a LiveData instance",
            event is androidx.lifecycle.LiveData<*>)
    }

    @Test
    fun testRequestSummarizeWithLongText() {
        val longText = "Paragraph. ".repeat(500)
        viewModel.requestSummarize(longText)
        assertEquals("Long text must be stored correctly",
            longText, viewModel.summarizeEvent.value)
    }

    @Test
    fun testFlowIntegrity_RequestThenClearThenRequestAgain() {
        // Full flow: search result → summarize → clear → new search → summarize again
        viewModel.requestSummarize("Result 1")
        assertEquals("Result 1", viewModel.summarizeEvent.value)

        viewModel.clearSummarizeEvent()
        assertNull(viewModel.summarizeEvent.value)

        viewModel.requestSummarize("Result 2")
        assertEquals("Result 2", viewModel.summarizeEvent.value)
    }
}
```

### Automated Test Command
```bash
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.SummarizeCrossTabFlowTest"
```

---
Next Phase: [Phase 05 — Integration & Verification](./phase-05-integration-verification.md)
