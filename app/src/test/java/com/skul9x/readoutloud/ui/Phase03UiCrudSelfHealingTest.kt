package com.skul9x.readoutloud.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.skul9x.readoutloud.data.ModelItem
import com.skul9x.readoutloud.data.ModelManager

@RunWith(RobolectricTestRunner::class)
class Phase03UiCrudSelfHealingTest {

    private lateinit var context: Context
    private lateinit var modelManager: ModelManager

    @Before
    fun setUp() {
        ModelManager.resetInstance()
        context = ApplicationProvider.getApplicationContext()
        modelManager = ModelManager.getInstance(context)
        modelManager.resetToDefault()
    }

    @Test
    fun testSelfHealingWhenModelsListIsSavedEmpty() {
        // Simulating clearing all models (empty list saved)
        modelManager.saveModelItems(emptyList())
        
        // ModelManager must automatically self-heal and return DEFAULT_MODELS
        val models = modelManager.getModelItems()
        assertEquals(ModelManager.DEFAULT_MODELS.size, models.size)
        assertEquals(ModelManager.DEFAULT_MODELS[0], models[0].name)
    }

    @Test
    fun testSelfHealingWhenAllModelsAreDisabled() {
        // Save items with all isEnabled = false
        val disabledItems = ModelManager.DEFAULT_MODELS.map { ModelItem(it, isEnabled = false) }
        modelManager.saveModelItems(disabledItems)
        
        // ModelManager getModels() (which returns enabled list) must self-heal and return default enabled list
        // to avoid app calling API with empty model list
        val enabledModels = modelManager.getModels()
        assertTrue(enabledModels.isNotEmpty())
        assertEquals(ModelManager.DEFAULT_MODELS.size, enabledModels.size)
        assertTrue(enabledModels.contains("models/gemini-3.1-flash-lite"))
    }

    @Test
    fun testModelCrudOperations() {
        val initialSize = modelManager.getModelItems().size
        
        // Add
        modelManager.addModel("models/gemini-2.0-pro-test")
        val afterAdd = modelManager.getModelItems()
        assertEquals(initialSize + 1, afterAdd.size)
        assertTrue(afterAdd.any { it.name == "models/gemini-2.0-pro-test" })
        
        // Remove
        val index = afterAdd.indexOfFirst { it.name == "models/gemini-2.0-pro-test" }
        modelManager.removeModel(index)
        val afterRemove = modelManager.getModelItems()
        assertEquals(initialSize, afterRemove.size)
        assertTrue(afterRemove.none { it.name == "models/gemini-2.0-pro-test" })
    }
}
