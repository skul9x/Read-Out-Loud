# Walkthrough: Gemini Integration

Hướng dẫn sử dụng và kiểm tra tính năng Gemini AI Clean Text trong Read-Out-Loud.

## 1. Cấu hình API Keys
- Mở menu **Settings** (biểu tượng bánh răng góc trên bên phải).
- Tìm trường **Gemini API Keys**.
- Nhập danh sách API Keys (mỗi key một dòng, hoặc cách nhau bởi dấu phẩy/khoảng trắng).
- Các key hợp lệ phải bắt đầu bằng `AIza` và có độ dài tối thiểu 30 ký tự.
- Nhấn **Save** (Tự động lưu khi nhập).

## 2. Bật tính năng AI
- Tại màn hình chính, gạt switch **GEMINI AI READ** sang ON.
- Nếu chưa có API Key, hệ thống sẽ yêu cầu cấu hình trước khi cho phép bật.

## 3. Sử dụng
- Sao chép một đoạn văn bản bất kỳ (có thể chứa Link, Markdown Table, hoặc ký tự rác).
- Nhấn nút **PASTE**.
- Phía dưới thanh trạng thái sẽ hiển thị: *"Gemini đang dọn dẹp văn bản..."*. Nút PASTE và READ sẽ bị vô hiệu hóa trong lúc chờ.
- Sau khi kết quả trả về, văn bản sạch sẽ xuất hiện trong ô soạn thảo.
- Nhấn **READ** để nghe TTS đọc đoạn văn bản đã sạch.

## 4. Cơ chế luân phiên (Rotation)
- Hệ thống tự động chuyển đổi giữa các API Key khi gặp lỗi **Quota Exceeded** (429).
- Hệ thống tự động chuyển đổi giữa 5 Model Gemini khác nhau (từ flash đến flash-lite) khi gặp lỗi **Model Not Found** (404).
- Luồng thử nghiệm: Thử tất cả API Keys cho Model đầu tiên -> Nếu thất bại tất cả, chuyển sang Model tiếp theo -> Thử lại tất cả Keys cho Model đó.

## 5. Xử lý lỗi
- Nếu tất cả API Keys hết hạn mức: Thông báo *"Hệ thống đang quá tải"*.
- Nếu rớt mạng: Thông báo *"Lỗi hệ thống"* và tự động lọc văn bản cơ bản (Local Regex) để không làm gián đoạn trải nghiệm người dùng.
