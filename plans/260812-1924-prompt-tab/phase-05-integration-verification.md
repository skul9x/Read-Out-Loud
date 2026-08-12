# Phase 05: Integration & End-to-End Verification (with UI/UX Polish)

Status: ✅ Completed
Dependencies: Phase 04

## Objective
Final integration phase: ensure all components work together seamlessly including all UI/UX polish elements. Run the full test suite (all existing tests + all new tests from Phases 01-04). Verify zero regressions, proper tab navigation with animations, clipboard operations, Gemini API calls, cross-tab summarize flow, inline loading/error states, and overall visual consistency. Deploy to a connected device via ADB for visual verification.

## UI/UX Verification Checklist

### Tab Navigation
- [ ] Tab icons (Read: book icon, Prompt: search icon) display correctly with tint color changes on selection.
- [ ] Tab indicator is content-width (not full width), `3dp` height, `?attr/colorPrimary` color.
- [ ] Tab ripple effect on touch.
- [ ] Swipe between tabs is smooth (ViewPager2 default animation).
- [ ] Tab divider line (1dp) separates tabs from content.
- [ ] `offscreenPageLimit = 1` keeps both fragments alive — no re-creation on tab switch.

### Prompt Tab
- [ ] Input field has clear button (✕) that appears when text is entered.
- [ ] Input field hint animates up when focused (Material 3 standard).
- [ ] Keyboard shows "Done" action, and hides on Done or outside tap.
- [ ] Both buttons disabled when input is empty (alpha 0.38).
- [ ] Both buttons re-enable as user types (TextWatcher).
- [ ] "Make Prompt" copies to clipboard and shows Snackbar (not Toast).
- [ ] "Search Now" hides keyboard before starting API call.
- [ ] Inline loading card (not full-screen overlay) shows during search with CircularProgressIndicator.
- [ ] Result card appears with fade-in animation (300ms).
- [ ] "Tóm tắt" button slides in from top (200ms, delayed 100ms after result card).
- [ ] Error card shows with retry button on API failure (red-tinted `colorErrorContainer`).
- [ ] Empty state (icon + hint text) shows before first search, hides after.
- [ ] Result persists when switching tabs and returning.

### Cross-Tab Flow
- [ ] Tapping "Tóm tắt" in Prompt tab: button scale animation (0.95→1.0), smooth tab switch to Read.
- [ ] Read tab receives text, scrolls to top, shows Snackbar "📄 Đang tóm tắt kết quả tìm kiếm...".
- [ ] Summarization starts automatically (loading overlay appears in Read tab).
- [ ] After summarization completes, result replaces the original text in Read tab.
- [ ] Returning to Prompt tab: original search result still visible, "Tóm tắt" button re-enabled.

### Read Tab (Regression)
- [ ] All existing features work: Paste, Read, Stop, AI Text, Tóm tắt, Copy, Volume.
- [ ] Karaoke highlight + auto-scroll works during TTS reading.
- [ ] Double-tap edit mode works.
- [ ] Settings button navigates to SettingsActivity.

## Requirements
### Functional
- [ ] All existing unit tests pass (49+ pre-existing test cases).
- [ ] All new Phase 01-04 tests pass.
- [ ] APK builds successfully (debug and release).
- [ ] App launches without crashes.

### Non-Functional
- [ ] Material 3 theming consistent across both tabs (dark mode).
- [ ] No memory leaks from Fragment lifecycle mismanagement.
- [ ] No ViewBinding null pointer exceptions.
- [ ] All animations are smooth (no frame drops on mid-range devices).

## Verification Steps
1. [ ] **Run all existing tests**: `./gradlew testDebugUnitTest` — must pass with zero failures.
2. [ ] **Run Phase 01 tests**: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.TabLayoutInfrastructureTest"`
3. [ ] **Run Phase 02 tests**: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabMakePromptTest"`
4. [ ] **Run Phase 03 tests**: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabSearchNowTest"`
5. [ ] **Run Phase 04 tests**: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.SummarizeCrossTabFlowTest"`
6. [ ] **Run integration test**: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.PromptTabIntegrationTest"`
7. [ ] **Build debug APK**: `./gradlew :app:assembleDebug`
8. [ ] **Install & visual test on device**: Use ADB MCP tool to install APK and take screenshots for:
   - Tab layout with icons
   - Prompt tab empty state
   - Prompt tab with input + enabled buttons
   - Prompt tab loading state (inline card)
   - Prompt tab with search result + "Tóm tắt" button
   - Prompt tab error state with retry
   - Cross-tab transition to Read tab
   - Read tab after receiving summarized text

## Integration Test (File-Based)
Create `app/src/test/java/com/skul9x/readoutloud/PromptTabIntegrationTest.kt`:

