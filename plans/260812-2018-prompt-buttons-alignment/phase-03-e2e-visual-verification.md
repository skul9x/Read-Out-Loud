# Phase 03: End-to-End Build & Device Visual Verification

Status: ✅ Completed  
Dependencies: Phase 01, Phase 02

## Objective
Perform a full end-to-end verification of the entire application: run all unit and integration test suites, build the debug APK, deploy to the connected device via ADB, capture screenshots, and visually confirm that the "Make Prompt" and "Search Now" buttons are perfectly aligned in a single line.

---

## Verification Steps

### 1. Automated Test Execution
Run the full test suite across the entire project to ensure 0 regressions:
```bash
./gradlew test
```
Verify that all unit tests across data, UI, rotation logic, prompt tab, and summarize flows pass.

### 2. APK Compilation
Build the debug APK:
```bash
./gradlew assembleDebug
```
Ensure build completes with 0 errors and 0 lint warnings.

### 3. Device Deployment & Visual Screenshot Verification
1. Install the APK onto the active device:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
2. Launch the app and navigate to the Prompt tab:
   ```bash
   adb shell am start -n com.skul9x.readoutloud/.MainActivity
   ```
3. Take a screenshot using the ADB screenshot tool and save to the artifacts directory.
4. Visually inspect the screenshot to verify:
   - "Make Prompt" is strictly rendered on 1 line.
   - "Search Now" is strictly rendered on 1 line.
   - Both buttons have identical height (`56dp`), identical corner rounding, centered icon/text groupings, and a balanced 50/50 horizontal split.
   - Top and bottom edges are perfectly parallel with zero baseline staggering.

---

## Success Criteria Checklist
- [x] Automated unit test suite passes 100% (`./gradlew test`).
- [x] `./gradlew assembleDebug` succeeds.
- [x] APK installed and running on Android device.
- [x] ADB screenshot confirms single-line text and exact horizontal & vertical button alignment.
- [x] No regression in "Make Prompt" clipboard copy or "Search Now" Gemini search.

---
End of Plan.
