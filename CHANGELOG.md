# Changelog

## [2026-03-19] - TTS Karaoke UI & Interaction
### Added
- **Karaoke Highlighting**: Full word-by-word highlighting with orange background (`#FF9800`) and bold font for the currently spoken text.
- **Auto-scroll Logic**: Implemented smooth automatic scrolling using `ObjectAnimator` to keep the reading word in view.
- **Interaction Delay Protocal**: Added a smart 3-second delay after user scrolling before resuming auto-navigation.
- **Double-Tap to Edit**: Enhanced `EditText` to be read-only by default (Double-tap to enable editing, auto-save on keyboard dismiss).
- **TTS Word Sync**: Updated `TtsService` to broadcast `EXTRA_WORD_START` and `EXTRA_WORD_END` indices for frame-perfect UI sync.
- **E2E Unit tests**: Created `TtsKaraokeTest` to verify highlight logic, interaction detection, and state management.

### Changed
- **Read-Only UI**: Default text area state is now focusable-false/cursor-hidden for better reading experience.
- **Progress Broadcast**: Unified progress and word synchronization into a single broadcast intent for reduced overhead.

### Fixed
- Fixed missing `androidx.test.core` dependency that prevented Robolectric unit tests from running correctly.
- Optimized Spannable reuse in `MainActivity` to prevent memory leaks during long reading sessions.


## [2026-03-19] - Material 3 UI Revamp & Reading Progress
### Added
- **Material 3 UI Migration**: Full project upgrade to Material Design 3 (M3). Updated colors, typography, surface cards, and buttons for a premium visual experience.
- **Indigo-Dark Theme**: Implemented a sophisticated dark mode palette using Material 3 color tokens.
- **Reading Progress (0-100%)**: Integrated a `LinearProgressIndicator` and percentage label to track real-time TTS reading position.
- **Progress Tracking Logic**: Enhanced `TtsService` to calculate absolute character progress across multiple chunks using `onRangeStart`.
- **ic_volume_up Asset**: Created custom vector asset for the toolbar volume control.

### Changed
- **UI Architecture**: Redesigned `activity_main.xml` with card-based layouts and improved spacing.
- **Settings Screen Refine**: Updated `activity_settings.xml` to match M3 aesthetics and simplified the Save button logic.
- **Volume Button Position**: Integrated volume control directly into the `MaterialToolbar` with a text-based percentage display.

### Fixed
- Fixed missing `ic_volume_up` and `Widget.Material3.Button.TonalButton` style references during the M3 transition.
- Resolved DataBinding errors by rebuilding with new M3-compliant view IDs.

## [2026-03-19] - UI & Audio Enhancements
### Added
- **Volume Control Interface**: Added a toolbar button to cycle device volume through **80% -> 85% -> 90%**.
- **Startup Volume logic**: App now automatically sets device music volume to 80% on launch.
- **TTS Voice Selection**: New dropdown in Settings for picking Vietnamese voices (Giọng đọc 1, 2, 3...).
... (rest of the file)
