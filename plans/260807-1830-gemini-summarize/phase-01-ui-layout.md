# Phase 01: UI Layout & View Binding (50/50 Dual Buttons)

Status: ✅ Completed
Dependencies: None

## Objective
Refactor the main screen (`activity_main.xml`) top action section from a single full-width "AI text" card into a 50/50 split horizontal layout containing two equal-width Material 3 buttons:
- **Left Button**: "AI Text" (`@+id/aiTextButton`)
- **Right Button**: "Tóm tắt" (`@+id/summarizeButton`)

Ensure optimal UI/UX with consistent padding, elevation, rounded corners (24dp/28dp), Material 3 tonal styling, and appropriate icons.

## Requirements
### Functional
- [x] Display "AI Text" and "Tóm tắt" buttons side by side with equal 50% width (`layout_weight="1"`).
- [x] Maintain `@+id/aiTextButton` ID for backward compatibility with existing listeners.
- [x] Add `@+id/summarizeButton` for the new Summarize action.
- [x] Add `@drawable/ic_summarize.xml` icon vector for visual identification.

### Non-Functional / UI UX
- [x] Material 3 Tonal Button design conforming to app color tokens (`?attr/colorSurfaceContainer`).
- [x] Seamless touch target sizes (minimum 48dp height, 64dp card height).
- [x] **Anti Text-Clipping Safeguards**:
  - Set `android:textSize="14sp"` or `15sp` with `android:maxLines="1"` and `android:ellipsize="end"`.
  - Set `app:autoSizeTextType="uniform"` (`app:autoSizeMinTextSize="10sp"`, `app:autoSizeMaxTextSize="15sp"`, `app:autoSizeStepGranularity="1sp"`) to automatically scale down text on narrow screens.
  - Optimize icon size to `20dp` (`app:iconSize="20dp"`) and icon padding to `6dp` (`app:iconPadding="6dp"`).
  - Compact horizontal padding (`android:paddingStart="4dp"`, `android:paddingEnd="4dp"`) to prevent text truncation on smaller devices.
- [x] Responsive layout on portrait and landscape screen sizes.

## Implementation Steps
1. [x] Create `@drawable/ic_summarize.xml` vector icon (list / summary representation).
2. [x] Modify `app/src/main/res/layout/activity_main.xml`:
   - Replace the single `MaterialCardView` wrapping `aiTextButton` with a horizontal `LinearLayout` container (`android:orientation="horizontal"`).
   - Put two `MaterialCardView` or `MaterialButton` widgets inside with `android:layout_weight="1"` and `android:layout_width="0dp"`.
   - Left side: `@+id/aiTextButton` with text "AI Text" and icon `@drawable/ic_auto_fix`.
   - Right side: `@+id/summarizeButton` with text "Tóm tắt" and icon `@drawable/ic_summarize`.
   - Apply auto-size text properties (`app:autoSizeTextType="uniform"`) and compact icon padding (`app:iconSize="20dp"`, `app:iconPadding="6dp"`) to prevent any text clipping on narrow screens.
3. [x] Verify view binding generation in `MainActivity.kt` so `binding.summarizeButton` is available.
4. [x] Capture screenshot via ADB MCP tool to visually inspect and verify zero text clipping on real device screen.

## Files to Create/Modify
- `app/src/main/res/drawable/ic_summarize.xml` - [NEW] Vector icon for summarize button
- `app/src/main/res/layout/activity_main.xml` - [MODIFY] Refactor top card into 50/50 dual button container
- `app/src/test/java/com/skul9x/readoutloud/ui/SummarizeButtonLayoutTest.kt` - [NEW] Robolectric UI test verifying layout split & button presence

## Verification Test (File-Based)
Create unit/UI test file `app/src/test/java/com/skul9x/readoutloud/ui/SummarizeButtonLayoutTest.kt`:

```kotlin
package com.skul9x.readoutloud.ui

import android.view.View
import com.google.android.material.button.MaterialButton
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class SummarizeButtonLayoutTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun testDualButtonsExistAndAreVisible() {
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)

        assertNotNull("aiTextButton should exist in main layout", aiTextButton)
        assertNotNull("summarizeButton should exist in main layout", summarizeButton)

        assertEquals("aiTextButton should be visible", View.VISIBLE, aiTextButton.visibility)
        assertEquals("summarizeButton should be visible", View.VISIBLE, summarizeButton.visibility)
        assertEquals("Tóm tắt", summarizeButton.text.toString())
    }
}
```

### Automated Test Command
```bash
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.SummarizeButtonLayoutTest"
```
