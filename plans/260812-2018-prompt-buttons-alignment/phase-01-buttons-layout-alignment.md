# Phase 01: Buttons Layout & Typography Alignment

Status: ✅ Completed  
Dependencies: None

## Objective
Fix the layout and typography misalignment between `makePromptButton` ("Make Prompt") and `searchNowButton` ("Search Now") in `fragment_prompt.xml` so that both buttons maintain a single line of text, equal heights, identical baselines, centered icon/text groupings, and harmonious visual weight across all screen sizes.

---

## Root Cause Analysis
1. **Unconstrained Multi-Line Wrapping**: `MaterialButton` wraps text onto multiple lines by default. "Make Prompt" has 11 characters and wider glyphs (`M`, `P`), exceeding the available width of a 50% column on standard viewports (360dp–412dp) when formatted at `15sp` bold with icon and default padding.
2. **Padding Overhead**: Default Material 3 button styles add 16dp–24dp horizontal padding on both sides, leaving insufficient text space.
3. **Missing Single-Line & Ellipsize Attributes**: The buttons lack `android:maxLines="1"`, `android:singleLine="true"`, and `android:ellipsize="end"`.
4. **Height & Inset Inconsistencies**: Without explicit `app:insetTop="0dp"` and `app:insetBottom="0dp"`, touch boundaries and vertical baselines can experience slight shifts.

---

## Requirements

### Functional Requirements
- Both `makePromptButton` and `searchNowButton` must display their full label on **exactly one line** without text wrapping.
- Both buttons must maintain equal heights (`56dp`), equal weights (`layout_weight="1"`), equal corner radii (`24dp`), and equal margins (`6dp` inner spacing, `16dp` outer margins).
- Icons (`ic_content_copy` and `ic_search`) and text labels must be centered horizontally as a unified unit using `app:iconGravity="textStart"` with `android:gravity="center"`.
- Buttons must handle small screens gracefully without breaking row alignment.

### Non-Functional Requirements
- **Performance**: Zero overhead; purely XML layout attribute improvements.
- **Accessibility**: Minimum touch target height >= 48dp (56dp provided).
- **M3 Compliance**: Follow Material Design 3 guidelines for Tonal & Filled button pairs.

---

## Implementation Steps

1. **Modify `app/src/main/res/layout/fragment_prompt.xml`**:
   - Update `makePromptButton` and `searchNowButton`:
     - Standardize height: `android:layout_height="56dp"`
     - Add `android:maxLines="1"`
     - Add `android:singleLine="true"`
     - Add `android:ellipsize="end"`
     - Adjust text size: `android:textSize="14sp"`
     - Add explicit balanced padding: `android:paddingStart="12dp"`, `android:paddingEnd="12dp"`
     - Set insets to zero: `app:insetTop="0dp"`, `app:insetBottom="0dp"`
     - Ensure `android:gravity="center"`, `app:iconGravity="textStart"`, `app:iconPadding="8dp"`, `app:iconSize="20dp"`

```xml
    <!-- Dual Action Buttons Row (50/50) -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="12dp"
        android:orientation="horizontal"
        android:gravity="center_vertical">

        <com.google.android.material.button.MaterialButton
            android:id="@+id/makePromptButton"
            style="@style/Widget.Material3.Button.TonalButton"
            android:layout_width="0dp"
            android:layout_height="56dp"
            android:layout_marginEnd="6dp"
            android:layout_weight="1"
            android:enabled="false"
            android:gravity="center"
            android:maxLines="1"
            android:singleLine="true"
            android:ellipsize="end"
            android:paddingStart="12dp"
            android:paddingEnd="12dp"
            android:text="Make Prompt"
            android:textSize="14sp"
            android:textStyle="bold"
            app:cornerRadius="24dp"
            app:icon="@drawable/ic_content_copy"
            app:iconGravity="textStart"
            app:iconPadding="8dp"
            app:iconSize="20dp"
            app:insetTop="0dp"
            app:insetBottom="0dp" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/searchNowButton"
            style="@style/Widget.Material3.Button"
            android:layout_width="0dp"
            android:layout_height="56dp"
            android:layout_marginStart="6dp"
            android:layout_weight="1"
            android:enabled="false"
            android:gravity="center"
            android:maxLines="1"
            android:singleLine="true"
            android:ellipsize="end"
            android:paddingStart="12dp"
            android:paddingEnd="12dp"
            android:text="Search Now"
            android:textSize="14sp"
            android:textStyle="bold"
            app:cornerRadius="24dp"
            app:icon="@drawable/ic_search"
            app:iconGravity="textStart"
            app:iconPadding="8dp"
            app:iconSize="20dp"
            app:insetTop="0dp"
            app:insetBottom="0dp" />
    </LinearLayout>
```

