# Phase 02: Prompt Tab UI & Make Prompt Button (with UI/UX Polish)

Status: ✅ Completed
Dependencies: Phase 01

## Objective
Build the complete `PromptFragment` UI and implement the "Make Prompt" button functionality. The template text from `1.txt` is bundled as `res/raw/prompt_template.txt`. When the user types a topic and taps "Make Prompt", the placeholder `{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}` in the template is replaced with the user's input, and the full constructed prompt is copied to the system clipboard.

## UI/UX Design Specification

### Layout Wireframe (Prompt Tab)
```
┌──────────────────────────────────────┐
│                                      │
│  ┌──────────────────────────────┐    │  ← Input Card (rounded 24dp)
│  │  🔍 Nhập chủ đề tìm kiếm... │    │     TextInputLayout + EditText
│  │                           ✕  │    │     Clear button (endIcon)
│  └──────────────────────────────┘    │
│                                      │
│  ┌──────────────┐ ┌──────────────┐   │  ← Dual action buttons (50/50)
│  │ 📋 Make      │ │ 🔍 Search    │   │     Tonal vs Filled/Primary
│  │   Prompt     │ │   Now        │   │
│  └──────────────┘ └──────────────┘   │
│                                      │
│  ╔══════════════════════════════╗    │  ← Result Card (hidden by default)
│  ║  [Tóm tắt] button (hidden)  ║    │     Appears after Search Now
│  ╠══════════════════════════════╣    │
│  ║                              ║    │     Scrollable result area
│  ║  Gemini response text...     ║    │     with nice typography
│  ║  line 1                      ║    │
│  ║  line 2                      ║    │
│  ║  ...                         ║    │
│  ╚══════════════════════════════╝    │
│                                      │
│  ── Empty State (shown initially) ── │  ← Centered illustration + hint
│  │       🔍 (64dp, muted)       │    │     Fades out when results appear
│  │  "Nhập chủ đề để bắt đầu"   │    │
│  ─────────────────────────────────   │
│                                      │
│        Status: Ready                 │  ← Status text (bottom)
└──────────────────────────────────────┘
```

### Visual Design Tokens
| Element | Style | Details |
|---------|-------|---------|
| Input Card | `MaterialCardView` 24dp corners | `colorSurfaceContainerLow` bg, no stroke |
| TextInputLayout | Outlined M3 style | `?attr/colorOutline` stroke, clear endIcon |
| Make Prompt btn | `Widget.Material3.Button.TonalButton` | `@drawable/ic_content_copy` icon, 64dp height |
| Search Now btn | `Widget.Material3.Button.FilledButton` | `@drawable/ic_search` icon, 64dp height, `colorPrimary` bg |
| Result Card | `MaterialCardView` 24dp corners | `colorSurfaceContainerLow` bg, appears with fade-in |
| Result Text | `TextAppearance.Material3.BodyLarge` | `20sp`, `4dp` lineSpacingExtra, scrollable |
| Empty State icon | `ImageView` 64dp | `colorOnSurfaceVariant` tint, alpha 0.3 |
| Empty State text | `TextAppearance.Material3.BodyLarge` | `colorOnSurfaceVariant`, alpha 0.6 |
| Status text | `TextAppearance.Material3.LabelSmall` | `colorOnSurfaceVariant`, centered |

## Requirements
### Functional
- [x] Bundle `1.txt` content as `app/src/main/res/raw/prompt_template.txt` (exact copy of the 228-line template file).
- [x] Create `fragment_prompt.xml` layout (full design — see wireframe above).
- [x] Implement `PromptTemplateHelper.kt` utility class:
  - `fun loadTemplate(context: Context): String` — reads `R.raw.prompt_template` into a String.
  - `fun buildPrompt(template: String, topic: String): String` — replaces `{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}` with the provided topic.
- [x] "Make Prompt" button click handler:
  1. Validate input is not blank (show Snackbar if empty — NOT Toast).
  2. Load template via `PromptTemplateHelper.loadTemplate(context)`.
  3. Build prompt via `PromptTemplateHelper.buildPrompt(template, userInput)`.
  4. Copy result to clipboard using `ClipboardManager`.
  5. Show Snackbar with checkmark: "✅ Đã copy prompt vào clipboard!".
  6. Update status text: "Prompt đã sẵn sàng trong clipboard".

