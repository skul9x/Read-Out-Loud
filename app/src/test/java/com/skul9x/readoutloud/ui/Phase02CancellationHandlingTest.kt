package com.skul9x.readoutloud.ui

import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.GeminiApiClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class Phase02CancellationHandlingTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity
    private lateinit var readFragment: ReadFragment

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
        ApiKeyManager.getInstance(activity).setApiKeys(listOf("fake-api-key"))

        readFragment = activity.supportFragmentManager.fragments
            .filterIsInstance<androidx.fragment.app.Fragment>()
            .firstNotNullOf { it as? ReadFragment }
    }

    private fun injectMockGemini(mockGemini: GeminiApiClient) {
        val geminiField = ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)
    }

    private fun setFragmentBindingNull() {
        val bindingField = ReadFragment::class.java.getDeclaredField("_binding")
        bindingField.isAccessible = true
        bindingField.set(readFragment, null)
    }

    @Test
    fun testProcessWithAiRethrowsCancellationException() {
        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } throws CancellationException("Coroutines cancelled")
        injectMockGemini(mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        val originalText = "Original content with markdown *bold* and #heading"
        editText.setText(originalText)

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()

        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertNotEquals("Status should NOT be 'Lỗi hệ thống' when cancelled", "Lỗi hệ thống", statusText)
        assertEquals("Original text should not be replaced by fallback regex when cancelled", originalText, editText.text.toString())
    }

    @Test
    fun testProcessSummarizeWithAiRethrowsCancellationException() {
        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.summarizeTextWithGemini(any()) } throws CancellationException("Summarize cancelled")
        injectMockGemini(mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Some text to summarize")

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        summarizeButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()

        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertNotEquals("Status should NOT be 'Lỗi hệ thống' on cancellation", "Lỗi hệ thống", statusText)
    }

    @Test
    fun testProcessWithAiGuardsAgainstNullBindingOnSuccess() {
        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } coAnswers {
            // Simulate fragment view destroyed while network call is in flight
            setFragmentBindingNull()
            GeminiApiClient.GeminiResult.Success("Cleaned text", "models/gemini-2.5-flash")
        }
        injectMockGemini(mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Text before destroy")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        // Should complete without throwing NullPointerException when returning to Dispatchers.Main
        Shadows.shadowOf(activity.mainLooper).idle()
        assertNull("Binding should remain null", ReadFragment::class.java.getDeclaredField("_binding").apply { isAccessible = true }.get(readFragment))
    }

    @Test
    fun testProcessWithAiGuardsAgainstNullBindingOnError() {
        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } coAnswers {
            // Simulate fragment view destroyed while network call is in flight and throws error
            setFragmentBindingNull()
            throw RuntimeException("Network connection failed")
        }
        injectMockGemini(mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Text before destroy")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        // Should catch and return@launch without throwing NullPointerException on binding.editText
        Shadows.shadowOf(activity.mainLooper).idle()
        assertNull("Binding should remain null", ReadFragment::class.java.getDeclaredField("_binding").apply { isAccessible = true }.get(readFragment))
    }

    @Test
    fun testProcessSummarizeWithAiGuardsAgainstNullBindingOnSuccess() {
        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.summarizeTextWithGemini(any()) } coAnswers {
            setFragmentBindingNull()
            GeminiApiClient.GeminiResult.Success("Summary result", "models/gemini-2.5-flash")
        }
        injectMockGemini(mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Long text to summarize")

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        summarizeButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()
        assertNull("Binding should remain null", ReadFragment::class.java.getDeclaredField("_binding").apply { isAccessible = true }.get(readFragment))
    }

    @Test
    fun testProcessSummarizeWithAiGuardsAgainstNullBindingOnError() {
        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.summarizeTextWithGemini(any()) } coAnswers {
            setFragmentBindingNull()
            throw RuntimeException("Summarize API failed")
        }
        injectMockGemini(mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Long text to summarize")

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        summarizeButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()
        assertNull("Binding should remain null", ReadFragment::class.java.getDeclaredField("_binding").apply { isAccessible = true }.get(readFragment))
    }

    @Test
    fun testNormalExceptionHandledWhenBindingActive() {
        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } throws RuntimeException("Unexpected error")
        injectMockGemini(mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("## Header *Text*")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()

        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertEquals("Status should be 'Lỗi hệ thống' on normal exceptions", "Lỗi hệ thống", statusText)
        assertEquals(" Header Text", editText.text.toString())
        assertTrue("Button should be re-enabled after error handling", aiTextButton.isEnabled)
    }
}
