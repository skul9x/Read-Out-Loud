package com.skul9x.readoutloud.ui

import android.view.View
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
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class TabLayoutInfrastructureTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun testTabLayoutExists() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull("TabLayout must exist in activity_main_tabs layout", tabLayout)
        assertEquals("TabLayout must be visible", View.VISIBLE, tabLayout.visibility)
    }

    @Test
    fun testViewPager2Exists() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertNotNull("ViewPager2 must exist in activity_main_tabs layout", viewPager)
        assertEquals("ViewPager2 must be visible", View.VISIBLE, viewPager.visibility)
    }

    @Test
    fun testTabLayoutHasTwoTabs() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull("TabLayout must exist", tabLayout)
        assertEquals("TabLayout must have exactly 2 tabs", 2, tabLayout.tabCount)
    }

    @Test
    fun testTabLabels() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertEquals("First tab label must be 'Read'", "Read", tabLayout.getTabAt(0)?.text)
        assertEquals("Second tab label must be 'Prompt'", "Prompt", tabLayout.getTabAt(1)?.text)
    }

    @Test
    fun testTabIconsExist() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertNotNull("Read tab must have an icon", tabLayout.getTabAt(0)?.icon)
        assertNotNull("Prompt tab must have an icon", tabLayout.getTabAt(1)?.icon)
    }

    @Test
    fun testViewPagerAdapterHasTwoPages() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertNotNull(viewPager)
        val adapter = viewPager.adapter
        assertNotNull("ViewPager2 must have an adapter set", adapter)
        assertEquals("Adapter must have exactly 2 items", 2, adapter!!.itemCount)
    }

    @Test
    fun testToolbarStillExistsAtActivityLevel() {
        val toolbar = activity.findViewById<View>(R.id.toolbar)
        assertNotNull("Toolbar must still exist in Activity", toolbar)
        assertEquals("Toolbar must be visible", View.VISIBLE, toolbar.visibility)
    }

    @Test
    fun testLoadingOverlayStillExists() {
        val overlay = activity.findViewById<View>(R.id.loadingOverlay)
        assertNotNull("Loading overlay must still exist in Activity", overlay)
        assertEquals("Loading overlay must be GONE initially", View.GONE, overlay.visibility)
    }

    @Test
    fun testDefaultTabIsRead() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        assertEquals("Default selected tab must be 0 (Read)", 0, tabLayout.selectedTabPosition)
    }

    @Test
    fun testTabIndicatorIsNotFullWidth() {
        val tabLayout = activity.findViewById<TabLayout>(R.id.tabLayout)
        assertNotNull(tabLayout)
        // M3 best practice: indicator should match content width, not full tab width
        assertFalse("Tab indicator should not be full width (M3 style)",
            tabLayout.isTabIndicatorFullWidth)
    }

    @Test
    fun testViewPagerOffscreenPageLimit() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertNotNull(viewPager)
        assertEquals("offscreenPageLimit should be 1 to keep both fragments alive",
            1, viewPager.offscreenPageLimit)
    }

    @Test
    fun testViewPagerUserInputDisabled() {
        val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
        assertNotNull(viewPager)
        assertFalse("ViewPager2 user input (swipe) should be disabled", viewPager.isUserInputEnabled)
    }
}
