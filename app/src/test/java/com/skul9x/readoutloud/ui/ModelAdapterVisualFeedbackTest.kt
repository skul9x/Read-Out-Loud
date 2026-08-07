package com.skul9x.readoutloud.ui

import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.ModelItem
import com.skul9x.readoutloud.data.ModelQuotaManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ModelAdapterVisualFeedbackTest {

    private lateinit var activity: SettingsActivity
    private lateinit var quotaManager: ModelQuotaManager
    private lateinit var apiKeyManager: ApiKeyManager
    private lateinit var parentView: FrameLayout

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        quotaManager = ModelQuotaManager.getInstance(activity)
        apiKeyManager = ApiKeyManager.getInstance(activity)
        parentView = FrameLayout(activity)
    }

    @Test
    fun testPriorityBadgeTextRendering() {
        val models = listOf(
            ModelItem("models/gemini-2.5-flash", isEnabled = true),
            ModelItem("models/gemini-2.5-pro", isEnabled = false)
        )
        val adapter = ModelAdapter(
            models = models,
            quotaManager = quotaManager,
            apiKeyManager = apiKeyManager,
            onToggle = {},
            onMoveUp = {},
            onMoveDown = {},
            onDelete = {},
            onEdit = {}
        )

        val holder0 = adapter.onCreateViewHolder(parentView, 0)
        adapter.onBindViewHolder(holder0, 0)

        val priorityBadge0 = holder0.itemView.findViewById<TextView>(R.id.priorityBadgeText)
        assertEquals("#1", priorityBadge0.text.toString())

        val holder1 = adapter.onCreateViewHolder(parentView, 0)
        adapter.onBindViewHolder(holder1, 1)

        val priorityBadge1 = holder1.itemView.findViewById<TextView>(R.id.priorityBadgeText)
        assertEquals("#2", priorityBadge1.text.toString())
    }

    @Test
    fun testCardAlphaVisualFeedbackForEnabledAndDisabledModels() {
        val models = listOf(
            ModelItem("models/gemini-2.5-flash", isEnabled = true),
            ModelItem("models/gemini-2.5-pro", isEnabled = false)
        )
        val adapter = ModelAdapter(
            models = models,
            quotaManager = quotaManager,
            apiKeyManager = apiKeyManager,
            onToggle = {},
            onMoveUp = {},
            onMoveDown = {},
            onDelete = {},
            onEdit = {}
        )

        val holder0 = adapter.onCreateViewHolder(parentView, 0)
        adapter.onBindViewHolder(holder0, 0)
        val card0 = holder0.itemView.findViewById<MaterialCardView>(R.id.modelItemCardView)
        assertEquals(1.0f, card0.alpha, 0.01f)

        val holder1 = adapter.onCreateViewHolder(parentView, 0)
        adapter.onBindViewHolder(holder1, 1)
        val card1 = holder1.itemView.findViewById<MaterialCardView>(R.id.modelItemCardView)
        assertEquals(0.55f, card1.alpha, 0.01f)
    }

    @Test
    fun testDynamicUpdateOnModelsChanged() {
        val models = listOf(
            ModelItem("models/gemini-2.5-flash", isEnabled = true),
            ModelItem("models/gemini-2.5-pro", isEnabled = false)
        )
        val adapter = ModelAdapter(
            models = models,
            quotaManager = quotaManager,
            apiKeyManager = apiKeyManager,
            onToggle = {},
            onMoveUp = {},
            onMoveDown = {},
            onDelete = {},
            onEdit = {}
        )

        // Swap order and states
        val updatedModels = listOf(
            ModelItem("models/gemini-2.5-pro", isEnabled = true),
            ModelItem("models/gemini-2.5-flash", isEnabled = false)
        )
        adapter.updateModels(updatedModels)

        val holder0 = adapter.onCreateViewHolder(parentView, 0)
        adapter.onBindViewHolder(holder0, 0)
        val priorityBadge0 = holder0.itemView.findViewById<TextView>(R.id.priorityBadgeText)
        val card0 = holder0.itemView.findViewById<MaterialCardView>(R.id.modelItemCardView)

        assertEquals("#1", priorityBadge0.text.toString())
        assertEquals(1.0f, card0.alpha, 0.01f)

        val holder1 = adapter.onCreateViewHolder(parentView, 0)
        adapter.onBindViewHolder(holder1, 1)
        val priorityBadge1 = holder1.itemView.findViewById<TextView>(R.id.priorityBadgeText)
        val card1 = holder1.itemView.findViewById<MaterialCardView>(R.id.modelItemCardView)

        assertEquals("#2", priorityBadge1.text.toString())
        assertEquals(0.55f, card1.alpha, 0.01f)
    }
}
