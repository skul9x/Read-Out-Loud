package com.skul9x.readoutloud.ui

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityCopyButtonLayoutTest {

    @Test
    fun testCopyButtonExistsInMainActivityLayout() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val copyButton = activity.findViewById<MaterialButton>(R.id.copyTextButton)

        assertNotNull("copyTextButton should exist in activity_main.xml layout", copyButton)
        assertEquals("copyTextButton visibility should be VISIBLE", View.VISIBLE, copyButton.visibility)
        assertEquals("copyTextButton text should be COPY", "COPY", copyButton.text.toString())
    }

    @Test
    fun testCopyButtonLayoutPositioningAndMargins() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val copyButton = activity.findViewById<MaterialButton>(R.id.copyTextButton)

        assertNotNull("copyTextButton should exist", copyButton)
        val params = copyButton.layoutParams as? FrameLayout.LayoutParams
        assertNotNull("copyTextButton layoutParams should be FrameLayout.LayoutParams", params)

        val expectedGravity = Gravity.BOTTOM or Gravity.END
        assertEquals("copyTextButton gravity should be bottom|end", expectedGravity, params!!.gravity and expectedGravity)

        val density = activity.resources.displayMetrics.density
        val expectedMarginPx = (12 * density).toInt()

        assertEquals("copyTextButton left margin should be 12dp", expectedMarginPx, params.leftMargin)
        assertEquals("copyTextButton top margin should be 12dp", expectedMarginPx, params.topMargin)
        assertEquals("copyTextButton right margin should be 12dp", expectedMarginPx, params.rightMargin)
        assertEquals("copyTextButton bottom margin should be 12dp", expectedMarginPx, params.bottomMargin)
    }

    @Test
    fun testEditTextPaddingBottomPreventsOverlap() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        val editText = activity.findViewById<TextInputEditText>(R.id.editText)

        assertNotNull("editText should exist", editText)
        val density = activity.resources.displayMetrics.density
        val expectedBottomPaddingPx = (56 * density).toInt()

        assertTrue(
            "editText paddingBottom should be at least 56dp to prevent overlaying copy button",
            editText.paddingBottom >= expectedBottomPaddingPx
        )
    }
}
