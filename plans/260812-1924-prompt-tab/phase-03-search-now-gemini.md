# Phase 03: Search Now & Gemini API Integration (with UI/UX Polish)

Status: ✅ Completed
Dependencies: Phase 02

## Objective
Implement the "Search Now" button in `PromptFragment`. When tapped, it constructs the full prompt (same as "Make Prompt" — template + user topic), sends it to the Gemini API via `GeminiApiClient`, shows an **inline loading state** within the Prompt tab, and displays the API response in a scrollable result card with proper animations.

## UI/UX Design Specification

### Loading State Wireframe
```
┌──────────────────────────────────────┐
│  ┌──────────────────────────────┐    │
│  │  chip bán dẫn TSMC 2nm      │    │  ← Input stays visible but disabled
│  └──────────────────────────────┘    │
│                                      │
│  ┌──────────────┐ ┌──────────────┐   │  ← Both buttons DISABLED (alpha 0.38)
│  │ 📋 Make      │ │ 🔍 Search    │   │
│  │   Prompt     │ │   Now        │   │
│  └──────────────┘ └──────────────┘   │
│                                      │
│  ┌──────────────────────────────┐    │  ← Inline loading indicator
│  │  ┌────────────────────────┐  │    │     (NOT full-screen overlay)
│  │  │  ⟳  Đang tìm kiếm...  │  │    │     CircularProgressIndicator
│  │  │     Gemini đang xử lý  │  │    │     + animated dots text
│  │  └────────────────────────┘  │    │
│  └──────────────────────────────┘    │
│                                      │
│        Status: Đang tìm kiếm...     │
└──────────────────────────────────────┘
```

### Result State Wireframe (after success)
```
┌──────────────────────────────────────┐
│  ┌──────────────────────────────┐    │
│  │  chip bán dẫn TSMC 2nm      │    │  ← Input re-enabled
│  └──────────────────────────────┘    │
│                                      │
│  ┌──────────────┐ ┌──────────────┐   │  ← Buttons re-enabled
│  │ 📋 Make      │ │ 🔍 Search    │   │
│  │   Prompt     │ │   Now        │   │
│  └──────────────┘ └──────────────┘   │
│                                      │
│  ╔══════════════════════════════╗    │  ← Result card (fade-in 300ms)
│  ║ [📄 Tóm tắt]               ║    │  ← Summarize btn (slide-down 200ms)
│  ╠══════════════════════════════╣    │
│  ║  Kết quả từ Gemini          ║    │  ← Scrollable result text
│  ║  (gemini-2.0-flash)         ║    │     with model badge
│  ║                              ║    │
│  ║  Phần 1: ...                 ║    │
│  ║  Phần 2: ...                 ║    │
│  ║  ...                         ║    │
│  ╚══════════════════════════════╝    │
│                                      │
│  Gemini: Done (gemini-2.0-flash)    │  ← Status with model name
└──────────────────────────────────────┘
```

### Error State Wireframe
```
┌──────────────────────────────────────┐
│  ...input + buttons (re-enabled)...  │
│                                      │
│  ┌──────────────────────────────┐    │  ← Error card (instead of result)
│  │  ⚠️ Lỗi kết nối             │    │     Red-tinted card bg
│  │                              │    │     `colorErrorContainer`
│  │  Hệ thống đang quá tải.     │    │
│  │  Vui lòng thử lại sau.      │    │
│  │                              │    │
│  │         [🔄 Thử lại]        │    │  ← Retry button (Outlined)
│  └──────────────────────────────┘    │
│                                      │
│        Status: Lỗi Gemini           │
└──────────────────────────────────────┘
```

## Requirements
### Functional
- [x] "Search Now" button click handler:
  1. Validate input is not blank (show Snackbar if empty).
  2. **Hide keyboard** before starting API call.
  3. Load template and build prompt (reuse `PromptTemplateHelper`).
  4. Show **inline loading state** (see below).
  5. Call `GeminiApiClient.searchWithPrompt(prompt)` on `Dispatchers.IO` via `viewLifecycleOwner.lifecycleScope`.
  6. On success: display result with animations, show "Tóm tắt" button, update status.
  7. On error: show error card with retry button.
  8. Re-enable buttons after completion.
- [x] Add `searchWithPrompt(prompt: String): GeminiResult` method to `GeminiApiClient.kt`.
- [x] Result text displayed as-is (plain text — the template instructs Gemini to output Vietnamese plain text).
- [x] After a successful result, it remains visible until the user performs another search.

