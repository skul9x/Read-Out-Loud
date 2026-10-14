package com.skul9x.readoutloud.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.widget.ProgressBar
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.TtsService
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.GeminiApiClient
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class Phase01StopTtsAndScrollResetTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity
    private lateinit var readFragment: ReadFragment

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
        ApiKeyManager.getInstance(activity).setApiKeys(emptyList())

        readFragment = activity.supportFragmentManager.fragments
            .filterIsInstance<androidx.fragment.app.Fragment>()
            .firstNotNullOf { it as? ReadFragment }
    }

    private fun simulateActiveReadingAndHighlight(editText: TextInputEditText) {
        editText.setText("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\nLine 6\nLine 7")
        val intent = Intent(TtsService.ACTION_PROGRESS).apply {
            putExtra(TtsService.EXTRA_PROGRESS_PERCENT, 50)
            putExtra(TtsService.EXTRA_WORD_START, 0)
            putExtra(TtsService.EXTRA_WORD_END, 6)
            setPackage(activity.packageName)
        }
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(intent)
        Shadows.shadowOf(activity.mainLooper).idle()

        // Verify highlight exists before stopping
        val spannable = editText.text as Spannable
        val spans = spannable.getSpans(0, 6, BackgroundColorSpan::class.java)
        assertTrue("Highlight span should be present before stopping", spans.isNotEmpty())
    }

    @Test
    fun testAiTextButtonClickStopsTtsAndClearsHighlights() {
        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        simulateActiveReadingAndHighlight(editText)

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // 1. Verify TtsService stop intent was dispatched
        val startedIntent = Shadows.shadowOf(activity).nextStartedService
        assertNotNull("Stop service intent should be sent", startedIntent)
        assertEquals(TtsService.ACTION_STOP, startedIntent?.action)

        // 2. Verify highlight was cleared
        val spannable = editText.text as Spannable
        val remainingSpans = spannable.getSpans(0, editText.length(), BackgroundColorSpan::class.java)
        assertTrue("Highlight spans should be cleared after stopReading()", remainingSpans.isEmpty())

        // 3. Verify reading progress reset to 0%
        val progressBar = activity.findViewById<ProgressBar>(R.id.readingProgressBar)
        assertEquals(0, progressBar.progress)
    }

    @Test
    fun testSummarizeButtonClickStopsTtsAndClearsHighlights() {
        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        simulateActiveReadingAndHighlight(editText)

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        summarizeButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // 1. Verify TtsService stop intent was dispatched
        val startedIntent = Shadows.shadowOf(activity).nextStartedService
        assertNotNull("Stop service intent should be sent", startedIntent)
        assertEquals(TtsService.ACTION_STOP, startedIntent?.action)

        // 2. Verify highlight was cleared
        val spannable = editText.text as Spannable
        val remainingSpans = spannable.getSpans(0, editText.length(), BackgroundColorSpan::class.java)
        assertTrue("Highlight spans should be cleared after stopReading()", remainingSpans.isEmpty())

        // 3. Verify reading progress reset to 0%
        val progressBar = activity.findViewById<ProgressBar>(R.id.readingProgressBar)
        assertEquals(0, progressBar.progress)
    }

    @Test
    fun testProcessWithAiSuccessResetsScrollToTop() {
        ApiKeyManager.getInstance(activity).setApiKeys(listOf("fake-key"))

        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } returns GeminiApiClient.GeminiResult.Success(
            "Cleaned multi-line text\nSecond line\nThird line", "models/gemini-2.5-flash"
        )

        val geminiField = ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Uncleaned original text\nLine 2\nLine 3\nLine 4\nLine 5")
        editText.scrollTo(0, 150)
        assertEquals("Initial scrollY should be 150", 150, editText.scrollY)

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("Cleaned multi-line text\nSecond line\nThird line", editText.text.toString())
        assertEquals("Scroll position Y should be reset to 0 on AI success", 0, editText.scrollY)
        assertEquals("Scroll position X should be 0", 0, editText.scrollX)
    }

    @Test
    fun testProcessWithAiFallbackResetsScrollToTop() {
        ApiKeyManager.getInstance(activity).setApiKeys(listOf("fake-key"))

        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } returns GeminiApiClient.GeminiResult.AllQuotaExhausted

        val geminiField = ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("## Header *Bold*\nLine 2\nLine 3")
        editText.scrollTo(0, 120)
        assertEquals("Initial scrollY should be 120", 120, editText.scrollY)

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals(" Header Bold\nLine 2\nLine 3", editText.text.toString())
        assertEquals("Scroll position Y should be reset to 0 on AI fallback", 0, editText.scrollY)
    }

    @Test
    fun testProcessSummarizeWithAiSuccessResetsScrollToTop() {
        ApiKeyManager.getInstance(activity).setApiKeys(listOf("fake-key"))

        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.summarizeTextWithGemini(any()) } returns GeminiApiClient.GeminiResult.Success(
            "Summary result text", "models/gemini-2.5-flash"
        )

        val geminiField = ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Long original text to be summarized\nLine 2\nLine 3")
        editText.scrollTo(0, 250)
        assertEquals("Initial scrollY should be 250", 250, editText.scrollY)

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        summarizeButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("Summary result text", editText.text.toString())
        assertEquals("Scroll position Y should be reset to 0 on summarize success", 0, editText.scrollY)
        assertEquals("Scroll position X should be 0", 0, editText.scrollX)
    }
}
