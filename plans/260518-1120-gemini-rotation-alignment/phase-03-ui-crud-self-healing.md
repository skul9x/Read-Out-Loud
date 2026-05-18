# Settings UI Model CRUD & Self-Healing
Status: ✅ Completed
Dependencies: Phase 02

## Objective
Cung cấp giao diện người dùng cấu hình mô hình động (Dynamic Model Management UI) hoàn chỉnh với đầy đủ chức năng CRUD (Thêm, Sửa, Xóa) và thiết lập cơ chế Tự Phục Hồi (Self-Healing) khi danh sách bị trống để tránh làm hỏng ứng dụng.

## Requirements
### Functional
1. **Hoàn thiện CRUD cho Mô hình trên UI**:
   - **Thêm mới (Add Model)**: Cung cấp ô nhập liệu (EditText trong Dialog) cho phép người dùng tự gõ chuỗi định danh mô hình (ví dụ: `models/gemini-2.5-pro`). Nút "Thêm" sẽ xuất hiện ở giao diện quản lý mô hình của Settings.
   - **Xóa mô hình (Delete)**: Thêm biểu tượng Xóa (Trash Icon) bên cạnh mỗi mô hình trong item RecyclerView để người dùng có thể xóa hẳn mô hình đó khỏi danh sách xoay tua.
   - **Chỉnh sửa mô hình (Edit)**: Khi click vào hoặc giữ lâu một mô hình, hiển thị Dialog cho phép chỉnh sửa lại chuỗi tên mô hình để sửa các lỗi chính tả nếu gõ sai.
2. **Cơ chế Tự Phục Hồi (Self-Healing Mechanism)**:
   - Nếu danh sách mô hình lưu trữ bị rỗng (do người dùng xóa hết) hoặc khi ứng dụng khởi chạy lần đầu tiên: hệ thống **ngay lập tức tự động khôi phục lại danh sách mô hình mặc định (`DEFAULT_MODELS`)**.
   - Nếu danh sách tất cả các mô hình bị tắt (disable): hệ thống tự động kích hoạt lại (enable) toàn bộ mô hình mặc định hoặc mô hình ưu tiên cao nhất, đảm bảo luôn có ít nhất một mô hình hoạt động để phục vụ cho các request.

### Non-Functional
- Giao diện thiết kế theo chuẩn Material Design 3, các nút và icon có khoảng cách bấm (touch target) tối thiểu 48dp.
- Quá trình tự phục hồi hoạt động mượt mà và lưu trữ bền vững ngay xuống SharedPreferences.

## Implementation Steps
1. [x] **Cập nhật `ModelManager.kt`**:
   - Tích hợp logic tự phục hồi trong `getModelItems()` và `getModels()`: Nếu danh sách từ Preferences rỗng, tự nạp lại mặc định và lưu.
   - Nếu danh sách models sau khi filter `isEnabled` là rỗng, tự động bật lại danh sách mặc định để tránh ứng dụng bị lỗi không có model nào.
2. [x] **Sửa đổi layout và UI**:
   - Cập nhật `item_model.xml` để bổ sung nút xóa (ImageButton với icon rác).
   - Cập nhật `activity_settings.xml` để thêm nút "Add Model" và nút "Reset to Default".
   - Cập nhật `SettingsActivity.kt` để xử lý sự kiện click của các nút mới, hiển thị AlertDialog nhập chuỗi tên mô hình khi Thêm / Sửa.
3. [x] **Cập nhật `ModelAdapter.kt`**:
   - Xử lý thêm callback xóa mô hình (`onDelete`) và cập nhật trạng thái UI tương ứng.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/data/ModelManager.kt` - Logic tự phục hồi
- `app/src/main/res/layout/activity_settings.xml` - Bổ sung các nút điều khiển
- `app/src/main/res/layout/item_model.xml` - Thêm nút xóa
- `app/src/main/java/com/skul9x/readoutloud/ui/SettingsActivity.kt` - Logic thêm, sửa, xóa mô hình bằng Dialog
- `app/src/main/java/com/skul9x/readoutloud/ui/ModelAdapter.kt` - Thêm callback sự kiện và biểu tượng xóa
- `app/src/test/java/com/skul9x/readoutloud/ui/Phase03UiCrudSelfHealingTest.kt` - File test kiểm chứng

## Test Criteria
- [x] Chạy lệnh `./gradlew test --tests "com.skul9x.readoutloud.ui.Phase03UiCrudSelfHealingTest"` đạt trạng thái thành công 100% (Green).
- [x] Test case `testSelfHealingWhenModelsListIsSavedEmpty` xác nhận danh sách tự phục hồi khi rỗng.
- [x] Test case `testSelfHealingWhenAllModelsAreDisabled` xác minh việc tự bật lại mô hình khi tất cả bị tắt.
- [x] Test case `testModelCrudOperations` kiểm chứng thêm và xóa hoạt động trơn tru.

---
Next Phase: [phase-04-testing-verification.md](file:///home/skul9x/Desktop/Test_code/Read-Out-Loud-main/plans/260518-1120-gemini-rotation-alignment/phase-04-testing-verification.md)
