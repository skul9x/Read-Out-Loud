# Phase 01: 3-Button Action Row UI in Prompt Tab Result Card

Status: ✅ Completed
Dependencies: None

## Objective
Design and implement a unified, horizontal 3-button action row in the "Search Now" result card on the Prompt tab. The row contains "Tóm tắt", "Read", and "Show" with consistent Material 3 styling, equal weights or compact spacing, icons, and smooth entrance animation.

## Requirements

### Functional Requirements
- [x] Replace the single `summarizeResultButton` in `fragment_prompt.xml` with a horizontal `LinearLayout` containing 3 buttons:
  1. **"Tóm tắt"** (`id: summarizeResultButton`): icon `ic_summarize`, text "Tóm tắt"
  2. **"Read"** (`id: readResultButton`): icon `ic_play_arrow`, text "Read"
  3. **"Show"** (`id: showResultButton`): icon `ic_fullscreen`, text "Show"
- [x] Create `res/drawable/ic_fullscreen.xml` (or `ic_open_in_full.xml`) vector asset for the "Show" button.
- [x] Ensure all 3 buttons are aligned horizontally on the same line with equal layout weight (`layout_width="0dp"`, `layout_weight="1"`, `layout_height="40dp"`) and anti-clipping specifications:
  - **Zero insets**: `android:insetTop="0dp"`, `android:insetBottom="0dp"`, `android:insetLeft="0dp"`, `android:insetRight="0dp"` to eliminate unnecessary default touch insets.
  - **Zero min dimensions**: `android:minWidth="0dp"`, `android:minHeight="0dp"` to allow flexible shrinking on narrow screens.
  - **Compact padding**: `android:paddingHorizontal="4dp"`, `android:paddingVertical="0dp"` to maximize printable area for label and icon.
  - **Auto-sizing typography**: `app:autoSizeTextType="uniform"`, `app:autoSizeMinTextSize="10sp"`, `app:autoSizeMaxTextSize="13sp"`, `app:autoSizeStepGranularity="1sp"`.
  - **Single-line & Diacritic preservation**: `android:maxLines="1"`, `android:ellipsize="end"`, `android:includeFontPadding="true"` to prevent clipping of Vietnamese tone marks in "Tóm tắt".
  - **Icon layout**: `app:iconGravity="textStart"`, `app:iconSize="16dp"`, `app:iconPadding="4dp"`.
  - **Rounded corners**: `app:cornerRadius="12dp"`.
- [x] In `PromptFragment.kt`:
  - Bind `summarizeResultButton`, `readResultButton`, and `showResultButton`.
  - When `showResult()` is invoked, make the 3-button row visible with animation (`slideDown` / `fadeIn`).
  - Enable all 3 buttons when results are loaded, and re-enable them in `onResume()`.
  - Disable buttons during active loading/searching.

### Non-Functional Requirements
- [x] Zero Text Clipping: Ensure labels and icons never truncate, clip, or drop Vietnamese diacritics across standard and narrow screen densities (mdpi to xxxhdpi) or when system font scale is adjusted.
- [x] Visual polish: Clean Material 3 Tonal Button styling with responsive text sizing and compact margins.
- [x] Haptic & touch feedback: Consistent touch scale animations and haptic feedback on button clicks.

## Implementation Steps
1. [x] Create `app/src/main/res/drawable/ic_fullscreen.xml` vector drawable.
2. [x] Update `app/src/main/res/layout/fragment_prompt.xml`:
   - Replace the single `summarizeResultButton` with a horizontal `LinearLayout` container (`id: resultActionsLayout`) containing `summarizeResultButton`, `readResultButton`, and `showResultButton` configured with zero insets, `minWidth="0dp"`, `layout_width="0dp"`, `layout_weight="1"`, and `app:autoSizeTextType="uniform"`.
3. [x] Update `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt`:
   - Animate and manage visibility and enabled states for `resultActionsLayout` and the 3 buttons.
4. [x] Create unit test `app/src/test/java/com/skul9x/readoutloud/ui/PromptResultActionButtonsLayoutTest.kt` to verify button presence, horizontal arrangement, anti-clipping attributes (`minWidth=0`, `maxLines=1`, `autoSizeTextType`), icons, text, and animation states.

## Files to Create/Modify
- `app/src/main/res/drawable/ic_fullscreen.xml` - [NEW] Fullscreen vector icon.
- `app/src/main/res/layout/fragment_prompt.xml` - [MODIFY] Horizontal action buttons container in `resultCard` with anti-clipping attributes.
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` - [MODIFY] View binding, animation, and state handling.
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptResultActionButtonsLayoutTest.kt` - [NEW] Robolectric layout & behavior test including anti-clipping attribute validations.

## Test Criteria
- [x] `testResultActionButtonsExistInLayout`: Verifies `summarizeResultButton`, `readResultButton`, and `showResultButton` are present inside `resultCard`.
- [x] `testResultActionButtonsHorizontalAlignment`: Verifies the buttons are siblings inside a horizontal `LinearLayout` with `layout_weight="1"`.
- [x] `testResultActionButtonsAntiClippingAttributes`: Verifies all 3 buttons have `maxLines == 1`, `minWidth == 0`, and autosizing configured.
- [x] `testButtonsVisibilityAndEnabledStateOnShowResult`: Verifies all 3 buttons are made visible and enabled when `showResult()` is called.
- [x] `testButtonsDisabledStateOnShowLoading`: Verifies buttons are hidden/disabled during loading.

---
Next Phase: [Phase 02: Cross-Tab Auto-Read Flow](./phase-02-cross-tab-auto-read.md)
