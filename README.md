<<<<<<< MERGED REMOTE
# 💡 Read-Out-Loud (Karaoke & AI Text Reader)

Ứng dụng Android chuyên dụng để đọc văn bản thành giọng nói (TTS) kết hợp với hiệu ứng Karaoke đồng bộ theo từng từ. Ứng dụng tích hợp trí tuệ nhân tạo Gemini để hỗ trợ dọn dẹp văn bản rác, mang lời trải nghiệm đọc liền mạch và trực quan.

---

## 🚀 Tính năng nổi bật

### 1. 🎤 Hiệu ứng Karaoke & Auto-Scroll
- **Đồng bộ nhịp nhàng**: Chữ được tô sáng (cam nổi bật, chữ trắng, in đậm) chính xác tại vị trí giọng đọc đang phát.
- **Cuộn màn hình thông minh (Auto-scroll)**: Màn hình tự động cuộn xuống để giữ từ đang đọc luôn ở trung tâm tầm nhìn.
- **Trì hoãn tương tác 3 giây**: Nếu bạn tự vuốt đọc các đoạn khác, hệ thống sẽ chờ 3 giây sau khi bạn ngừng chạm mới tự động "bắt kịp" lại dòng đang đọc.

### 2. 🔒 Chế độ Đọc An Toàn (Read-Only)
- **Double-tap to Edit**: Tránh chạm nhầm hiện bàn phím che mất nội dung. Chạm 1 lần bị vô hiệu hóa, chỉ khi chạm 2 lần liên tiếp mới mở trình chỉnh sửa.
- **Tự động lưu**: Hệ thống tự động chuyển lại chế độ Read-only khi bàn phím đóng lại.

### 3. 🤖 Sức mạnh Gemini AI
- **Dọn dẹp văn bản**: Một chạm để gỡ bỏ các ký tự rác, mã lỗi, hoặc format hỏng trong văn bản dán từ clipboard.
- **Xử lý thông minh**: Tự động nhận diện cấu trúc văn bản để dọn dẹp mà không mất nội dung gốc.

### 4. 🎨 Giao diện Material 3 (M3)
- **Dark Mode cao cấp**: Tối ưu hóa cho việc đọc lâu không mỏi mắt.
- **Màu sắc Indigo-Dark**: Sang trọng, chuyên nghiệp và hiện đại.
- **Toolbar tiện ích**: Thay đổi âm lượng nhanh (80-85-90%) ngay trên thanh công cụ.

---

## 🛠️ Công nghệ sử dụng
- **Ngôn ngữ**: Kotlin (Android SDK 35).
- **UI Framework**: Material Design 3 (M3).
- **Audio**: Android TextToSpeech API, AudioManager.
- **AI**: Google Gemini API (Model rotation & Quota management).
- **Testing**: Robolectric (Unit tests for UI & Services).
- **Security**: EncryptedSharedPreferences (Google Security Crypto).

---

## 📂 Cấu trúc thư mục chính
- `app/src/main/java`: Mã nguồn logic (MainActivity, TtsService, Gemini API Client).
- `app/src/main/res/layout`: Giao diện chi tiết (Material 3 Cards & Progress indicators).
- `.brain/`: Hệ thống lưu trữ kiến thức dự án (Eternal Context).
- `docs/`: Specs chi tiết, Plans triển khai và Changelog.

---

## ⚙️ Hướng dẫn cài đặt
1. **Clone repository**:
   ```bash
   git clone https://github.com/skul9x/Read-Out-Loud.git
   ```
2. **Mở bằng Android Studio** (Ladybug trở lên được khuyến nghị).
3. **Cấu hình Gemini API Key**:
   - Vào Settings trong ứng dụng để dán API Key của bạn.
4. **Build & Run**:
   - Sử dụng Gradle để đồng bộ và cài đặt lên máy/máy ảo Android (API >= 26).

---

## 📖 Cách sử dụng
1. **Dán văn bản**: Copy text và ấn biểu tượng **PASTE**.
2. **AI Polish (Tùy chọn)**: Ấn **AI text** nếu muốn Gemini dọn dẹp văn bản rác.
3. **Đọc văn bản**: Ấn **READ** (Màu Indigo nổi bật) để bắt đầu.
4. **Điều hướng**: 
   - Muốn sửa text? **Chạm 2 lần (Double-tap)** vào vùng văn bản.
   - Muốn dừng? Ấn nút **STOP**.
   - Muốn chỉnh âm lượng? Ấn vào số phần trăm/icon âm loa trên thanh Toolbar.

