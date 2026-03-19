# Phase 03: UI Fixes & Settings Enhancements
Status: ⬜ Pending
Dependencies: phase-01-ui.md

## Objective
Sửa lỗi giao diện bị lẹm vào Status Bar trên toàn ứng dụng. Cải tiến trải nghiệm người dùng trong màn hình Settings (thêm nút Paste tự động parse Key và hỗ trợ thanh cuộn cho danh sách Key dài).

## Requirements
### Functional & UI
- [ ] Xử lý lỗi UI bị lẹm Status Bar: Áp dụng `WindowInsets` hoặc `fitSystemWindows` cho `activity_main.xml` và `activity_settings.xml` để thanh Toolbar và các nội dung bị che khuất được đẩy xuống dưới thanh trạng thái của hệ thống.
- [ ] Màn hình Settings (`activity_settings.xml`): Thêm một nút **"PASTE"** nổi bật bên cạnh hoặc phía trên khu vực nhập API Keys.
- [ ] Logic nút Paste trong Settings: Khi bấm, lấy mảng văn bản từ Clipboard, lọc tìm tất cả các chuỗi bắt đầu bằng `AIza...` (Regex match) và tự động điền chúng (mỗi key 1 dòng) vào ô nhập liệu `EditText`. Kèm Toast thông báo "Đã tìm thấy X keys".
- [ ] Ô nhập liệu API Key: Bật tính năng cuộn dọc (Vertical Scrollbar) khi người dùng nhập quá nhiều dòng (nhiều keys) để tránh bị tràn màn hình hoặc khó xem lại.

### Implementation Steps
1. [ ] Sửa thuộc tính padding/margins hoặc dùng `ViewCompat.setOnApplyWindowInsetsListener` trong `MainActivity.kt` và `SettingsActivity.kt`.
2. [ ] Thêm `ImageButton` hoặc `MaterialButton` (Icon Paste) vào Layout Settings.
3. [ ] Viết hàm đọc Clipboard và chạy regex tìm API key trong `SettingsActivity.kt`. Biến chuỗi tìm được hiển thị lên màn hình (chưa save).
4. [ ] Thêm thuộc tính `android:isScrollContainer="true"`, `android:scrollbarAlwaysDrawVerticalTrack="true"`, `android:scrollbars="vertical"`, và set movement method trong code cho ô EditText nhập keys.

## Test Criteria
- [ ] Toolbar không còn dính vào các icon Wifi/Pin trên điện thoại.
- [ ] Copy một đoạn văn bản LỚN chứa lẫn lộn văn bản thường và 3 API Keys, bấm "Paste" trong màn hình Settings -> UI tự động lọc và chỉ hiện ra đúng 3 keys.
- [ ] Nhập/dán 20 API keys -> Cuộn lên cuộn xuống mượt mà trong ô EditText.
