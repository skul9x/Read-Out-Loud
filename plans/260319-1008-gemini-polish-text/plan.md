# Plan: Polish Text with Gemini
Created: 2026-03-19T10:08:00
Status: 🟡 In Progress

## Overview
Tách tính năng gọi Gemini AI ra khỏi luồng Paste mặc định. Thêm nút "Polish Text" riêng biệt. Khi bấm, hiển thị màn hình loading overlay ngăn thao tác, gửi đoạn văn bản hiện tại (bất kể vừa nhập hay vừa dán) lên Gemini để làm sạch. Sau khi có kết quả, tự động thay thế văn bản trên màn hình và tắt overlay. Nút Polish Text chỉ sáng lên khi Switch Gemini đang BẬT.

## Tech Stack
- Frontend: Android XML Layout (Material Components)
- Backend Logic: Kotlin Coroutines, OkHttp3
- Security: EncryptedSharedPreferences

## Phases

| Phase | Name | Status | Progress |
|-------|------|--------|----------|
| 01 | UI Updates (Button & Overlay) | ✅ Done | 100% |
| 02 | Logic Integration (MainActivity) | ✅ Done | 100% |
| 03 | UI Fixes & Settings Enhancements | ✅ Done | 100% |
| 04 | Testing & Refinement | 🟡 Verifying | 80% |

## Requirements
### Functional
- [ ] Thêm nút "Polish Text" bên cạnh Gemini Switch hoặc khu vực Paste/Read.
- [ ] Nút Polish Text phải bị vô hiệu hóa (hoặc đổi màu/mờ đi) khi Switch Gemini tắt.
- [ ] Xóa logic gọi AI tự động khỏi nút Paste. Nút Paste giờ chỉ thực hiện lọc cơ bản (Local Regex) hoặc dán thuần túy.
- [ ] Thêm thành phần Giao diện Overlay (có chứa CircularProgressIndicator và text "Đang xử lý...") phủ lên toàn bộ màn hình hoặc khu vực text.
- [ ] Khi bấm Polish Text: Bật Overlay chặn click -> Gọi Gemini -> Đợi kết quả -> Thay thế Text -> Tắt Overlay.

### Non-Functional
- [ ] Trải nghiệm mượt mà, không giật lag UI khi gọi API.
- [ ] Người dùng có thể hiểu rõ tiến trình đang diễn ra (nhờ Overlay).

## Quick Commands
- Bắt đầu Phase 1: `/code phase-01`
- Kiểm tra tiến độ: `/next`
- Xem trực quan UI: `/visualize`
