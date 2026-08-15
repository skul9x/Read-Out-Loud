# Plan: Prompt Template Update & Disable Swipe Back on Show Screen

Created: 2026-08-15 09:35
Status: ✅ Completed

## Overview
Update the research prompt template in the Prompt Tab to use the structured JSON prompt from `prompt.txt` with placeholder `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`. Additionally, disable the swipe back gesture on the full screen "Show" result screen (`FullScreenReaderActivity`), ensuring users navigate only via toolbar back or system back buttons.

## Requirements
1. **Prompt Template Update**:
   - Replace template in `res/raw/prompt_template.txt` with content of `prompt.txt`.
   - Update `PromptTemplateHelper.kt` to replace `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]` placeholder with user topic input.
   - Update all corresponding unit and integration tests to validate the new prompt structure and substitution.
2. **Disable Swipe Back on Show Screen**:
   - Remove swipe gesture detector and touch event intercept from `FullScreenReaderActivity.kt`.
   - Retain toolbar back arrow navigation and system back button handler.
   - Update `FullScreenReaderActivityTest.kt` to verify swipe-to-back is disabled.

## Tech Stack
- Kotlin / Android SDK 34
- Android Views / Material 3
- Robolectric / JUnit 4 for unit and integration testing

## Phases

| Phase | Name | Status | Progress |
|-------|------|--------|----------|
| 01 | Prompt Template Update & Placeholder Integration | ✅ Completed | 100% |
| 02 | Disable Swipe Back on Show Screen | ✅ Completed | 100% |
| 03 | Full Regression & Verification | ✅ Completed | 100% |

## Quick Commands
- Start Phase 1: `/code phase-01`
- Run Tests: `./gradlew testDebugUnitTest`
