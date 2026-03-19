# Changelog

Tài liệu ghi lại các thay đổi quan trọng của dự án Read-Out-Loud.

## [2026-03-19] - Gemini Integration Phase
### Added
- **Gemini AI Integration:** Tích hợp mô hình Gemini (Flash, Flash-Lite) để làm sạch văn bản (loại bỏ Markdown, Link, Bảng biểu) trước khi đọc.
- **Minimalist Dark Mode UI:** Giao diện Dark Mode lấy cảm hứng từ phong cách Clean & Bold. Sử dụng CardView cho các nút điều khiển chính (Dán, Đọc, Dừng).
- **Settings Activity:** Màn hình cài đặt để quản lý danh sách API Keys Gemini.
- **ApiKeyManager:** Quản lý API Keys an toàn với `EncryptedSharedPreferences`.
- **GeminiApiClient:** Client xử lý call API với cơ chế **Xoay vòng Model/Key** thông minh (thử hết key cho model này mới sang model khác).
- **Unit Testing:** Thêm Robolectric và MockK cho việc kiểm thử Unit Test trên JVM.

### Changed
- **MainActivity Refactor:** Cập nhật logic `MainActivity` để hỗ trợ bật/tắt Gemini AI.
- **SDK Update:** Nâng cấp `compileSdk` và `targetSdk` lên **35** để tương thích với các thư viện Android mới nhất.
- **ApiKeyManager Robustness:** Thêm cơ chế fallback sang SharedPreferences thường nếu `EncryptedSharedPreferences` bị lỗi (phục vụ môi trường Test/Robolectric).

### Fixed
- Lỗi `Unresolved reference` do thiếu import khi chuyển đổi sang UI mới.
- Lỗi compile `core-ktx 1.16.0` yêu cầu SDK 35.
- Lỗi Regex parse API Key trong chuỗi Kotlin thông thường (chuyển sang Raw String).
