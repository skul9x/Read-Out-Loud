# Plan: TTS Karaoke & Read-Only UI
Created: 2026-03-19T14:00
Status: 🟡 In Progress

## Overview
Nâng cấp trải nghiệm text area (Read-Only mặc định, Double-tap để sửa) và tích hợp hiệu ứng Karaoke chân thực khi Text-To-Speech đang đọc (Highlight nền cam, chữ trắng, in đậm, auto-scroll delay 3s). Mỗi phase đều đi kèm test cẩn thận.

## Tech Stack
- UI: Jetpack Compose (TextField, Spannable/VisualTransformation, Gestures)
- Backend Logic: TtsService (onRangeStart callback), Coroutine Flow
- State Management: ViewModels & BroadcastReceiver

## Phases

| Phase | Name | Status | Progress |
|-------|------|--------|----------|
| 01 | Read-Only & Gesture Handling | ✅ Complete | 100% |
| 02 | TTS Synchronization Logic | ✅ Complete | 100% |
| 03 | Highlight Animation | ✅ Complete | 100% |
| 04 | Auto-scroll Behavior | ✅ Complete | 100% |
| 05 | E2E Integration & Testing | ✅ Complete | 100% |

## Quick Commands
- Start Phase 1: `/code phase-01`
- Check progress: `/next`
- Save context: `/save-brain`
