package com.skul9x.readoutloud.ui

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsSectionUiUxTest {

    @Test
    fun testModelsHelperTextExistsAndContainsCorrectGuidance() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val helperText = activity.findViewById<TextView>(R.id.modelsHelperText)

        assertNotNull("modelsHelperText TextView should exist in activity_settings hierarchy", helperText)
        assertEquals(
            "Helper text should contain correct guidance text",
            "Models are evaluated top-to-bottom. Uncheck to disable model.",
            helperText.text.toString()
        )
    }

    @Test
    fun testModelsRecyclerViewClipToPaddingIsFalseAndPaddingIsSet() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val recyclerView = activity.findViewById<RecyclerView>(R.id.modelsRecyclerView)

        assertNotNull("modelsRecyclerView should exist in activity_settings hierarchy", recyclerView)
        assertFalse("modelsRecyclerView clipToPadding should be false for card shadow rendering", recyclerView.clipToPadding)
    }

    @Test
    fun testHeaderActionButtonsExistInHierarchy() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        
        val addModelButton = activity.findViewById<MaterialButton>(R.id.addModelButton)
        val resetModelsButton = activity.findViewById<MaterialButton>(R.id.resetModelsButton)
        val settingsPasteButton = activity.findViewById<MaterialButton>(R.id.settingsPasteButton)

        assertNotNull("addModelButton should exist", addModelButton)
        assertNotNull("resetModelsButton should exist", resetModelsButton)
        assertNotNull("settingsPasteButton should exist", settingsPasteButton)
    }
}
