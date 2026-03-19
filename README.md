# 📚 Read-Out-Loud (Android M3 + Gemini AI)

## 📋 Tổng quan
**Read-Out-Loud** là một ứng dụng Android hỗ trợ đọc văn bản (Text-To-Speech) hiện đại, được xây dựng trên ngôn ngữ thiết kế **Material Design 3 (M3)** và tích hợp trí tuệ nhân tạo **Google Gemini AI**. Ứng dụng không chỉ đọc văn bản đơn thuần mà còn có khả năng "làm sạch" dữ liệu rác, loại bỏ các định dạng Markdown phức tạp để mang lại trải nghiệm nghe đọc tự nhiên và chuẩn xác nhất.

---

## ✨ Các tính năng nổi bật
- **🎨 Giao diện Material 3 (M3):** Thiết kế Dark Mode cao cấp với bảng màu Indigo-Deep Grey, các thành phần giao diện bo góc mềm mại và hiệu ứng Tonal Button chuyên nghiệp.
- **📊 Theo dõi tiến độ đọc (Reading Progress):** Thanh tiến trình (Linear Progress Indicator) và hiển thị phần trăm (%) cụ thể giúp bạn biết chính xác AI đang đọc đến đâu trong văn bản.
- **🧠 Làm sạch văn bản thông minh (AI Polish):** Nút **"AI text"** tích hợp sức mạnh của Gemini AI để tự động loại bỏ các ký tự thừa, đường link, định dạng Markdown rác, giúp bản đọc trôi chảy hơn.
- **🔊 Điều khiển âm lượng tối ưu:** Tự động thiết lập âm lượng hệ thống ở mức **80%** ngay khi mở ứng dụng. Nút điều khiển nhanh cho phép xoay vòng qua các mức **80% -> 85% -> 90%** vô cùng tiện lợi.
- **♻️ Cơ chế Xoay Tua API Chuyên nghiệp:** Tự động luân chuyển giữa danh sách nhiều API Key và các dòng model Gemini (Flash, Lite...) để xử lý lỗi vượt hạn mức (Quota Exceeded) một cách mượt mà.
- **🎤 Tùy chọn Giọng đọc Tiếng Việt:** Cho phép người dùng lựa chọn giữa nhiều giọng đọc Google TTS tiếng Việt chất lượng cao (Giọng đọc 1, Giọng đọc 2...).

---

## 🛠 Công nghệ sử dụng
- **Ngôn ngữ chính:** Kotlin
- **Giao diện:** Material Design 3 (M3)
- **Hệ điều hành:** Android SDK 35 (Target)
- **Thư viện quan trọng:** 
  - **OkHttp 4.12.0:** Xử lý các yêu cầu API mạnh mẽ.
  - **Kotlinx Serialization:** Chuyển đổi dữ liệu JSON tốc độ cao.
  - **Security Crypto:** Bảo mật API Keys bằng `EncryptedSharedPreferences`.
  - **Material Components 1.13+:** Cung cấp các thành phần giao diện M3 mới nhất.

---

## ⚙️ Hướng dẫn Cài đặt & Sử dụng
1. **Clone Repository:** 
   ```bash
   git clone https://github.com/skul9x/Read-Out-Loud.git
   ```
2. **Build dự án:** Mở bằng Android Studio và đợi Gradle đồng bộ hóa.
3. **Cấu hình API Key:** 
   - Lấy API Key tại [Google AI Studio](https://aistudio.google.com/).
   - Vào mục **Settings** (biểu tượng bánh răng) trong ứng dụng và dán danh sách key vào. Ứng dụng sẽ tự động phân tách và quản lý cho bạn.
4. **Sử dụng:** 
   - Dán văn bản vào màn hình chính.
   - Nhấn **"AI text"** để AI dọn dẹp văn bản (tùy chọn).
   - Nhấn **READ** để bắt đầu nghe.

---

## 📁 Cấu trúc thư mục (Tóm tắt)
- `app/src/main/java/com/skul9x/readoutloud/`: Chứa mã nguồn logic chính (MainActivity, TtsService, ApiClient...).
- `app/src/main/res/layout/`: Chứa các file giao diện XML (M3 styled).
- `app/src/main/res/values/`: Chứa định nghĩa màu sắc (`colors.xml`) và chủ đề (`themes.xml`) Material 3.
- `.brain/`: Chứa các file kiến thức dự án (brain.json, session.json) - phục vụ cho AI context.

---

**Phiên bản:** 1.3.0  
**Tác giả:** Nguyễn Duy Trường  
**Năm phát triển:** 2026
