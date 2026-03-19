# Phase 01: Project Setup
Status: ✅ Complete
Dependencies: None

## Objective
Thêm các thư viện cần thiết vào dự án `build.gradle.kts` và tinh chỉnh cấu trúc trước khi code chức năng lớn.

## Requirements
### Functional
- [ ] Ứng dụng phải có khả năng gọi API qua mạng.
- [ ] Hỗ trợ JSON parsing và lưu trữ an toàn.

### Non-Functional
- [ ] Bảo mật: Request API Keys không bị log ra ngoài console (Cleartext traffic, v.v.)
- [ ] Đồng bộ thư viện: Quản lý version hợp lý cho `kotlinx-coroutines`, `okhttp3`, `security-crypto`, và `serialization`.

## Implementation Steps
1. [x] Mở file `app/build.gradle.kts`.
2. [x] Thêm plugin `org.jetbrains.kotlin.plugin.serialization`.
3. [x] Thêm dependencies: 
   - `org.jetbrains.kotlinx:kotlinx-coroutines-android`
   - `com.squareup.okhttp3:okhttp`
   - `org.jetbrains.kotlinx:kotlinx-serialization-json`
   - `androidx.security:security-crypto`
4. [x] Khai báo quyền `INTERNET` trong `AndroidManifest.xml` (nếu chưa có).
5. [x] Sync Gradle.

## Files to Modify
- `app/build.gradle.kts` - Chứa dependencies.
- `app/src/main/AndroidManifest.xml` - Chứa Permission.

## Test Criteria
- [ ] Dự án cài đặt thành công, build không báo lỗi.
- [ ] Các thư viện được nhận diện đầy đủ ở IDE.

---
Next Phase: phase-02-backend.md
