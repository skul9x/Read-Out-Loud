package com.skul9x.readoutloud.ui

import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ItemModelCardLayoutTest {

    @Test
    fun testModelItemCardViewIsRootContainer() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null)

        assertTrue("Root container of item_model should be MaterialCardView", itemView is MaterialCardView)
        val cardView = itemView as MaterialCardView
        assertEquals("Root card ID should be modelItemCardView", R.id.modelItemCardView, cardView.id)
    }

    @Test
    fun testModelItemCardViewDesignProperties() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val cardView = inflater.inflate(R.layout.item_model, null) as MaterialCardView

        val density = activity.resources.displayMetrics.density
        val expectedRadiusPx = 16 * density
        val expectedStrokeWidthPx = (1 * density).toInt()

        assertEquals("Card corner radius should be 16dp", expectedRadiusPx, cardView.radius, 0.5f)
        assertEquals("Card stroke width should be 1dp", expectedStrokeWidthPx, cardView.strokeWidth)
    }

    @Test
    fun testPriorityBadgeTextExistsInHierarchy() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null)

        val priorityBadgeText = itemView.findViewById<TextView>(R.id.priorityBadgeText)
        assertNotNull("priorityBadgeText TextView should exist in inflated view hierarchy", priorityBadgeText)
    }
}
