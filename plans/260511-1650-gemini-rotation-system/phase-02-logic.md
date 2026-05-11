# Phase 02: Rotation Logic Implementation
Status: ✅ Completed
Dependencies: Phase 01

## Objective
Implement the nested loop rotation algorithm in `GeminiApiClient`.

## Requirements
### Functional
- Nested loop: Outer (Models), Inner (Keys).
- Check `isAvailable` before each call.
- Error Handling:
    - 429 + "per minute/Rate limit" -> `markCooldown`, Next Key.
    - 429 + "daily/quota" -> `markExhausted`, Next Key.
    - 503 -> `markCooldown`, Next Key.
    - 400/404 -> Next Model (break inner loop).
- Streaming Safety: `hasStarted` flag to prevent rotation mid-stream.
- `retryWithBackoff` only for `IOException` before streaming starts.

## Implementation Steps
1. [ ] Refactor `GeminiApiClient.cleanTextWithGemini`:
    - Loop through `ModelManager.getModels()`.
    - Inner loop through `ApiKeyManager.getApiKeys()`.
2. [ ] Implement `tryGenerateContent` with precise error response parsing.
3. [ ] Implement state updates in `ModelQuotaManager` based on response codes/messages.
4. [ ] Implement streaming check logic.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/data/GeminiApiClient.kt`
