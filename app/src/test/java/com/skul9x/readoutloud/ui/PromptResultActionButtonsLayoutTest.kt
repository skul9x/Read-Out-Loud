package com.skul9x.readoutloud.ui

import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.TextViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
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
class PromptResultActionButtonsLayoutTest {

    private lateinit var activity: MainActivity
    private var promptFragment: PromptFragment? = null
    private lateinit var summarizeButton: MaterialButton
    private lateinit var readButton: MaterialButton
    private lateinit var showButton: MaterialButton
    private lateinit var resultActionsLayout: LinearLayout
    private lateinit var resultCard: MaterialCardView

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        promptFragment = activity.supportFragmentManager.fragments.filterIsInstance<PromptFragment>().firstOrNull()
            ?: activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment

        resultCard = activity.findViewById(R.id.resultCard)
        resultActionsLayout = activity.findViewById(R.id.resultActionsLayout)
        summarizeButton = activity.findViewById(R.id.summarizeResultButton)
        readButton = activity.findViewById(R.id.readResultButton)
        showButton = activity.findViewById(R.id.showResultButton)
    }

    @Test
    fun testResultActionButtonsExistInLayout() {
        assertNotNull("resultCard must exist", resultCard)
        assertNotNull("resultActionsLayout must exist", resultActionsLayout)
        assertNotNull("summarizeResultButton must exist", summarizeButton)
        assertNotNull("readResultButton must exist", readButton)
        assertNotNull("showResultButton must exist", showButton)

        // Verify labels and icons
        assertEquals("Tóm tắt", summarizeButton.text.toString())
        assertEquals("Read", readButton.text.toString())
        assertEquals("Show", showButton.text.toString())

        assertNotNull("Summarize button icon must not be null", summarizeButton.icon)
        assertNotNull("Read button icon must not be null", readButton.icon)
        assertNotNull("Show button icon must not be null", showButton.icon)
    }

    @Test
    fun testResultActionButtonsHorizontalAlignment() {
        assertEquals("resultActionsLayout orientation must be HORIZONTAL", LinearLayout.HORIZONTAL, resultActionsLayout.orientation)
        assertEquals("resultActionsLayout must contain exactly 3 action buttons", 3, resultActionsLayout.childCount)
        assertSame("First child must be summarizeButton", summarizeButton, resultActionsLayout.getChildAt(0))
        assertSame("Second child must be readButton", readButton, resultActionsLayout.getChildAt(1))
        assertSame("Third child must be showButton", showButton, resultActionsLayout.getChildAt(2))

        val sumParams = summarizeButton.layoutParams as LinearLayout.LayoutParams
        val readParams = readButton.layoutParams as LinearLayout.LayoutParams
        val showParams = showButton.layoutParams as LinearLayout.LayoutParams

        assertEquals("Summarize button weight must be 1.0", 1.0f, sumParams.weight, 0.01f)
        assertEquals("Read button weight must be 1.0", 1.0f, readParams.weight, 0.01f)
        assertEquals("Show button weight must be 1.0", 1.0f, showParams.weight, 0.01f)

        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (40 * density).toInt()
        assertEquals("Summarize button height must be 40dp", expectedHeightPx, sumParams.height)
        assertEquals("Read button height must be 40dp", expectedHeightPx, readParams.height)
        assertEquals("Show button height must be 40dp", expectedHeightPx, showParams.height)
    }

    @Test
    fun testResultActionButtonsAntiClippingAttributes() {
        val buttons = listOf(summarizeButton, readButton, showButton)
        for (button in buttons) {
            assertEquals("maxLines must be 1", 1, button.maxLines)
            assertEquals("minWidth must be 0", 0, button.minimumWidth)
            assertEquals("minHeight must be 0", 0, button.minimumHeight)
            assertTrue("includeFontPadding must be true", button.includeFontPadding)

            // Auto size text
            val autoSizeTextType = TextViewCompat.getAutoSizeTextType(button)
            assertEquals("autoSizeTextType must be uniform", TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM, autoSizeTextType)

            // Icon configuration
            assertEquals("iconGravity must be TEXT_START", MaterialButton.ICON_GRAVITY_TEXT_START, button.iconGravity)
            val density = activity.resources.displayMetrics.density
            assertEquals("iconSize must be 16dp", (16 * density).toInt(), button.iconSize)
            assertEquals("iconPadding must be 4dp", (4 * density).toInt(), button.iconPadding)
            assertEquals("cornerRadius must be 12dp", (12 * density).toInt(), button.cornerRadius)
        }
    }

    @Test
    fun testButtonsVisibilityAndEnabledStateOnShowResult() {
        assertNotNull(promptFragment)
        promptFragment?.showLoading()
        Shadows.shadowOf(activity.mainLooper).idle()

        promptFragment?.showResult("Sample Search Result Content", "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("resultCard must be VISIBLE", View.VISIBLE, resultCard.visibility)
        assertEquals("resultActionsLayout must be VISIBLE", View.VISIBLE, resultActionsLayout.visibility)
        assertTrue("summarizeButton must be enabled", summarizeButton.isEnabled)
        assertTrue("readButton must be enabled", readButton.isEnabled)
        assertTrue("showButton must be enabled", showButton.isEnabled)
    }

    @Test
    fun testButtonsDisabledStateOnShowLoading() {
        assertNotNull(promptFragment)
        promptFragment?.showLoading()
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("resultCard must be GONE during loading", View.GONE, resultCard.visibility)
        assertEquals("resultActionsLayout must be GONE during loading", View.GONE, resultActionsLayout.visibility)
        assertFalse("summarizeButton must be disabled during loading", summarizeButton.isEnabled)
        assertFalse("readButton must be disabled during loading", readButton.isEnabled)
        assertFalse("showButton must be disabled during loading", showButton.isEnabled)
    }

    @Test
    fun testButtonsReenabledOnResume() {
        assertNotNull(promptFragment)
        promptFragment?.showLoading()
        promptFragment?.showResult("Sample Result", "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        // Simulate disable
        summarizeButton.isEnabled = false
        readButton.isEnabled = false
        showButton.isEnabled = false

        promptFragment?.onResume()

        assertTrue("summarizeButton must be re-enabled on onResume", summarizeButton.isEnabled)
        assertTrue("readButton must be re-enabled on onResume", readButton.isEnabled)
        assertTrue("showButton must be enabled on onResume", showButton.isEnabled)
    }
}
