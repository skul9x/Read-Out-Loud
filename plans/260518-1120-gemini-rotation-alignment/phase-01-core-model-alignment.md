# Phase 01: Core Hash Format & Model Priority Alignment
Status: ✅ Completed
Dependencies: None

## Objective
Đảm bảo định dạng Hash định danh cặp `(Model, Key)` và danh sách các mô hình mặc định cùng với thứ tự ưu tiên của chúng tuân thủ tuyệt đối 100% các nguyên tắc trong `rotation.md`.

## Requirements
### Functional
1. **Sửa đổi Unique Hash ID Format**:
   - `rotation.md` quy định: `HashedId = ModelName + "::" + SHA256(ApiKey).take(8)` (trong đó 8-byte SHA-256 hash tương ứng với lấy 16 ký tự hex đầu tiên của chuỗi băm SHA-256).
   - Mã nguồn hiện tại trả về toàn bộ 64 ký tự hex của SHA-256 hash. Cần refactor để chỉ lấy 8 bytes đầu tiên (16 ký tự hex đầu tiên).
2. **Cập nhật Models Priority List**:
   - `rotation.md` quy định danh sách mặc định gồm 4 mô hình theo thứ tự ưu tiên giảm dần:
     1. `models/gemini-3.1-flash-lite` (Ưu tiên cao nhất)
     2. `models/gemini-2.5-flash-lite` (Ưu tiên thứ hai)
     3. `models/gemini-3-flash-preview` (Ưu tiên thứ ba)
     4. `models/gemini-2.5-flash` (Fallback cuối cùng)
   - Cần cập nhật hằng số `DEFAULT_MODELS` trong `ModelManager.kt` và sửa các unit test cũ cho khớp với danh sách này.

### Non-Functional
- Đảm bảo tính nhất quang giữa danh sách mô hình của `ModelManager` và client gọi API `GeminiApiClient`. `GeminiApiClient` nên lấy danh sách mô hình năng động từ `ModelManager.getModels()` để bảo đảm thứ tự ưu tiên của người dùng được áp dụng trực tiếp.
- Loại bỏ danh sách `MODELS` hardcoded bị thừa trong companion object của `GeminiApiClient.kt`, đồng thời refactor hàm `getCurrentStatus()` để lấy tên mô hình năng động từ `ModelManager` nhằm tránh hiển thị sai lệch chỉ mục khi danh sách mô hình thay đổi.

## Implementation Steps
1. [x] **Sửa đổi `SecurityUtils.kt`**:
   - Refactor hàm `getPairHash` để thực hiện đúng việc băm SHA-256 của API Key, lấy 8 bytes đầu tiên (16 ký tự hex) và nối thành chuỗi dạng `"$model::$hexHash"`.
2. [x] **Cập nhật `ModelManager.kt`**:
   - Thay đổi danh sách `DEFAULT_MODELS` để có 4 mô hình đúng theo thứ tự ưu tiên của `rotation.md`.
3. [x] **Cập nhật `GeminiApiClient.kt`**:
   - Xóa hằng số `MODELS` cũ trong companion object.
   - Refactor hàm `getCurrentStatus()` để lấy tên mô hình thực tế đang chạy năng động từ `modelManager.getModels()`.
4. [x] **Kiểm tra và Sửa đổi các Unit Test hiện có**:
   - Một số test cũ (như `ModelManagerTest.kt`, `ModelQuotaManagerTest.kt`) có thể đang mock hoặc sử dụng các hằng số cũ, cần cập nhật để đồng bộ với danh sách mô hình mặc định mới.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/utils/SecurityUtils.kt` - Sửa hàm `getPairHash`
- `app/src/main/java/com/skul9x/readoutloud/data/ModelManager.kt` - Cập nhật `DEFAULT_MODELS`
- `app/src/test/java/com/skul9x/readoutloud/data/Phase01ModelAlignmentTest.kt` - File test kiểm chứng

## Test Criteria
- [x] Chạy lệnh `./gradlew test --tests "com.skul9x.readoutloud.data.Phase01ModelAlignmentTest"` đạt trạng thái thành công 100% (Green).
- [x] Test case `testPairHashFormat` xác nhận định dạng hash gồm đúng tên model và 16 ký tự băm hex của khóa.
- [x] Test case `testDefaultModelsPriority` và `testModelManagerDefaultPriorityOnFirstStart` xác thực danh sách mô hình mặc định đúng thứ tự ưu tiên.

---
Next Phase: [phase-02-cooldown-delay-physical-network.md](file:///home/skul9x/Desktop/Test_code/Read-Out-Loud-main/plans/260518-1120-gemini-rotation-alignment/phase-02-cooldown-delay-physical-network.md)
