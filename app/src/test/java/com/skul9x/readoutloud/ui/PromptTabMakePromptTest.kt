package com.skul9x.readoutloud.ui

import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.utils.PromptTemplateHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PromptTabMakePromptTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testRawPromptTemplateResourceExists() {
        val resId = context.resources.getIdentifier("prompt_template", "raw", context.packageName)
        assertTrue("prompt_template raw resource must exist", resId != 0)
    }

    @Test
    fun testLoadTemplateReturnsNonEmptyString() {
        val template = PromptTemplateHelper.loadTemplate(context)
        assertTrue("Template must not be blank", template.isNotBlank())
        assertTrue("Template must contain the placeholder",
            template.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"))
        assertTrue("Template must contain the expert role",
            template.contains("\"role\": \"EXPERT IN MULTILINGUAL INTERNET RESEARCH"))
    }

    @Test
    fun testBuildPromptReplacesPlaceholder() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "chiến tranh thương mại Mỹ Trung 2026"
        val result = PromptTemplateHelper.buildPrompt(template, topic)

        assertFalse("Built prompt must NOT contain placeholder",
            result.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"))
        assertTrue("Built prompt must contain the user's topic", result.contains(topic))
        assertTrue("Built prompt must contain topic_to_research key with topic value",
            result.contains("\"topic_to_research\": \"chiến tranh thương mại Mỹ Trung 2026\""))
        assertTrue("Built prompt must contain autonomous_execution",
            result.contains("\"autonomous_execution\""))
    }

    @Test
    fun testBuildPromptPreservesTemplateStructure() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "AI regulation in EU"
        val result = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue("Must contain research_principles",
            result.contains("\"research_principles\""))
        assertTrue("Must contain country_specific_rule",
            result.contains("\"country_specific_rule\""))
        assertTrue("Must contain ultimate_goal",
            result.contains("\"ultimate_goal\""))
        assertTrue("Must contain execution_rule",
            result.contains("\"execution_rule\""))
    }

    @Test
    fun testBuildPromptWithEmptyTopicReplacesPlaceholder() {
        val template = "Test [INFORMATION/NEWS/TOPIC I WANT TO RESEARCH] end"
        val result = PromptTemplateHelper.buildPrompt(template, "")
        assertEquals("Test  end", result)

        val fullTemplate = PromptTemplateHelper.loadTemplate(context)
        val fullResult = PromptTemplateHelper.buildPrompt(fullTemplate, "")
        assertFalse(fullResult.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"))
        assertTrue(fullResult.contains("\"topic_to_research\": \"\""))
    }

    @Test
    fun testMakePromptCopiesToClipboard() {
        val topic = "Test topic"
        val template = PromptTemplateHelper.loadTemplate(context)
        val builtPrompt = PromptTemplateHelper.buildPrompt(template, topic)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Prompt", builtPrompt)
        clipboard.setPrimaryClip(clip)

        assertTrue("Clipboard must have content", clipboard.hasPrimaryClip())
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        assertTrue("Clipboard must contain the topic", clipText.contains(topic))
        assertFalse("Clipboard must NOT contain placeholder",
            clipText.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"))
    }

    @Test
    fun testPromptFragmentInitialViewStatesAndMakePromptFlow() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val promptTopicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val makePromptButton = activity.findViewById<MaterialButton>(R.id.makePromptButton)
        val searchNowButton = activity.findViewById<MaterialButton>(R.id.searchNowButton)
        val emptyStateGroup = activity.findViewById<View>(R.id.emptyStateGroup)
        val resultCard = activity.findViewById<View>(R.id.resultCard)
        val resultActionsLayout = activity.findViewById<View>(R.id.resultActionsLayout)
        val summarizeResultButton = activity.findViewById<View>(R.id.summarizeResultButton)
        val promptStatusText = activity.findViewById<TextView>(R.id.promptStatusText)

        assertNotNull(promptTopicInput)
        assertNotNull(makePromptButton)
        assertNotNull(searchNowButton)
        assertNotNull(emptyStateGroup)
        assertNotNull(resultCard)
        assertNotNull(resultActionsLayout)
        assertNotNull(summarizeResultButton)
        assertNotNull(promptStatusText)

        // Initial states
        assertFalse("Make prompt button should be disabled when topic is empty", makePromptButton.isEnabled)
        assertFalse("Search now button should be disabled when topic is empty", searchNowButton.isEnabled)
        assertEquals(View.VISIBLE, emptyStateGroup.visibility)
        assertEquals(View.GONE, resultCard.visibility)
        assertEquals(View.GONE, resultActionsLayout.visibility)
        assertEquals("Ready", promptStatusText.text.toString())

        // Type input
        promptTopicInput.setText("Quantum Computing 2026")
        assertTrue(makePromptButton.isEnabled)
        assertTrue(searchNowButton.isEnabled)

        // Click Make Prompt
        makePromptButton.performClick()

        // Verify Status Text
        assertEquals("Prompt đã sẵn sàng trong clipboard", promptStatusText.text.toString())

        // Verify Clipboard content
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        assertTrue(clipboard.hasPrimaryClip())
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        assertTrue(clipText.contains("Quantum Computing 2026"))
        assertFalse(clipText.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"))

        // Clear input
        promptTopicInput.setText("")
        assertFalse(makePromptButton.isEnabled)
        assertFalse(searchNowButton.isEnabled)
    }
}
