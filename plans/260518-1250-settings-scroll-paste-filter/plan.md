# Plan: Nâng Cấp Bộ Lọc Paste & Tối Ưu Hóa UI Cài Đặt (Scroll, Text Area, Sửa Lỗi Ngắt Chữ & Vùng Chạm)

Bản kế hoạch này được thiết kế nhằm nâng cao trải nghiệm người dùng khi làm việc với văn bản dán từ clipboard và sửa đổi toàn bộ các lỗi UI/UX nghiêm trọng trên giao diện Cài đặt (`SettingsActivity`) dựa trên phân tích hình ảnh thực tế từ thiết bị.

*   **Thời gian khởi tạo:** 2026-05-18 12:50
*   **Trạng thái:** 🟡 Đang chờ duyệt (Pending Approval)
*   **Tác giả:** Hà (Product Manager - Antigravity Strategy Lead)

---

## 🎯 Mục tiêu dự án

1.  **Lọc văn bản thông minh khi Paste (Phase 01):** 
    *   Khi người dùng dán (paste) văn bản vào vùng đọc chính ở màn hình `MainActivity`, bất kỳ cụm từ `"---"` nào xuất hiện trong văn bản sẽ tự động bị loại bỏ hoàn toàn trước khi hiển thị.
2.  **Khắc phục triệt để các lỗi UI/UX nghiêm trọng trên màn hình Cài đặt (Phase 02):**
    *   **🔴 Sửa lỗi biến mất ô nhập API Key:** Thay đổi chiều cao của Card chứa API Key (`MaterialCardView` chứa `apiKeyEditText`) thành chiều cao cố định **`180dp`** thay vì `0dp`/`weight="1"` để ngăn chặn việc bị co rúm thành 0px khi đo đạc layout.
    *   **🔴 Sửa lỗi biến mất nút lưu "SAVE CONFIGURATION":** Bọc toàn bộ nội dung cài đặt dưới thanh Toolbar bằng `NestedScrollView` và di chuyển nút Save vào dưới cùng vùng cuộn. Giúp người dùng cuộn nhẹ màn hình là bấm lưu dễ dàng, giải quyết lỗi nút bấm bị đẩy ra ngoài vùng nhìn thấy.
    *   **🔴 Sửa lỗi ngắt chữ Model xấu xí (Model Name Text Wrapping):** Cập nhật `item_model.xml` thiết lập TextView hiển thị tên model có chiều rộng chiếm hết vùng chứa (`match_parent`), khống chế chỉ hiển thị trên một dòng duy nhất (`maxLines="1"`, `singleLine="true"`) và có dấu ba chấm chuyên nghiệp khi quá dài (`ellipsize="end"`).
    *   **🟡 Sửa lỗi Touch Target nút tăng/giảm/xóa quá bé và sát nhau:** Thiết lập khoảng cách giãn cách ngang (horizontal margins) hợp lý giữa các nút điều hướng và nút xóa trong `item_model.xml` để người dùng không bấm nhầm, đồng thời đảm bảo vùng touch target tối thiểu luôn đạt chuẩn `48dp` của Google.
    *   **🟢 Ngăn chặn xung đột cuộn mượt mà:** Triển khai cơ chế disallow intercept touch event cho `apiKeyEditText` để người dùng cuộn nội dung bên trong Text Area mà không bị NestedScrollView đánh chặn cuộn cả màn hình.

---

## 🛠️ Công nghệ & Thành phần liên quan

*   **UI/UX:** XML Layout (`activity_settings.xml`, `item_model.xml`), `androidx.core.widget.NestedScrollView`, Material 3 Components.
*   **Logic:** `MainActivity.kt` (Xử lý sự kiện paste và lọc văn bản), `SettingsActivity.kt` (Xử lý sự kiện chạm touch interceptor), `ModelAdapter.kt` (Quản lý list model).
*   **Testing:** `Robolectric`, `JUnit 4` để viết các bài test tự động giả lập Activity, kiểm thử layout trực quan của Settings và các thuộc tính chống lỗi của Item Model mà không cần chạy máy ảo thật.

---

## 📋 Danh sách các Phases & Tiến độ

| Phase | Tên Giai Đoạn | File Kế Hoạch | Trạng Thế | Tiến Độ |
| :--- | :--- | :--- | :--- | :--- |
| **Phase 01** | Lọc bỏ `"---"` khi dán văn bản chính | [phase-01-paste-filtering.md](file:///home/skul9x/Desktop/Test_code/Read-Out-Loud-main/plans/260518-1250-settings-scroll-paste-filter/phase-01-paste-filtering.md) | ⬜ Pending | 0% |
| **Phase 02** | Nâng cấp toàn diện UI/UX Settings (Scroll, Text Area, Spacing, Wrapping Fix) | [phase-02-settings-ui-upgrade.md](file:///home/skul9x/Desktop/Test_code/Read-Out-Loud-main/plans/260518-1250-settings-scroll-paste-filter/phase-02-settings-ui-upgrade.md) | ⬜ Pending | 0% |

---

## ⚡ Các lệnh điều hướng nhanh (Quick Commands)

*   **Bắt đầu thực hiện Phase 01:** `/vietcode plans/260518-1250-settings-scroll-paste-filter/phase-01-paste-filtering.md`
*   **Bắt đầu thực hiện Phase 02:** `/vietcode plans/260518-1250-settings-scroll-paste-filter/phase-02-settings-ui-upgrade.md`
*   **Kiểm tra tiến độ tổng thể:** `/next`
*   **Lưu lại tri thức lập trình:** `/save-brain`

---

## 🛡️ Hướng dẫn Kiểm thử tự động (Testing Guideline)

Mỗi giai đoạn được đi kèm với một tệp kiểm thử tự động chi tiết sử dụng **Robolectric Test Runner**. Bạn có thể chạy kiểm thử toàn dự án bằng lệnh:
```bash
./gradlew testDebugUnitTest
```
Hoặc chạy cụ thể tệp kiểm thử của từng Phase được mô tả trong các file phase tương ứng để đảm bảo tính đúng đắn trước khi bàn giao.
