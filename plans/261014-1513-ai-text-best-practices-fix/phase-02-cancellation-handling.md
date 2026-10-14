# Phase 02: Xử lý Coroutine Cancellation chuẩn trong Fragment

Status: ✅ Completed
Dependencies: Phase 01

## Objective
Áp dụng chuẩn Structured Concurrency trong Kotlin Coroutines: Không nuốt `CancellationException` và bảo vệ chống crash `NullPointerException` đối với `_binding` khi Fragment View bị destroy trong lúc API đang chạy nền.

## Changes
File: `app/src/main/java/com/skul9x/readoutloud/ui/ReadFragment.kt`

1. **Cập nhật khối catch trong `processWithAI`:**
   ```kotlin
   } catch (e: Exception) {
       if (e is kotlinx.coroutines.CancellationException) throw e
       if (_binding == null) return@launch
       setLoading(false)
       updateStatus("Lỗi hệ thống")
       binding.editText.setText(text.replace(Regex("[*#_`~]"), ""))
       binding.editText.scrollTo(0, 0)
   }
   ```

2. **Cập nhật khối catch trong `processSummarizeWithAI`:**
   ```kotlin
   } catch (e: Exception) {
       if (e is kotlinx.coroutines.CancellationException) throw e
       if (_binding == null) return@launch
       setLoading(false)
       updateStatus("Lỗi hệ thống")
   }
   ```

3. **Bảo vệ an toàn `withContext(Dispatchers.Main)`:**
   Trước khi truy cập `binding.editText` trong luồng Main, kiểm tra `if (_binding == null) return@withContext` để đảm bảo 100% không phát sinh lỗi View null.
