# Plan: Gemini Model Rotation System (Strict Implementation)
Created: 2026-05-11 16:50
Status: 🟡 In Progress

## Overview
Implement the Gemini API rotation system EXACTLY as specified in `rotation.md`.
- **Model-First Rotation**: Nested loops (Models outer, Keys inner).
- **Error-Specific Handling**: 
    - "per minute" or "Rate limit" (429) -> Cooldown (5m, RAM).
    - Daily quota (429) -> Exhausted (30h, Disk).
    - 503 -> Cooldown (5m, RAM).
    - 400/404 -> Skip to next Model.
- **Streaming Safety**: No rotation after data starts streaming.
- **Quota Manager**: Secure pairing using `$model::SHA256(apiKey)`.

## Tech Stack
- **Kotlin/Android SDK 35**
- **EncryptedSharedPreferences** (Keys)
- **SharedPreferences** (Exhausted states)
- **OkHttp**
- **Kotlin Serialization**

## Phases

| Phase | Name | Status | Progress |
|-------|------|--------|----------|
| 01 | Core Infrastructure | ✅ Done | 100% |
| 02 | Rotation Logic Implementation | ✅ Done | 100% |
| 03 | Persistence & Hashing | ✅ Done | 100% |
| 04 | Settings & Model Management UI | ⬜ Pending | 0% |
| 05 | Testing & Verification | ⬜ Pending | 0% |

## Quick Commands
- Check progress: `/next`
- Save context: `/save-brain`
- Run UI tests: `./gradlew test`
