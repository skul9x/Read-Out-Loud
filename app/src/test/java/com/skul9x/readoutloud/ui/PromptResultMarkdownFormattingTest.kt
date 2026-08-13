package com.skul9x.readoutloud.ui

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import io.noties.markwon.Markwon
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
class PromptResultMarkdownFormattingTest {

    private lateinit var markwon: Markwon

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        markwon = Markwon.create(context)
    }

    @Test
    fun testHeadingMarkdownFormattingRendersWithoutHashAndAppliesSpans() {
        val markdown = "# Main Heading\n## Subheading"
        val spanned = markwon.toMarkdown(markdown)

        val renderedText = spanned.toString().trim()
        assertFalse("Rendered text must not contain markdown hash symbol '#'", renderedText.contains("#"))
        assertTrue("Rendered text should contain 'Main Heading'", renderedText.contains("Main Heading"))
        assertTrue("Rendered text should contain 'Subheading'", renderedText.contains("Subheading"))

        val spans = spanned.getSpans(0, spanned.length, Any::class.java)
        assertTrue("Spans must be applied for headings", spans.isNotEmpty())
    }

    @Test
    fun testBoldMarkdownFormattingRendersWithoutAsterisksAndAppliesBoldSpan() {
        val markdown = "This is **important text** here"
        val spanned = markwon.toMarkdown(markdown)

        val renderedText = spanned.toString()
        assertFalse("Rendered text must not contain asterisks '**'", renderedText.contains("**"))
        assertTrue("Rendered text should contain 'important text'", renderedText.contains("important text"))

        val startIndex = renderedText.indexOf("important text")
        val endIndex = startIndex + "important text".length
        val spans = spanned.getSpans(startIndex, endIndex, Any::class.java)
        assertTrue("Spans must be present on bold text", spans.isNotEmpty())
        val hasBoldSpan = spans.any { 
            it is StyleSpan && (it.style == Typeface.BOLD || it.style == Typeface.BOLD_ITALIC) ||
            it.javaClass.simpleName.contains("Strong", ignoreCase = true) ||
            it.javaClass.simpleName.contains("Bold", ignoreCase = true)
        }
        assertTrue("Bold/Strong emphasis span must be present on bold text, found: ${spans.map { it.javaClass.name }}", hasBoldSpan)
    }

    @Test
    fun testItalicMarkdownFormattingRendersWithoutAsterisksAndAppliesItalicSpan() {
        val markdown = "This is *italic note* here"
        val spanned = markwon.toMarkdown(markdown)

        val renderedText = spanned.toString()
        assertFalse("Rendered text must not contain asterisks '*'", renderedText.contains("*italic note*"))
        assertTrue("Rendered text should contain 'italic note'", renderedText.contains("italic note"))

        val startIndex = renderedText.indexOf("italic note")
        val endIndex = startIndex + "italic note".length
        val spans = spanned.getSpans(startIndex, endIndex, Any::class.java)
        assertTrue("Spans must be present on italic text", spans.isNotEmpty())
        val hasItalicSpan = spans.any { 
            it is StyleSpan && (it.style == Typeface.ITALIC || it.style == Typeface.BOLD_ITALIC) ||
            it.javaClass.simpleName.contains("Emphasis", ignoreCase = true) ||
            it.javaClass.simpleName.contains("Italic", ignoreCase = true)
        }
        assertTrue("Emphasis/Italic span must be present on italic text, found: ${spans.map { it.javaClass.name }}", hasItalicSpan)
    }

    @Test
    fun testBulletListMarkdownFormattingRendersCleanList() {
        val markdown = "* Point 1\n* Point 2\n* Point 3"
        val spanned = markwon.toMarkdown(markdown)

        val renderedText = spanned.toString()
        assertTrue("Rendered text should contain 'Point 1'", renderedText.contains("Point 1"))
        assertTrue("Rendered text should contain 'Point 2'", renderedText.contains("Point 2"))
        assertTrue("Rendered text should contain 'Point 3'", renderedText.contains("Point 3"))

        val spans = spanned.getSpans(0, spanned.length, Any::class.java)
        assertTrue("Spans must be attached for bullet lists", spans.isNotEmpty())
    }

    @Test
    fun testEdgeCaseEmptyAndWhitespaceText() {
        val emptyMarkdown = ""
        val emptySpanned = markwon.toMarkdown(emptyMarkdown)
        assertEquals("", emptySpanned.toString())

        val whitespaceMarkdown = "   \n\n  "
        val whitespaceSpanned = markwon.toMarkdown(whitespaceMarkdown.trim())
        assertEquals("", whitespaceSpanned.toString())
    }

    @Test
    fun testEdgeCasePlainTextWithoutMarkdown() {
        val plain = "This is regular plain text without any formatting."
        val spanned = markwon.toMarkdown(plain)
        assertEquals(plain, spanned.toString().trim())
    }

    @Test
    fun testMixedMarkdownRendering() {
        val mixed = """
            # Executive Summary
            
            Here is **crucial** data regarding the *quarterly* performance:
            * Revenue increased by 20%
            * Customer satisfaction at 98%
        """.trimIndent()

        val spanned = markwon.toMarkdown(mixed)
        val rendered = spanned.toString()

        assertFalse("Should not contain markdown # symbol", rendered.contains("#"))
        assertFalse("Should not contain markdown ** symbol", rendered.contains("**"))
        assertTrue("Should contain 'Executive Summary'", rendered.contains("Executive Summary"))
        assertTrue("Should contain 'crucial'", rendered.contains("crucial"))
        assertTrue("Should contain 'quarterly'", rendered.contains("quarterly"))
        assertTrue("Should contain list content", rendered.contains("Revenue increased by 20%"))
    }

    @Test
    fun testPromptFragmentShowResultRendersMarkdownInTextView() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val fragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val resultCard = activity.findViewById<MaterialCardView>(R.id.resultCard)
        val resultTextView = activity.findViewById<TextView>(R.id.resultTextView)
        val promptStatusText = activity.findViewById<TextView>(R.id.promptStatusText)

        val markdownContent = "# Báo cáo AI\n\n**Điểm nổi bật**: Công nghệ đang phát triển vượt bậc."
        fragment.showResult(markdownContent, "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals(View.VISIBLE, resultCard.visibility)
        assertEquals("Gemini: Done (gemini-2.0-flash)", promptStatusText.text.toString())

        val renderedCharSequence = resultTextView.text
        assertTrue("resultTextView.text should be Spanned instance", renderedCharSequence is Spanned)

        val renderedString = renderedCharSequence.toString()
        assertFalse("Result string should not contain raw # symbol", renderedString.contains("#"))
        assertFalse("Result string should not contain raw ** symbol", renderedString.contains("**"))
        assertTrue("Result string should contain 'Báo cáo AI'", renderedString.contains("Báo cáo AI"))
        assertTrue("Result string should contain 'Điểm nổi bật'", renderedString.contains("Điểm nổi bật"))
    }

    @Test
    fun testSummarizeButtonWithMarkdownRenderedText() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val fragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeResultButton)
        val markdownContent = "## Phân tích thị trường\n\n* Tiềm năng **rất lớn**"
        fragment.showResult(markdownContent, "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        assertTrue(summarizeButton.isEnabled)
        summarizeButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // Verify it navigated to Tab 0 (Read tab)
        assertEquals(0, viewPager.currentItem)
    }
}