### UI/UX Requirements (NEW)
- [x] **Inline Loading State** (NOT full-screen overlay):
  - Hide the empty state group.
  - Show a new `@+id/loadingCard` (`MaterialCardView`, centered in the result area):
    - `CircularProgressIndicator` (indeterminate, `48dp`, `?attr/colorPrimary`).
    - `TextView`: "Đang tìm kiếm..." with `TextAppearance.Material3.BodyLarge`, bold.
    - `TextView`: "Gemini đang xử lý yêu cầu của bạn" with `BodyMedium`, muted color.
  - The loading card fades in (200ms) when search starts.
  - The loading card fades out and is replaced by result card when response arrives.
  - Disable both action buttons (`makePromptButton`, `searchNowButton`) and input field.
  - Status text updates to "Đang tìm kiếm...".
- [x] **Result Display with Animations**:
  - `resultCard` appears with **fade-in animation** (alpha 0→1, 300ms, `AccelerateDecelerateInterpolator`).
  - `summarizeResultButton` appears with **slide-down animation** (translateY -20dp→0dp + alpha 0→1, 200ms) — delayed 100ms after result card.
  - Empty state group hides (`GONE`).
  - Result text has proper typography: `20sp`, `4dp` lineSpacingExtra, matching Read tab's text style.
- [x] **Model Badge**: After successful result, show the model name in a small badge/chip below the result text or in the status bar: "Gemini: Done (gemini-2.0-flash)".
- [x] **Error Card with Retry**:
  - On API error, instead of just a Toast, show an **error card** (`MaterialCardView`):
    - Background: `?attr/colorErrorContainer`.
    - Icon: ⚠️ or `@drawable/ic_error` with `?attr/colorError` tint.
    - Title: error-specific message (e.g., "Lỗi kết nối", "Hết quota API").
    - Body: `result.getFinalText()` in `?attr/colorOnErrorContainer`.
    - **Retry button** (`OutlinedButton`): tapping retries the last search with the same input.
  - Error card also has fade-in animation.
  - The error card replaces the loading card/result card.
- [x] **Haptic Feedback**: Slight vibration (10ms) on Search Now button press for tactile confirmation.

### Non-Functional
- [x] Loading indicator must be clearly visible during API call.
- [x] Buttons must be disabled during API processing to prevent double-tap.
- [x] Error states must be handled gracefully with user-friendly Vietnamese messages.
- [x] The Prompt tab must not interfere with the Read tab's state or functionality.
- [x] All animations must be cancellable (if user switches tabs during loading).

## Implementation Steps
1. [x] **Add `searchWithPrompt` to `GeminiApiClient.kt`**:
   ```kotlin
   suspend fun searchWithPrompt(prompt: String): GeminiResult {
       return executeGeminiRequest { apiKey, model ->
           tryGenerateContentWithPrompt(apiKey, model, prompt)
       }
   }
   ```

2. [x] **Add loading card to `fragment_prompt.xml`**:
   ```xml
   <!-- Inline Loading Card (centered, initially GONE) -->
   <com.google.android.material.card.MaterialCardView
       android:id="@+id/loadingCard"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:visibility="gone"
       app:cardBackgroundColor="?attr/colorSurfaceContainerLow"
       app:cardCornerRadius="24dp">
       
       <LinearLayout orientation="vertical" gravity="center" padding="32dp">
           <com.google.android.material.progressindicator.CircularProgressIndicator
               android:layout_width="wrap_content"
               android:layout_height="wrap_content"
               android:indeterminate="true"
               app:indicatorSize="48dp"
               app:trackThickness="4dp" />
           <TextView text="Đang tìm kiếm..." textAppearance="BodyLarge" bold />
           <TextView text="Gemini đang xử lý yêu cầu của bạn" textAppearance="BodyMedium" muted />
       </LinearLayout>
   </com.google.android.material.card.MaterialCardView>
   ```

