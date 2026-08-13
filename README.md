# 💡 Read-Out-Loud (Karaoke Text-To-Speech & Gemini AI Research Assistant)

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%20100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![Gemini AI](https://img.shields.io/badge/AI-Google%20Gemini-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev)
[![Markwon](https://img.shields.io/badge/Markdown-Markwon%204.6.2-FF5722?style=for-the-badge)](https://noties.io/Markwon)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=gradle&logoColor=white)](https://github.com/skul9x/Read-Out-Loud)
[![License](https://img.shields.io/badge/License-Proprietary-red?style=for-the-badge)](LICENSE)

**Read-Out-Loud** là ứng dụng Android đa nhiệm hiện đại kết hợp trình đọc văn bản thành giọng nói (Text-To-Speech) với hiệu ứng **Karaoke Highlight thời gian thực**, trợ lý nghiên cứu chuyên sâu **Google Gemini AI**, cùng trình đọc **Markdown toàn màn hình (Fullscreen Reader)** mượt mà.

</div>

---

## 🌟 Điểm nổi bật & Tính năng chính

### 📖 1. Tab "Read" — Trải nghiệm Đọc Karaoke & Xử lý Văn bản Thông minh
* **🎤 Real-Time Karaoke Highlight:** Tô sáng và in đậm chính xác từng từ khớp với giọng đọc TextToSpeech của thiết bị theo thời gian thực.
* **📜 Tự động cuộn thông minh (Auto-Scroll):** Tự động căn chỉnh màn hình để từ đang đọc luôn hiển thị ở vị trí dễ nhìn nhất.
* **🔒 Chế độ Đọc an toàn (Read-Only) & Chạm đúp chỉnh sửa:** Khóa bàn phím khi đang nghe để tránh gõ nhầm; hỗ trợ Double-Tap để chuyển đổi nhanh sang chế độ chỉnh sửa.
* **✨ AI Text Clean:** Dùng Gemini AI để tự động chuẩn hóa văn bản, loại bỏ quảng cáo, ký tự rác, dòng trống và sửa lỗi chính tả.
* **📄 AI Tóm tắt (Summarize):** Trích xuất các ý chính, số liệu quan trọng của văn bản thành bản tóm tắt súc tích, dễ theo dõi.
* **📋 Tiện ích một chạm:** Dán văn bản (Paste), sao chép (Copy), hiển thị thanh tiến trình đọc (%) và phím tắt điều chỉnh âm lượng nhanh.

---

### 🔍 2. Tab "Prompt" — Nghiên cứu Chuyên sâu, Tìm kiếm & Điều hướng Đa hành động
* **📝 Deep Research Prompt Generator ("Make Prompt"):** Tự động sinh siêu prompt nghiên cứu toàn diện chuẩn chuyên gia phân tích (~8.000 từ) và tự động sao chép vào Clipboard.
* **⚡ Tìm kiếm trực tiếp với Gemini ("Search Now"):** Gửi yêu cầu nghiên cứu trực tiếp lên Gemini API ngay trong ứng dụng, nhận câu trả lời tổng hợp đa chiều từ AI.
* **📑 Kết xuất Markdown chuẩn mực (Markwon):** Kết quả tìm kiếm hiển thị với định dạng Markdown chuyên nghiệp: tiêu đề (H1, H2, H3), in đậm/nghiêng, danh sách gạch đầu dòng, khối trích dẫn rõ ràng.
* **🚀 Thanh tác vụ kết quả đa năng (3-Button Action Row):**
  - **Tóm tắt:** Chuyển kết quả sang tab Read và kích hoạt luồng tóm tắt nội dung tức thì.
  - **Read (Đọc ngay):** Chuyển tiếp toàn bộ nội dung sang tab Read và tự động kích hoạt giọng đọc Text-To-Speech phát ngay lập tức.
  - **Show (Toàn màn hình):** Mở trình đọc văn bản toàn màn hình chuyên dụng không bị phân tâm.
* **📐 Thiết kế chống tràn chữ & Cân đối hoàn hảo:** Cấu trúc nút Material 3 tỷ lệ đồng đều với `autoSizeTextType="uniform"`, `minWidth="0dp"`, `zero insets` hiển thị trọn vẹn văn bản trên mọi tỉ lệ màn hình.
* **🛡️ Chống lướt nhầm tab (Tab Swipe Lock):** Khóa tính năng vuốt ngang chuyển tab trên ViewPager2 nhằm tối ưu thao tác cuộn và chọn văn bản mượt mà, chuyển tab chủ động qua thanh TabLayout.

---

### 🖥️ 3. Màn hình Trình đọc Toàn màn hình (Fullscreen Reader)
* **📱 Thiết kế tràn viền Material 3 Edge-to-Edge:** Tối ưu hóa không gian hiển thị tối đa, tự động điều chỉnh padding theo thanh trạng thái (Status bar) và thanh điều hướng (Navigation bar).
* **📜 Thanh cuộn dọc trực quan (Vertical Scrollbar):** Hiển thị thanh cuộn rõ ràng giúp người dùng luôn nắm bắt vị trí đọc hiện tại trong các tài liệu dài.
* **👈 Cử chỉ Vuốt để quay lại (Swipe-Back Gesture):** Hỗ trợ vuốt ngang từ trái sang phải để đóng màn hình đọc và quay về tab Prompt mượt mà, bảo toàn 100% trạng thái tìm kiếm trước đó.
* **📋 Sao chép nhanh:** Tích hợp nút sao chép toàn bộ nội dung tài liệu ngay trên thanh công cụ (Toolbar).

---

### 🧠 4. Hệ thống Xoay tua API Keys & Models (Smart Model-First Rotation)
* **Chiến lược Model-First, Key-Second:** Thử nghiệm danh sách API Keys trên model có độ ưu tiên cao nhất trước khi tự động chuyển xuống model tiếp theo.
* **MD5 Hashing Quota Manager:** Quản lý hạn mức sử dụng (Quota) độc lập cho từng cặp `Model_ApiKey` bằng mã băm MD5, tự động ghi nhớ trạng thái cạn kiệt (Exhausted).
* **Cooldown & Auto-Recovery:** Tự động hoãn 300ms khi gặp lỗi giới hạn tốc độ (429/503) và tự động mở lại sau 5 phút cooldown.
* **Tự động ngắt khẩn cấp:** Dừng chu trình ngay lập tức khi phát hiện mất kết nối mạng (`UnknownHostException`, `ConnectException`) để tiết kiệm tài nguyên.
* **Quản lý & Tự phục hồi cấu hình (Self-Healing):** Giao diện thêm, xóa, đổi thứ tự ưu tiên các models AI và tự động khôi phục cấu hình an toàn khi gặp sự cố.

---

## 🛠️ Công nghệ & Kiến trúc

| Thành phần | Công nghệ / Thư viện |
|---|---|
| **Ngôn ngữ** | Kotlin 100% (Coroutines, StateFlow, LiveData, Serialization) |
| **Kiến trúc** | MVVM (Model-View-ViewModel) + MainSharedViewModel điều phối liên Tab |
| **UI Components** | Material Design 3 (M3), ViewPager2, TabLayout, NestedScrollView |
| **Markdown Rendering** | Markwon Core 4.6.2 |
| **Âm thanh (TTS)** | Android TextToSpeech Engine + `UtteranceProgressListener` |
| **AI Integration** | Google Gemini API (hỗ trợ `gemini-2.0-flash`, `gemini-1.5-flash`, `gemini-1.5-pro`,...) |
| **Bảo mật** | `EncryptedSharedPreferences` (Android Jetpack Security Crypto) |
| **Mạng & Xử lý** | OkHttp 4, Kotlinx Serialization Json |
| **Kiểm thử** | JUnit 4, Robolectric 4.12+, MockK, Kotlinx Coroutines Test (>80 tests) |

---

## 📂 Cấu trúc dự án

```text
Read-Out-Loud/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/
│   │   │   │   └── prompt.txt                  # Template nghiên cứu chuyên sâu (~8.000 từ)
│   │   │   ├── java/com/skul9x/readoutloud/
│   │   │   │   ├── MainActivity.kt             # Activity chính điều phối TabLayout & ViewPager2
│   │   │   │   ├── SettingsActivity.kt         # Màn hình quản lý API Keys & Models
│   │   │   │   ├── TtsService.kt               # Service điều khiển TextToSpeech nền
│   │   │   │   ├── data/                       # GeminiApiClient, ModelManager, QuotaManager
│   │   │   │   ├── ui/                         # ReadFragment, PromptFragment, FullScreenReaderActivity, MainSharedViewModel
│   │   │   │   └── utils/                      # SecurePreferencesHelper, PromptTemplateHelper
│   │   │   └── res/                            # Layouts, Drawable icons, M3 Themes, Colors
│   │   └── test/                               # Toàn bộ bộ Unit Test & Integration Test (>80 test cases)
│   └── build.gradle.kts                        # Cấu hình build Gradle module App
├── plans/                                      # Hồ sơ kế hoạch & theo dõi phát triển (Phase plans)
├── gradle/                                     # Gradle Wrapper & Version Catalog
├── build.gradle.kts                            # Root build configuration
├── settings.gradle.kts                         # Project settings & dependency resolution
└── README.md                                   # Tài liệu hướng dẫn dự án
```

---

## ⚙️ Cài đặt & Biên dịch (Build)

### Yêu cầu môi trường:
* **JDK:** Java 17 trở lên.
* **Android SDK:** Compile SDK 35 (Android 15), Min SDK 26 (Android 8.0 Oreo+).
* **Gradle:** Gradle 8.11.1 (sử dụng Gradle Wrapper đi kèm).

### Các lệnh thực thi chính:

1. **Cấp quyền chạy script Gradle:**
   ```bash
   chmod +x gradlew
   ```

2. **Chạy toàn bộ bộ kiểm thử tự động (Unit Tests & Integration Tests):**
   ```bash
   ./gradlew test
   ```

3. **Biên dịch bản cài đặt Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   *File APK tạo tại:* `app/build/outputs/apk/debug/app-debug.apk`

4. **Biên dịch bản phát hành Release APK:**
   ```bash
   ./gradlew assembleRelease
   ```
   *File APK Release tạo tại:* `app/build/outputs/apk/release/app-release.apk`

---

## 📖 Hướng dẫn sử dụng nhanh

1. **Cài đặt & Cấu hình API Key:**
   * Mở ứng dụng, nhấn vào biểu tượng **⚙️ Cài đặt** ở góc phải thanh tiêu đề.
   * Nhập một hoặc nhiều Google Gemini API Key (phân tách nhau bởi dấu phẩy `,`).
   * Chọn các models AI mong muốn và sắp xếp thứ tự ưu tiên.
2. **Nghe đọc & Karaoke (Tab Read):**
   * Dán văn bản vào ô nhập bằng nút **PASTE**.
   * Nhấn **AI text** để chuẩn hóa hoặc **Tóm tắt** để tóm tắt văn bản.
   * Nhấn **READ** để bắt đầu nghe đọc với hiệu ứng tô màu Karaoke từng từ.
3. **Tạo Prompt, Tìm kiếm & Đọc Markdown (Tab Prompt):**
   * Chuyển sang tab **Prompt**.
   * Nhập chủ đề cần tìm kiếm vào thanh tìm kiếm.
   * Nhấn **Make Prompt** để sao chép siêu prompt phân tích chuyên sâu vào Clipboard.
   * Hoặc nhấn **Search Now** để nhận kết quả phân tích định dạng Markdown trực tiếp từ Gemini.
   * Tại thanh tác vụ kết quả:
     - Nhấn **Tóm tắt** để tóm tắt liên tab.
     - Nhấn **Read** để tự động chuyển tiếp và đọc to văn bản ngay lập tức.
     - Nhấn **Show** để mở chế độ đọc toàn màn hình với thanh cuộn dọc và cử chỉ vuốt để quay lại.

---

## 👨‍💻 Tác giả & Bản quyền

Dự án được thiết kế, phát triển và duy trì bởi **Nguyễn Duy Trường (skul9x)**.  
Mọi quyền được bảo lưu.

Copyright © 2026 **Nguyễn Duy Trường**.
