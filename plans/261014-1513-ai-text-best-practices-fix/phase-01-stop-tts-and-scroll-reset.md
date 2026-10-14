# Phase 01: Dừng TTS & Reset Cuộn về đầu trang

Status: ✅ Completed
Dependencies: None

## Objective
Ngắt dịch vụ đọc TTS và xóa highlight ngay khi người dùng bấm nút làm sạch ("AI text") hoặc tóm tắt ("Tóm tắt"), đồng thời đưa vùng cuộn của `editText` về đầu trang `(0, 0)` khi nạp văn bản mới.

## Changes
File: `app/src/main/java/com/skul9x/readoutloud/ui/ReadFragment.kt`

1. **Thêm lệnh dừng đọc khi click nút:**
   ```kotlin
   binding.aiTextButton.setOnClickListener {
       val textToPolish = binding.editText.text?.toString() ?: ""
       if (textToPolish.isBlank()) {
           Toast.makeText(requireContext(), "Không có nội dung để dọn dẹp", Toast.LENGTH_SHORT).show()
           return@setOnClickListener
       }
       stopReading() // Dừng đọc và xóa highlight ngay lập tức
       sharedPreferences.edit().putBoolean(KEY_GEMINI_ENABLED, true).apply()
       updateStatus("Gemini AI: ON")
       processWithAI(textToPolish)
   }
   ```
   Tương tự đối với `binding.summarizeButton.setOnClickListener`.

2. **Thêm lệnh cuộn về đầu trang `(0, 0)`:**
   - Trong `processWithAI`:
     ```kotlin
     is GeminiApiClient.GeminiResult.Success -> {
         binding.editText.setText(result.text)
         binding.editText.scrollTo(0, 0)
         updateStatus("Gemini: Done (${result.model.substringAfter("/")})")
     }
     ```
   - Và trong nhánh fallback Regex:
     ```kotlin
     binding.editText.setText(text.replace(Regex("[*#_`~]"), ""))
     binding.editText.scrollTo(0, 0)
     ```
   - Tương tự trong `processSummarizeWithAI`.