### UI/UX Requirements (NEW)
- [x] **Input Field UX**:
  - `TextInputLayout` with `app:endIconMode="clear_text"` — shows ✕ button when text is entered.
  - `android:imeOptions="actionDone"` — keyboard shows "Done" action.
  - Keyboard auto-hides when "Done" is pressed or when tapping outside input.
  - Hint text: "Nhập chủ đề bạn muốn tìm kiếm..." with `hintAnimationEnabled="true"`.
  - Support multiline input but default to 3-line display (`android:minLines="1"`, `android:maxLines="3"`).
- [x] **Snackbar Instead of Toast**:
  - All user feedback (empty input, copy success) uses `Snackbar` from the Prompt tab root — more Material 3-native and supports action buttons.
  - Copy success Snackbar: "✅ Đã copy prompt!" with short duration.
  - Empty input Snackbar: "⚠️ Vui lòng nhập chủ đề" with `LENGTH_SHORT`.
- [x] **Button States**:
  - Disabled state when input is empty (both buttons have `alpha=0.38` when disabled — M3 standard).
  - Enabled state auto-updates as user types (via `TextWatcher` on EditText).
  - Ripple effect on tap (default M3 MaterialButton behavior).
- [x] **Empty State**:
  - Shows centered icon + text when no search has been performed.
  - `@+id/emptyStateGroup` — a `Group` or `LinearLayout` containing the icon and hint text.
  - Visibility: `VISIBLE` initially, `GONE` after first successful search result.
- [x] **Result Card (Hidden by Default)**:
  - `@+id/resultCard` — `MaterialCardView` wrapping a `ScrollView` + `TextView`.
  - Visibility: `GONE` initially, becomes `VISIBLE` with **fade-in animation** (300ms alpha 0→1) after search result.
  - Result text: `20sp`, `4dp` line spacing, `?attr/colorOnSurface` text color.
  - Card has subtle `cardElevation="2dp"` for depth.
- [x] **"Tóm tắt" Button (Hidden by Default)**:
  - `@+id/summarizeResultButton` — inside the result card, at the top before the scroll area.
  - Visibility: `GONE` initially, `VISIBLE` with **slide-down animation** (200ms translateY -20dp→0dp + alpha 0→1) after search result loads.
  - Same style as Read tab's existing "Tóm tắt" button: `TonalButton`, `@drawable/ic_summarize` icon.
- [x] **Keyboard Handling**:
  - When "Make Prompt" is tapped: hide keyboard first, then copy.
  - When "Search Now" is tapped: hide keyboard first, then start API call.
  - `hideKeyboard()` utility: `InputMethodManager.hideSoftInputFromWindow()`.

### Non-Functional
- [x] Material 3 styling consistent with existing dark theme.
- [x] Buttons must have minimum 48dp touch targets (64dp card height).
- [x] All animations use `AccelerateDecelerateInterpolator` for natural feel.

## Implementation Steps
1. [x] **Copy `1.txt` → `res/raw/prompt_template.txt`**: Copy the file content exactly as-is from `1.txt` into the raw resources directory.

2. [x] **Create `PromptTemplateHelper.kt`** in `utils/` package:
   ```kotlin
   package com.skul9x.readoutloud.utils

   import android.content.Context

   object PromptTemplateHelper {
       private const val PLACEHOLDER = "{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"

       fun loadTemplate(context: Context): String {
           return context.resources.openRawResource(
               context.resources.getIdentifier("prompt_template", "raw", context.packageName)
           ).bufferedReader().use { it.readText() }
       }

       fun buildPrompt(template: String, topic: String): String {
           return template.replace(PLACEHOLDER, topic)
       }
   }
   ```

3. [x] **Create `@drawable/ic_search.xml`**: Vector icon for the "Search Now" button (magnifying glass, 24dp).

4. [x] **Create `fragment_prompt.xml`** with full M3 design:
   - Root: `FrameLayout` (allows stacking empty state behind result card).
   - Top section: `LinearLayout` (vertical) containing:
     - Input card (`MaterialCardView` with `TextInputLayout`/`TextInputEditText`).
     - Button row (`LinearLayout` horizontal, 50/50 weight split).
   - Middle section: Empty state group (icon + text, centered, `@+id/emptyStateGroup`).
   - Result section: `MaterialCardView` (`@+id/resultCard`, initially GONE) containing:
     - "Tóm tắt" button (`@+id/summarizeResultButton`, initially GONE inside card).
     - `ScrollView` → `TextView` (`@+id/resultTextView`).
   - Bottom: Status text (`@+id/promptStatusText`).

