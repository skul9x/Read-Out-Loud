# 💡 BRIEF: Read-Out-Loud Gemini Upgrade

**Ngày tạo:** 2026-03-19
**Trạng thái:** Completed (v1.1.0) vớ UI/Audio Enhancements.

---

## 1. VẤN ĐỀ CẦN GIẢI QUYẾT
Người dùng dán văn bản từ web/markdown vào app thường chứa nhiều "rác" kĩ thuật. Việc xử lý bằng Regex hiện tại còn quá đơn giản và không triệt để.

## 2. GIẢI PHÁP ĐỀ XUẤT
Tích hợp **Gemini API** để thực hiện "Làm sạch văn bản thông minh".
- Sử dụng mô hình Gemini để loại bỏ markdown, link, và viết lại đoạn văn không dùng bảng biểu.
- Cơ chế quản lý API Key:
    - Có riêng một màn hình/phần Setting UI.
    - Cho phép paste danh sách nhiều API keys cùng lúc.
    - App tự động phân tách danh sách.
- Thuật toán Xoay Tua Model giống `RSS-Reader-main`:
    - Xoay vòng chính xác các model sau nếu gặp lỗi Quota/Rate Limit:
      1. `models/gemini-3-flash-preview`
      2. `models/gemini-2.5-flash`
      3. `models/gemini-2.5-flash-lite`
      4. `models/gemini-flash-latest`
      5. `models/gemini-flash-lite-latest`
- Chức năng Bật/Tắt (Toggle):
    - Mặc định là OFF.
    - Người dùng có thể On/Off chức năng sử dụng Gemini dễ dàng trên UI.

## 3. ĐỐI TƯỢNG SỬ DỤNG
- Người dùng dán văn bản từ nhiều nguồn (đặc biệt là web/markdown).

## 4. TÍNH NĂNG CHÍNH

### 🚀 MVP (Hoàn thành):
- [x] Màn hình Settings nhập danh sách API Key.
- [x] Lưu API Keys an toàn và phân tách chuỗi tự động.
- [x] Switch (Bật/Tắt) chức năng Gemini ở màn hình chính (Mặc định: OFF).
- [x] Gemini API Client với logic xoay tua model như `RSS-Reader-main`.
- [x] Prompt tối ưu: Không markdown, không link, đặc biệt KHÔNG DÙNG BẢNG BIỂU (tables).
- [x] Âm lượng thông minh 80-85-90% & Chọn giọng đọc VN.


## 5. BƯỚC TIẾP THEO
Lên kế hoạch chi tiết (`/plan`) và chuyển sang thiết kế (`/design`) hoặc viết code (`/code`).
