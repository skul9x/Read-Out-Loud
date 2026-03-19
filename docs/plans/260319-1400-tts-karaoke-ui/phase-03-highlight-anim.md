# Phase 03: Highlight Animation (Karaoke)
Status: ⬜ Pending
Dependencies: phase-02

## Objective
Hiển thị UI tô màu chữ dựa trên tọa độ từ `Phase 02`. Chữ đang đọc phải có nền cam, chữ trắng, in đậm và fade animation đẹp mắt (nhưng KHÔNG phóng to size để tránh reflow).

## Requirements
### Functional
- [ ] Implement `VisualTransformation` hoặc `SpannableString` trong Compose.
- [ ] Vẽ highlight từ `startIndex` đến `endIndex` bằng cách: Background Orange, Foreground White, FontWeight Bold.
- [ ] Áp dụng Animation (Crossfade color) một cách nhẹ nhàng nếu possible mà không ảnh hưởng 60fps.
- [ ] Đảm bảo Font size giữ nguyên (x2 mặc định từ phase 01), không scale-up chữ.
- [ ] Tự động tắt highlight ngay khi rơi vào `isEditing` mode.

### Quality / Test Criteria
- [ ] Test Visual: Đảm bảo không bị "text reflow" (nhảy dòng, giật đoạn văn) khi chữ đổi màu hoặc in đậm.
- [ ] Test UX: Double tap trong lúc đọc -> Highlight tắt tức thì, Focus Text bình thường, giọng đọc vẫn đọc câu cũ.
