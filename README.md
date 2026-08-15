# 💡 Read-Out-Loud (Karaoke Text-To-Speech & Gemini AI Research Assistant)

<div align="center">

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%20100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![Gemini AI](https://img.shields.io/badge/AI-Google%20Gemini-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev)
[![Markwon](https://img.shields.io/badge/Markdown-Markwon%204.6.2-FF5722?style=for-the-badge)](https://noties.io/Markwon)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=gradle&logoColor=white)](https://github.com/skul9x/Read-Out-Loud)
[![License](https://img.shields.io/badge/License-Proprietary-red?style=for-the-badge)](LICENSE)

**Read-Out-Loud** là ứng dụng Android đa nhiệm cao cấp kết hợp trình đọc văn bản bằng giọng nói (Text-To-Speech) với hiệu ứng **Karaoke Highlight thời gian thực**, trợ lý nghiên cứu chuyên sâu **Google Gemini AI**, hệ thống **Xoay tua Model & API Key thông minh (Model-First Rotation)**, cùng trình đọc **Markdown toàn màn hình (Fullscreen Reader)** tối ưu cho việc nghiên cứu và đọc tài liệu.

</div>

---

## 🌟 Điểm nổi bật & Tính năng chính

### 📖 1. Tab "Read" — Trải nghiệm Đọc Karaoke & Xử lý Văn bản AI
* **🎤 Real-Time Karaoke Highlight:** Tô sáng và in đậm chính xác từng từ khớp với giọng đọc TextToSpeech của thiết bị theo thời gian thực (màu cam `#FF9800` nổi bật).
* **📜 Tự động cuộn thông minh (Auto-Scroll):** Tự động cuộn mượt mà màn hình để từ đang đọc luôn nằm ở vị trí trung tâm mắt nhìn.
* **🔒 Chế độ Đọc an toàn (Read-Only) & Chạm đúp chỉnh sửa:** Tự động khóa bàn phím khi đang đọc để tránh thao tác nhầm; hỗ trợ Double-Tap để kích hoạt nhanh chế độ chỉnh sửa.
* **✨ AI Text Clean:** Ứng dụng Gemini AI để làm sạch và chuẩn hóa văn bản, loại bỏ quảng cáo, ký tự rác, dòng trống thừa và tự động sửa lỗi chính tả.
* **📄 AI Tóm tắt (Summarize):** Trích xuất các ý chính, thông tin và số liệu quan trọng của văn bản thành bản tóm tắt ngắn gọn, rõ ràng.
* **📋 Tiện ích một chạm:** Dán nhanh văn bản (Paste), sao chép (Copy), theo dõi thanh tiến trình đọc (%) và phím tắt điều chỉnh âm lượng thiết bị nhanh.

---

### 🔍 2. Tab "Prompt" — Trợ lý Nghiên cứu AI Chuyên sâu & Trình đọc Markdown
* **📝 Trình tạo Prompt Nghiên cứu Chuyên sâu ("Make Prompt"):** Tự động tạo siêu prompt phân tích toàn diện chuẩn chuyên gia từ template chuyên dụng với tham số thay thế linh hoạt `[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]`, tự động sao chép vào Clipboard.
* **⚡ Tìm kiếm trực tiếp với Gemini ("Search Now"):** Gửi truy vấn nghiên cứu trực tiếp tới Gemini API và nhận câu trả lời phân tích đa chiều ngay trong ứng dụng.
* **📑 Kết xuất Markdown chuẩn mực (Markwon 4.6.2):** Hiển thị kết quả tìm kiếm với chuẩn định dạng Markdown chuyên nghiệp: các cấp tiêu đề (H1, H2, H3), in đậm/in nghiêng, danh sách gạch đầu dòng, khối trích dẫn và mã code rõ ràng.
* **🚀 Thanh tác vụ kết quả đa năng (3-Button Action Toolbar):**
  - **Tóm tắt:** Chuyển nội dung kết quả sang tab Read và kích hoạt luồng tóm tắt văn bản bằng AI ngay lập tức.
  - **Read (Đọc ngay):** Chuyển tiếp toàn bộ nội dung sang tab Read và tự động phát giọng đọc Karaoke TTS ngay lập tức.
  - **Show (Toàn màn hình):** Mở trình đọc văn bản toàn màn hình chuyên dụng giúp tập trung tối đa mà không bị phân tâm.
* **📐 Thiết kế chống tràn chữ & Cân đối hoàn hảo:** Cấu trúc nút Material 3 tỷ lệ đồng đều với `autoSizeTextType="uniform"`, `minWidth="0dp"`, `zero insets` hiển thị trọn vẹn văn bản trên mọi tỉ lệ màn hình.
* **🛡️ Chống lướt nhầm tab (Tab Swipe Lock):** Khóa tính năng vuốt ngang chuyển tab trên ViewPager2 nhằm tối ưu thao tác cuộn và chọn văn bản mượt mà, chuyển tab chủ động qua thanh TabLayout.

---

### 🖥️ 3. Màn hình Trình đọc Toàn màn hình (Fullscreen Reader - "Show")
* **📱 Thiết kế tràn viền Material 3 Edge-to-Edge:** Tối ưu hóa không gian hiển thị tối đa, tự động tính toán inset theo thanh trạng thái (Status bar) và thanh điều hướng (Navigation bar).
* **📜 Thanh cuộn dọc trực quan (Vertical Scrollbar):** Hiển thị thanh cuộn rõ ràng giúp theo dõi chính xác vị trí đọc trong các bài nghiên cứu dài.
* **🎯 Điều hướng chuẩn xác:** Hỗ trợ phím quay lại trên thanh Toolbar và nút Back hệ thống (đã tối ưu tắt thao tác vuốt mép màn hình vô tình gây đóng ứng dụng).
* **📋 Sao chép nội dung nhanh:** Nút sao chép toàn bộ nội dung tài liệu tiện lợi tích hợp sẵn trên thanh Toolbar.

---

### 🧠 4. Hệ thống Xoay tua API Keys & Models (Smart Model-First Rotation)
* **Chiến lược Model-First, Key-Second:** Thử nghiệm danh sách API Keys trên model có độ ưu tiên cao nhất trước khi tự động chuyển xuống model tiếp theo.
* **MD5 Hashing Quota Manager:** Quản lý hạn mức sử dụng (Quota) độc lập cho từng cặp `Model_ApiKey` bằng mã băm MD5, tự động ghi nhớ trạng thái cạn kiệt (Exhausted).
* **Cooldown & Auto-Recovery:** Tự động hoãn 300ms khi gặp lỗi giới hạn tốc độ (429/503) và tự động mở lại sau 5 phút cooldown.
* **Tự động ngắt khẩn cấp:** Dừng chu trình ngay lập tức khi phát hiện mất kết nối mạng (`UnknownHostException`, `ConnectException`) để tiết kiệm tài nguyên.
* **Quản lý & Tự phục hồi cấu hình (Self-Healing):** Giao diện thêm, xóa, đổi thứ tự ưu tiên các models AI và tự động khôi phục cấu hình an toàn khi gặp sự cố trong Cài đặt (Settings).

---

## 🛠️ Công nghệ & Kiến trúc

| Thành phần | Công nghệ / Thư viện |
|---|---|
| **Ngôn ngữ** | Kotlin 100% (Coroutines, StateFlow, LiveData, Kotlinx Serialization) |
| **Kiến trúc** | MVVM (Model-View-ViewModel) + `MainSharedViewModel` điều phối sự kiện liên Tab |
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
│   │   │   │   ├── SettingsActivity.kt         # Màn hình quản lý API Keys & Models AI
│   │   │   │   ├── TtsService.kt               # Service điều khiển TextToSpeech nền & Karaoke sync
│   │   │   │   ├── data/                       # GeminiApiClient, ModelManager, QuotaManager
│   │   │   │   ├── ui/                         # ReadFragment, PromptFragment, FullScreenReaderActivity, MainSharedViewModel
│   │   │   │   └── utils/                      # SecurePreferencesHelper, PromptTemplateHelper
│   │   │   └── res/                            # Layouts, Drawable icons, M3 Themes, Colors, Raw templates
│   │   └── test/                               # Bộ Unit Test & Integration Test tự động (>80 test cases)
│   └── build.gradle.kts                        # Cấu hình build Gradle module App
├── plans/                                      # Hồ sơ kế hoạch & theo dõi phát triển dự án
├── docs/                                       # Tài liệu kỹ thuật & kiến trúc
├── gradle/                                     # Gradle Wrapper & Version Catalog
├── build.gradle.kts                            # Root build configuration
├── settings.gradle.kts                         # Project settings & dependency resolution
└── README.md                                   # Tài liệu hướng dẫn sử dụng & phát triển dự án
```

---

## ⚙️ Cài đặt & Biên dịch (Build)

### Yêu cầu môi trường:
* **JDK:** Java 17 trở lên.
* **Android SDK:** Compile SDK 35 (Android 15), Min SDK 26 (Android 8.0 Oreo+).
* **Gradle:** Gradle 8.11.1 (sử dụng Gradle Wrapper đi kèm).

### Các lệnh thực thi chính:

1. **Cấp quyền chạy script Gradle (trên Linux/macOS):**
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
   * Nhấn **AI text** để làm sạch chuẩn hóa hoặc **Tóm tắt** để tóm tắt văn bản.
   * Nhấn **READ** để bắt đầu nghe đọc với hiệu ứng tô màu Karaoke từng từ.
3. **Tạo Prompt, Tìm kiếm & Đọc Markdown (Tab Prompt):**
   * Chuyển sang tab **Prompt**.
   * Nhập chủ đề cần tìm kiếm vào thanh tìm kiếm.
   * Nhấn **Make Prompt** để sao chép siêu prompt phân tích chuyên sâu vào Clipboard.
   * Hoặc nhấn **Search Now** để nhận kết quả phân tích định dạng Markdown trực tiếp từ Gemini.
   * Tại thanh tác vụ kết quả:
     - Nhấn **Tóm tắt** để tóm tắt nội dung liên tab.
     - Nhấn **Read** để tự động chuyển tiếp và đọc to văn bản ngay lập tức.
     - Nhấn **Show** để mở chế độ đọc toàn màn hình với thanh cuộn dọc.

---

## 👨‍💻 Tác giả & Bản quyền

Dự án được thiết kế, phát triển và duy trì bởi **Nguyễn Duy Trường (skul9x)**.  
Mọi quyền được bảo lưu.

Copyright © 2026 **Nguyễn Duy Trường**.
