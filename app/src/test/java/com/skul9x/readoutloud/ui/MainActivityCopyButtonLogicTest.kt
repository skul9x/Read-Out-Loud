package com.skul9x.readoutloud.ui

import android.content.ClipboardManager
import android.content.Context
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class MainActivityCopyButtonLogicTest {

    private lateinit var context: Context
    private lateinit var clipboardManager: ClipboardManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Test
    fun testCopyButtonWithTextCopiesToClipboardAndShowsToast() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        val editText = activity.findViewById<EditText>(R.id.editText)
        val copyButton = activity.findViewById<MaterialButton>(R.id.copyTextButton)

        val testText = "Hello World"
        editText.setText(testText)

        copyButton.performClick()

        assertTrue("Clipboard should have a primary clip", clipboardManager.hasPrimaryClip())
        val primaryClip = clipboardManager.primaryClip
        assertNotNull("Primary clip should not be null", primaryClip)
        assertEquals("Copied text should match editText content", testText, primaryClip?.getItemAt(0)?.text?.toString())

        val latestToastText = ShadowToast.getTextOfLatestToast()
        assertEquals("Toast message should announce copy success", "Đã sao chép văn bản vào bộ nhớ tạm", latestToastText)
    }

    @Test
    fun testCopyButtonWithEmptyTextDoesNotCopyToClipboardAndShowsEmptyToast() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        val editText = activity.findViewById<EditText>(R.id.editText)
        val copyButton = activity.findViewById<MaterialButton>(R.id.copyTextButton)

        editText.setText("")

        copyButton.performClick()

        assertFalse("Clipboard should not have primary clip when copying empty text", clipboardManager.hasPrimaryClip())

        val latestToastText = ShadowToast.getTextOfLatestToast()
        assertEquals("Toast message should announce no text to copy", "Không có văn bản để sao chép", latestToastText)
    }

    @Test
    fun testCopyButtonWithWhitespaceTextDoesNotCopyToClipboardAndShowsEmptyToast() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        val editText = activity.findViewById<EditText>(R.id.editText)
        val copyButton = activity.findViewById<MaterialButton>(R.id.copyTextButton)

        editText.setText("   \n\t  ")

        copyButton.performClick()

        assertFalse("Clipboard should not have primary clip when copying whitespace-only text", clipboardManager.hasPrimaryClip())

        val latestToastText = ShadowToast.getTextOfLatestToast()
        assertEquals("Toast message should announce no text to copy", "Không có văn bản để sao chép", latestToastText)
    }
}
