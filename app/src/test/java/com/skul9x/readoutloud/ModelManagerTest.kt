package com.skul9x.readoutloud

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.skul9x.readoutloud.data.ModelItem
import com.skul9x.readoutloud.data.ModelManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ModelManagerTest {

    private lateinit var context: Context
    private lateinit var modelManager: ModelManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        modelManager = ModelManager.getInstance(context)
        modelManager.resetToDefault()
    }

    @Test
    fun `test initial models are default`() {
        val models = modelManager.getModels()
        assertEquals(ModelManager.DEFAULT_MODELS, models)
    }

    @Test
    fun `test add model`() {
        val newModel = "models/gemini-pro-new"
        modelManager.addModel(newModel)
        
        val models = modelManager.getModels()
        assertTrue(models.contains(newModel))
        assertEquals(ModelManager.DEFAULT_MODELS.size + 1, models.size)
    }

    @Test
    fun `test toggle model`() {
        // Toggle first model off
        modelManager.toggleModel(0)
        
        val models = modelManager.getModels()
        assertFalse(models.contains(ModelManager.DEFAULT_MODELS[0]))
        assertEquals(ModelManager.DEFAULT_MODELS.size - 1, models.size)
        
        // Toggle it back on
        modelManager.toggleModel(0)
        assertTrue(modelManager.getModels().contains(ModelManager.DEFAULT_MODELS[0]))
    }

    @Test
    fun `test move up`() {
        val secondModel = ModelManager.DEFAULT_MODELS[1]
        modelManager.moveUp(1)
        
        val models = modelManager.getModels()
        assertEquals(secondModel, models[0])
    }

    @Test
    fun `test move down`() {
        val firstModel = ModelManager.DEFAULT_MODELS[0]
        modelManager.moveDown(0)
        
        val models = modelManager.getModels()
        assertEquals(firstModel, models[1])
    }

    @Test
    fun `test remove model`() {
        val initialSize = modelManager.getModels().size
        modelManager.removeModel(0)
        
        assertEquals(initialSize - 1, modelManager.getModels().size)
    }
}
