# Phase 04: Testing & Finalization
Status: ⬜ Pending
Dependencies: phase-03-frontend.md

## Objective
Nghiệm thu thủ công tính năng và test các luồng lỗi của Google Gemini API trước khi đóng tính năng. Cập nhật WALKTHROUGH.

## Requirements
- [ ] Log Activity/Console cần hiển thị model nào đang được dùng, có đổi Model/Key đúng không khi rớt API.
- [ ] Format văn bản sau khi qua Gemini phải phù hợp cho TTS (như Read-Out-Loud ban đầu).
- [ ] Chống Spam click: Khi đang làm sạch văn bản thì Disable nút Dán.

## Implementation Steps
1. [ ] Dán thử văn bản dài 3000 từ có Markdown Table vào clipboard. Đợi xem AI có xử lý thành list hoặc text hay không.
2. [ ] Tạo một API key lỗi (Random String) làm Key1, dán Key đúng làm Key2. Chạy thử xem ứng dụng có chuyển đổi tự động sang Key2 hay không.
3. [ ] Xem lại Codebase xem có quên đóng file tài nguyên nào không (Cleanup).
4. [ ] Tạo `walkthrough.md`.

## Test Criteria
- [ ] Các cases lỗi Rate Limit xử lý tốt.
- [ ] Chữ đọc ra từ TTS không còn từ *"bảng"*, *"sao sao"*, *"thăng"*...
