# Phase 01: Infrastructure & Tab Shell (with UI/UX Polish)

Status: ✅ Completed
Dependencies: None

## Objective
Convert `MainActivity` from a single-screen Activity into a TabLayout + ViewPager2 tab-based Activity. Extract the entire current main screen UI into `ReadFragment` and create an empty `PromptFragment` shell. The app must look and behave identically after this refactoring — all existing functionality (Read, AI Text, Summarize, Paste, Copy, TTS, Karaoke) must continue working inside the Read tab.

## Requirements
### Functional
- [x] Add `androidx.viewpager2:viewpager2:1.1.0` dependency to `libs.versions.toml` and `build.gradle.kts`.
- [x] Create new layout `activity_main_tabs.xml` containing only: `MaterialToolbar` (top bar with volume + settings), `TabLayout` (2 tabs: "Read" and "Prompt"), `ViewPager2` (fills remaining space), and the existing `loadingOverlay` FrameLayout.
- [x] Create `ReadFragment.kt` with its layout `fragment_read.xml` — this layout contains the **entire** existing main screen content (AI buttons row, action grid, text workspace card, progress bar, status/footer). Move all relevant UI logic from `MainActivity` into `ReadFragment`.
- [x] Create empty `PromptFragment.kt` with minimal layout `fragment_prompt.xml` (empty state with illustration hint).
- [x] Create `MainPagerAdapter.kt` extending `FragmentStateAdapter` with 2 pages: position 0 → ReadFragment, position 1 → PromptFragment.
- [x] Update `MainActivity.kt` to use `activity_main_tabs.xml`, set up `ViewPager2` + `TabLayout` via `TabLayoutMediator`, and keep toolbar/loading overlay logic in the Activity level.
- [x] All existing TTS, AI text cleaning, summarize, paste, copy, volume, gesture, and karaoke highlight features must work correctly inside `ReadFragment`.

### UI/UX Requirements (NEW)
- [x] **Tab Icons + Text**: Each tab must display both an icon and a text label (Material 3 guideline: never mix icon-only and text-only tabs).
  - Read tab: `@drawable/ic_tab_read` (book/headphones icon) + "Read"
  - Prompt tab: `@drawable/ic_tab_prompt` (search/lightbulb icon) + "Prompt"
- [x] **Tab Indicator Styling**: Use `app:tabIndicatorColor="?attr/colorPrimary"` with `app:tabIndicatorFullWidth="false"` for a clean M3 content-width indicator. Indicator corner radius `4dp`.
- [x] **Tab Typography**: `app:tabTextAppearance="@style/TextAppearance.Material3.LabelLarge"` with bold for selected, normal for unselected.
- [x] **Tab Selected/Unselected Colors**: 
  - Selected: `?attr/colorPrimary` (icon + text)
  - Unselected: `?attr/colorOnSurfaceVariant` (icon + text, 60% opacity feel)
  - Use `app:tabIconTint` and `app:tabTextColor` with `ColorStateList` selector.
- [x] **Tab Ripple**: Enable ripple on tab touch with `app:tabRippleColor="?attr/colorPrimary"` at 12% alpha.
- [x] **ViewPager2 Page Transition**: Use a subtle `MarginPageTransformer(0)` or custom `DepthPageTransformer` for smooth swipe transitions (avoid jarring flat scroll).
- [x] **Tab Divider**: Add a thin divider line below `TabLayout` (`1dp`, `?attr/colorSurfaceVariant`) for visual separation from content.
- [x] **PromptFragment Empty State (Placeholder Phase)**: Instead of plain text, show:
  - A centered `ImageView` with `@drawable/ic_tab_prompt` at `64dp` (muted `?attr/colorOnSurfaceVariant` tint, 40% alpha)
  - Text: "Nhập chủ đề để bắt đầu tìm kiếm" with `TextAppearance.Material3.BodyLarge` and muted color
  - This gives users a clear visual cue of what the tab will do before Phase 02 fills it in.
