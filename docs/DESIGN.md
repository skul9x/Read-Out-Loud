# 🎨 DESIGN: Read-Out-Loud Gemini Integration

Ngày tạo: 2026-03-19
Dựa trên: [SPECS.md](file:///home/skul9x/Desktop/Test_code/Read-Out-Loud-master/docs/specs/gemini_integration_spec.md)

---

## 1. Cách Lưu Thông Tin (Database)

Dự án này không cần Database phức tạp như SQL, chúng ta chỉ cần lưu cài đặt cục bộ trên máy.

📦 **SƠ ĐỒ LƯU TRỮ:**

┌─────────────────────────────────────────────────────────────┐
│  🔐 API_KEYS_SECURE (Kho lưu trữ an toàn)                   │
│  ├── Chỉ lưu danh sách các Gemini API Keys của anh.        │
│  └── Được mã hóa chuẩn ngân hàng (EncryptedSharedPreferences)│
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  ⚙️ APP_PREFERENCES (Cài đặt chung)                        │
│  ├── Trạng thái Toggle (Đang BẬT hay TẮT Gemini?)          │
│  └── Các cài đặt cũ (Giọng đọc đã chọn...)                 │
└─────────────────────────────────────────────────────────────┘

## 2. Danh Sách Màn Hình

| # | Tên | Mục đích | Có gì trên đó? |
|---|-----|----------|-------------|
| 1 | 🏠 Trang Chủ | Dán văn bản và nghe đọc | Dấu tick/switch "Dùng Gemini làm sạch", Nút Cài đặt (Bánh răng) |
| 2 | ⚙️ Cài đặt API | Nhập khoá API Gemini | Một ô trống siêu to để anh dán 1 lúc nhiều Key, Nút "Lưu" |

## 3. Luồng Hoạt Động (User Journey)

🚶 **HÀNH TRÌNH: Dọn dẹp rác kỹ thuật khi dán văn bản**

1️⃣ Mở app → Bấm bật "Dùng Gemini làm sạch văn bản".
2️⃣ Bấm nút "Dán" từ Clipboard.
3️⃣ App kiểm tra: Đã có API Key chưa?
    - *NẾU CHƯA*: Hiện thông báo "Anh cần nhập API Key trước nhé" và mở màn hình Cài Đặt.
    - *NẾU RỒI*: Sang bước 4.
4️⃣ Hiển thị vòng xoay Loading ⏳ chờ Gemini làm phép.
5️⃣ Gemini tự động xóa sạch các dòng chữ linh tinh, link, cấu trúc bảng biểu.
6️⃣ Trả về văn bản sạch bóng, dán thẳng vào khung chữ để chuản bị đọc.

## 4. Checklist Kiểm Tra (Acceptance Criteria) & Test Cases

### Tính năng: Làm sạch văn bản với Gemini

✅ **Cơ bản:**
- [ ] Bật tắt được switch Gemini. Switch phải nhớ trạng thái ở lần bật app sau.
- [ ] Màn hình Settings lưu được chuỗi API Keys cắt nhau bằng dấu phẩy hoặc xuống dòng.
- [ ] Dán văn bản vào khi Switch đang ON thì app gọi được Gemini.

✅ **Nâng cao (Test Cases):**

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
TC-01: Happy Path (Dọn dẹp thành công)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Given: Đã bật Gemini, đã có API Key hợp lệ.
When:  Dán nội dung có chứa Markdown Table và Links.
Then:  ✓ Hiện Loading.
       ✓ Gemini trả về chữ thuần tuý, biến mất hoàn toàn bảng biểu.
       
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
TC-02: Validation (Hết Quota / API lỗi)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Given: Đã bật Gemini, có 2 API Keys nhưng Key 1 bị lỗi limit.
When:  Dán văn bản.
Then:  ✓ App nhận thấy Key 1 lỗi.
       ✓ Tự động âm thầm chuyển sang Key 2 để xử lý.
       ✓ Vẫn trả về kết quả thành công cho người dùng.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
TC-03: Edge Case (Tắt Gemini)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Given: Tắt chức năng Gemini.
When:  Dán văn bản có chứa Markdown Table.
Then:  ✓ Trả về ngay lập tức (không loading).
       ✓ Bảng biểu và markdown chỉ được lọc bằng thuật toán Regex cũ.

---

*Tạo bởi AWF 4.0 - Design Phase*
