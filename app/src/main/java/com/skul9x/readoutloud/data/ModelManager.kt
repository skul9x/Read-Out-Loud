package com.skul9x.readoutloud.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manager for handling the list of Gemini models and their priority.
 */
class ModelManager private constructor(context: Context) {

    companion object {
        private const val PREFS_NAME = "model_manager_prefs"
        private const val KEY_MODEL_ITEMS = "gemini_model_items"
        
        val DEFAULT_MODELS = listOf(
            "models/gemini-3.1-flash-lite-preview",
            "models/gemini-3-flash-preview",
            "models/gemini-2.5-flash-lite",
            "models/gemini-2.5-flash"
        )

        @Volatile
        private var instance: ModelManager? = null

        fun getInstance(context: Context): ModelManager {
            return instance ?: synchronized(this) {
                instance ?: ModelManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Get the list of ModelItems in order of priority.
     */
    fun getModelItems(): List<ModelItem> {
        val json = prefs.getString(KEY_MODEL_ITEMS, null) ?: return DEFAULT_MODELS.map { ModelItem(it) }
        return try {
            Json.decodeFromString<List<ModelItem>>(json)
        } catch (e: Exception) {
            DEFAULT_MODELS.map { ModelItem(it) }
        }
    }

    /**
     * Get the list of enabled model names for the API client.
     */
    fun getModels(): List<String> {
        return getModelItems().filter { it.isEnabled }.map { it.name }
    }

    /**
     * Save the model item list.
     */
    fun saveModelItems(items: List<ModelItem>) {
        val json = Json.encodeToString(items)
        prefs.edit().putString(KEY_MODEL_ITEMS, json).apply()
    }

    /**
     * Add a new model to the end of the list.
     */
    fun addModel(modelName: String) {
        val currentItems = getModelItems().toMutableList()
        if (currentItems.none { it.name == modelName }) {
            currentItems.add(ModelItem(modelName))
            saveModelItems(currentItems)
        }
    }

    /**
     * Toggle model enabled status.
     */
    fun toggleModel(index: Int) {
        val currentItems = getModelItems().toMutableList()
        if (index in currentItems.indices) {
            val item = currentItems[index]
            currentItems[index] = item.copy(isEnabled = !item.isEnabled)
            saveModelItems(currentItems)
        }
    }

    /**
     * Remove a model at the specified index.
     */
    fun removeModel(index: Int) {
        val currentItems = getModelItems().toMutableList()
        if (index in currentItems.indices) {
            currentItems.removeAt(index)
            saveModelItems(currentItems)
        }
    }

    /**
     * Move a model up in priority (towards index 0).
     */
    fun moveUp(index: Int) {
        if (index <= 0) return
        val currentItems = getModelItems().toMutableList()
        if (index in currentItems.indices) {
            val item = currentItems.removeAt(index)
            currentItems.add(index - 1, item)
            saveModelItems(currentItems)
        }
    }

    /**
     * Move a model down in priority.
     */
    fun moveDown(index: Int) {
        val currentItems = getModelItems().toMutableList()
        if (index >= currentItems.size - 1) return
        if (index in currentItems.indices) {
            val item = currentItems.removeAt(index)
            currentItems.add(index + 1, item)
            saveModelItems(currentItems)
        }
    }

    /**
     * Reset models to default.
     */
    fun resetToDefault() {
        saveModelItems(DEFAULT_MODELS.map { ModelItem(it) })
    }
}
