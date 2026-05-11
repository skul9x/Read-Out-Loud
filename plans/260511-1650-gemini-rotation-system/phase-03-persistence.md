# Phase 03: Persistence & Security
Status: ✅ Completed
Dependencies: Phase 01, Phase 02

## Objective
Ensure durability of system state and security of API keys.

## Requirements
### Functional
- Save `Exhausted` states for 30 hours even after app restart.
- Save user-defined `Model` priorities.
- Use SHA-256 for key hashing when creating quota identifiers.

### Security
- Never store raw API keys in plain SharedPreferences (only in `EncryptedSharedPreferences`).
- Quota identifiers should be `model::SHA256(apiKey)`.

## Implementation Steps
1. [x] Implement SHA-256 hashing utility.
2. [x] Add persistence logic to `ModelManager` (saving `ModelItem` list to SharedPreferences).
3. [x] Add persistence logic to `ModelQuotaManager` for `Exhausted` status.
4. [x] Implement cleanup logic for expired `Exhausted` entries.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/data/ModelManager.kt` - Modify
- `app/src/main/java/com/skul9x/readoutloud/data/ModelQuotaManager.kt` - Modify
- `app/src/main/java/com/skul9x/readoutloud/utils/SecurityUtils.kt` - New

## Test Criteria
- Quota status persists after app relaunch.
- Model order persists after app relaunch.
- No raw keys found in standard SharedPreferences files.

---
Next Phase: [Phase 04: Settings UI Integration](phase-04-ui.md)