- [x] **Swipe between tabs**: Enabled. No `app:userInputEnabled="false"`.

### Non-Functional
- [x] Material 3 TabLayout styling consistent with existing dark theme.
- [x] No regression on any existing unit tests.
- [x] ViewPager2 `offscreenPageLimit = 1` to keep both fragments alive (prevents re-creation on tab switch).

## Implementation Steps
1. [x] **Add ViewPager2 dependency**:
   - Add `viewpager2 = "1.1.0"` to `gradle/libs.versions.toml` `[versions]` section.
   - Add `androidx-viewpager2 = { group = "androidx.viewpager2", name = "viewpager2", version.ref = "viewpager2" }` to `[libraries]` section.
   - Add `implementation(libs.androidx.viewpager2)` to `app/build.gradle.kts` dependencies.
   - Add `implementation(libs.androidx.activity)` if not already present (needed for `activityViewModels()`).
   - Add `implementation("androidx.fragment:fragment-ktx:1.8.6")` for `activityViewModels()` delegate.

2. [x] **Create tab icon drawables**:
   - `@drawable/ic_tab_read.xml` — Material Symbols "menu_book" or "headphones" vector (24dp).
   - `@drawable/ic_tab_prompt.xml` — Material Symbols "search" or "lightbulb" vector (24dp).
   - `@drawable/tab_icon_color_selector.xml` — `ColorStateList` for selected/unselected tab icon/text tinting.

3. [x] **Create `fragment_read.xml`**:
   - Extract the content inside the root `LinearLayout` of `activity_main.xml` (everything below the toolbar and above the loading overlay): AI buttons row, action grid, text workspace card, reading progress card, and status/footer.
   - The toolbar and loading overlay stay at the Activity level.

4. [x] **Create `ReadFragment.kt`**:
   - Move all UI setup logic (`setupUI`, `enterEditMode`, `exitEditMode`, `pasteFromClipboard`, `copyToClipboard`, `processWithAI`, `processSummarizeWithAI`, `highlightWord`, `autoScrollToHighlight`, `clearHighlight`, `setLoading`, `checkPermissionsAndRead`, `startReading`, `stopReading`) from `MainActivity` into `ReadFragment`.
   - Use `FragmentReadBinding` for view binding.
   - Use `viewLifecycleOwner.lifecycleScope` for coroutines.
   - Access `GeminiApiClient`, `SharedPreferences`, TTS via the fragment's `requireContext()` / `requireActivity()`.
   - Register/unregister `BroadcastReceiver` for TTS progress in `onStart`/`onStop`.
   - Implement `_binding = null` in `onDestroyView()` to prevent memory leaks.

5. [x] **Create `fragment_prompt.xml` (empty state)**:
   - `LinearLayout` (vertical, centered gravity).
   - `ImageView` (64dp, `@drawable/ic_tab_prompt`, tinted `?attr/colorOnSurfaceVariant`, alpha 0.4).
   - `TextView` ("Nhập chủ đề để bắt đầu tìm kiếm", `BodyLarge`, muted color).
   - This is a **temporary** placeholder; Phase 02 replaces with the full UI.

6. [x] **Create `PromptFragment.kt`**:
   - Minimal fragment inflating `fragment_prompt.xml`. No logic yet.

7. [x] **Create `MainPagerAdapter.kt`**:
   - Extends `FragmentStateAdapter(activity: FragmentActivity)`.
   - `getItemCount() = 2`.
   - `createFragment(position)`: 0 → `ReadFragment()`, 1 → `PromptFragment()`.

