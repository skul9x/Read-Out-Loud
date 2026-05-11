package com.skul9x.readoutloud.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.ModelManager
import com.skul9x.readoutloud.data.ModelQuotaManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class SettingsActivityTest {

    private lateinit var context: Context
    private lateinit var activityController: ActivityController<SettingsActivity>

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        
        // Reset managers
        ModelManager.getInstance(context).resetToDefault()
        ModelQuotaManager.getInstance(context).clearStatus()
        ApiKeyManager.getInstance(context).clearAllKeys()
        
        activityController = Robolectric.buildActivity(SettingsActivity::class.java)
    }

    @Test
    fun `test activity initialization and models list`() {
        val activity = activityController.setup().get()
        val recyclerView = activity.findViewById<RecyclerView>(R.id.modelsRecyclerView)
        
        assertNotNull("RecyclerView should be initialized", recyclerView)
        assertNotNull("Adapter should be set", recyclerView.adapter)
        
        val adapter = recyclerView.adapter as ModelAdapter
        assertEquals("Should have 4 models by default", 4, adapter.itemCount)
    }

    @Test
    fun `test model toggle logic`() {
        val activity = activityController.setup().get()
        val modelManager = ModelManager.getInstance(context)
        
        val initialItems = modelManager.getModelItems()
        assertTrue("First model should be enabled by default", initialItems[0].isEnabled)
        
        // Simulate toggle via adapter (we can't easily click UI in unit test without more boilerplate)
        // But we can check if the adapter's onToggle callback works if we exposed it, 
        // or just verify the ModelManager which the Activity uses.
        
        modelManager.toggleModel(0)
        assertFalse("First model should now be disabled", modelManager.getModelItems()[0].isEnabled)
    }
}