---

## 📝 Thông tin bổ sung
- Copyright 2026 **Nguyễn Duy Trường**
- App tập trung vào trải nghiệm tối giản nhưng hiệu năng cao (Visual Excellence).

---
======= OUR CHANGES
# 💡 Read-Out-Loud (Karaoke & AI Text Reader)

<<<<<<< HEAD
Ứng dụng Android chuyên dụng để đọc văn bản thành giọng nói (TTS) kết hợp với hiệu ứng Karaoke đồng bộ theo từng từ. Ứng dụng tích hợp trí tuệ nhân tạo Gemini để hỗ trợ dọn dẹp văn bản rác, mang lời trải nghiệm đọc liền mạch và trực quan.

---

## 🚀 Tính năng nổi bật

### 1. 🎤 Hiệu ứng Karaoke & Auto-Scroll
- **Đồng bộ nhịp nhàng**: Chữ được tô sáng (cam nổi bật, chữ trắng, in đậm) chính xác tại vị trí giọng đọc đang phát.
- **Cuộn màn hình thông minh (Auto-scroll)**: Màn hình tự động cuộn xuống để giữ từ đang đọc luôn ở trung tâm tầm nhìn.
- **Trì hoãn tương tác 3 giây**: Nếu bạn tự vuốt đọc các đoạn khác, hệ thống sẽ chờ 3 giây sau khi bạn ngừng chạm mới tự động "bắt kịp" lại dòng đang đọc.

### 2. 🔒 Chế độ Đọc An Toàn (Read-Only)
- **Double-tap to Edit**: Tránh chạm nhầm hiện bàn phím che mất nội dung. Chạm 1 lần bị vô hiệu hóa, chỉ khi chạm 2 lần liên tiếp mới mở trình chỉnh sửa.
- **Tự động lưu**: Hệ thống tự động chuyển lại chế độ Read-only khi bàn phím đóng lại.

### 3. 🤖 Sức mạnh Gemini AI
- **Dọn dẹp văn bản**: Một chạm để gỡ bỏ các ký tự rác, mã lỗi, hoặc format hỏng trong văn bản dán từ clipboard.
- **Xử lý thông minh**: Tự động nhận diện cấu trúc văn bản để dọn dẹp mà không mất nội dung gốc.

### 4. 🎨 Giao diện Material 3 (M3)
- **Dark Mode cao cấp**: Tối ưu hóa cho việc đọc lâu không mỏi mắt.
- **Màu sắc Indigo-Dark**: Sang trọng, chuyên nghiệp và hiện đại.
- **Toolbar tiện ích**: Thay đổi âm lượng nhanh (80-85-90%) ngay trên thanh công cụ.
=======
Read-Out-Loud là một ứng dụng Android chuyên dụng để chuyển văn bản thành giọng nói (TTS) kết hợp với hiệu ứng Karaoke đồng bộ theo từng từ. Ứng dụng dùng Google Gemini để xử lý dọn dẹp văn bản và cung cấp trải nghiệm đọc liền mạch, trực quan.

---

## 🚀 Tính năng chính

- Hiệu ứng Karaoke: chữ được tô sáng theo từng từ đang phát, kèm auto-scroll để giữ từ đang đọc ở trung tâm.
- Chế độ Read-Only an toàn với thao tác Double-tap để chỉnh sửa.
- Tích hợp AI (Gemini) để dọn dẹp văn bản rác, format hỏng.
- Giao diện Material 3, hỗ trợ Dark Mode và các hiệu ứng mượt mà.
>>>>>>> ca11023 (Cập nhật README tiếng Việt và ẩn API keys trong tests)

---

## 🛠️ Công nghệ sử dụng
<<<<<<< HEAD
- **Ngôn ngữ**: Kotlin (Android SDK 35).
- **UI Framework**: Material Design 3 (M3).
- **Audio**: Android TextToSpeech API, AudioManager.
- **AI**: Google Gemini API (Model rotation & Quota management).
- **Testing**: Robolectric (Unit tests for UI & Services).
- **Security**: EncryptedSharedPreferences (Google Security Crypto).

---

