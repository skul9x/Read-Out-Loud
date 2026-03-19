# Plan: Gemini Integration
Created: 2026-03-19T09:08:00
Status: 🟡 In Progress

## Overview
Tích hợp AI Gemini vào Read-Out-Loud để làm sạch văn bản dán vào (chặn bảng biểu, xóa markdown/link) trước khi đọc bằng Text-to-Speech. Cung cấp UI cấu hình API Keys và tắt/mật chức năng.

## Tech Stack
- Khung: Android Kotlin
- Networking: OkHttp3
- Xử lý Thread: Kotlin Coroutines
- Dữ liệu an toàn: AndroidX Security Crypto (EncryptedSharedPreferences)
- Parse JSON: Kotlinx Serialization

## Phases

| Phase | Name | Status | Progress |
|-------|------|--------|----------|
| 01 | Setup Environment | ✅ Complete | 100% |
| 02 | Backend Logic (API & Storage) | ✅ Complete | 100% |
| 03 | Frontend UI (Toggle & Settings) | ✅ Complete | 100% |
| 04 | Final Testing & Refactor | ✅ Complete | 100% |

## Quick Commands
- Bắt đầu Phase 1: `/code phase-01`
- Kiểm tra tiến độ: `/next`
- Xem trực quan UI: `/visualize`
