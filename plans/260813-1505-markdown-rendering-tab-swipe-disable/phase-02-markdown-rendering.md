# Phase 02: Markdown Rendering for Search Now

Status: ✅ Completed  
Dependencies: [Phase 01: Disable Tab Swipe Gestures](./phase-01-disable-tab-swipe.md)

## Objective
Enhance the "Search Now" result display in the Prompt tab to render clean, formatted rich text (bold, italics, headers, bulleted lists) using the `Markwon` library, eliminating unsightly raw markdown characters like `#`, `##`, `*`, `**`, and `_`.

## Requirements
### Functional
- [x] Add `io.noties.markwon:core:4.6.2` to the Gradle version catalog (`libs.versions.toml`) and `app/build.gradle.kts`.
- [x] Initialize `Markwon` instance inside `PromptFragment.kt`.
- [x] In `PromptFragment.showResult(text: String, model: String)`, render markdown onto `binding.resultTextView` via `markwon.setMarkdown(binding.resultTextView, text.trim())` (using `.trim()` to avoid unwanted leading/trailing whitespace/newlines).
- [x] Ensure raw markdown syntax symbols (`#`, `##`, `*`, `**`, `_`) are replaced with Android `Spanned` styling (Bold, Italic, RelativeSizeSpan, BulletSpan, etc.).
- [x] Ensure clicking "Tóm tắt" (Summarize) properly retrieves the text from `binding.resultTextView.text.toString()` and transmits it to the Read tab.

### Non-Functional
- [x] Zero crash or degradation on complex AI responses or empty text.
- [x] Fast rendering performance without blocking the UI thread.
- [x] Maintain consistent Material 3 styling, font family, and colors.

## Implementation Steps
1. [x] **Update `gradle/libs.versions.toml`**:
   - In `[versions]`, add: `markwon = "4.6.2"`.
   - In `[libraries]`, add: `markwon-core = { group = "io.noties.markwon", name = "core", version.ref = "markwon" }`.
2. [x] **Update `app/build.gradle.kts`**:
   - Under `dependencies`, add: `implementation(libs.markwon.core)`.
3. [x] **Update `PromptFragment.kt`**:
   - Import `io.noties.markwon.Markwon`.
   - Declare `private lateinit var markwon: Markwon`.
   - In `onViewCreated(...)`, initialize `markwon = Markwon.create(requireContext())`.
   - In `showResult(text: String, model: String)`, replace `binding.resultTextView.text = text` with `markwon.setMarkdown(binding.resultTextView, text.trim())`.
4. [x] **Create Unit Test Suite `PromptResultMarkdownFormattingTest.kt`**:
   - Test markdown heading parsing (`# Title`, `## Subtitle`) renders without `#` and applies spans.
   - Test bold text (`**important**`) renders styled without asterisks.
   - Test italic text (`*note*`) renders styled without asterisks.
   - Test bullet list items (`* Point 1\n* Point 2`) render as clean bulleted list.
   - Test edge cases (empty text, plain text without markdown, mixed markdown).

## Files to Create/Modify
- `gradle/libs.versions.toml` - Add Markwon dependency definitions.
- `app/build.gradle.kts` - Add `libs.markwon.core` dependency.
- `app/src/main/java/com/skul9x/readoutloud/ui/PromptFragment.kt` - Integrate Markwon renderer in `showResult`.
- `app/src/test/java/com/skul9x/readoutloud/ui/PromptResultMarkdownFormattingTest.kt` - Unit tests for markdown rendering.

## Test Criteria
- [x] `PromptResultMarkdownFormattingTest` passes all assertions for bold, italic, headings, and lists.
- [x] `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.PromptResultMarkdownFormattingTest"` passes cleanly.

---
Next Phase: [Phase 03: Cross-Tab Integration & Test Suite Verification](./phase-03-integration-verification.md)
