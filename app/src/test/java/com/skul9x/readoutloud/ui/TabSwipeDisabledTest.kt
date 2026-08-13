package com.skul9x.readoutloud.ui

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TabSwipeDisabledTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
        viewPager = activity.findViewById(R.id.viewPager)
        tabLayout = activity.findViewById(R.id.tabLayout)
    }

    @Test
    fun testViewPagerUserInputIsDisabled() {
        assertNotNull("ViewPager2 should exist", viewPager)
        assertFalse("ViewPager2 isUserInputEnabled must be false to disable swipe gestures", viewPager.isUserInputEnabled)
    }

    @Test
    fun testTappingPromptTabSwitchesPage() {
        assertNotNull("TabLayout should exist", tabLayout)
        val promptTab = tabLayout.getTabAt(1)
        assertNotNull("Prompt tab (index 1) should exist", promptTab)

        promptTab?.select()
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("TabLayout selectedTabPosition should be 1 (Prompt)", 1, tabLayout.selectedTabPosition)
        assertEquals("ViewPager currentItem should be 1 after selecting Prompt tab", 1, viewPager.currentItem)
    }

    @Test
    fun testTappingReadTabSwitchesPage() {
        // Start at Prompt tab
        tabLayout.getTabAt(1)?.select()
        Shadows.shadowOf(activity.mainLooper).idle()
        assertEquals(1, viewPager.currentItem)

        // Select Read tab
        val readTab = tabLayout.getTabAt(0)
        assertNotNull("Read tab (index 0) should exist", readTab)
        readTab?.select()
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("TabLayout selectedTabPosition should be 0 (Read)", 0, tabLayout.selectedTabPosition)
        assertEquals("ViewPager currentItem should be 0 after selecting Read tab", 0, viewPager.currentItem)
    }

    @Test
    fun testProgrammaticSwitchToTab() {
        // Switch to Prompt tab (index 1)
        activity.switchToTab(1)
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("ViewPager currentItem should be 1", 1, viewPager.currentItem)
        assertEquals("TabLayout selectedTabPosition should be 1", 1, tabLayout.selectedTabPosition)

        // Switch back to Read tab (index 0)
        activity.switchToTab(0)
        Shadows.shadowOf(activity.mainLooper).idle()

        assertEquals("ViewPager currentItem should be 0", 0, viewPager.currentItem)
        assertEquals("TabLayout selectedTabPosition should be 0", 0, tabLayout.selectedTabPosition)
    }
}
