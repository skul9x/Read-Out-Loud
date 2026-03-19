# Phase 01: UI Updates (Button & Overlay)
Status: ⬜ Pending

## Objective
Thêm nút "Polish Text" vào giao diện chính và thiết kế một lớp Overlay dùng để chặn tương tác khi ứng dụng đang kết nối với Gemini API.

## Requirements
### Functional
- [ ] Mở rộng giao diện `activity_main.xml`.
- [ ] Thêm nút `polishButton` nằm ngay cạnh nút Switch Gemini (hoặc trong cụm điều khiển).
- [ ] Thêm một `FrameLayout` (overlay) phủ toàn màn hình, chứa `CircularProgressIndicator` và dòng chữ "Gemini đang xử lý...". Overlay này mặc định `android:visibility="gone"`.
- [ ] Nút `polishButton` chỉ được `Enabled` (và hiện sáng) khi Switch "GEMINI AI READ" đang bật (isChecked = true). Tối màu khi tắt.

### Implementation Steps
1. [ ] Sửa `activity_main.xml`: Thêm Button "POLISH" vào giao diện.
2. [ ] Sửa `activity_main.xml`: Thêm Overlay Layout (Background mờ, chặn click xuyên qua).

## Test Criteria
- [ ] Chạy app lên, giao diện không vỡ.
- [ ] Khi bật/tắt Gemini Toggle, nút Polish sáng/tối tương ứng.
- [ ] Overlay không tự động hiện lên khi mới vào app.
