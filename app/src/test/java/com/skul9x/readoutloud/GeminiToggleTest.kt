package com.skul9x.readoutloud

import android.content.Context
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Use a stable SDK for Robolectric
class GeminiToggleTest {

    private lateinit var activity: MainActivity
    private val PREFS_NAME = "ReadOutLoudPrefs"
    private val KEY_GEMINI_ENABLED = "gemini_enabled"

    @Before
    fun setUp() {
        // Start MainActivity using Robolectric
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
    }

    @Test
    fun testDefaultStateIsOff() {
        val sharedPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isGeminiEnabled = sharedPrefs.getBoolean(KEY_GEMINI_ENABLED, false)
        
        // Assert initial state in prefs
        assertFalse("Gemini should be disabled by default in prefs", isGeminiEnabled)
    }

    @Test
    fun testToggleEnablesGeminiAndSavesToPrefs() {
        val sharedPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Find the toggle (MaterialSwitch)
        // Note: Using binding is better but we need to access it from activity
        // For simplicity in test, we can use findViewById
        val geminiToggle = activity.findViewById<com.google.android.material.materialswitch.MaterialSwitch>(R.id.geminiToggle)
        
        // 1. Initial state should be false
        assertFalse(geminiToggle.isChecked)
        
        // 2. Perform click/check
        geminiToggle.isChecked = true
        
        // 3. Verify it's saved in prefs
        assertTrue("Gemini should be saved as ENABLED in prefs", sharedPrefs.getBoolean(KEY_GEMINI_ENABLED, false))
        
        // 4. Verify text status bar updated
        val statusText = activity.findViewById<android.widget.TextView>(R.id.statusText)
        assertEquals("Gemini AI: ON", statusText.text.toString())
    }

    @Test
    fun testToggleDisablesGeminiAndSavesToPrefs() {
        val sharedPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val geminiToggle = activity.findViewById<com.google.android.material.materialswitch.MaterialSwitch>(R.id.geminiToggle)
        
        // Set to true first
        geminiToggle.isChecked = true
        assertTrue(sharedPrefs.getBoolean(KEY_GEMINI_ENABLED, false))
        
        // Perform uncheck
        geminiToggle.isChecked = false
        
        // Verify it's saved as false
        assertFalse("Gemini should be saved as DISABLED in prefs", sharedPrefs.getBoolean(KEY_GEMINI_ENABLED, false))
        
        // Verify status text
        val statusText = activity.findViewById<android.widget.TextView>(R.id.statusText)
        assertEquals("Gemini AI: OFF", statusText.text.toString())
    }
}
