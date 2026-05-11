package com.skul9x.readoutloud.data

import android.content.Context
import com.skul9x.readoutloud.utils.SecurityUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class InfrastructureTest {

    private lateinit var context: Context
    private lateinit var modelManager: ModelManager
    private lateinit var quotaManager: ModelQuotaManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        modelManager = ModelManager.getInstance(context)
        quotaManager = ModelQuotaManager.getInstance(context)
        
        // Reset state before each test
        modelManager.resetToDefault()
        quotaManager.clearStatus()
    }

    @Test
    fun `test ModelManager default models`() {
        val models = modelManager.getModels()
        assertEquals(4, models.size)
        assertTrue(models.contains("models/gemini-3.1-flash-lite-preview"))
    }

    @Test
    fun `test ModelManager ModelItem persistence`() {
        val items = modelManager.getModelItems()
        assertEquals(4, items.size)
        assertTrue(items[0].isEnabled)
        
        // Toggle first model
        modelManager.toggleModel(0)
        assertFalse("First model should be disabled", modelManager.getModelItems()[0].isEnabled)
        assertEquals("Should have 3 enabled models", 3, modelManager.getModels().size)
    }

    @Test
    fun `test ModelManager move up and down`() {
        val initialModels = modelManager.getModels()
        val secondModel = initialModels[1]
        
        // Move second model up
        modelManager.moveUp(1)
        assertEquals(secondModel, modelManager.getModels()[0])
        
        // Move first model down
        modelManager.moveDown(0)
        assertEquals(secondModel, modelManager.getModels()[1])
    }

    @Test
    fun `test ModelQuotaManager availability`() {
        val model = "models/gemini-3.1-flash"
        val apiKey = "REDACTED_API_KEY_TEST_1"
        val hash = SecurityUtils.getPairHash(model, apiKey)
        
        assertTrue("Should be available initially", quotaManager.isAvailable(hash))
        
        quotaManager.markCooldown(hash)
        assertFalse("Should not be available during cooldown", quotaManager.isAvailable(hash))
    }

    @Test
    fun `test ModelQuotaManager persistence and cleanup`() {
        val model = "models/gemini-3.1-flash"
        val apiKey = "REDACTED_API_KEY_TEST_2"
        val hash = SecurityUtils.getPairHash(model, apiKey)
        
        quotaManager.markExhausted(hash)
        assertFalse("Should be exhausted", quotaManager.isAvailable(hash))
        
        // Simulate app restart
        val newQuotaManager = ModelQuotaManager.getInstance(context)
        assertFalse("Should still be exhausted after re-instantiation", newQuotaManager.isAvailable(hash))
        
        // Test manual cleanup (nothing should change since it's not expired)
        newQuotaManager.cleanupExpiredEntries()
        assertFalse("Should still be exhausted after cleanup", newQuotaManager.isAvailable(hash))
    }

    @Test
    fun `test SecurityUtils hashing`() {
        val key = "REDACTED_API_KEY_TEST_3"
        val hash1 = SecurityUtils.sha256(key)
        val hash2 = SecurityUtils.sha256(key)
        
        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length) // SHA-256 is 64 hex chars
    }
}
