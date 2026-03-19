# Phase 01: Read-Only & Gesture Handling
Status: ⬜ Pending
Dependencies: None

## Objective
Biến vùng hiển thị văn bản hiện tại thành dạng Read-Only mặc định (font x2 to rõ), chặn Single Tap, và chỉ cho phép Edit khi Double Tap. Quá trình chuyển từ Read-Only sang Edit phải mượt và độc lập với TTS nếu cần.

## Requirements
### Functional
- [ ] Text hiển thị x2 kích thước mặc định.
- [ ] Chạm 1 lần không hiển thị bàn phím (Consume touch event).
- [ ] Double-tap: Hiển thị bàn phím, chuyển trạng thái UI sang `isEditing`.
- [ ] Double-tap khi đang đọc: Bàn phím hiện, nhưng TTS vẫn đọc tiếp đoạn text cũ.
- [ ] Thoát Edit: Lưu text mới, buộc user phải bấm Play lại từ đầu để đọc text vừa sửa.

### Quality / Test Criteria
- [ ] Test UI: Single tap không hiện Focus / Keyboard.
- [ ] Test UI: Double tap mở keyboard và có Focus box thành công.
- [ ] Test Unit/Mock: Khi `isEditing = true`, kiểm tra hành vi stop highlight. Sửa văn bản > đóng keyboard > Play từ đầu đúng text mới.
