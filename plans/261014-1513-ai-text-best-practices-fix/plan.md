# Plan: AI Text & Summarize Best Practices Fix

Created: 2026-10-14 15:13
Status: ✅ Completed

## Overview
Khắc phục 3 vấn đề tiềm ẩn theo chuẩn Android Best Practices khi người dùng tương tác với nút **"AI text"** (và cả **"Tóm tắt"**):
1. **Dừng TTS phát âm thanh cũ:** Ngắt ngay `TtsService` và xóa highlight khi bấm làm sạch hoặc tóm tắt để tránh đọc lệch pha âm thanh với chữ mới.
2. **Reset vị trí cuộn về đầu trang:** Tự động đưa `editText` về tọa độ `(0, 0)` khi nạp văn bản mới đã làm sạch để người dùng không bị nhìn vào khoảng trắng/phần giữa.
3. **Tuân thủ chuẩn Coroutine Cancellation:** Không nuốt `CancellationException` trong các khối `catch (e: Exception)` và kiểm tra an toàn `_binding` để triệt tiêu nguy cơ crash `NullPointerException` khi người dùng thoát màn hình đột ngột.

## Requirements
1. **Dừng TTS khi bắt đầu xử lý AI**:
   - Gọi `stopReading()` ngay khi người dùng nhấn `aiTextButton` hoặc `summarizeButton`.
   - Đảm bảo service `TtsService` nhận lệnh dừng và thanh tiến trình đọc reset về 0%.
2. **Tự động cuộn về đầu trang**:
   - Gọi `binding.editText.scrollTo(0, 0)` khi cập nhật văn bản thành công (`GeminiResult.Success`) hoặc khi chạy fallback dọn dẹp Regex.
3. **Xử lý Coroutine Cancellation chuẩn**:
   - Trong `processWithAI` và `processSummarizeWithAI`:
     ```kotlin
     catch (e: Exception) {
         if (e is kotlinx.coroutines.CancellationException) throw e
         if (_binding == null) return@launch
         // Xử lý lỗi thông thường...
     }
     ```
   - Tránh crash `_binding!!` khi Fragment view đã bị destroy.
4. **Kiểm thử tự động**:
   - Bổ sung unit tests trong `AiTextButtonTest.kt` để kiểm tra việc dừng TTS và reset scroll.
   - Chạy toàn bộ regression test suite (`./gradlew testDebugUnitTest`) đạt 100% pass.

## Phases

| Phase | Tên giai đoạn | Mục tiêu chính | Trạng thái |
| :--- | :--- | :--- | :---: |
| 01 | **Dừng TTS & Reset Cuộn** | Thêm `stopReading()` và `scrollTo(0, 0)` trong `ReadFragment.kt` | ✅ Completed |
| 02 | **Xử lý Coroutine Cancellation** | Re-throw `CancellationException` & guard `_binding` an toàn | ✅ Completed |
| 03 | **Unit Test & Regression Suite** | Viết test case bổ sung & chạy verify `./gradlew testDebugUnitTest` | ✅ Completed |

## Quick Commands
- Bắt đầu thực thi: `/code`
- Chạy kiểm thử: `./gradlew testDebugUnitTest`