2. **Create Test File `app/src/test/java/com/skul9x/readoutloud/ui/PromptButtonsLayoutAlignmentTest.kt`**:
   - Write comprehensive Robolectric tests checking:
     - Both buttons exist and have identical `layout_weight` (1.0f).
     - Both buttons have identical layout height (`56dp`).
     - Both buttons enforce `maxLines == 1`.
     - Both buttons have identical `cornerRadius` (`24dp`).
     - Both buttons have identical `iconSize` (`20dp`) and `iconPadding` (`8dp`).
     - Both buttons have `iconGravity == ICON_GRAVITY_TEXT_START`.
     - Both buttons have matching `textSize` (`14sp`).
     - Parent container is horizontal LinearLayout with `center_vertical` gravity.

---

## Files to Create / Modify
- `app/src/main/res/layout/fragment_prompt.xml` — [MODIFY] Apply typography, padding, single-line, and height constraints.
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptButtonsLayoutAlignmentTest.kt` — [NEW] Unit test verifying button alignment parameters and attributes.

---

## Detailed File-Based Test Specification
File: `app/src/test/java/com/skul9x/readoutloud/ui/PromptButtonsLayoutAlignmentTest.kt`

```kotlin
package com.skul9x.readoutloud.ui

import android.view.Gravity
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PromptButtonsLayoutAlignmentTest {

    private lateinit var activity: MainActivity
    private lateinit var makePromptButton: MaterialButton
    private lateinit var searchNowButton: MaterialButton

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        makePromptButton = activity.findViewById(R.id.makePromptButton)
        searchNowButton = activity.findViewById(R.id.searchNowButton)
    }

    @Test
    fun testButtonsExistAndAreVisible() {
        assertNotNull("makePromptButton must exist", makePromptButton)
        assertNotNull("searchNowButton must exist", searchNowButton)
    }

    @Test
    fun testButtonsHaveEqualWeightsAndEqualHeight() {
        val makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        val searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams

        assertEquals("Make Prompt button weight must be 1.0", 1.0f, makeParams.weight, 0.01f)
        assertEquals("Search Now button weight must be 1.0", 1.0f, searchParams.weight, 0.01f)

        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (56 * density).toInt()
        assertEquals("Make Prompt button height must be 56dp", expectedHeightPx, makeParams.height)
        assertEquals("Search Now button height must be 56dp", expectedHeightPx, searchParams.height)
    }

    @Test
    fun testButtonsEnforceSingleLineConstraints() {
        assertEquals("Make Prompt button maxLines must be 1", 1, makePromptButton.maxLines)
        assertEquals("Search Now button maxLines must be 1", 1, searchNowButton.maxLines)
    }

    @Test
    fun testButtonsHaveMatchingIconPropertiesAndGravity() {
        assertEquals("Make Prompt icon size must be 20dp", (20 * activity.resources.displayMetrics.density).toInt(), makePromptButton.iconSize)
        assertEquals("Search Now icon size must be 20dp", (20 * activity.resources.displayMetrics.density).toInt(), searchNowButton.iconSize)

        assertEquals("Make Prompt icon padding must be 8dp", (8 * activity.resources.displayMetrics.density).toInt(), makePromptButton.iconPadding)
        assertEquals("Search Now icon padding must be 8dp", (8 * activity.resources.displayMetrics.density).toInt(), searchNowButton.iconPadding)

        assertEquals("Make Prompt iconGravity must be TEXT_START", MaterialButton.ICON_GRAVITY_TEXT_START, makePromptButton.iconGravity)
        assertEquals("Search Now iconGravity must be TEXT_START", MaterialButton.ICON_GRAVITY_TEXT_START, searchNowButton.iconGravity)
    }

    @Test
    fun testButtonsHaveMatchingCornerRadius() {
        val expectedRadiusPx = (24 * activity.resources.displayMetrics.density).toInt()
        assertEquals("Make Prompt corner radius must be 24dp", expectedRadiusPx, makePromptButton.cornerRadius)
        assertEquals("Search Now corner radius must be 24dp", expectedRadiusPx, searchNowButton.cornerRadius)
    }
}
```

---

## Test Criteria
- [x] `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptButtonsLayoutAlignmentTest"` passes with 100% success.
- [x] Existing `PromptTabMakePromptTest` and `PromptTabSearchNowTest` pass without regression.

---
Next Phase: [Phase 02 — Responsive Behavior & Dynamic State Testing](./phase-02-responsive-behavior-tests.md)
