# 💡 BRIEF: Read-Out-Loud (Karaoke & Read-Only UI)

**Ngày tạo:** 2026-03-19
**Brainstorm cùng:** skul9x

---

## 1. VẤN ĐỀ CẦN GIẢI QUYẾT
- Text area hiện tại dễ bị chạm nhầm gây hiện bàn phím, che mất nội dung.
- Quá trình đọc chưa có hiệu ứng trực quan sinh động theo từng từ, khiến user khó theo dõi văn bản.

## 2. GIẢI PHÁP ĐỀ XUẤT
- Thiết kế một **chế độ Read-Only an toàn**: Chạm 1 lần không có tác dụng, chỉ khi **Double-tap** mới cho phép sửa văn bản.
- Tích hợp **Hiệu ứng Karaoke mượt mà**: Chữ đang đọc sẽ được highlight (nền cam màu nổi, chữ trắng, in đậm) và animatet fade nhẹ nhàng, không nhảy kích thước gây vỡ dòng. Toàn bộ font trong text area tăng x2 kích thước.

## 3. TÍNH NĂNG CHI TIẾT ĐÃ CHỐT (Edge Cases)
1. **Double-tap khi đang đọc:** TTS vẫn tiếp tục đọc đoạn text cũ trong bộ nhớ, bàn phím hiện lên và hiệu ứng highlight tạm ẩn đi.
2. **Sau khi sửa (Exit Edit Mode):** Update text mới nhưng KHÔNG tự động đọc tiếp (Stop TTS). User cần bấm nút Play từ đầu để load lại và đọc file mới.
3. **Double-tap nhưng Đóng bàn phím ngay (Không sửa):** Hiệu ứng highlight lập tức bật lại và nhảy đúng từ theo tiếng TTS đang đọc tiếp.
4. **Hiệu ứng Highlight:** KHÔNG phóng to kích thước từ để tránh text reflow. Chỉ hiển thị nền cam, chữ trắng, in đậm, kèm hiệu ứng chuyển màu mềm mại (fade). Font chữ mặc định trong text area phóng to x2 để dễ đọc.
5. **Auto-scroll thông minh (Chống tranh giành cuộn):** 
   - Cuộn tĩnh tự động: Chữ đọc tới đâu sẽ tự cuộn nhẹ nhàng tới đó để chữ luôn nằm trong tầm nhìn (scroll behavior mượt mà).
   - User chủ động vuốt: App tạm ngưng auto-scroll để user xem các vị trí khác (bài vẫn đọc & highlight bình thường).
   - Tự động bắt kịp (Snap back): Sau **3 giây** không phát hiện người dùng vuốt/chạm, màn hình cuộn xuống bắt kịp lại đúng dòng text đang được đánh dấu (visual mượt).
6. **Single-tap:** Chạm vô dụng, không làm gì cả.

## 4. ƯỚC TÍNH SƠ BỘ
- **Độ phức tạp:** Khá Phức tạp (Cần xử lý luồng Compose Gestures riêng, Custom Spannable String/VisualTransformation cho animation fade mượt, Debouncer timer 3s cho Scroll State).
- **Rủi ro:** Scroll State và Tts Word Boundary đồng bộ theo mili-giây có thể gặp sai số, yêu cầu kiến trúc quản lý progress tracking chính xác. 

## 5. BƯỚC TIẾP THEO
→ Chạy `/plan` để lên thiết kế chi tiết (UI mockup + Architecture).
