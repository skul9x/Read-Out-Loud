package com.skul9x.readoutloud.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.view.View
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.TtsService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class ReadFragmentTest {

    private lateinit var activity: MainActivity
    private lateinit var clipboardManager: ClipboardManager

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Test
    fun testReadFragmentViewsExist() {
        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        val pasteCard = activity.findViewById<MaterialCardView>(R.id.pasteCard)
        val readCard = activity.findViewById<MaterialCardView>(R.id.readCard)
        val stopCard = activity.findViewById<MaterialCardView>(R.id.stopCard)
        val copyButton = activity.findViewById<MaterialButton>(R.id.copyTextButton)
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)

        assertNotNull("editText should exist in ReadFragment", editText)
        assertNotNull("pasteCard should exist in ReadFragment", pasteCard)
        assertNotNull("readCard should exist in ReadFragment", readCard)
        assertNotNull("stopCard should exist in ReadFragment", stopCard)
        assertNotNull("copyButton should exist in ReadFragment", copyButton)
        assertNotNull("aiTextButton should exist in ReadFragment", aiTextButton)
        assertNotNull("summarizeButton should exist in ReadFragment", summarizeButton)
    }

    @Test
    fun testPasteAndCopyInReadFragment() {
        val clip = ClipData.newPlainText("test", "Test---*Content*")
        clipboardManager.setPrimaryClip(clip)

        val pasteCard = activity.findViewById<MaterialCardView>(R.id.pasteCard)
        pasteCard.performClick()

        val editText = activity.findViewById<EditText>(R.id.editText)
        assertEquals("TestContent", editText.text.toString())

        val copyButton = activity.findViewById<MaterialButton>(R.id.copyTextButton)
        copyButton.performClick()

        val latestToast = ShadowToast.getTextOfLatestToast()
        assertEquals("Đã sao chép văn bản vào bộ nhớ tạm", latestToast)
    }

    @Test
    fun testProgressAndHighlightInReadFragment() {
        val testText = "Quick Brown Fox"
        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText(testText)

        val intent = Intent(TtsService.ACTION_PROGRESS).apply {
            putExtra(TtsService.EXTRA_PROGRESS_PERCENT, 30)
            putExtra(TtsService.EXTRA_WORD_START, 6)
            putExtra(TtsService.EXTRA_WORD_END, 11)
            setPackage(activity.packageName)
        }
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(intent)
        Shadows.shadowOf(activity.mainLooper).idle()

        val spannable = editText.text as Spannable
        val spans = spannable.getSpans(6, 11, BackgroundColorSpan::class.java)
        assertTrue("Spans should be present", spans.isNotEmpty())
        assertEquals(Color.parseColor("#FF9800"), spans[0].backgroundColor)
    }
}
