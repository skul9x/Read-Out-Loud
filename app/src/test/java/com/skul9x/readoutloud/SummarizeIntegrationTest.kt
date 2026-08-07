package com.skul9x.readoutloud

import android.widget.EditText
import com.google.android.material.button.MaterialButton
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class SummarizeIntegrationTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun testSummarizeButtonEmptyTextShowsToast() {
        val editText = activity.findViewById<EditText>(R.id.editText)
        editText.setText("") // Empty content

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        summarizeButton.performClick()

        val latestToast = ShadowToast.getTextOfLatestToast()
        assertEquals("Không có nội dung để tóm tắt", latestToast)
    }

    @Test
    fun testSummarizeButtonDisabledDuringLoadingState() {
        val setLoadingMethod = MainActivity::class.java.getDeclaredMethod("setLoading", Boolean::class.java)
        setLoadingMethod.isAccessible = true

        val summarizeButton = activity.findViewById<MaterialButton>(R.id.summarizeButton)
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)

        // Set loading = true
        setLoadingMethod.invoke(activity, true)
        assertFalse("summarizeButton should be disabled during loading", summarizeButton.isEnabled)
        assertFalse("aiTextButton should be disabled during loading", aiTextButton.isEnabled)

        // Set loading = false
        setLoadingMethod.invoke(activity, false)
        assertTrue("summarizeButton should be re-enabled after loading", summarizeButton.isEnabled)
        assertTrue("aiTextButton should be re-enabled after loading", aiTextButton.isEnabled)
    }
}
