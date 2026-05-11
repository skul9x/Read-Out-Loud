# 💡 Read-Out-Loud (Karaoke & AI Text Reader)

Read-Out-Loud là một ứng dụng Android chuyên dụng để chuyển văn bản thành giọng nói (TTS) kết hợp với hiệu ứng Karaoke đồng bộ theo từng từ. Ứng dụng sử dụng Google Gemini để xử lý dọn dẹp văn bản và cung cấp trải nghiệm đọc liền mạch, trực quan.

---

## 🚀 Tính năng chính

- Hiệu ứng Karaoke: chữ được tô sáng theo từng từ đang phát, kèm auto-scroll để giữ từ đang đọc ở trung tâm.
- Chế độ Read-Only an toàn với thao tác Double-tap để chỉnh sửa.
- Tích hợp AI (Gemini) để dọn dẹp văn bản rác, format hỏng.
- Giao diện Material 3, hỗ trợ Dark Mode và các hiệu ứng mượt mà.

---

## 🛠️ Công nghệ sử dụng

- Ngôn ngữ: Kotlin
- Android SDK: mục tiêu Android 13+ (compileSdk/targetSdk trong Gradle)
- UI: Material Design 3 (M3)
- Audio: Android TextToSpeech API
- AI: Google Gemini API (client và logic quản lý model, quota)
- Bảo mật: EncryptedSharedPreferences (Security Crypto)
- Testing: Robolectric

---

## 📂 Cấu trúc thư mục chính

- app/: mã nguồn ứng dụng Android
  - src/main/java: mã Kotlin
  - src/main/res: tài nguyên (layout, drawable,...)
- docs/: tài liệu và hướng dẫn
- plans/: các kế hoạch phát triển
- .brain/: (nếu có) lưu trữ thông tin context dự án (được giữ và upload theo yêu cầu)

---

## ⚙️ Hướng dẫn cài đặt

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

## 📖 Cách sử dụng nhanh

1. Dán (Paste) văn bản vào ứng dụng.
2. (Tùy chọn) Ấn "AI text" để Gemini dọn dẹp văn bản.
3. Bấm "READ" để bắt đầu đọc với hiệu ứng Karaoke.
4. Double-tap vào vùng văn bản để chỉnh sửa.

---

## 📝 Thông tin bổ sung

Copyright 2026 Nguyễn Duy Trường

---
