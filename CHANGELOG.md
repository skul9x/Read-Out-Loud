# Changelog

## [2026-03-19] - UI & Audio Enhancements
### Added
- **Volume Control Interface**: Added a toolbar button to cycle device volume through **80% -> 85% -> 90%**.
- **Startup Volume logic**: App now automatically sets device music volume to 80% on launch.
- **TTS Voice Selection**: New dropdown in Settings for picking Vietnamese voices (Giọng đọc 1, 2, 3...).
- **UI Rearrangement**: Moved action cards (Paste, Read, Stop) above the main input field for better ergonomy.
- **Centered Toolbar Title**: Custom `TextView` for "READ-OUT-LOUD" to prevent overlap with side buttons.
- **Copyright Footer**: Added legal notice at the bottom of the main screen.

### Changed
- Updated `GeminiApiClient` model list with newer preview/lite versions for better reliability.
- Optimized `SettingsActivity` with clipboard auto-parsing for multiple API keys.

## [2026-03-19] - Gemini Integration
### Added
- Gemini API Integration logic...

### Changed
- Updated `compileSdk` and `targetSdk` to **35**.
- Updated `core-ktx` to **1.16.0**.
- Switched dependency management to **libs.versions.toml** (Version Catalog).

### Status
- **Phase 01 & 02 Completed.**
- Ready for Phase 03: Frontend & UI.
