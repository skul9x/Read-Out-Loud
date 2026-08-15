package com.skul9x.readoutloud

import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.ui.MainSharedViewModel
import com.skul9x.readoutloud.utils.PromptTemplateHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import androidx.test.core.app.ApplicationProvider

@RunWith(RobolectricTestRunner::class)
class PromptTabIntegrationTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity
    private lateinit var context: Context

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testTabLayoutAndViewPagerIntegrated() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)

        assertNotNull("TabLayout must exist", tabLayout)
        assertNotNull("ViewPager2 must exist", viewPager)
        assertEquals(2, tabLayout.tabCount)
        assertEquals(2, viewPager.adapter?.itemCount)
    }

    @Test
    fun testTabsHaveIcons() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull("Read tab must have icon", tabLayout.getTabAt(0)?.icon)
        assertNotNull("Prompt tab must have icon", tabLayout.getTabAt(1)?.icon)
    }

    @Test
    fun testPromptTemplateLoadsCorrectly() {
        val template = PromptTemplateHelper.loadTemplate(context)
        assertTrue("Template must contain research expert role",
            template.contains("EXPERT IN MULTILINGUAL INTERNET RESEARCH"))
        assertTrue("Template must contain placeholder",
            template.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"))
    }

    @Test
    fun testPromptBuildingEndToEnd() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "tình hình kinh tế Việt Nam Q3 2026"
        val prompt = PromptTemplateHelper.buildPrompt(template, topic)

        assertTrue("Prompt must contain topic", prompt.contains(topic))
        assertFalse("Prompt must NOT contain placeholder",
            prompt.contains("[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"))
        assertTrue("Prompt must be longer than 5000 chars", prompt.length > 5000)
    }

    @Test
    fun testClipboardCopyIntegration() {
        val template = PromptTemplateHelper.loadTemplate(context)
        val topic = "semiconductor industry"
        val prompt = PromptTemplateHelper.buildPrompt(template, topic)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Prompt", prompt)
        clipboard.setPrimaryClip(clip)

        assertTrue(clipboard.hasPrimaryClip())
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        assertTrue(clipText.contains(topic))
    }

    @Test
    fun testSharedViewModelCrossTabCommunication() {
        val viewModel = MainSharedViewModel()

        val searchResult = "Kết quả nghiên cứu: ..."
        viewModel.requestSummarize(searchResult)
        assertEquals(searchResult, viewModel.summarizeEvent.value)

        viewModel.clearSummarizeEvent()
        assertNull(viewModel.summarizeEvent.value)
    }

    @Test
    fun testGeminiApiClientHasSearchWithPromptMethod() {
        val client = GeminiApiClient(context)
        val hasMethod = GeminiApiClient::class.java.methods.any { it.name == "searchWithPrompt" }
        assertTrue("GeminiApiClient must have searchWithPrompt method", hasMethod)
    }

    @Test
    fun testAllExistingViewsStillAccessible() {
        val toolbar = activity.findViewById<View>(R.id.toolbar)
        val loadingOverlay = activity.findViewById<View>(R.id.loadingOverlay)

        assertNotNull("Toolbar must still be accessible", toolbar)
        assertNotNull("Loading overlay must still be accessible", loadingOverlay)
    }

    @Test
    fun testDefaultTabIsReadTab() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertEquals("Default tab must be Read (index 0)", 0, tabLayout.selectedTabPosition)
    }

    @Test
    fun testTabIndicatorNotFullWidth() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertFalse("Tab indicator should not be full width (M3 style)",
            tabLayout.isTabIndicatorFullWidth)
    }

    @Test
    fun testViewPagerKeepsBothFragmentsAlive() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertEquals("offscreenPageLimit should be 1",
            1, viewPager.offscreenPageLimit)
    }
}
