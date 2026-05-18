package com.skul9x.readoutloud.data

import android.content.Context
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.skul9x.readoutloud.utils.SecurityUtils

@RunWith(RobolectricTestRunner::class)
class Phase01ModelAlignmentTest {

    private lateinit var context: Context
    private lateinit var modelManager: ModelManager

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        modelManager = ModelManager.getInstance(context)
        modelManager.resetToDefault()
    }

    @Test
    fun testPairHashFormat() {
        val model = "models/gemini-3.1-flash-lite"
        val key = "AIzaSyDummyKeyForTestingRotation"
        val pairHash = SecurityUtils.getPairHash(model, key)
        
        // Spec: HashedId = ModelName + "::" + SHA256(ApiKey).take(16) [8 bytes = 16 hex chars]
        assertTrue(pairHash.startsWith("$model::"))
        val hashPart = pairHash.substringAfter("::")
        assertEquals(16, hashPart.length) // 8 bytes hash = 16 hex characters
    }

    @Test
    fun testDefaultModelsPriority() {
        val expectedModels = listOf(
            "models/gemini-3.1-flash-lite",
            "models/gemini-2.5-flash-lite",
            "models/gemini-3-flash-preview",
            "models/gemini-2.5-flash"
        )
        assertEquals(expectedModels, ModelManager.DEFAULT_MODELS)
    }

    @Test
    fun testModelManagerDefaultPriorityOnFirstStart() {
        val modelNames = modelManager.getModels()
        assertEquals(4, modelNames.size)
        assertEquals("models/gemini-3.1-flash-lite", modelNames[0])
        assertEquals("models/gemini-2.5-flash-lite", modelNames[1])
        assertEquals("models/gemini-3-flash-preview", modelNames[2])
        assertEquals("models/gemini-2.5-flash", modelNames[3])
    }
}
