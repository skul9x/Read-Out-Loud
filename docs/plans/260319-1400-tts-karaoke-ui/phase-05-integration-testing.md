# Phase 05: E2E Integration Testing
Status: ✅ Complete
Dependencies: phase-04

## Objective
Chạy test tổng thể End-to-End toàn bộ UI Flow để đảm bảo tất cả Edge Cases đã giải quyết đúng với bảng SPEC. Không để lọt bug Concurrent UI state.

## Requirements
### Test Criteria
1. [x] **Text Length & Indexing Test**: Gõ dấu cách, ký tự đặc biệt, xuống dòng nhiều lần xem index TTS có bị chệch màu không.
2. [x] **UX Flow Test**: Sửa 1 chữ trong lúc đang đọc, thoát Edit, rồi check xem app có tắt hẳn giọng đọc (TTS stop) như spec không, và ấn Play có load lại chính xác text mới hay không.
3. [x] **Memory & Perf Test**: Bật load đoạn text siêu dài > 5 phút, check Auto-scroll có bị lag do cấp phát highlight liên tục (Spannable re-allocation) không.
4. [x] **Stress Test**: Chạm Single-tap liên thanh, Double-tap liên thanh, đóng/mở bàn phím nhanh liên tục xem app có crack, văng app (ConcurrentModificationException) hay mất state không.
