package com.skul9x.readoutloud

import android.content.Context
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class AiTextButtonTest {

    private lateinit var activityController: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.setup().get()
    }

    @Test
    fun `test aiTextButton click updates preference and triggers processing`() {
        // 1. Setup sample text
        activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText).setText("Sample text")

        // 2. Perform click on aiTextButton
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        aiTextButton.performClick()

        // 3. Verify preference saved
        val sharedPrefs = activity.getSharedPreferences("ReadOutLoudPrefs", Context.MODE_PRIVATE)
        assertTrue("gemini_enabled should be true after click", sharedPrefs.getBoolean("gemini_enabled", false))

        // 4. Verify some status change containing "Gemini"
        val statusText = activity.findViewById<TextView>(R.id.statusText).text.toString()
        assertTrue("Status should contain Gemini, but was: $statusText", statusText.contains("Gemini"))
    }

    @Test
    fun `test aiTextButton is disabled when loading`() {
        // Access private method or set loading state via reflection/internal exposing
        // In this case, we can trigger the action and check if it becomes disabled if we mock the API response
        
        // Mocking the loading state directly via reflection for quick check
        val setLoadingMethod = MainActivity::class.java.getDeclaredMethod("setLoading", Boolean::class.java)
        setLoadingMethod.isAccessible = true
        
        // Set loading to true
        setLoadingMethod.invoke(activity, true)
        
        val aiTextButton = activity.findViewById<MaterialButton>(R.id.aiTextButton)
        assertFalse("Button should be disabled during loading", aiTextButton.isEnabled)
        
        // Set loading to false
        setLoadingMethod.invoke(activity, false)
        assertTrue("Button should be enabled after loading", aiTextButton.isEnabled)
    }
}
