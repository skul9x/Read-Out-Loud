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