5. [x] **Update `PromptFragment.kt`**: 
   - Inflate `fragment_prompt.xml` with ViewBinding.
   - Set up `TextWatcher` on input to enable/disable buttons.
   - Set up clear button via `TextInputLayout.endIconMode`.
   - Implement "Make Prompt" click: hide keyboard → build prompt → copy to clipboard → show Snackbar.
   - Set up keyboard hiding on "Done" IME action.
   - `_binding = null` in `onDestroyView()`.

6. [x] **Create animation utilities** (in `PromptFragment` or separate helper):
   ```kotlin
   private fun fadeIn(view: View, duration: Long = 300L) {
       view.alpha = 0f
       view.visibility = View.VISIBLE
       view.animate().alpha(1f).setDuration(duration)
           .setInterpolator(AccelerateDecelerateInterpolator()).start()
   }
   
   private fun slideDown(view: View, duration: Long = 200L) {
       view.alpha = 0f
       view.translationY = -20f.dpToPx()
       view.visibility = View.VISIBLE
       view.animate().alpha(1f).translationY(0f).setDuration(duration)
           .setInterpolator(AccelerateDecelerateInterpolator()).start()
   }
   ```

7. [x] **Create verification tests**.

## Files to Create/Modify
- `app/src/main/res/raw/prompt_template.txt` — [NEW] Bundled template (copy of `1.txt`)
- `app/src/main/java/com/skul9x/readoutloud/utils/PromptTemplateHelper.kt` — [NEW] Template loading and placeholder substitution utility
- `app/src/main/res/layout/fragment_prompt.xml` — [MODIFY] Full Prompt tab layout with M3 polish
- `app/src/main/res/drawable/ic_search.xml` — [NEW] Search icon vector
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` — [MODIFY] Implement Make Prompt logic + animations + keyboard handling
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabMakePromptTest.kt` — [NEW] Verification tests

## Verification Test (File-Based)
Create `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabMakePromptTest.kt`:

```kotlin
package com.skul9x.readoutloud.ui

import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.utils.PromptTemplateHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import androidx.test.core.app.ApplicationProvider

@RunWith(RobolectricTestRunner::class)
class PromptTabMakePromptTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testRawPromptTemplateResourceExists() {
        val resId = context.resources.getIdentifier("prompt_template", "raw", context.packageName)
        assertTrue("prompt_template raw resource must exist", resId != 0)
    }

    @Test
    fun testLoadTemplateReturnsNonEmptyString() {
        val template = PromptTemplateHelper.loadTemplate(context)
        assertTrue("Template must not be blank", template.isNotBlank())
        assertTrue("Template must contain the placeholder", 
            template.contains("{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"))
    }

    @Test
    fun testBuildPromptReplacesPlaceholder() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "chiến tranh thương mại Mỹ Trung 2026"
        val result = PromptTemplateHelper.buildPrompt(template, topic)

        assertFalse("Built prompt must NOT contain placeholder",
            result.contains("{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"))
        assertTrue("Built prompt must contain the user's topic", result.contains(topic))
        assertTrue("Built prompt must still contain research instructions",
            result.contains("CHUYÊN GIA NGHIÊN CỨU THÔNG TIN"))
    }

    @Test
    fun testBuildPromptPreservesTemplateStructure() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "AI regulation in EU"
        val result = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue("Must contain multilingual search principle",
            result.contains("NGUYÊN TẮC TÌM KIẾM ĐA NGÔN NGỮ"))
        assertTrue("Must contain source quality section",
            result.contains("ƯU TIÊN NGUỒN GỐC VÀ NGUỒN CHẤT LƯỢNG CAO"))
        assertTrue("Must contain final synthesis section",
            result.contains("TỔNG HỢP CUỐI CÙNG"))
    }

    @Test
    fun testBuildPromptWithEmptyTopicReplacesPlaceholder() {
        val template = "Test {THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM} end"
        val result = PromptTemplateHelper.buildPrompt(template, "")
        assertEquals("Test  end", result)
    }

    @Test
    fun testMakePromptCopiesToClipboard() {
        val topic = "Test topic"
        val template = PromptTemplateHelper.loadTemplate(context)
        val builtPrompt = PromptTemplateHelper.buildPrompt(template, topic)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Prompt", builtPrompt)
        clipboard.setPrimaryClip(clip)

        assertTrue("Clipboard must have content", clipboard.hasPrimaryClip())
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        assertTrue("Clipboard must contain the topic", clipText.contains(topic))
        assertFalse("Clipboard must NOT contain placeholder",
            clipText.contains("{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"))
    }
}
```

### Automated Test Command
```bash
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabMakePromptTest"
```

---
Next Phase: [Phase 03 — Search Now & Gemini Integration](./phase-03-search-now-gemini.md)
