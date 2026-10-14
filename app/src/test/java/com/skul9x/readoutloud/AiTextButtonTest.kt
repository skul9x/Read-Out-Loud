package com.skul9x.readoutloud

import android.content.Context
import android.content.Intent
import android.widget.ProgressBar
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import com.skul9x.readoutloud.ui.ReadFragment
import io.mockk.*
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
class AiTextButtonTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
        com.skul9x.readoutloud.data.ApiKeyManager.getInstance(activity).setApiKeys(emptyList())
        org.robolectric.shadows.ShadowToast.reset()
    }

    @Test
    fun `test aiTextButton click updates preference and triggers processing`() {
        // 1. Setup sample text
        activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText).setText("Sample text")

        // 2. Perform click on aiTextButton
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        // 3. Verify preference saved
        val sharedPrefs = activity.getSharedPreferences("ReadOutLoudPrefs", Context.MODE_PRIVATE)
        assertTrue("gemini_enabled should be true after click", sharedPrefs.getBoolean("gemini_enabled", false))

        // 4. Verify some status change containing "Gemini"
        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertTrue("Status should contain Gemini, but was: $statusText", statusText.contains("Gemini"))
    }

    @Test
    fun `test aiTextButton is disabled when loading`() {
        // Access private method or set loading state via reflection/internal exposing
        // In this case, we can trigger the action and check if it becomes disabled if we mock the API response
        
        // Mocking the loading state directly via reflection for quick check
        val setLoadingMethod = MainActivity::class.java.getDeclaredMethod("setLoading", Boolean::class.java)
        setLoadingMethod.isAccessible = true
        
        // Set loading to true
        setLoadingMethod.invoke(activity, true)
        
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        assertFalse("Button should be disabled during loading", aiTextButton.isEnabled)
        
        // Set loading to false
        setLoadingMethod.invoke(activity, false)
        assertTrue("Button should be enabled after loading", aiTextButton.isEnabled)
    }

    @Test
    fun `test aiTextButton with blank text shows toast and does not trigger processing`() {
        val editText = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText)
        editText.setText("   ")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        val latestToast = org.robolectric.shadows.ShadowToast.getTextOfLatestToast()
        assertEquals("Không có nội dung để dọn dẹp", latestToast)

        val sharedPrefs = activity.getSharedPreferences("ReadOutLoudPrefs", Context.MODE_PRIVATE)
        assertFalse("gemini_enabled should not be set when blank", sharedPrefs.getBoolean("gemini_enabled", false))
    }

    @Test
    fun `test aiTextButton without API key shows toast and updates status`() {
        val editText = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText)
        editText.setText("Văn bản cần làm sạch")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        val latestToast = org.robolectric.shadows.ShadowToast.getTextOfLatestToast()
        assertEquals("Chưa cấu hình Gemini API Key", latestToast)

        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertEquals("Gemini: Không có API Key", statusText)
    }

    @Test
    fun `test aiTextButton success flow updates editText with cleaned text`() {
        val readFragment = activity.supportFragmentManager.fragments
            .filterIsInstance<androidx.fragment.app.Fragment>()
            .firstNotNullOf { it as? com.skul9x.readoutloud.ui.ReadFragment }

        // Inject API Key into ApiKeyManager
        val apiKeyManager = com.skul9x.readoutloud.data.ApiKeyManager.getInstance(activity)
        apiKeyManager.setApiKeys(listOf("fake-api-key"))

        // Mock GeminiApiClient
        val mockGemini = mockk<com.skul9x.readoutloud.data.GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } returns com.skul9x.readoutloud.data.GeminiApiClient.GeminiResult.Success(
            "Văn bản đã được làm sạch hoàn toàn", "models/gemini-2.5-flash"
        )

        // Inject mock via reflection into readFragment
        val geminiField = com.skul9x.readoutloud.ui.ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)

        val editText = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText)
        editText.setText("## Header\n* Bullet point")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        org.robolectric.Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("Văn bản đã được làm sạch hoàn toàn", editText.text.toString())
        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertEquals("Gemini: Done (gemini-2.5-flash)", statusText)
    }

    @Test
    fun `test aiTextButton error flow falls back to regex cleaning`() {
        val readFragment = activity.supportFragmentManager.fragments
            .filterIsInstance<androidx.fragment.app.Fragment>()
            .firstNotNullOf { it as? com.skul9x.readoutloud.ui.ReadFragment }

        val apiKeyManager = com.skul9x.readoutloud.data.ApiKeyManager.getInstance(activity)
        apiKeyManager.setApiKeys(listOf("fake-api-key"))

        val mockGemini = mockk<com.skul9x.readoutloud.data.GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } returns com.skul9x.readoutloud.data.GeminiApiClient.GeminiResult.AllQuotaExhausted

        val geminiField = com.skul9x.readoutloud.ui.ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)

        val editText = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText)
        editText.setText("## Heading *bold* text")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        org.robolectric.Shadows.shadowOf(activity.mainLooper).idle()

        // Fallback cleans * # _ ` ~
        assertEquals(" Heading bold text", editText.text.toString())
        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertEquals("Lỗi Gemini", statusText)
    }

    @Test
    fun `test aiTextButton click stops tts and resets progress to zero`() {
        val readFragment = activity.supportFragmentManager.fragments
            .filterIsInstance<androidx.fragment.app.Fragment>()
            .firstNotNullOf { it as? ReadFragment }

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Sample text to read aloud")

        // 1. Start reading using startReading()
        val startReadingMethod = ReadFragment::class.java.getDeclaredMethod("startReading")
        startReadingMethod.isAccessible = true
        startReadingMethod.invoke(readFragment)
        Shadows.shadowOf(activity.mainLooper).idle()

        // Verify start intent was dispatched and consume it
        val startIntent = Shadows.shadowOf(activity).nextStartedService
        assertNotNull("Start service intent should be sent", startIntent)
        assertEquals(TtsService.ACTION_START, startIntent?.action)

        // Simulate reading progress update to 50%
        val progressIntent = Intent(TtsService.ACTION_PROGRESS).apply {
            putExtra(TtsService.EXTRA_PROGRESS_PERCENT, 50)
            putExtra(TtsService.EXTRA_WORD_START, 0)
            putExtra(TtsService.EXTRA_WORD_END, 6)
            setPackage(activity.packageName)
        }
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(progressIntent)
        Shadows.shadowOf(activity.mainLooper).idle()

        val progressBar = activity.findViewById<ProgressBar>(R.id.readingProgressBar)
        assertEquals(50, progressBar.progress)

        // 2. Click aiTextButton
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // 3. Verify service received ACTION_STOP and progress reset to 0%
        val stopIntent = Shadows.shadowOf(activity).nextStartedService
        assertNotNull("Stop service intent should be sent", stopIntent)
        assertEquals(TtsService.ACTION_STOP, stopIntent?.action)
        assertEquals(0, progressBar.progress)
        val percentText = activity.findViewById<TextView>(R.id.readingPercentText)
        assertEquals("0%", percentText.text.toString())
    }

    @Test
    fun `test aiTextButton processing resets scroll position to zero`() {
        val readFragment = activity.supportFragmentManager.fragments
            .filterIsInstance<androidx.fragment.app.Fragment>()
            .firstNotNullOf { it as? ReadFragment }

        val apiKeyManager = ApiKeyManager.getInstance(activity)
        apiKeyManager.setApiKeys(listOf("fake-api-key"))

        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } returns GeminiApiClient.GeminiResult.Success(
            "Cleaned text output\nLine 2\nLine 3", "models/gemini-2.5-flash"
        )

        val geminiField = ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\nLine 6\nLine 7")
        editText.scrollTo(0, 150)
        assertEquals(150, editText.scrollY)

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("Cleaned text output\nLine 2\nLine 3", editText.text.toString())
        assertEquals("Scroll Y should be reset to 0", 0, editText.scrollY)
    }

    @Test
    fun `test coroutine cancellation safe against NullPointerException on lifecycle cancel`() {
        val readFragment = activity.supportFragmentManager.fragments
            .filterIsInstance<androidx.fragment.app.Fragment>()
            .firstNotNullOf { it as? ReadFragment }

        val apiKeyManager = ApiKeyManager.getInstance(activity)
        apiKeyManager.setApiKeys(listOf("fake-api-key"))

        val mockGemini = mockk<GeminiApiClient>(relaxed = true)
        coEvery { mockGemini.cleanTextWithGemini(any()) } coAnswers {
            // Simulate lifecycle cancellation when view is destroyed
            val bindingField = ReadFragment::class.java.getDeclaredField("_binding")
            bindingField.isAccessible = true
            bindingField.set(readFragment, null)
            throw CancellationException("viewLifecycleOwner.lifecycleScope was cancelled")
        }

        val geminiField = ReadFragment::class.java.getDeclaredField("geminiApiClient")
        geminiField.isAccessible = true
        geminiField.set(readFragment, mockGemini)

        val editText = activity.findViewById<TextInputEditText>(R.id.editText)
        editText.setText("Sample text before cancellation")

        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        try {
            Shadows.shadowOf(activity.mainLooper).idle()
        } catch (e: NullPointerException) {
            fail("Should not throw NullPointerException on coroutine cancellation: ${e.message}")
        }
    }
}
