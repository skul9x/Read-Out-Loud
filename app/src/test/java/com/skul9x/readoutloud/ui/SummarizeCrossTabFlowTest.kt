package com.skul9x.readoutloud.ui

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
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
class SummarizeCrossTabFlowTest {

    private lateinit var viewModel: MainSharedViewModel

    @Before
    fun setUp() {
        viewModel = MainSharedViewModel()
    }

    @Test
    fun testSharedViewModelInitialStateIsNull() {
        assertNull("Initial summarize event should be null",
            viewModel.summarizeEvent.value)
    }

    @Test
    fun testRequestSummarizeSetsEvent() {
        val testText = "This is a test article about AI regulations."
        viewModel.requestSummarize(testText)
        assertEquals("summarizeEvent must contain the requested text",
            testText, viewModel.summarizeEvent.value)
    }

    @Test
    fun testClearSummarizeEventResetsToNull() {
        viewModel.requestSummarize("Some text")
        assertNotNull(viewModel.summarizeEvent.value)

        viewModel.clearSummarizeEvent()
        assertNull("After clearing, summarizeEvent must be null",
            viewModel.summarizeEvent.value)
    }

    @Test
    fun testRequestSummarizeOverwritesPreviousValue() {
        viewModel.requestSummarize("First text")
        assertEquals("First text", viewModel.summarizeEvent.value)

        viewModel.requestSummarize("Second text")
        assertEquals("Second text must overwrite first",
            "Second text", viewModel.summarizeEvent.value)
    }

    @Test
    fun testSummarizeEventIsLiveData() {
        val event = viewModel.summarizeEvent
        assertNotNull("summarizeEvent LiveData must not be null", event)
        assertTrue("summarizeEvent must be a LiveData instance",
            event is androidx.lifecycle.LiveData<*>)
    }

    @Test
    fun testRequestSummarizeWithLongText() {
        val longText = "Paragraph. ".repeat(500)
        viewModel.requestSummarize(longText)
        assertEquals("Long text must be stored correctly",
            longText, viewModel.summarizeEvent.value)
    }

    @Test
    fun testFlowIntegrity_RequestThenClearThenRequestAgain() {
        // Full flow: search result → summarize → clear → new search → summarize again
        viewModel.requestSummarize("Result 1")
        assertEquals("Result 1", viewModel.summarizeEvent.value)

        viewModel.clearSummarizeEvent()
        assertNull(viewModel.summarizeEvent.value)

        viewModel.requestSummarize("Result 2")
        assertEquals("Result 2", viewModel.summarizeEvent.value)
    }

    @Test
    fun testMainActivitySwitchToTab() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        
        activity.switchToTab(1)
        Shadows.shadowOf(activity.mainLooper).idle()
        assertEquals(1, viewPager.currentItem)

        activity.switchToTab(0)
        Shadows.shadowOf(activity.mainLooper).idle()
        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun testPromptFragmentSummarizeButtonFlow() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val promptFragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeResultButton)
        assertNotNull("summarizeResultButton must exist", summarizeButton)

        // Show a result
        promptFragment.showResult("AI technology report 2026", "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        assertTrue(summarizeButton.isEnabled)

        // Click summarize button
        summarizeButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // Button should be disabled to prevent double click
        assertFalse(summarizeButton.isEnabled)

        // Tab should have switched to Read tab (index 0)
        assertEquals(0, viewPager.currentItem)
    }

    @Test
    fun testPromptFragmentSummarizeButtonReEnabledOnResume() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val promptFragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeResultButton)
        promptFragment.showResult("AI technology report 2026", "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        summarizeButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()
        assertFalse(summarizeButton.isEnabled)

        // Simulating onResume when returning to prompt fragment
        promptFragment.onResume()
        assertTrue("summarizeResultButton should be re-enabled on resume", summarizeButton.isEnabled)
    }
}
