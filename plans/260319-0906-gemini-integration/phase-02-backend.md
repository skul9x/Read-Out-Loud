# Phase 02: Backend Logic
Status: ✅ Complete
Dependencies: phase-01-setup.md

## Objective
Xây dựng Data Layer bảo mật và Logic làm sạch văn bản API Client để kết nối với Google Gemini AI, với thuật toán xoay vòng Model như `RSS-Reader-main`.

## Requirements
### Functional
- [ ] Lưu trữ và tách mảng API Key (Paste list => Array).
- [ ] Gọi API `generateContent` với 5 model tuần tự. Nếu lỗi ở Key1 model 1, chuyển qua Key2 model 1; hết chuỗi Key thì sang Model 2, v.v.
- [ ] Prompt AI yêu cầu lọc markdown, chặn bảng biểu, xóa URL link. Không tóm tắt nội dung kiểu thay đổi ý chính.

### Non-Functional
- [ ] Performance: Fail nhanh khi timeout / bad request.
- [ ] Code tái sử dụng mô hình Thread-safe / Mutex.

## Implementation Steps
1. [ ] Tạo file `ApiKeyManager.kt`: Quản lý lưu API Key vào `EncryptedSharedPreferences`. Cung cấp hàm `addApiKeys(keysString)` tách bằng dấu phẩy, dấu cách hoặc xuống dòng.
2. [ ] Tạo file `GeminiApiClient.kt`: Có `stateMutex` và `tryGenerateContent`. Đưa 5 model theo đúng thứ tự (`3-flash-preview` đứng đầu...).
3. [ ] Viết hàm Request Payload JSON, đảm bảo cấu hình `temperature`, `maxOutputTokens`.
4. [ ] Viết xử lý Response, return `Success(cleanText)` hoặc `Error/QuotaExceeded`.

## Files to Create
- `app/src/main/java/com/skul9x/readoutloud/data/ApiKeyManager.kt`
- `app/src/main/java/com/skul9x/readoutloud/data/GeminiApiClient.kt`

## Test Criteria
- [ ] Hàm test nhận vào 1 đoạn markdown table có trả về văn bản sạch không.
- [ ] Hàm tách mã API Keys chạy đúng.

---
Next Phase: phase-03-frontend.md
