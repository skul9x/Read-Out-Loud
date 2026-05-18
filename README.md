# 💡 Read-Out-Loud (Karaoke & AI Text Reader)

**Read-Out-Loud** là một ứng dụng Android chuyên dụng giúp chuyển đổi văn bản thành giọng nói (TTS) kết hợp với hiệu ứng Karaoke đồng bộ theo từng từ thời gian thực. Ứng dụng tích hợp mô hình ngôn ngữ lớn **Google Gemini AI** nhằm chuẩn hóa, làm sạch văn bản thô (loại bỏ quảng cáo, dòng trống, ký tự rác, lỗi chính tả) trước khi đọc, mang lại trải nghiệm đọc sách, bài viết vô cùng trực quan và liền mạch.

---

## 🚀 Tính năng nổi bật

### 🎤 Trải nghiệm đọc Karaoke thời gian thực
* **Highlight đồng bộ:** Tô sáng màu cam và in đậm từng từ chính xác theo giọng đọc TextToSpeech hiện tại.
* **Tự động cuộn thông minh (Auto-scroll):** Tự động dịch chuyển vùng văn bản để giữ từ đang đọc luôn ở vùng trung tâm màn hình.
* **Chế độ đọc an toàn (Read-Only):** Ngăn chặn việc bàn phím ảo vô tình kích hoạt khi đang đọc. Hỗ trợ chạm đúp (Double-tap) để chuyển sang chế độ chỉnh sửa nhanh.

### 🧠 Dọn dẹp văn bản thông minh bằng Gemini AI
* **AI Text Clean:** Một chạm gửi văn bản gốc lên Gemini AI để tự động sửa lỗi chính tả, loại bỏ các thẻ quảng cáo, ký hiệu rác và căn chỉnh bố cục đoạn văn khoa học.
* **Xoay tua API Keys & Models thế hệ mới (Rotation System):**
  * **Chiến lược Model-First, Key-Second:** Thử nghiệm tất cả API Keys hiện có cho mô hình có độ ưu tiên cao nhất trước khi hạ cấp xuống mô hình thấp hơn để tiết kiệm tài nguyên và nâng cao chất lượng phản hồi.
  * **MD5 Hashing Quota Manager:** Quản lý hạn mức (Quota) của từng cặp `Model_ApiKey` riêng biệt bằng mã hóa băm MD5, tự động lưu trạng thái cạn kiệt (Exhausted) xuống thiết bị.
  * **Trì hoãn Cooldown thông minh:** Tự động trì hoãn 300ms khi gặp lỗi giới hạn RPM (429) hoặc dịch vụ bận (503) để tránh spam API. Trạng thái Cooldown tự giải phóng sau 5 phút.
  * **Tự động ngắt khẩn cấp:** Dừng ngay lập tức toàn bộ chu trình xoay tua khi phát hiện mất kết nối mạng vật lý (`UnknownHostException`, `ConnectException`) để tránh lãng phí tài nguyên và phản hồi nhanh cho người dùng.
  * **Giao diện cấu hình Model & Key linh hoạt:** Cho phép thêm, xóa, thay đổi thứ tự ưu tiên của mô hình và tự phục hồi (Self-Healing) về trạng thái mặc định an toàn nếu danh sách rỗng.

---

## 🛠️ Công nghệ sử dụng

* **Ngôn ngữ phát triển:** Kotlin (100%)
* **Kiến trúc ứng dụng:** MVVM (Model-View-ViewModel) sạch, hướng cấu trúc dữ liệu rõ ràng.
* **Giao diện người dùng (UI):** Material Design 3 (M3) hiện đại với hiệu ứng Glassmorphism tinh tế và hỗ trợ Dark Mode hoàn hảo.
* **Công nghệ âm thanh:** Android TextToSpeech (TTS) Engine tích hợp bộ lắng nghe tiến trình đọc (`UtteranceProgressListener`).
* **Trí tuệ nhân tạo:** Google Gemini API (hỗ trợ xoay tua linh hoạt các phiên bản Gemini 1.5/2.0/2.5/3.0).
* **Bảo mật dữ liệu:** `EncryptedSharedPreferences` bảo vệ tuyệt mật danh sách API Keys cá nhân.
* **Bộ thư viện Unit Test:** JUnit 4, Robolectric, MockK hỗ trợ kiểm thử chất lượng tích hợp và logic xoay tua API.

