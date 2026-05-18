# Phase 02: Cooldown Delay & Physical Network Failure
Status: ✅ Completed
Dependencies: Phase 01

## Objective
Nâng cao tính tự vệ của hệ thống xoay tua thông qua việc thiết lập độ trễ an toàn khi gặp lỗi máy chủ quá tải và ngắt xoay tua ngay lập tức khi mất kết nối mạng vật lý.

## Requirements
### Functional
1. **Cooldown Delay (300ms)**:
   - Khi API của một cặp `(Model, Key)` trả về HTTP 503 (Service Unavailable) hoặc 429 RPM (Rate Limited), trước khi xoay sang API key tiếp theo trong danh sách, hệ thống phải thực hiện trì hoãn (delay) ngắn 300ms (`delay(300)`).
   - Mục đích: Tránh việc dồn dập gửi các request tiếp theo tới các khóa khác ngay lập tức, giảm thiểu rủi ro làm quá tải hàng loạt.
2. **Ngắt xoay tua khi mất mạng vật lý (Physical Network Failure Termination)**:
   - Khi tiến hành gọi API mà ném ra ngoại lệ `IOException` dạng lỗi kết nối mạng vật lý (cụ thể là `UnknownHostException` - thiết bị mất mạng không phân giải được DNS, hoặc `ConnectException` - không thể kết nối tới server).
   - Hệ thống **phải dừng ngay lập tức mọi chu trình xoay tua** (không thử các khóa tiếp theo của model hiện tại và không thử các model tiếp theo).
   - Lý do: Mất mạng vật lý là lỗi toàn cục, thử khóa hay model nào khác cũng đều sẽ thất bại và làm hao tổn hiệu năng ứng dụng vô ích.
   - Ứng dụng phải trả về một lỗi mạng rõ ràng và dễ hiểu cho người dùng (ví dụ: `GeminiResult.Error("Lỗi kết nối mạng vật lý. Vui lòng kiểm tra kết nối internet của bạn.")`).

### Non-Functional
- Đảm bảo coroutine không bị block thread chính trong quá trình delay 300ms.
- Phải log đầy đủ thông tin cảnh báo mạng bị ngắt.
- **Thread-Safety (An toàn đa luồng)**: `ModelQuotaManager` phải đảm bảo thread-safe tuyệt đối khi đọc/ghi trạng thái cặp `(Model, Key)` từ nhiều coroutines/luồng chạy song song bằng cách tích hợp `kotlinx.coroutines.sync.Mutex` đúng như chỉ định của `rotation.md`.

## Implementation Steps
1. [x] **Sửa đổi `ModelQuotaManager.kt`**:
   - Khai báo và sử dụng `kotlinx.coroutines.sync.Mutex` để đồng bộ hóa (thread-safe) các hàm `isAvailable`, `markCooldown`, `markExhausted`, `cleanupExpiredEntries` và `clearStatus`.
   - Chuyển các hàm này thành hàm `suspend` để có thể sử dụng `mutex.withLock { ... }` một cách bất đồng bộ không gây block thread.
2. [x] **Sửa đổi `GeminiApiClient.kt`**:
   - Cập nhật các lệnh gọi `quotaManager.isAvailable`, `quotaManager.markCooldown`, và `quotaManager.markExhausted` thành dạng `suspend` gọi tương ứng với `ModelQuotaManager` mới.
   - Thêm xử lý trì hoãn 300ms trong các nhánh `ApiResult.RateLimited` và `ApiResult.ServiceUnavailable` trước khi `continue` vòng lặp khóa.
   - Thêm logic try-catch trong vòng lặp chính của `cleanTextWithGemini` hoặc bắt cụ thể ngoại lệ `UnknownHostException` / `ConnectException` từ `tryGenerateContent`.
   - Nếu phát hiện lỗi mạng vật lý này, ngắt chu kỳ xoay tua lập tức bằng cách ném ra hoặc trả về `GeminiResult.Error` với thông báo thân thiện.
3. [x] **Sửa đổi các API result mapping**:
   - Định nghĩa thêm trạng thái lỗi mạng vật lý nếu cần thiết trong lớp kết quả của API client.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/data/ModelQuotaManager.kt` - Tích hợp Mutex Thread-Safety
- `app/src/main/java/com/skul9x/readoutloud/data/GeminiApiClient.kt` - Cập nhật logic xoay tua và xử lý ngoại lệ
- `app/src/test/java/com/skul9x/readoutloud/data/Phase02CooldownAndNetworkTest.kt` - File test kiểm chứng

## Test Criteria
- [x] Chạy lệnh `./gradlew test --tests "com.skul9x.readoutloud.data.Phase02CooldownAndNetworkTest"` đạt trạng thái thành công 100% (Green).
- [x] Test case `testCooldownDelayOn503` kiểm chứng thời gian chờ ngắn hoạt động khi gặp 503.
- [x] Test case `testPhysicalNetworkFailureTerminationOnUnknownHostException` xác minh vòng lặp xoay tua bị chấm dứt ngay khi mất mạng.
- [x] Test case `testPhysicalNetworkFailureTerminationOnConnectException` xác minh vòng lặp xoay tua bị chấm dứt khi không kết nối được server.

---
Next Phase: [phase-03-ui-crud-self-healing.md](file:///home/skul9x/Desktop/Test_code/Read-Out-Loud-main/plans/260518-1120-gemini-rotation-alignment/phase-03-ui-crud-self-healing.md)