## 📂 Cấu trúc thư mục chính
- `app/src/main/java`: Mã nguồn logic (MainActivity, TtsService, Gemini API Client).
- `app/src/main/res/layout`: Giao diện chi tiết (Material 3 Cards & Progress indicators).
- `.brain/`: Hệ thống lưu trữ kiến thức dự án (Eternal Context).
- `docs/`: Specs chi tiết, Plans triển khai và Changelog.
=======

- Ngôn ngữ: Kotlin
- Android SDK: mục tiêu Android 13+ (compileSdk/targetSdk trong Gradle)
- UI: Material Design 3 (M3)
- Audio: Android TextToSpeech API
- AI: Google Gemini API (client và logic quản lý model, quota)
- Bảo mật: EncryptedSharedPreferences (Security Crypto)
- Testing: Robolectric
>>>>>>> ca11023 (Cập nhật README tiếng Việt và ẩn API keys trong tests)

---

## ⚙️ Hướng dẫn cài đặt
<<<<<<< HEAD
1. **Clone repository**:
   ```bash
   git clone https://github.com/skul9x/Read-Out-Loud.git
   ```
2. **Mở bằng Android Studio** (Ladybug trở lên được khuyến nghị).
3. **Cấu hình Gemini API Key**:
   - Vào Settings trong ứng dụng để dán API Key của bạn.
4. **Build & Run**:
   - Sử dụng Gradle để đồng bộ và cài đặt lên máy/máy ảo Android (API >= 26).

---

## 📖 Cách sử dụng
1. **Dán văn bản**: Copy text và ấn biểu tượng **PASTE**.
2. **AI Polish (Tùy chọn)**: Ấn **AI text** nếu muốn Gemini dọn dẹp văn bản rác.
3. **Đọc văn bản**: Ấn **READ** (Màu Indigo nổi bật) để bắt đầu.
4. **Điều hướng**: 
   - Muốn sửa text? **Chạm 2 lần (Double-tap)** vào vùng văn bản.
   - Muốn dừng? Ấn nút **STOP**.
   - Muốn chỉnh âm lượng? Ấn vào số phần trăm/icon âm loa trên thanh Toolbar.
=======

Yêu cầu môi trường:
- Java JDK 11+
- Android SDK + build-tools tương thích
- Gradle wrapper (đã kèm trong repo)

Cài đặt và build (CLI):

1. Clone repository:

   git clone https://github.com/skul9x/Read-Out-Loud.git

2. Di chuyển vào thư mục project:

   cd Read-Out-Loud

3. Đồng bộ Gradle và build debug APK:

   ./gradlew assembleDebug

4. APK đầu ra (debug):

   app/build/outputs/apk/debug/app-debug.apk

Bạn có thể mở project bằng Android Studio (phiên bản Ladybug trở lên được khuyến nghị) để chạy trực tiếp trên thiết bị hoặc AVD.

---

## 🔐 Cấu hình API Keys

- Ứng dụng hỗ trợ cấu hình API Key cho Gemini/Google trong phần Settings.
- Lưu ý: Tuyệt đối không commit các API key thật lên GitHub. Trong repository này, mọi chuỗi giống key (ví dụ bắt đầu bằng "AIza") đã được ẩn/mask trong các file test và tài liệu trước khi push.

---

## 📂 Cấu trúc thư mục chính

- app/: mã nguồn ứng dụng Android
  - src/main/java: mã Kotlin
  - src/main/res: tài nguyên (layout, drawable,...)
  - build.gradle (module)
- docs/: tài liệu và hướng dẫn
- plans/: các kế hoạch phát triển
- .brain/: (nếu có) lưu trữ thông tin context dự án (được giữ và upload theo yêu cầu)

---

## 📖 Cách sử dụng nhanh

1. Dán (Paste) văn bản vào ứng dụng.
2. (Tùy chọn) Ấn "AI text" để Gemini dọn dẹp văn bản.
3. Bấm "READ" để bắt đầu đọc với hiệu ứng Karaoke.
4. Double-tap vào vùng văn bản để chỉnh sửa.
>>>>>>> ca11023 (Cập nhật README tiếng Việt và ẩn API keys trong tests)

---

## 📝 Thông tin bổ sung
<<<<<<< HEAD
- Copyright 2026 **Nguyễn Duy Trường**
- App tập trung vào trải nghiệm tối giản nhưng hiệu năng cao (Visual Excellence).
=======

Copyright 2026 Nguyễn Duy Trường
>>>>>>> ca11023 (Cập nhật README tiếng Việt và ẩn API keys trong tests)

---
>>>>>>> MERGED