8. [x] **Create `activity_main_tabs.xml`** with UI polish:
   ```xml
   <!-- Root FrameLayout -->
   <FrameLayout ...>

       <LinearLayout orientation="vertical">

           <!-- MaterialToolbar (existing — volume, title, settings) -->
           <MaterialToolbar ... />

           <!-- Material 3 TabLayout with icons + text -->
           <com.google.android.material.tabs.TabLayout
               android:id="@+id/tabLayout"
               android:layout_width="match_parent"
               android:layout_height="wrap_content"
               android:background="?attr/colorSurface"
               app:tabMode="fixed"
               app:tabGravity="fill"
               app:tabIndicatorColor="?attr/colorPrimary"
               app:tabIndicatorFullWidth="false"
               app:tabIndicatorHeight="3dp"
               app:tabSelectedTextColor="?attr/colorPrimary"
               app:tabTextColor="?attr/colorOnSurfaceVariant"
               app:tabIconTint="@color/tab_icon_color_selector"
               app:tabTextAppearance="@style/TextAppearance.Material3.LabelLarge"
               app:tabRippleColor="@color/tab_ripple_color" />

           <!-- Thin divider below tabs -->
           <View
               android:layout_width="match_parent"
               android:layout_height="1dp"
               android:background="?attr/colorSurfaceVariant" />

           <!-- ViewPager2 fills remaining space -->
           <androidx.viewpager2.widget.ViewPager2
               android:id="@+id/viewPager"
               android:layout_width="match_parent"
               android:layout_height="0dp"
               android:layout_weight="1" />

       </LinearLayout>

       <!-- Loading Overlay (existing — Activity-level) -->
       <FrameLayout android:id="@+id/loadingOverlay" ... />

   </FrameLayout>
   ```

9. [x] **Create color selectors** for tab icon/text:
   - `res/color/tab_icon_color_selector.xml`:
     ```xml
     <selector>
         <item android:color="?attr/colorPrimary" android:state_selected="true" />
         <item android:color="?attr/colorOnSurfaceVariant" />
     </selector>
     ```
   - `res/color/tab_ripple_color.xml`:
     ```xml
     <selector>
         <item android:alpha="0.12" android:color="?attr/colorPrimary" android:state_pressed="true" />
         <item android:alpha="0.08" android:color="?attr/colorPrimary" />
     </selector>
     ```

10. [x] **Refactor `MainActivity.kt`**:
    - Change `setContentView` to use `activity_main_tabs.xml`.
    - Set up `ViewPager2` with `MainPagerAdapter`.
    - Set `viewPager.offscreenPageLimit = 1` to keep both fragments alive.
    - Connect `TabLayout` + `ViewPager2` via `TabLayoutMediator`:
      ```kotlin
      TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
          when (position) {
              0 -> {
                  tab.text = "Read"
                  tab.setIcon(R.drawable.ic_tab_read)
              }
              1 -> {
                  tab.text = "Prompt"
                  tab.setIcon(R.drawable.ic_tab_prompt)
              }
          }
      }.attach()
      ```
    - Keep toolbar setup (volume button, settings button) in Activity.
    - Keep `loadingOverlay` control in Activity (fragments call Activity methods to show/hide it).
    - Remove all fragment-specific UI logic that has been moved to `ReadFragment`.

11. [x] **Verify zero regression** — run all existing tests.

