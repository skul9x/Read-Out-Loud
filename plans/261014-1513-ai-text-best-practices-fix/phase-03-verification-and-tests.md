# Phase 03: Unit Test & Regression Suite

Status: ✅ Completed
Dependencies: Phase 01, Phase 02

## Objective
Xây dựng các bài kiểm thử tự động bổ sung trong `AiTextButtonTest.kt` nhằm xác thực cả 3 cải tiến và đảm bảo toàn bộ bộ test cũ không phát sinh bất kỳ hồi quy nào.

## Test Scenarios to Add
1. **Kiểm tra dừng TTS khi click AI text:**
   - Khởi động đọc một đoạn văn bản (`startReading()`).
   - Click `aiTextButton`.
   - Xác thực service nhận lệnh dừng (`TtsService.ACTION_STOP`) và thanh tiến trình quay về 0%.
2. **Kiểm tra vị trí cuộn reset về 0:**
   - Giả lập người dùng cuộn xuống vị trí `scrollY = 150`.
   - Click `aiTextButton` và hoàn thành xử lý.
   - Xác thực `binding.editText.scrollY` được reset về `0`.
3. **Kiểm tra an toàn Coroutine Cancellation:**
   - Giả lập hủy lifecycle (`viewLifecycleOwner.lifecycleScope` bị cancel).
   - Xác thực ứng dụng không ném ra `NullPointerException`.

## Regression Verification Command
```bash
./gradlew testDebugUnitTest
```
Yêu cầu: 100% tests pass cleanly.
