# Phase 02: Logic Integration (MainActivity)
Status: ⬜ Pending
Dependencies: phase-01-ui.md

## Objective
Tích hợp logic xử lý sự kiện click của nút Polish Text, tách biệt hoàn toàn tính năng AI ra khỏi nút Paste gốc để người dùng có quyền chủ động quyết định lúc nào cần dùng AI.

## Requirements
### Functional
- [ ] Nút Paste (`pasteCard`) bây giờ chỉ thực hiện sao chép từ Clipboard và lọc cơ bản (Local Regex), không gọi AI nữa. Lần trước đã sửa trong `pasteFromClipboard()`.
- [ ] Bấm nút `polishButton`: 
  - 1. Kiểm tra text trong ô `editText` có trống không. Nếu trống -> Báo lỗi.
  - 2. Bật Overlay (hiện ProgressBar và chặn click).
  - 3. Khởi động Coroutine gọi `geminiApiClient.cleanTextWithGemini(text)`.
  - 4. Khi có kết quả: Thay thế nội dung trong `editText` bằng kết quả API trả về (hoặc fallback nếu lỗi).
  - 5. Tắt Overlay, khôi phục tương tác cho người dùng.

### Implementation Steps
1. [ ] Khai báo binding cho `polishButton` và `overlayLayout` trong `MainActivity.kt`.
2. [ ] Update hàm `setupUI()`: Lắng nghe sự kiện CheckChange của Toggle để `enable/disable` nút Polish.
3. [ ] Chuyển logic `processWithAI` sang thành `polishText()` (được trigger bởi Polish Button).
4. [ ] Xóa logic gọi AI tự động khỏi hàm `pasteFromClipboard()`.
5. [ ] Thay vì Disable các nút (như `setLoading` cũ), sử dụng Overlay để chặn thao tác và hiển thị loading rõ ràng hơn. (Hàm `showLoadingOverlay(boolean)`).

## Test Criteria
- [ ] Bấm Paste: Văn bản hiện ra ngay lập tức, không chờ API.
- [ ] Bấm Polish Text: Có loading overlay che màn hình, không bấm được gì khác -> Sau đó Text tự thay đổi -> Loading tắt.
