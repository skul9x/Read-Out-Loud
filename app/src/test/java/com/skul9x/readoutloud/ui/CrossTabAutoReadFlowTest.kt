package com.skul9x.readoutloud.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
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
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CrossTabAutoReadFlowTest {

    private lateinit var viewModel: MainSharedViewModel

    @Before
    fun setUp() {
        viewModel = MainSharedViewModel()
    }

    @Test
    fun testSharedViewModelReadAloudEvent() {
        // Initial state
        assertNull("Initial readAloudEvent should be null", viewModel.readAloudEvent.value)

        // Posting event
        val testContent = "This is a search result to read aloud."
        viewModel.requestReadAloud(testContent)
        assertEquals("readAloudEvent must contain posted text", testContent, viewModel.readAloudEvent.value)

        // Overwriting
        val updatedContent = "Updated content for reader."
        viewModel.requestReadAloud(updatedContent)
        assertEquals("readAloudEvent must overwrite previous value", updatedContent, viewModel.readAloudEvent.value)

        // Clearing event
        viewModel.clearReadAloudEvent()
        assertNull("readAloudEvent must be null after clearing", viewModel.readAloudEvent.value)

        // Is LiveData instance
        assertTrue("readAloudEvent must be an instance of LiveData", viewModel.readAloudEvent is LiveData<*>)
    }

    @Test
    fun testPromptFragmentReadButtonClick_SwitchesTabAndPostsEvent() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val promptFragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val readButton = activity.findViewById<MaterialButton>(R.id.readResultButton)
        val readEditText = activity.findViewById<EditText>(R.id.editText)
        assertNotNull("readResultButton must exist", readButton)
        assertNotNull("readEditText must exist", readEditText)

        val sampleResult = "Deep learning breakthrough announced in 2026."
        promptFragment.showResult(sampleResult, "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        assertTrue("readResultButton should be enabled after showing result", readButton.isEnabled)

        // Click Read button
        readButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // Button should be disabled to prevent double tap
        assertFalse("readResultButton should be disabled immediately after click", readButton.isEnabled)

        // ViewPager should switch to Read tab (index 0)
        assertEquals("ViewPager must switch to Read tab (index 0)", 0, viewPager.currentItem)

        // ReadFragment editText should receive the text
        assertEquals("ReadFragment editText should be populated with the result text", sampleResult, readEditText.text.toString())
    }

    @Test
    fun testReadFragmentReceivesReadAloudEvent_PopulatesEditTextAndTriggersPlayback() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val shadowApp = Shadows.shadowOf(app)
        shadowApp.grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
        while (shadowApp.nextStartedService != null) { /* drain */ }

        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val readEditText = activity.findViewById<EditText>(R.id.editText)
        assertNotNull("readEditText must exist", readEditText)

        val readFragment = activity.supportFragmentManager.findFragmentByTag("f0") as? ReadFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? ReadFragment }
        assertNotNull("ReadFragment must exist", readFragment)

        val testText = "Automatic speech synthesis test content."
        val sharedViewModel = androidx.lifecycle.ViewModelProvider(activity)[MainSharedViewModel::class.java]
        sharedViewModel.requestReadAloud(testText)
        Shadows.shadowOf(activity.mainLooper).idle()

        // Assert editText has the text
        assertEquals("ReadFragment editText must match the posted text", testText, readEditText.text.toString())

        // Assert event was cleared to prevent re-triggering
        assertNull("readAloudEvent must be cleared after being handled", sharedViewModel.readAloudEvent.value)

        // Verify TTS Service was started
        val startedIntent = shadowApp.nextStartedService
        assertNotNull("TtsService should be started when readAloudEvent is received", startedIntent)
        assertEquals(TtsService.ACTION_START, startedIntent?.action)
        assertEquals(testText, startedIntent?.getStringExtra(TtsService.EXTRA_TEXT))
    }

    @Test
    fun testReadButtonReEnabledOnResume() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val promptFragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val readButton = activity.findViewById<MaterialButton>(R.id.readResultButton)
        promptFragment.showResult("Autonomous agent documentation.", "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        // Click Read button -> disabled
        readButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()
        assertFalse("readResultButton should be disabled after click", readButton.isEnabled)

        // Simulate returning to prompt fragment (onResume)
        promptFragment.onResume()
        assertTrue("readResultButton should be re-enabled on resume", readButton.isEnabled)
    }

    @Test
    fun testReadButtonClickWithBlankResultDoesNotSwitchTab() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val readButton = activity.findViewById<MaterialButton>(R.id.readResultButton)
        // With no result text, perform click
        readButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        // Should stay on tab 1
        assertEquals("Should not switch tab when result text is blank", 1, viewPager.currentItem)
    }

    @Test
    fun testMarkdownCrossTabAutoReadFlow() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = 1
        Shadows.shadowOf(activity.mainLooper).idle()

        val promptFragment = activity.supportFragmentManager.findFragmentByTag("f1") as? PromptFragment
            ?: activity.supportFragmentManager.fragments.firstNotNullOf { it as? PromptFragment }

        val readButton = activity.findViewById<MaterialButton>(R.id.readResultButton)
        val readEditText = activity.findViewById<EditText>(R.id.editText)

        val markdownText = "# Title\n- Bullet 1\n- Bullet 2\n**Bold Text**"
        promptFragment.showResult(markdownText, "models/gemini-2.0-flash")
        Shadows.shadowOf(activity.mainLooper).idle()

        readButton.performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("Must switch to Read tab", 0, viewPager.currentItem)
        assertTrue("ReadFragment must contain markdown content", readEditText.text.toString().contains("Bullet 1"))
    }
}
