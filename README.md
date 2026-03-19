# 📚 Read-Out-Loud (Tích hợp Gemini AI)

## Tổng quan
**Read-Out-Loud** là một ứng dụng Android hỗ trợ đọc văn bản (Text-To-Speech) được nâng cấp mạnh mẽ với trí tuệ nhân tạo **Google Gemini AI**. Ứng dụng không chỉ đọc văn bản đơn thuần mà còn có khả năng "làm sạch" dữ liệu rác, loại bỏ các định dạng Markdown phức tạp để mang lại trải nghiệm nghe đọc tự nhiên và chuẩn xác nhất.

## ✨ Các tính năng nổi bật
- **🧠 Làm sạch văn bản thông minh (AI Polish):** Tự động loại bỏ các ký tự thừa, đường link, định dạng Markdown và đặc biệt là các bảng biểu phức tạp nhờ sức mạnh của Gemini AI, giúp bản đọc trôi chảy hơn.
- **🔊 Điều khiển âm lượng tối ưu:** Tự động thiết lập âm lượng hệ thống ở mức **80%** ngay khi mở ứng dụng. Nút điều khiển nhanh cho phép xoay vòng qua các mức **80% -> 85% -> 90%** vô cùng tiện lợi.
- **♻️ Cơ chế Xoay Tua API Chuyên nghiệp:** Tự động luân chuyển giữa danh sách nhiều API Key và các dòng model Gemini (Flash, Lite...) để xử lý lỗi vượt hạn mức (Quota Exceeded) một cách mượt mà.
- **🎤 Tùy chọn Giọng đọc Tiếng Việt:** Cho phép người dùng lựa chọn giữa nhiều giọng đọc Google TTS tiếng Việt chất lượng cao (Giọng đọc 1, Giọng đọc 2...).
- **💎 Giao diện Dark Mode Cao cấp:** Ngôn ngữ thiết kế tối giản, hiện đại với bảng màu được tinh chỉnh, giúp người dùng tập trung tối đa vào nội dung.

## 🛠 Công nghệ sử dụng
- **Ngôn ngữ chính:** Kotlin
- **Môi trường phát triển:** Android Studio Ladybug+
- **Phiên bản hệ điều hành:** Android SDK 35 (Target)
- **Kiến trúc:** Clean Architecture & Data Layer Pattern
- **Thư viện quan trọng:** 
  - **OkHttp 4.12.0:** Xử lý các yêu cầu API mạnh mẽ.
  - **Kotlinx Serialization:** Chuyển đổi dữ liệu JSON tốc độ cao.
  - **Security Crypto:** Bảo mật API Keys bằng `EncryptedSharedPreferences`.
  - **Material Components 1.12.0:** Cung cấp các thành phần giao diện chuẩn Material 3.

## ⚙️ Hướng dẫn Cài đặt & Sử dụng
1. **Clone Repository:** `git clone https://github.com/skul9x/Read-Out-Loud.git`
2. **Build dự án:** Mở bằng Android Studio và đợi Gradle đồng bộ hóa.
3. **Cấu hình API Key:** 
   - Lấy API Key tại [Google AI Studio](https://aistudio.google.com/).
   - Vào mục **Settings** trong app và dán danh sách key vào. App sẽ tự động phân tách và quản lý cho bạn.
4. **Sử dụng:** Dán văn bản vào màn hình chính, bật chế độ "Gemini AI Read" và nhấn **READ**.

---
**Tác giả:** Nguyễn Duy Trường
**Phiên bản:** 1.0.0
**Năm phát triển:** 2026
