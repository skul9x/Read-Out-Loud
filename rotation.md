# 🔄 Gemini API & Model Rotation Logic (YTSummary)

Tài liệu này mô tả chi tiết thuật toán xoay tua API Key và Model trong dự án YTSummary. Đây là tài liệu kỹ thuật chuẩn dành cho AI để hiểu và tái lập logic này.

---

## 🧩 1. Kiến trúc tổng quan
Hệ thống xoay tua dựa trên 3 thành phần chính:
- **`ApiKeyManager`**: Quản lý danh sách API Key (lưu trữ mã hóa).
- **`ModelManager`**: Quản lý danh sách Model và thứ tự ưu tiên (lưu trữ tùy chỉnh).
- **`ModelQuotaManager`**: Theo dõi trạng thái Quota/Cooldown của từng cặp `(Model, Key)`.

---

## ⚙️ 2. Thuật toán Xoay tua (Model-First Rotation)

Thuật toán được thực hiện theo cấu trúc vòng lặp lồng nhau (Nested Loops) để tối ưu hóa việc sử dụng các model "xịn" hoặc model ưu tiên trước khi chuyển sang model dự phòng.

### Quy trình thực hiện:
1. **Lấy danh sách Model**: Truy xuất từ `ModelManager` (đã sắp xếp theo priority của user).
2. **Lấy danh sách Keys**: Truy xuất từ `ApiKeyManager`.
3. **Vòng lặp Model (Outer Loop)**: Duyệt qua từng Model trong danh sách.
4. **Vòng lặp Key (Inner Loop)**: Với mỗi Model, thử lần lượt từng API Key.
5. **Kiểm tra khả dụng**: Trước khi gọi API, kiểm tra cặp `(Model, Key)` có đang bị "Ban" hoặc "Cooldown" không thông qua `ModelQuotaManager`.
6. **Xử lý phản hồi (Error Handling)**:
   - **429 (Rate Limit - RPM)**: Nếu lỗi chứa "per minute" hoặc "Rate limit", đánh dấu **Cooldown (5 phút)**. Nhảy sang **Key tiếp theo** của cùng model đó.
   - **429 (Quota Exhausted - Daily)**: Nếu là lỗi vượt định mức ngày, đánh dấu **Exhausted (30 giờ)**. Lưu vào bộ nhớ máy. Nhảy sang **Key tiếp theo**.
   - **503 (Server Busy)**: Đánh dấu **Cooldown (5 phút)**. Nhảy sang **Key tiếp theo**.
   - **400/404 (Model Unavailable)**: Nếu model không được key đó hỗ trợ hoặc không tồn tại, **DỪNG thử Key trên model này** và nhảy ngay sang **Model tiếp theo** (Outer loop).
7. **Streaming Safety**: Nếu text đã bắt đầu trả về (`hasStarted = true`) mà gặp lỗi mạng, **KHÔNG xoay tua** (để tránh rác dữ liệu trên UI). Trả về lỗi cho người dùng.

---

## 📊 3. Quản lý Trạng thái (Quota Management)

`ModelQuotaManager` sử dụng cơ chế định danh cặp bằng hash: `key = "$model::SHA256(apiKey)"`.

| Trạng thái | Thời gian hiệu lực | Lưu trữ | Kịch bản kích hoạt |
| :--- | :--- | :--- | :--- |
| **Exhausted** | 30 giờ | Persistent (Disk) | Hết quota ngày (Daily Quota) |
| **Cooldown** | 5 phút | In-memory (RAM) | Bị giới hạn tốc độ (RPM) hoặc Server bận |

---

## 🛠️ 4. Cách cấu hình & Edit thứ tự (Settings)

Người dùng (hoặc AI điều khiển) có thể thay đổi hành vi xoay tua thông qua `ModelManager`:

### Edit thứ tự Model:
- **Priority**: Model có index thấp hơn (nằm trên cùng) sẽ được thử trước với tất cả các keys trước khi model bên dưới được sờ tới.
- **Thao tác**: 
    - `moveUp(index)`: Tăng mức độ ưu tiên.
    - `moveDown(index)`: Giảm mức độ ưu tiên.
    - `addModel(name)`: Thêm model mới vào cuối danh sách dự phòng.
    - `removeModel(index)`: Loại bỏ model khỏi danh sách xoay tua.

### Edit API Keys:
- Hệ thống sẽ thử Key theo thứ tự được thêm vào (`FIFO`). 
- Có thể thêm nhiều key để nhân bản quota cho cùng một model.

---

## 📝 5. Danh sách Model mặc định (Default Priority)
Nếu người dùng không cấu hình, hệ thống sẽ xoay tua theo thứ tự:
1. `models/gemini-3.1-flash-lite-preview`
2. `models/gemini-3-flash-preview`
3. `models/gemini-2.5-flash-lite`
4. `models/gemini-2.5-flash`

---

## ⚠️ Lưu ý quan trọng cho AI khác
- **Hash Key**: Khi lưu trạng thái Exhausted, không bao giờ lưu API Key nguyên bản. Luôn sử dụng hash.
- **SSE Support**: Client sử dụng Server-Sent Events (`streamGenerateContent`). Mọi logic xoay tua phải xử lý được luồng dữ liệu streaming.
- **Retry**: Chỉ retry tự động (`retryWithBackoff`) với lỗi `IOException` và khi chưa có dữ liệu nào được stream về.
