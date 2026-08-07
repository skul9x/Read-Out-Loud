package com.skul9x.readoutloud.ui

import android.view.View
import com.google.android.material.button.MaterialButton
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
class SummarizeButtonLayoutTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun testDualButtonsExistAndAreVisible() {
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)

        assertNotNull("aiTextButton should exist in main layout", aiTextButton)
        assertNotNull("summarizeButton should exist in main layout", summarizeButton)

        assertEquals("aiTextButton should be visible", View.VISIBLE, aiTextButton.visibility)
        assertEquals("summarizeButton should be visible", View.VISIBLE, summarizeButton.visibility)
        assertEquals("Tóm tắt", summarizeButton.text.toString())
    }
}
