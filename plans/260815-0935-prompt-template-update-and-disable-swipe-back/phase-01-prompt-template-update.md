# Phase 01: Prompt Template Update & Placeholder Integration

Status: ✅ Completed
Dependencies: None

## Objective
Update the Prompt tab research template to the new JSON structure defined in `prompt.txt`. Replace the placeholder `{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}` with `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]` so that user-entered topics are injected directly into `"topic_to_research": "[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"`.

## Requirements
### Functional
- [x] Replace `app/src/main/res/raw/prompt_template.txt` with the exact content from `prompt.txt`.
- [x] Update `PromptTemplateHelper.kt`:
  - Change `PLACEHOLDER` constant to `"[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"`.
  - Ensure `buildPrompt(template, topic)` properly replaces `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]` with `topic`.
- [x] Ensure "Make Prompt" button copies the constructed JSON prompt with user topic substituted.
- [x] Ensure "Search Now" button submits the constructed JSON prompt to Gemini API.

### Non-Functional
- [x] Maintain fast in-memory string replacement performance.
- [x] Ensure backward compatibility of template loading logic in `PromptTemplateHelper`.

## Implementation Steps
1. [x] Copy content of `d:\skul9x\Read-Out-Loud-main\prompt.txt` to `app/src/main/res/raw/prompt_template.txt`.
2. [x] In `app/src/main/java/com/skul9x/readoutloud/utils/PromptTemplateHelper.kt`:
   - Update `PLACEHOLDER = "[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"`.
3. [x] Update unit and integration tests:
   - `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabMakePromptTest.kt`:
     - Update assertion checks from old Vietnamese strings to JSON prompt keys (`"role": "EXPERT IN MULTILINGUAL INTERNET RESEARCH"`, `"autonomous_execution"`, `"country_specific_rule"`).
     - Update placeholder assertion to `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`.
   - `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabSearchNowTest.kt`:
     - Update start/end assertions and keyword checks for the JSON format.
     - Update placeholder check to `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`.
   - `app/src/test/java/com/skul9x/readoutloud/PromptTabIntegrationTest.kt`:
     - Update role keyword checks and placeholder assertions.

## Files to Create/Modify
- `app/src/main/res/raw/prompt_template.txt` — [MODIFY] Replace with JSON research prompt from `prompt.txt`.
- `app/src/main/java/com/skul9x/readoutloud/utils/PromptTemplateHelper.kt` — [MODIFY] Update `PLACEHOLDER` constant.
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabMakePromptTest.kt` — [MODIFY] Update test assertions for new template and placeholder.
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptTabSearchNowTest.kt` — [MODIFY] Update test assertions for new template and placeholder.
- `app/src/test/java/com/skul9x/readoutloud/PromptTabIntegrationTest.kt` — [MODIFY] Update integration test assertions.

## Detailed File-Based Test Specifications

### 1. `PromptTabMakePromptTest.kt`
- `testRawPromptTemplateResourceExists()`:
  - Verifies raw resource `prompt_template` exists and is readable.
- `testLoadTemplateReturnsNonEmptyString()`:
  - Verifies `PromptTemplateHelper.loadTemplate(context)` returns non-blank text.
  - Verifies loaded template contains `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`.
  - Verifies loaded template contains `"role": "EXPERT IN MULTILINGUAL INTERNET RESEARCH"`.
- `testBuildPromptReplacesPlaceholder()`:
  - Passes topic `"chiến tranh thương mại Mỹ Trung 2026"`.
  - Asserts result does NOT contain `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`.
  - Asserts result contains `"topic_to_research": "chiến tranh thương mại Mỹ Trung 2026"`.
  - Asserts result contains `"autonomous_execution"`.
- `testBuildPromptPreservesTemplateStructure()`:
  - Verifies presence of `"research_principles"`, `"country_specific_rule"`, `"ultimate_goal"`, `"execution_rule"`.
- `testBuildPromptWithEmptyTopicReplacesPlaceholder()`:
  - Builds prompt with `""`.
  - Asserts result does NOT contain placeholder and has `"topic_to_research": ""` (or empty replacement).
- `testMakePromptCopiesToClipboard()`:
  - Simulates typing topic and clicking "Make Prompt" button.
  - Asserts clipboard text contains the substituted topic and does not contain `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`.

### 2. `PromptTabSearchNowTest.kt`
- `testPromptIsNotWrappedInAdditionalSystemPrompt()`:
  - Verifies `builtPrompt` starts with `{\n  "role": "EXPERT IN MULTILINGUAL INTERNET RESEARCH`.
  - Verifies `builtPrompt.trimEnd()` ends with `"topic_to_research": "Test topic"\n}`.
- `testFullPromptConstructionForSearchNow()`:
  - Verifies `builtPrompt` contains `"multi_language_search"`, `"country_specific_rule"`, topic, and does not contain `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`.
  - Verifies `builtPrompt.length > 5000`.

### 3. `PromptTabIntegrationTest.kt`
- `testPromptTemplateLoadsCorrectly()`:
  - Verifies `template.contains("EXPERT IN MULTILINGUAL INTERNET RESEARCH")`.
  - Verifies `template.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]")`.
- `testPromptBuildingEndToEnd()`:
  - Verifies prompt contains topic, does not contain `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`, and length > 5000.

---
Next Phase: [Phase 02: Disable Swipe Back on Show Screen](file:///d:/skul9x/Read-Out-Loud-main/plans/260815-0935-prompt-template-update-and-disable-swipe-back/phase-02-disable-swipe-back-show-screen.md)
