# Phase 01: Disable Tab Swipe Gestures

Status: ✅ Completed  
Dependencies: None

## Objective
Remove horizontal swipe gesture navigation between the "Read" tab and the "Prompt" tab so that tab switching only occurs when the user taps on the tab headers directly or when triggered programmatically.

## Requirements
### Functional
- [x] Set `isUserInputEnabled = false` on `binding.viewPager` in `MainActivity.kt`.
- [x] Verify that clicking on TabLayout tabs (Read, Prompt) switches the active page smoothly.
- [x] Verify that programmatic tab switching (`switchToTab(index)`) continues to work as expected.
- [x] Ensure swiping touch events on the screen do not trigger page transitions between tabs.

### Non-Functional
- [x] Preserve TabLayoutMediator configuration and tab indicator styling.
- [x] Zero performance regression during tab selection.

## Implementation Steps
1. [x] **Update `MainActivity.kt`**:
   - In `setupTabs()` method, add `binding.viewPager.isUserInputEnabled = false` immediately after initializing `binding.viewPager.adapter`.
2. [x] **Update / Create Unit Tests**:
   - Update `app/src/test/java/com/skul9x/readoutloud/ui/TabLayoutInfrastructureTest.kt` to assert `viewPager.isUserInputEnabled` is `false`.
   - Create `app/src/test/java/com/skul9x/readoutloud/ui/TabSwipeDisabledTest.kt` to test:
     - ViewPager2 touch input is disabled (`isUserInputEnabled == false`).
     - Tapping TabLayout tab 0 selects Read tab and switches ViewPager2 currentItem to 0.
     - Tapping TabLayout tab 1 selects Prompt tab and switches ViewPager2 currentItem to 1.
     - `activity.switchToTab(0)` and `activity.switchToTab(1)` switch pages correctly.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/MainActivity.kt` - Disable `isUserInputEnabled` on `viewPager`.
- `app/src/test/java/com/skul9x/readoutloud/ui/TabLayoutInfrastructureTest.kt` - Add assertion for `isUserInputEnabled == false`.
- `app/src/test/java/com/skul9x/readoutloud/ui/TabSwipeDisabledTest.kt` - Comprehensive tests for swipe disabled & tab click switching.

## Test Criteria
- [x] `TabLayoutInfrastructureTest` passes with assertion verifying `isUserInputEnabled == false`.
- [x] `TabSwipeDisabledTest` passes verifying tap-based tab transitions and programmatic navigation.
- [x] `./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.ui.*Tab*"` passes without errors.

---
Next Phase: [Phase 02: Markdown Rendering for Search Now](./phase-02-markdown-rendering.md)
