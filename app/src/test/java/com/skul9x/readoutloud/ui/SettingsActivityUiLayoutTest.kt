package com.skul9x.readoutloud.ui

import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.material.card.MaterialCardView
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsActivityUiLayoutTest {

    @Before
    fun setUp() {
        // Khởi tạo trạng thái môi trường test nếu cần
    }

    @Test
    fun testSettingsLayoutContainsNestedScrollView() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()

        var foundScrollView = false
        val root = activity.findViewById<android.view.View>(android.R.id.content) as android.view.ViewGroup
        
        fun searchForNestedScrollView(view: android.view.View) {
            if (view is NestedScrollView) {
                foundScrollView = true
                return
            }
            if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) {
                    searchForNestedScrollView(view.getChildAt(i))
                }
            }
        }
        searchForNestedScrollView(root)

        assertTrue("Settings layout should contain a NestedScrollView to support vertical scrolling", foundScrollView)
    }

    @Test
    fun testApiKeyInputCardHasFixedTextAreaHeight() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        
        // Tìm Card chứa API Key qua ID
        val cardView = activity.findViewById<MaterialCardView>(R.id.apiKeyCard)
        assertNotNull("MaterialCardView holding the apiKeyEditText should exist", cardView)
        
        val layoutParams = cardView.layoutParams
        assertNotNull("Layout params should not be null", layoutParams)
        
        // Quy đổi 180dp thành pixel tương ứng theo density để test chạy mượt trên mọi môi trường
        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (180 * density).toInt()
        
        assertEquals("Card height should be set to 180dp", expectedHeightPx, layoutParams.height)
    }

    @Test
    fun testApiKeyEditTextPropertiesAsTextArea() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val apiKeyEditText = activity.findViewById<EditText>(R.id.apiKeyEditText)
        
        assertNotNull("apiKeyEditText should exist", apiKeyEditText)
        
        assertEquals("EditText minLines should be set to 5", 5, apiKeyEditText.minLines)
        assertEquals("EditText gravity should be set to top|start", 
            android.view.Gravity.TOP or android.view.Gravity.START, 
            apiKeyEditText.gravity and (android.view.Gravity.TOP or android.view.Gravity.START)
        )
    }

    @Test
    fun testApiKeyEditTextTouchListenerAssigned() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val apiKeyEditText = activity.findViewById<EditText>(R.id.apiKeyEditText)
        
        assertNotNull("apiKeyEditText should exist", apiKeyEditText)
        
        var disallowInterceptCalled = false
        val customParent = object : android.widget.FrameLayout(activity) {
            override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                disallowInterceptCalled = disallowIntercept
                super.requestDisallowInterceptTouchEvent(disallowIntercept)
            }
        }
        
        val originalParent = apiKeyEditText.parent as? android.view.ViewGroup
        originalParent?.removeView(apiKeyEditText)
        customParent.addView(apiKeyEditText)
        
        val downEvent = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 0f, 0f, 0)
        apiKeyEditText.dispatchTouchEvent(downEvent)
        downEvent.recycle()
        
        assertTrue("requestDisallowInterceptTouchEvent(true) should be called on parent during touch down", disallowInterceptCalled)
    }

    @Test
    fun testModelItemViewPropertiesAvoidTextWrapping() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null)
        
        val modelNameText = itemView.findViewById<TextView>(R.id.modelNameText)
        assertNotNull("modelNameText should exist in item_model layout", modelNameText)
        
        // Khẳng định chống lỗi ngắt chữ model
        assertEquals("modelNameText maxLines should be set to 1 to avoid text wrapping bug", 1, modelNameText.maxLines)
        assertEquals("modelNameText ellipsize should be set to END to display trailing dots professionally", 
            android.text.TextUtils.TruncateAt.END, modelNameText.ellipsize)
    }

    @Test
    fun testModelItemActionButtonsTouchTargetAndSpacing() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null)
        
        val moveUpButton = itemView.findViewById<ImageButton>(R.id.moveUpButton)
        val moveDownButton = itemView.findViewById<ImageButton>(R.id.moveDownButton)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButton)
        
        assertNotNull(moveUpButton)
        assertNotNull(moveDownButton)
        assertNotNull(deleteButton)
        
        val density = activity.resources.displayMetrics.density
        val compactTouchTargetPx = (36 * density).toInt()
        
        // Khẳng định kích thước vùng chạm tối thiểu là 36dp đạt chuẩn thiết kế compact
        assertTrue("moveUpButton width should be at least 36dp", moveUpButton.layoutParams.width >= compactTouchTargetPx)
        assertTrue("moveUpButton height should be at least 36dp", moveUpButton.layoutParams.height >= compactTouchTargetPx)
        
        // Khẳng định các nút có margins giãn cách ngang để tránh chạm nhầm
        val upParams = moveUpButton.layoutParams as? android.view.ViewGroup.MarginLayoutParams
        val downParams = moveDownButton.layoutParams as? android.view.ViewGroup.MarginLayoutParams
        
        assertNotNull("Layout params should be MarginLayoutParams", upParams)
        assertNotNull("Layout params should be MarginLayoutParams", downParams)
        
        assertTrue("moveUpButton should have margin to separate from checkbox and adjacent buttons", 
            upParams!!.leftMargin > 0 || upParams.rightMargin > 0)
        assertTrue("moveDownButton should have margin to separate from adjacent buttons", 
            downParams!!.leftMargin > 0 || downParams.rightMargin > 0)
    }

    @Test
    fun testModelItemTwoRowVerticalLayout() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null) as MaterialCardView
        val innerLayout = itemView.getChildAt(0) as android.widget.LinearLayout
        
        // Inner container must be vertical to stack model name row and action buttons row
        assertEquals("item_model inner layout orientation should be VERTICAL", android.widget.LinearLayout.VERTICAL, innerLayout.orientation)
        
        val modelNameText = itemView.findViewById<TextView>(R.id.modelNameText)
        assertNotNull("modelNameText should exist in item_model layout", modelNameText)
    }

    @Test
    fun testModelActionButtonDimensionsCompact() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null)
        
        val moveUpButton = itemView.findViewById<ImageButton>(R.id.moveUpButton)
        val moveDownButton = itemView.findViewById<ImageButton>(R.id.moveDownButton)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButton)
        
        assertNotNull(moveUpButton)
        assertNotNull(moveDownButton)
        assertNotNull(deleteButton)
        
        val density = activity.resources.displayMetrics.density
        val expectedSizePx = (36 * density).toInt()
        val expectedMarginPx = (2 * density).toInt()
        
        val buttons = listOf(moveUpButton, moveDownButton, deleteButton)
        for (button in buttons) {
            assertEquals("Button width should be 36dp", expectedSizePx, button.layoutParams.width)
            assertEquals("Button height should be 36dp", expectedSizePx, button.layoutParams.height)
            
            val marginParams = button.layoutParams as? android.view.ViewGroup.MarginLayoutParams
            assertNotNull("Button layoutParams should be MarginLayoutParams", marginParams)
            assertEquals("Button start/left margin should be 2dp", expectedMarginPx, marginParams!!.leftMargin)
            assertEquals("Button end/right margin should be 2dp", expectedMarginPx, marginParams.rightMargin)
        }
    }
}