## Files to Create/Modify
- `gradle/libs.versions.toml` — [MODIFY] Add viewpager2 version + library
- `app/build.gradle.kts` — [MODIFY] Add viewpager2 + fragment-ktx dependency
- `app/src/main/res/layout/activity_main_tabs.xml` — [NEW] Tab-based main layout with M3 styled TabLayout
- `app/src/main/res/layout/fragment_read.xml` — [NEW] Extracted Read tab layout
- `app/src/main/res/layout/fragment_prompt.xml` — [NEW] Empty state placeholder with icon + hint
- `app/src/main/res/drawable/ic_tab_read.xml` — [NEW] Tab icon for Read
- `app/src/main/res/drawable/ic_tab_prompt.xml` — [NEW] Tab icon for Prompt
- `app/src/main/res/color/tab_icon_color_selector.xml` — [NEW] Tab icon tint selector
- `app/src/main/res/color/tab_ripple_color.xml` — [NEW] Tab ripple color selector
- `app/src/main/java/com/skul9x/readoutloud/ui/ReadFragment.kt` — [NEW] Read tab fragment with all existing logic
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` — [NEW] Empty Prompt tab fragment
- `app/src/main/java/com/skul9x/readoutloud/ui/MainPagerAdapter.kt` — [NEW] ViewPager2 adapter
- `app/src/main/java/com/skul9x/readoutloud/MainActivity.kt` — [MODIFY] Refactored to host tabs
- `app/src/test/java/com/skul9x/readoutloud/ui/TabLayoutInfrastructureTest.kt` — [NEW] Verification tests

## Verification Test (File-Based)
Create `app/src/test/java/com/skul9x/readoutloud/ui/TabLayoutInfrastructureTest.kt`:

```kotlin
package com.skul9x.readoutloud.ui

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
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
class TabLayoutInfrastructureTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun testTabLayoutExists() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull("TabLayout must exist in activity_main_tabs layout", tabLayout)
        assertEquals("TabLayout must be visible", View.VISIBLE, tabLayout.visibility)
    }

    @Test
    fun testViewPager2Exists() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertNotNull("ViewPager2 must exist in activity_main_tabs layout", viewPager)
        assertEquals("ViewPager2 must be visible", View.VISIBLE, viewPager.visibility)
    }

    @Test
    fun testTabLayoutHasTwoTabs() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull("TabLayout must exist", tabLayout)
        assertEquals("TabLayout must have exactly 2 tabs", 2, tabLayout.tabCount)
    }

    @Test
    fun testTabLabels() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertEquals("First tab label must be 'Read'", "Read", tabLayout.getTabAt(0)?.text)
        assertEquals("Second tab label must be 'Prompt'", "Prompt", tabLayout.getTabAt(1)?.text)
    }

    @Test
    fun testTabIconsExist() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertNotNull("Read tab must have an icon", tabLayout.getTabAt(0)?.icon)
        assertNotNull("Prompt tab must have an icon", tabLayout.getTabAt(1)?.icon)
    }

    @Test
    fun testViewPagerAdapterHasTwoPages() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertNotNull(viewPager)
        val adapter = viewPager.adapter
        assertNotNull("ViewPager2 must have an adapter set", adapter)
        assertEquals("Adapter must have exactly 2 items", 2, adapter!!.itemCount)
    }

    @Test
    fun testToolbarStillExistsAtActivityLevel() {
        val toolbar = activity.findViewById<View>(R.id.toolbar)
        assertNotNull("Toolbar must still exist in Activity", toolbar)
        assertEquals("Toolbar must be visible", View.VISIBLE, toolbar.visibility)
    }

    @Test
    fun testLoadingOverlayStillExists() {
        val overlay = activity.findViewById<View>(R.id.loadingOverlay)
        assertNotNull("Loading overlay must still exist in Activity", overlay)
        assertEquals("Loading overlay must be GONE initially", View.GONE, overlay.visibility)
    }

    @Test
    fun testDefaultTabIsRead() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertEquals("Default selected tab must be 0 (Read)", 0, tabLayout.selectedTabPosition)
    }

    @Test
    fun testTabIndicatorIsNotFullWidth() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        // M3 best practice: indicator should match content width, not full tab width
        assertFalse("Tab indicator should not be full width (M3 style)",
            tabLayout.isTabIndicatorFullWidth)
    }

    @Test
    fun testViewPagerOffscreenPageLimit() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertNotNull(viewPager)
        assertEquals("offscreenPageLimit should be 1 to keep both fragments alive",
            1, viewPager.offscreenPageLimit)
    }
}
```

### Automated Test Command
```bash
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.TabLayoutInfrastructureTest"
```

---
Next Phase: [Phase 02 — Prompt Tab UI & Make Prompt](./phase-02-prompt-tab-ui.md)
