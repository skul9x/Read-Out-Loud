# Phase 02: TTS Synchronization Logic
Status: ⬜ Pending
Dependencies: phase-01

## Objective
Kết nối `TtsService` để gửi liên tục Data vị trí chữ đang đọc (từ ký tự X đến ký tự Y) lên `MainActivity/ViewModel` một cách chính xác mà không giật lag. 

## Requirements
### Functional
- [ ] Bắt callback `onRangeStart` trong `TtsService`.
- [ ] Tính toán lại `AbsoluteOffset` (vì hệ thống TTS xé nhỏ text thành nhiều chunk để đọc).
- [ ] Gửi Broadcast hoặc Flow State mang data `{ start: Int, end: Int, text: String }`.
- [ ] Bắt tín hiệu trong UI để lấy tọa độ chuẩn bị tô màu.

### Quality / Test Criteria
- [ ] Test Auto: Chạy text dài xem Index (start/end) cập nhật đúng ký tự không.
- [ ] Test Behavior: Cắt text thành nhiều mẩu qua TTS, logic `AbsoluteOffset` phải tiến đều, tuyệt đối không bị đếm lại từ đầu hay khựng giữa chừng.
