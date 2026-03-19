# Phase 04: Auto-scroll Behavior
Status: ⬜ Pending
Dependencies: phase-03

## Objective
Theo dấu (track) highlight chữ đang đọc và auto-scroll thanh cuộn xuống nhẹ nhàng, CÓ cơ chế nhường quyền cuộn cho user 3 giây.

## Requirements
### Functional
- [ ] Theo dõi toạ độ Y-offset của word đang đọc so với khu vực nhìn thấy (visible area) của TextField.
- [ ] Nếu user chạm/vuốt (Scroll event detected) → ngưng auto-scroll lập tức, chuyển trạng thái `isUserScrolling = true`.
- [ ] Debounce 3000ms (3 giây) từ lần chạm/vuốt cuối cùng của user.
- [ ] Sau 3s không có tương tác: thực hiện lệnh `smoothScrollTo` về vị trí chữ đang highlight & set `isUserScrolling = false`.

### Quality / Test Criteria
- [ ] Test Unit/UI: Bỏ tay khỏi máy > Đọc bình thường app tự kéo trang theo.
- [ ] Test Edge Case: Vuốt mạnh tay lên trang đầu -> App không kéo trang xuống dù TTS vẫn đang đọc & highlight ở chỗ khác. Đếm nhẩm 3s -> App tự nảy mượt (snap back) về đúng đoạn chữ cam đang được tô.
