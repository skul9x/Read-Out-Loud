# Phase 01: Core Infrastructure
Status: ✅ Completed
Dependencies: None

## Objective
Implement the data structures and managers for model rotation and quota tracking.

## Requirements
### Functional
- `ApiKeyManager`: Manage encrypted API keys (already partially exists).
- `ModelManager`: Manage list of models and their priority (moveUp/moveDown/add/remove).
- `ModelQuotaManager`: Track state of `(Model, Key)` pairs.

## Implementation Steps
1. [x] Implement `ModelManager`:
    - Storage for model list and user-defined priority.
    - Default models: `gemini-3.1-flash-lite-preview`, `gemini-3-flash-preview`, `gemini-2.5-flash-lite`, `gemini-2.5-flash`.
2. [x] Implement `ModelQuotaManager` skeleton:
    - In-memory map for `Cooldown` (5 mins).
    - Persistent map for `Exhausted` (30 hours).
    - Method `isAvailable(model, keyHash): Boolean`.
3. [x] Define SHA-256 hashing for key identifiers.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/data/ModelManager.kt`
- `app/src/main/java/com/skul9x/readoutloud/data/ModelQuotaManager.kt`
- `app/src/main/java/com/skul9x/readoutloud/data/ApiKeyManager.kt` (Update)
