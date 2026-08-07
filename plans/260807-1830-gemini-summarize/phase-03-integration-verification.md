# Phase 03: Integration & End-to-End Verification

Status: ✅ Completed
Dependencies: Phase 01, Phase 02

## Objective
Wire up the "Tóm tắt" button (`@+id/summarizeButton`) in `MainActivity.kt`, connect it with `GeminiApiClient.summarizeTextWithGemini`, manage loading UI states, handle edge cases (empty text, missing API keys, rate limits), and update the status indicator.

## Requirements
### Functional
- [x] Clicking `summarizeButton` validates that `editText` is not blank (displays toast `"Không có nội dung để tóm tắt"` if blank).
- [x] Set `setLoading(true)` during API invocation and update status text to `"Gemini đang tóm tắt..."`.
- [x] On successful summarization, replace `editText` content with summarized text and update status to `"Gemini: Tóm tắt xong (<model>)"`.
- [x] On API error or quota exhaustion, display error message via Toast and restore status.
- [x] Disable both `aiTextButton` and `summarizeButton` while loading to prevent concurrent API spamming.

### UI / UX Polish
- [x] Smooth transition during text replacement.
- [x] Ensure edit mode / read-only mode gestures function normally with summarized text.

## Implementation Steps
1. [x] Update `setupUI()` in `MainActivity.kt` to attach a click listener to `binding.summarizeButton`.
2. [x] Implement `processSummarizeWithAI(text: String)` in `MainActivity.kt`.
3. [x] Update `setLoading(isLoading: Boolean)` in `MainActivity.kt` to disable/enable both `binding.aiTextButton` and `binding.summarizeButton`.
4. [x] Create integration test `SummarizeIntegrationTest.kt` using Robolectric.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/MainActivity.kt` - [MODIFY] Connect `summarizeButton` click listener, loading state & result handling
- `app/src/test/java/com/skul9x/readoutloud/SummarizeIntegrationTest.kt` - [NEW] Integration test verifying click behavior, empty text toast, and loading state toggles

## Verification Test (File-Based)
Create integration test file `app/src/test/java/com/skul9x/readoutloud/SummarizeIntegrationTest.kt`:

```kotlin
package com.skul9x.readoutloud

import android.widget.EditText
import com.google.android.material.button.MaterialButton
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class SummarizeIntegrationTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun testSummarizeButtonEmptyTextShowsToast() {
        val editText = activity.findViewById<EditText>(R.id.editText)
        editText.setText("") // Empty content

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        summarizeButton.performClick()

        val latestToast = ShadowToast.getTextOfLatestToast()
        assertEquals("Không có nội dung để tóm tắt", latestToast)
    }

    @Test
    fun testSummarizeButtonDisabledDuringLoadingState() {
        val setLoadingMethod = MainActivity::class.java.getDeclaredMethod("setLoading", Boolean::class.java)
        setLoadingMethod.isAccessible = true

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)

        // Set loading = true
        setLoadingMethod.invoke(activity, true)
        assertFalse("summarizeButton should be disabled during loading", summarizeButton.isEnabled)
        assertFalse("aiTextButton should be disabled during loading", aiTextButton.isEnabled)

        // Set loading = false
        setLoadingMethod.invoke(activity, false)
        assertTrue("summarizeButton should be re-enabled after loading", summarizeButton.isEnabled)
        assertTrue("aiTextButton should be re-enabled after loading", aiTextButton.isEnabled)
    }
}
```

### Automated Test Command
```bash
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.SummarizeIntegrationTest"
```
