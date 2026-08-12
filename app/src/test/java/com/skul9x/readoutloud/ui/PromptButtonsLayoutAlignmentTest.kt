package com.skul9x.readoutloud.ui

import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
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
class PromptButtonsLayoutAlignmentTest {

    private lateinit var activity: MainActivity
    private lateinit var makePromptButton: MaterialButton
    private lateinit var searchNowButton: MaterialButton
    private var promptFragment: PromptFragment? = null

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        makePromptButton = activity.findViewById(R.id.makePromptButton)
        searchNowButton = activity.findViewById(R.id.searchNowButton)
        promptFragment = activity.supportFragmentManager.fragments.filterIsInstance<PromptFragment>().firstOrNull()
    }

    @Test
    fun testButtonsExistAndAreVisible() {
        assertNotNull("makePromptButton must exist", makePromptButton)
        assertNotNull("searchNowButton must exist", searchNowButton)
    }

    @Test
    fun testButtonsHaveEqualWeightsAndEqualHeight() {
        val makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        val searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams

        assertEquals("Make Prompt button weight must be 1.0", 1.0f, makeParams.weight, 0.01f)
        assertEquals("Search Now button weight must be 1.0", 1.0f, searchParams.weight, 0.01f)

        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (56 * density).toInt()
        assertEquals("Make Prompt button height must be 56dp", expectedHeightPx, makeParams.height)
        assertEquals("Search Now button height must be 56dp", expectedHeightPx, searchParams.height)
    }

    @Test
    fun testButtonsEnforceSingleLineConstraints() {
        assertEquals("Make Prompt button maxLines must be 1", 1, makePromptButton.maxLines)
        assertEquals("Search Now button maxLines must be 1", 1, searchNowButton.maxLines)
    }

    @Test
    fun testButtonsHaveMatchingIconPropertiesAndGravity() {
        assertEquals("Make Prompt icon size must be 20dp", (20 * activity.resources.displayMetrics.density).toInt(), makePromptButton.iconSize)
        assertEquals("Search Now icon size must be 20dp", (20 * activity.resources.displayMetrics.density).toInt(), searchNowButton.iconSize)

        assertEquals("Make Prompt icon padding must be 6dp", (6 * activity.resources.displayMetrics.density).toInt(), makePromptButton.iconPadding)
        assertEquals("Search Now icon padding must be 6dp", (6 * activity.resources.displayMetrics.density).toInt(), searchNowButton.iconPadding)

        assertEquals("Make Prompt iconGravity must be TEXT_START", MaterialButton.ICON_GRAVITY_TEXT_START, makePromptButton.iconGravity)
        assertEquals("Search Now iconGravity must be TEXT_START", MaterialButton.ICON_GRAVITY_TEXT_START, searchNowButton.iconGravity)
    }

    @Test
    fun testButtonsHaveMatchingCornerRadius() {
        val expectedRadiusPx = (24 * activity.resources.displayMetrics.density).toInt()
        assertEquals("Make Prompt corner radius must be 24dp", expectedRadiusPx, makePromptButton.cornerRadius)
        assertEquals("Search Now corner radius must be 24dp", expectedRadiusPx, searchNowButton.cornerRadius)
    }

    @Test
    fun testButtonStateSynchronizationOnTextChange() {
        val promptTopicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)

        // Initial state: empty -> both disabled
        assertFalse("Make Prompt button should be disabled initially", makePromptButton.isEnabled)
        assertFalse("Search Now button should be disabled initially", searchNowButton.isEnabled)

        // Type text -> both enabled
        promptTopicInput.setText("AI News 2026")
        assertTrue("Make Prompt button should be enabled after entering text", makePromptButton.isEnabled)
        assertTrue("Search Now button should be enabled after entering text", searchNowButton.isEnabled)

        // Clear text -> both disabled
        promptTopicInput.setText("")
        assertFalse("Make Prompt button should be disabled when text is cleared", makePromptButton.isEnabled)
        assertFalse("Search Now button should be disabled when text is cleared", searchNowButton.isEnabled)
    }

    @Test
    fun testButtonDimensionsInvariantDuringAndAfterStateTransitions() {
        val promptTopicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (56 * density).toInt()

        promptTopicInput.setText("Quantum Computing")

        // Check params before click
        var makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        var searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams
        assertEquals(expectedHeightPx, makeParams.height)
        assertEquals(expectedHeightPx, searchParams.height)
        assertEquals(1.0f, makeParams.weight, 0.01f)
        assertEquals(1.0f, searchParams.weight, 0.01f)
        assertEquals(1, makePromptButton.maxLines)
        assertEquals(1, searchNowButton.maxLines)

        // Perform click on Make Prompt
        makePromptButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // Verify params remain unchanged
        makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams
        assertEquals(expectedHeightPx, makeParams.height)
        assertEquals(expectedHeightPx, searchParams.height)
        assertEquals(1.0f, makeParams.weight, 0.01f)
        assertEquals(1.0f, searchParams.weight, 0.01f)
        assertEquals(1, makePromptButton.maxLines)
        assertEquals(1, searchNowButton.maxLines)
    }

    @Test
    fun testButtonDimensionsInvariantDuringLoadingState() {
        val promptTopicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (56 * density).toInt()

        promptTopicInput.setText("Deep Learning")
        assertNotNull(promptFragment)
        promptFragment?.showLoading()
        Shadows.shadowOf(activity.mainLooper).idle()

        // During loading, buttons are disabled
        assertFalse("Make Prompt button should be disabled during loading", makePromptButton.isEnabled)
        assertFalse("Search Now button should be disabled during loading", searchNowButton.isEnabled)

        // Geometry invariant
        val makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        val searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams
        assertEquals(expectedHeightPx, makeParams.height)
        assertEquals(expectedHeightPx, searchParams.height)
        assertEquals(1.0f, makeParams.weight, 0.01f)
        assertEquals(1.0f, searchParams.weight, 0.01f)
        assertEquals(1, makePromptButton.maxLines)
        assertEquals(1, searchNowButton.maxLines)
    }

    @Test
    fun testButtonDimensionsInvariantAfterErrorState() {
        val promptTopicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (56 * density).toInt()

        promptTopicInput.setText("Network Failure Topic")
        assertNotNull(promptFragment)
        promptFragment?.showLoading()
        promptFragment?.showError("Error Title", "Error Details")
        Shadows.shadowOf(activity.mainLooper).idle()

        // After error, buttons are re-enabled if input has text
        assertTrue("Make Prompt button should be enabled after error recovery", makePromptButton.isEnabled)
        assertTrue("Search Now button should be enabled after error recovery", searchNowButton.isEnabled)

        // Geometry invariant
        val makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        val searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams
        assertEquals(expectedHeightPx, makeParams.height)
        assertEquals(expectedHeightPx, searchParams.height)
        assertEquals(1.0f, makeParams.weight, 0.01f)
        assertEquals(1.0f, searchParams.weight, 0.01f)
        assertEquals(1, makePromptButton.maxLines)
        assertEquals(1, searchNowButton.maxLines)
    }

    @Test
    fun testButtonDimensionsInvariantAfterResultDisplay() {
        val promptTopicInput = activity.findViewById<TextInputEditText>(R.id.promptTopicInput)
        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (56 * density).toInt()

        promptTopicInput.setText("Success Topic")
        assertNotNull(promptFragment)
        promptFragment?.showLoading()
        promptFragment?.showResult("Generated Result Content", "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        // After result, buttons are re-enabled
        assertTrue("Make Prompt button should be enabled after result display", makePromptButton.isEnabled)
        assertTrue("Search Now button should be enabled after result display", searchNowButton.isEnabled)

        // Geometry invariant
        val makeParams = makePromptButton.layoutParams as LinearLayout.LayoutParams
        val searchParams = searchNowButton.layoutParams as LinearLayout.LayoutParams
        assertEquals(expectedHeightPx, makeParams.height)
        assertEquals(expectedHeightPx, searchParams.height)
        assertEquals(1.0f, makeParams.weight, 0.01f)
        assertEquals(1.0f, searchParams.weight, 0.01f)
        assertEquals(1, makePromptButton.maxLines)
        assertEquals(1, searchNowButton.maxLines)
    }
}
