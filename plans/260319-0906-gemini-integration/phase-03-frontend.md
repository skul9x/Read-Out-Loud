# Phase 03: Frontend UI
Status: ✅ Completed
Dependencies: phase-02-backend.md

## Objective
Xây dựng giao diện cho Settings (dán API Keys) và cập nhật màn hình chính để Bật/Tắt (Toggle) Google Gemini AI làm sạch văn bản.

## Requirements
### Functional
- [x] Một SettingsActivity chứa `EditText` to, nút "Dán API Keys".
- [x] Cập nhật `activity_main.xml`: Thêm nút Setting, thêm MaterialSwitch cho phép bật "Sử dụng Gemini dọn dẹp văn bản".
- [x] Tích hợp `pasteFromClipboard`: Nếu Switch ON -> Hiển thị ProgressBar -> Chạy qua `GeminiApiClient` -> Điền vào `EditText` của `MainActivity`. Nếu OFF -> Như cũ.
- [x] Hiện cảnh báo nếu Bật Switch mà chưa có API Key.

## Implementation Steps
1. [x] Thiết kế `activity_settings.xml` (EditText multiline, Button).
2. [x] Code `SettingsActivity.kt` (Gọi phương thức `ApiKeyManager.addApiKeys`).
3. [x] Chỉnh sửa `activity_main.xml` thêm `Switch` và `ImageButton`.
4. [x] Nối logic ở `MainActivity.kt`: Lắng nghe nút Dán, check Switch.
5. [x] Gọi Coroutine `lifecycleScope.launch` để chạy AI bất đồng bộ. Cập nhật thanh tiến độ lúc dán.

## Files to Modify/Create
- `app/src/main/res/layout/activity_main.xml` (Modified)
- `app/src/main/java/com/skul9x/readoutloud/MainActivity.kt` (Modified)
- `app/src/main/res/layout/activity_settings.xml` (Created)
- `app/src/main/java/com/skul9x/readoutloud/ui/SettingsActivity.kt` (Created)

## Test Criteria
- [x] Nếu Switch OFF, chạy hàm Regex Cũ.
- [x] Cập nhật UI Loading khi đang call API.
- [x] SettingActivity lưu được Api Keys và back về Main được.

---
Next Phase: phase-04-testing.md
