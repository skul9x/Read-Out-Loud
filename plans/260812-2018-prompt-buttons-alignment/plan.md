# Plan: Fix Prompt Tab Action Buttons Alignment ("Make Prompt" & "Search Now")

Created: 2026-08-12
Status: ✅ Completed

## Problem Statement
In the Prompt tab (`fragment_prompt.xml`), the two primary action buttons — **"Make Prompt"** (`makePromptButton`) and **"Search Now"** (`searchNowButton`) — are visually misaligned and asymmetric:
1. **Multi-line Text Wrapping Asymmetry**: "Make Prompt" (11 chars, wide letters) wraps to two lines ("Make\nPrompt"), while "Search Now" (10 chars) remains on a single line.
2. **Vertical Offset & Baseline Mismatch**: Because one button renders 2 lines of text and the other renders 1 line, their text baselines, icon vertical centering, and visual weights are completely uneven.
3. **Missing Constraints & Padding Issues**: Buttons lack `android:maxLines="1"`, `android:singleLine="true"`, `android:ellipsize="end"`, and have excessive default horizontal padding causing font overflow at 15sp bold on standard Android viewport widths (360dp–412dp).

## Objective
Restore perfect horizontal and vertical alignment between "Make Prompt" and "Search Now" buttons with robust single-line typography constraints, balanced padding, uniform height, centered icon-text gravity, and full test coverage across screen densities and dynamic states.

---

## Phase Overview

| Phase | Name | Status | Description |
|-------|------|--------|-------------|
| 01 | Buttons Layout & Typography Alignment | ✅ Completed | Update `fragment_prompt.xml` with single-line constraints, 14sp font size, balanced internal padding (`8dp`), `insetTop/Bottom="0dp"`, standardized `56dp` height, and `iconGravity="textStart"`. Add dedicated Robolectric layout test `PromptButtonsLayoutAlignmentTest.kt`. |
| 02 | Responsive Behavior & Dynamic State Testing | ✅ Completed | Validate dynamic text watcher transitions, enabled/disabled states, loading state triggers, and ensure layout parameters remain strictly invariant under state changes. |
| 03 | End-to-End Build & Device Visual Verification | ✅ Completed | Execute full test suite, assemble Debug APK, deploy to connected device via ADB, capture visual screenshots, and verify regression-free layout alignment. |

---

## Phases Detail

### [Phase 01: Buttons Layout & Typography Alignment](./phase-01-buttons-layout-alignment.md)
- **Files to Modify**: `app/src/main/res/layout/fragment_prompt.xml`
- **Files to Create**: `app/src/test/java/com/skul9x/readoutloud/ui/PromptButtonsLayoutAlignmentTest.kt`
- **Goal**: Apply strict single-line text constraints, optimized typography/padding, and assert structural alignment via unit tests.

### [Phase 02: Responsive Behavior & Dynamic State Testing](./phase-02-responsive-behavior-tests.md)
- **Files to Modify**: `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` (if needed for font scaling/insets), `app/src/test/java/com/skul9x/readoutloud/ui/PromptButtonsLayoutAlignmentTest.kt`
- **Goal**: Verify button state parity (disabled, enabled, loading, retry) across configuration changes and font scales without layout drift.

### [Phase 03: End-to-End Build & Device Visual Verification](./phase-03-e2e-visual-verification.md)
- **Verification Commands**:
  - `./gradlew testDebugUnitTest`
  - `./gradlew assembleDebug`
  - ADB screenshot capture & visual verification
- **Goal**: Ensure clean compile, all unit tests pass (100%), and screenshot confirms perfect visual alignment on actual hardware/emulator.

---

## Quick Commands
- Run Alignment Tests: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptButtonsLayoutAlignmentTest"`
- Run All Prompt Tab Tests: `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptTab*"`
- Run Entire Test Suite: `./gradlew test`