---

## 📂 Cấu trúc thư mục dự án

```text
Read-Out-Loud-main/
├── app/                              # Module chính của ứng dụng Android
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/skul9x/readoutloud/
│   │   │   │   ├── data/             # Lớp dữ liệu (Model, ApiClient, QuotaManager, ModelManager)
│   │   │   │   ├── ui/               # Lớp giao diện (MainActivity, SettingsActivity, Adapter)
│   │   │   │   └── utils/            # Thư viện tiện ích & Bảo mật dữ liệu
│   │   │   └── res/                  # Tài nguyên ứng dụng (layout, values, drawable, xml)
│   │   └── test/                     # Hệ thống Unit Test & Integration Test (49 test cases)
│   └── build.gradle.kts              # Cấu hình build Gradle của Module App (signingConfigs, minify, R8)
├── .github/
│   └── workflows/
│       └── android.yml               # Pipeline CI/CD tự động biên dịch, ký số và phát hành Release
├── .brain/                           # Bộ nhớ lưu trữ context kiến trúc dự án vĩnh viễn (Antigravity Brain)
├── skul9x.jks                        # Chứng chỉ khóa ký ứng dụng dạng local
├── build.gradle.kts                  # Cấu hình Gradle của dự án gốc
└── README.md                         # Tài liệu hướng dẫn sử dụng tiếng Việt
```

---

## ⚙️ Hướng dẫn cài đặt và Build

### Yêu cầu hệ thống:
* **Java JDK:** Phiên bản 17 trở lên.
* **Android SDK:** Hỗ trợ compile và target SDK 35 (Android 15).
* **Môi trường Gradle:** Gradle wrapper phiên bản 8.4+ (đã đi kèm).

### Các bước biên dịch thủ công tại Local:
1. Clone mã nguồn về máy:
   ```bash
   git clone https://github.com/skul9x/Read-Out-Loud.git
   cd Read-Out-Loud
   ```
2. Cấp quyền chạy file Gradle wrapper:
   ```bash
   chmod +x gradlew
   ```
3. Biên dịch bản phát hành Debug APK:
   ```bash
   ./gradlew :app:assembleDebug
   ```
4. Biên dịch bản phát hành tối ưu Release APK (sử dụng chứng chỉ cục bộ `skul9x.jks` đã tích hợp):
   ```bash
   ./gradlew :app:assembleRelease
   ```
   *File APK Release cực kỳ gọn nhẹ (~2.5 MB) sau khi build sẽ nằm tại thư mục:* `app/build/outputs/apk/release/app-release.apk`

---

## 📖 Hướng dẫn sử dụng nhanh

1. **Khởi chạy ứng dụng:** Mở ứng dụng Read-Out-Loud trên điện thoại Android của bạn.
2. **Cài đặt API Keys:** Nhấn vào biểu tượng Bánh răng (Cài đặt), dán danh sách API Keys Gemini của bạn (phân tách nhau bằng dấu phẩy `,`).
3. **Cấu hình Models:** Bật/tắt hoặc kéo thả thay đổi mức độ ưu tiên của các dòng mô hình AI phù hợp với hạn mức tài khoản.
4. **Nhập văn bản:** Dán đoạn văn bản hoặc tài liệu bạn muốn đọc vào ô văn bản lớn tại màn hình chính.
5. **AI Làm sạch:** Nhấn nút **AI text** để hệ thống tự động làm sạch và định dạng văn bản qua Gemini AI.
6. **Thưởng thức giọng đọc:** Nhấn **READ** để bắt đầu nghe đọc sách với hiệu ứng Karaoke đồng bộ sinh động! Chạm đúp (Double-tap) bất kỳ lúc nào để quay lại chế độ chỉnh sửa.

---

## 📝 Thông tin bổ sung & Bản quyền

Dự án được phát triển và duy trì bởi **Nguyễn Duy Trường**. Mọi quyền được bảo lưu.

Copyright 2026 Nguyễn Duy Trường
