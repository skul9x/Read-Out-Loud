package com.skul9x.readoutloud.ui

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.utils.PromptTemplateHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.lang.reflect.Method

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PromptTabSearchNowTest {

    private lateinit var context: Context
    private lateinit var client: GeminiApiClient

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        client = GeminiApiClient(context)
    }

    @Test
    fun testSearchWithPromptMethodExists() {
        val hasSuspendMethod = GeminiApiClient::class.java.methods.any { it.name == "searchWithPrompt" }
        assertTrue("GeminiApiClient must have a searchWithPrompt method", hasSuspendMethod)
    }

    @Test
    fun testPromptIsNotWrappedInAdditionalSystemPrompt() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "Test topic"
        val builtPrompt = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue("Built prompt must start with the template instructions",
            builtPrompt.startsWith("Bạn hãy đóng vai một CHUYÊN GIA NGHIÊN CỨU"))
        assertTrue("Built prompt must end with the user's topic",
            builtPrompt.trimEnd().endsWith(topic))
    }

    @Test
    fun testGeminiResultSealedClassCoversAllCases() {
        val successResult = GeminiApiClient.GeminiResult.Success("test", "models/gemini-2.0-flash")
        val exhaustedResult = GeminiApiClient.GeminiResult.AllQuotaExhausted
        val noKeysResult = GeminiApiClient.GeminiResult.NoApiKeys
        val errorResult = GeminiApiClient.GeminiResult.Error("test error")

        assertEquals("test", successResult.text)
        assertTrue(exhaustedResult.getFinalText().contains("quá tải"))
        assertTrue(noKeysResult.getFinalText().contains("API Key"))
        assertTrue(errorResult.getFinalText().contains("test error"))
    }

    @Test
    fun testFullPromptConstructionForSearchNow() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "chip bán dẫn TSMC 2nm"
        val builtPrompt = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue(builtPrompt.contains("NGUYÊN TẮC TÌM KIẾM ĐA NGÔN NGỮ"))
        assertTrue(builtPrompt.contains("ĐỐI CHIẾU THÔNG TIN"))
        assertTrue(builtPrompt.contains("KIỂM CHỨNG"))
        assertTrue(builtPrompt.contains(topic))
        assertFalse(builtPrompt.contains("{THÔNG TIN/TIN TỨC/CHỦ ĐỀ TÔI MUỐN TÌM KIẾM}"))
        assertTrue("Built prompt should be substantial (>5000 chars)", builtPrompt.length > 5000)
    }

    @Test
    fun testGeminiResultModelExtraction() {
        // Verify model name can be extracted for the status badge
        val result = GeminiApiClient.GeminiResult.Success("text", "models/gemini-2.0-flash")
        val modelShortName = result.model.substringAfter("/")
        assertEquals("gemini-2.0-flash", modelShortName)
    }

    @Test
    fun testLoadingAndErrorCardsExistInLayout() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val loadingCard = activity.findViewById<MaterialCardView>(R.id.loadingCard)
        val errorCard = activity.findViewById<MaterialCardView>(R.id.errorCard)
        val retryButton = activity.findViewById<MaterialButton>(R.id.retryButton)
        val errorTitle = activity.findViewById<TextView>(R.id.errorTitle)
        val errorMessage = activity.findViewById<TextView>(R.id.errorMessage)

        assertNotNull("loadingCard must exist in layout", loadingCard)
        assertNotNull("errorCard must exist in layout", errorCard)
        assertNotNull("retryButton must exist in layout", retryButton)
        assertNotNull("errorTitle must exist in layout", errorTitle)
        assertNotNull("errorMessage must exist in layout", errorMessage)

        assertEquals(View.GONE, loadingCard.visibility)
        assertEquals(View.GONE, errorCard.visibility)
    }

    @Test
    fun testPromptFragmentShowLoadingState() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val fragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val topicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val makePromptButton = activity.findViewById<MaterialButton>(R.id.makePromptButton)
        val searchNowButton = activity.findViewById<MaterialButton>(R.id.searchNowButton)
        val loadingCard = activity.findViewById<MaterialCardView>(R.id.loadingCard)
        val emptyStateGroup = activity.findViewById<View>(R.id.emptyStateGroup)
        val promptStatusText = activity.findViewById<TextView>(R.id.promptStatusText)

        topicInput.setText("AI Future")
        fragment.showLoading()

        assertEquals(View.GONE, emptyStateGroup.visibility)
        assertEquals(View.VISIBLE, loadingCard.visibility)
        assertFalse(topicInput.isEnabled)
        assertFalse(makePromptButton.isEnabled)
        assertFalse(searchNowButton.isEnabled)
        assertEquals("Đang tìm kiếm...", promptStatusText.text.toString())
    }

    @Test
    fun testPromptFragmentShowResultState() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val fragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val topicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val loadingCard = activity.findViewById<MaterialCardView>(R.id.loadingCard)
        val resultCard = activity.findViewById<MaterialCardView>(R.id.resultCard)
        val resultTextView = activity.findViewById<TextView>(R.id.resultTextView)
        val promptStatusText = activity.findViewById<TextView>(R.id.promptStatusText)
        val makePromptButton = activity.findViewById<MaterialButton>(R.id.makePromptButton)
        val searchNowButton = activity.findViewById<MaterialButton>(R.id.searchNowButton)

        topicInput.setText("AI Future")
        fragment.showLoading()
        fragment.showResult("Báo cáo về tương lai AI", "models/gemini-2.0-flash")

        assertEquals(View.GONE, loadingCard.visibility)
        assertEquals(View.VISIBLE, resultCard.visibility)
        assertEquals("Báo cáo về tương lai AI", resultTextView.text.toString().trim())
        assertEquals("Gemini: Done (gemini-2.0-flash)", promptStatusText.text.toString())
        assertTrue(topicInput.isEnabled)
        assertTrue(makePromptButton.isEnabled)
        assertTrue(searchNowButton.isEnabled)
    }

    @Test
    fun testPromptFragmentShowErrorState() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val fragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val topicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val loadingCard = activity.findViewById<MaterialCardView>(R.id.loadingCard)
        val errorCard = activity.findViewById<MaterialCardView>(R.id.errorCard)
        val errorTitle = activity.findViewById<TextView>(R.id.errorTitle)
        val errorMessage = activity.findViewById<TextView>(R.id.errorMessage)
        val promptStatusText = activity.findViewById<TextView>(R.id.promptStatusText)
        val makePromptButton = activity.findViewById<MaterialButton>(R.id.makePromptButton)
        val searchNowButton = activity.findViewById<MaterialButton>(R.id.searchNowButton)

        topicInput.setText("AI Future")
        fragment.showLoading()
        fragment.showError("⚠️ Lỗi kết nối", "Không thể kết nối Internet")

        assertEquals(View.GONE, loadingCard.visibility)
        assertEquals(View.VISIBLE, errorCard.visibility)
        assertEquals("⚠️ Lỗi kết nối", errorTitle.text.toString())
        assertEquals("Không thể kết nối Internet", errorMessage.text.toString())
        assertEquals("Lỗi Gemini", promptStatusText.text.toString())
        assertTrue(topicInput.isEnabled)
        assertTrue(makePromptButton.isEnabled)
        assertTrue(searchNowButton.isEnabled)
    }
}
