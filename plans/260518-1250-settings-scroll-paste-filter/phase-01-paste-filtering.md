# Phase 01: Lọc Bỏ "---" Khi Dán Văn Bản Ở Màn Hình Chính

## 🎯 Mục tiêu
Đảm bảo rằng khi người dùng thực hiện thao tác dán (paste) nội dung từ Clipboard vào ô nhập liệu chính ở màn hình `MainActivity`, tất cả các cụm từ `"---"` sẽ được loại bỏ hoàn toàn trước khi hiển thị lên giao diện người dùng. Điều này giúp dọn dẹp các ký tự phân đoạn dư thừa thường gặp khi sao chép từ sách hoặc tài liệu Markdown.

---

## 🛠️ Các file cần chỉnh sửa & tạo mới

### 1. File sửa đổi: `app/src/main/java/com/skul9x/readoutloud/MainActivity.kt`
*   **Vị trí sửa đổi:** Hàm `pasteFromClipboard()` (khoảng dòng 293).
*   **Logic cần thay đổi:**
    *   Hiện tại:
        ```kotlin
        val plainText = rawText.replace(Regex("[*#_`~]"), "")
        ```
    *   Cần sửa thành:
        ```kotlin
        val plainText = rawText.replace("---", "").replace(Regex("[*#_`~]"), "")
        ```

### 2. File tạo mới: `app/src/test/java/com/skul9x/readoutloud/MainActivityPasteFilteringTest.kt`
*   **Mục đích:** Kiểm thử logic dán văn bản giả lập và đảm bảo cụm từ `"---"` được loại bỏ chính xác.
*   **Công nghệ sử dụng:** Robolectric Test Runner, JUnit 4.

---

## 💻 Mã nguồn tệp kiểm thử chi tiết (`MainActivityPasteFilteringTest.kt`)

Chúng ta sẽ tạo một tệp kiểm thử mới để kiểm tra trực tiếp hành động dán văn bản trên `MainActivity` thông qua Robolectric:

```kotlin
package com.skul9x.readoutloud

import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MainActivityPasteFilteringTest {

    private lateinit var context: Context
    private lateinit var clipboardManager: ClipboardManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Test
    fun testPasteFilteringRemovesDashesAndMarkdown() {
        // 1. Khởi chạy MainActivity trong môi trường giả lập Robolectric
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        // 2. Chuẩn bị nội dung clipboard có chứa cả "---" và các ký tự Markdown khác
        val rawInputText = "Chào mừng bạn!---\nĐây là dòng thứ 2 *in đậm* và _in nghiêng_.\n---\nChúc một ngày tốt lành!~"
        val clip = ClipData.newPlainText("text", rawInputText)
        clipboardManager.setPrimaryClip(clip)

        // 3. Thực hiện kích hoạt sự kiện click vào nút Paste (pasteCard)
        val pasteCard = activity.findViewById<android.view.View>(R.id.pasteCard)
        pasteCard.performClick()

        // 4. Lấy kết quả text hiển thị trên editText của MainActivity
        val editText = activity.findViewById<android.widget.EditText>(R.id.editText)
        val actualResult = editText.text.toString()

        // 5. Kết quả mong đợi: Đã loại bỏ sạch "---" và các ký tự [*#_`~]
        val expectedResult = "Chào mừng bạn!\nĐây là dòng thứ 2 in đậm và in nghiêng.\n\nChúc một ngày tốt lành!"
        
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun testPasteWithoutDashesRemainsIntact() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        val rawInputText = "Hello World! Không có ký tự đặc biệt nào cả."
        val clip = ClipData.newPlainText("text", rawInputText)
        clipboardManager.setPrimaryClip(clip)

        val pasteCard = activity.findViewById<android.view.View>(R.id.pasteCard)
        pasteCard.performClick()

        val editText = activity.findViewById<android.widget.EditText>(R.id.editText)
        val actualResult = editText.text.toString()

        assertEquals(rawInputText, actualResult)
    }
}
```

---

## 📋 Các bước thực hiện cụ thể
1.  **Bước 1:** Tạo file test `app/src/test/java/com/skul9x/readoutloud/MainActivityPasteFilteringTest.kt` với mã nguồn như trên. [Đã hoàn thành] ✅
2.  **Bước 2:** Chạy thử bộ test (sẽ lỗi ở test case 1 vì chưa sửa code chính). [Đã hoàn thành] ✅
3.  **Bước 3:** Chỉnh sửa file `MainActivity.kt` ở hàm `pasteFromClipboard()` để tích hợp thêm `.replace("---", "")`. [Đã hoàn thành] ✅
4.  **Bước 4:** Chạy lại bộ test để đảm bảo tất cả test cases đều chuyển sang màu xanh lá (Passed) ✅. [Đã hoàn thành] ✅

---
[Xem tiếp Phase 02: Nâng cấp UI Settings](file:///home/skul9x/Desktop/Test_code/Read-Out-Loud-main/plans/260518-1250-settings-scroll-paste-filter/phase-02-settings-ui-upgrade.md)