```kotlin
package com.skul9x.readoutloud

import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.ui.MainSharedViewModel
import com.skul9x.readoutloud.utils.PromptTemplateHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import androidx.test.core.app.ApplicationProvider

@RunWith(RobolectricTestRunner::class)
class PromptTabIntegrationTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity
    private lateinit var context: Context

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testTabLayoutAndViewPagerIntegrated() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)

        assertNotNull("TabLayout must exist", tabLayout)
        assertNotNull("ViewPager2 must exist", viewPager)
        assertEquals(2, tabLayout.tabCount)
        assertEquals(2, viewPager.adapter?.itemCount)
    }

    @Test
    fun testTabsHaveIcons() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull("Read tab must have icon", tabLayout.getTabAt(0)?.icon)
        assertNotNull("Prompt tab must have icon", tabLayout.getTabAt(1)?.icon)
    }

    @Test
    fun testPromptTemplateLoadsCorrectly() {
        val template = PromptTemplateHelper.loadTemplate(context)
        assertTrue("Template must contain research expert role",
            template.contains("CHUYÊN GIA NGHIÊN CỨU"))
        assertTrue("Template must contain placeholder",
            template.contains("{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"))
    }

    @Test
    fun testPromptBuildingEndToEnd() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "tình hình kinh tế Việt Nam Q3 2026"
        val prompt = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue("Prompt must contain topic", prompt.contains(topic))
        assertFalse("Prompt must NOT contain placeholder",
            prompt.contains("{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"))
        assertTrue("Prompt must be longer than 5000 chars", prompt.length > 5000)
    }

    @Test
    fun testClipboardCopyIntegration() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "semiconductor industry"
        val prompt = PromptTemplateHelper.buildPrompt(template, topic)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Prompt", prompt)
        clipboard.setPrimaryClip(clip)

        assertTrue(clipboard.hasPrimaryClip())
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        assertTrue(clipText.contains(topic))
    }

    @Test
    fun testSharedViewModelCrossTabCommunication() {
        val viewModel = MainSharedViewModel()

        val searchResult = "Kết quả nghiên cứu: ..."
        viewModel.requestSummarize(searchResult)
        assertEquals(searchResult, viewModel.summarizeEvent.value)

        viewModel.clearSummarizeEvent()
        assertNull(viewModel.summarizeEvent.value)
    }

    @Test
    fun testGeminiApiClientHasSearchWithPromptMethod() {
        val client = GeminiApiClient(context)
        val hasMethod = GeminiApiClient::class.java.methods.any { it.name == "searchWithPrompt" }
        assertTrue("GeminiApiClient must have searchWithPrompt method", hasMethod)
    }

    @Test
    fun testAllExistingViewsStillAccessible() {
        val toolbar = activity.findViewById<View>(R.id.toolbar)
        val loadingOverlay = activity.findViewById<View>(R.id.loadingOverlay)

        assertNotNull("Toolbar must still be accessible", toolbar)
        assertNotNull("Loading overlay must still be accessible", loadingOverlay)
    }

    @Test
    fun testDefaultTabIsReadTab() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertEquals("Default tab must be Read (index 0)", 0, tabLayout.selectedTabPosition)
    }

    @Test
    fun testTabIndicatorNotFullWidth() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertFalse("Tab indicator should not be full width (M3 style)",
            tabLayout.isTabIndicatorFullWidth)
    }

    @Test
    fun testViewPagerKeepsBothFragmentsAlive() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertEquals("offscreenPageLimit should be 1",
            1, viewPager.offscreenPageLimit)
    }
}
```

### Automated Test Commands
```bash
# Run integration test
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.PromptTabIntegrationTest"

# Run ALL tests (existing + new)
./gradlew testDebugUnitTest

# Build debug APK
./gradlew :app:assembleDebug
```

## Files to Create/Modify
- `app/src/test/java/com/skul9x/readoutloud/PromptTabIntegrationTest.kt` — [NEW] End-to-end integration test suite

## Checklist Summary
| Item | Test File | Command |
|------|-----------|---------|
| TabLayout + ViewPager2 + Icons | `TabLayoutInfrastructureTest.kt` | Phase 01 |
| Prompt template & Make Prompt + Snackbar | `PromptTabMakePromptTest.kt` | Phase 02 |
| Search Now + Inline Loading + Error Card | `PromptTabSearchNowTest.kt` | Phase 03 |
| Cross-tab summarize + Animations | `SummarizeCrossTabFlowTest.kt` | Phase 04 |
| Full integration + UI/UX visual | `PromptTabIntegrationTest.kt` | Phase 05 |
| All existing tests (regression) | All `*Test.kt` files | `./gradlew test` |

## Visual Verification Screenshots (ADB)
After successful build, take screenshots for these states:
1. `screenshot_tabs_default.png` — App launch, Read tab selected, tab icons visible
2. `screenshot_prompt_empty.png` — Prompt tab, empty state with icon + hint
3. `screenshot_prompt_input.png` — Prompt tab, user typed topic, buttons enabled
4. `screenshot_prompt_loading.png` — Prompt tab, inline loading card during search
5. `screenshot_prompt_result.png` — Prompt tab, search result with "Tóm tắt" button
6. `screenshot_prompt_error.png` — Prompt tab, error card with retry button
7. `screenshot_cross_tab.png` — Read tab, text injected from Prompt, summarizing

---
Previous Phase: [Phase 04 — Summarize Cross-Tab Flow](./phase-04-summarize-cross-tab.md)