3. [x] **Add error card to `fragment_prompt.xml`**:
   ```xml
   <!-- Error Card (initially GONE) -->
   <com.google.android.material.card.MaterialCardView
       android:id="@+id/errorCard"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:visibility="gone"
       app:cardBackgroundColor="?attr/colorErrorContainer"
       app:cardCornerRadius="24dp">
       
       <LinearLayout orientation="vertical" padding="24dp">
           <TextView android:id="@+id/errorTitle" text="⚠️ Lỗi" bold />
           <TextView android:id="@+id/errorMessage" />
           <MaterialButton android:id="@+id/retryButton"
               style="@style/Widget.Material3.Button.OutlinedButton"
               text="🔄 Thử lại" />
       </LinearLayout>
   </com.google.android.material.card.MaterialCardView>
   ```

4. [x] **Update `PromptFragment.kt`** with Search Now logic + animations:
   - `showLoading()`: fade out empty state, fade in loading card, disable buttons + input.
   - `showResult(text, model)`: fade out loading card, fade in result card, slide down summarize button, update status with model name.
   - `showError(message)`: fade out loading card, fade in error card, re-enable buttons.
   - Retry button re-executes last search.
   - `hideKeyboard()` called before API request.

5. [x] **Create verification tests**.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/data/GeminiApiClient.kt` — [MODIFY] Add `searchWithPrompt` method
- `app/src/main/res/layout/fragment_prompt.xml` — [MODIFY] Add loading card, error card
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` — [MODIFY] Add Search Now logic, animations, loading/error states, keyboard handling
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabSearchNowTest.kt` — [NEW] Verification tests

## Verification Test (File-Based)
Create `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabSearchNowTest.kt`:

```kotlin
package com.skul9x.readoutloud.ui

import android.content.Context
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.utils.PromptTemplateHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import androidx.test.core.app.ApplicationProvider
import java.lang.reflect.Method

@RunWith(RobolectricTestRunner::class)
class PromptTabSearchNowTest {

    private lateinit var context: Context
    private lateinit var client: GeminiApiClient

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        client = GeminiApiClient(context)
    }

    @Test
    fun testSearchWithPromptMethodExists() {
        val hasSuspendMethod = GeminiApiClient::class.java.methods.any { it.name == "searchWithPrompt" }
        assertTrue("GeminiApiClient must have a searchWithPrompt method", hasSuspendMethod)
    }

    @Test
    fun testPromptIsNotWrappedInAdditionalSystemPrompt() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "Test topic"
        val builtPrompt = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue("Built prompt must start with the template instructions",
            builtPrompt.startsWith("Bạn hãy đóng vai một CHUYÊN GIA NGHIÊN CỨU"))
        assertTrue("Built prompt must end with the user's topic",
            builtPrompt.trimEnd().endsWith(topic))
    }

    @Test
    fun testGeminiResultSealedClassCoversAllCases() {
        val successResult = GeminiApiClient.GeminiResult.Success("test", "models/gemini-2.0-flash")
        val exhaustedResult = GeminiApiClient.GeminiResult.AllQuotaExhausted
        val noKeysResult = GeminiApiClient.GeminiResult.NoApiKeys
        val errorResult = GeminiApiClient.GeminiResult.Error("test error")

        assertEquals("test", successResult.text)
        assertTrue(exhaustedResult.getFinalText().contains("quá tải"))
        assertTrue(noKeysResult.getFinalText().contains("API Key"))
        assertTrue(errorResult.getFinalText().contains("test error"))
    }

    @Test
    fun testFullPromptConstructionForSearchNow() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "chip bán dẫn TSMC 2nm"
        val builtPrompt = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue(builtPrompt.contains("NGUYÊN TẮC TÌM KIẾM ĐA NGÔN NGỮ"))
        assertTrue(builtPrompt.contains("ĐỐI CHIẾU THÔNG TIN"))
        assertTrue(builtPrompt.contains("KIỂM CHỨNG"))
        assertTrue(builtPrompt.contains(topic))
        assertFalse(builtPrompt.contains("{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"))
        assertTrue("Built prompt should be substantial (>5000 chars)", builtPrompt.length > 5000)
    }

    @Test
    fun testGeminiResultModelExtraction() {
        // Verify model name can be extracted for the status badge
        val result = GeminiApiClient.GeminiResult.Success("text", "models/gemini-2.0-flash")
        val modelShortName = result.model.substringAfter("/")
        assertEquals("gemini-2.0-flash", modelShortName)
    }
}
```

### Automated Test Command
```bash
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTabSearchNowTest"
```

---
Next Phase: [Phase 04 — Summarize Cross-Tab Flow](./phase-04-summarize-cross-tab.md)
