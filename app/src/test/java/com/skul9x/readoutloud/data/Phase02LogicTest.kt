package com.skul9x.readoutloud.data

import android.content.Context
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import android.util.Log
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class Phase02LogicTest {

    private lateinit var context: Context
    private lateinit var apiKeyManager: ApiKeyManager
    private lateinit var modelManager: ModelManager
    private lateinit var quotaManager: ModelQuotaManager
    private lateinit var geminiApiClient: GeminiApiClient

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        apiKeyManager = mockk()
        modelManager = mockk()
        quotaManager = mockk(relaxed = true)

        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0

        // Default models and keys
        every { modelManager.getModels() } returns listOf("model-1", "model-2")
        every { apiKeyManager.getApiKeys() } returns emptyList()
        every { quotaManager.isAvailable(any<String>()) } returns true
        
        // Initialize client
        geminiApiClient = spyk(GeminiApiClient(context, apiKeyManager, modelManager, quotaManager))
    }

    @Test
    fun `test nested loop - rotation through keys then models`() = runBlocking {
        // Setup: 2 models, 2 keys
        val keys = listOf("key-1", "key-2")
        every { apiKeyManager.getApiKeys() } returns keys

        // Scenario:
        // Model 1, Key 1: RateLimited (429) -> markCooldown
        // Model 1, Key 2: QuotaExceeded (429 daily) -> markExhausted
        // Model 2, Key 1: Success
        
        coEvery { geminiApiClient.tryGenerateContent("key-1", "model-1", any()) } returns GeminiApiClient.ApiResult.RateLimited
        coEvery { geminiApiClient.tryGenerateContent("key-2", "model-1", any()) } returns GeminiApiClient.ApiResult.QuotaExceeded
        coEvery { geminiApiClient.tryGenerateContent("key-1", "model-2", any()) } returns GeminiApiClient.ApiResult.Success("Success text")

        val result = geminiApiClient.cleanTextWithGemini("Input")

        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Success text", (result as GeminiApiClient.GeminiResult.Success).text)
        assertEquals("model-2", result.model)

        // Verify quota manager calls
        verify { quotaManager.markCooldown(com.skul9x.readoutloud.utils.SecurityUtils.getPairHash("model-1", "key-1")) }
        verify { quotaManager.markExhausted(com.skul9x.readoutloud.utils.SecurityUtils.getPairHash("model-1", "key-2")) }
    }

    @Test
    fun `test skip unavailable pairs`() = runBlocking {
        val keys = listOf("key-1")
        every { apiKeyManager.getApiKeys() } returns keys
        
        // Scenario: model-1/key-1 is NOT available
        val hash = com.skul9x.readoutloud.utils.SecurityUtils.getPairHash("model-1", "key-1")
        every { quotaManager.isAvailable(hash) } returns false
        every { quotaManager.isAvailable(not(hash)) } returns true

        coEvery { geminiApiClient.tryGenerateContent("key-1", "model-2", any()) } returns GeminiApiClient.ApiResult.Success("Success from model 2")

        val result = geminiApiClient.cleanTextWithGemini("Input")

        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("model-2", (result as GeminiApiClient.GeminiResult.Success).model)
        
        // Verify model-1/key-1 was NEVER called
        coVerify(exactly = 0) { geminiApiClient.tryGenerateContent("key-1", "model-1", any()) }
    }

    @Test
    fun `test model not found skips to next model`() = runBlocking {
        val keys = listOf("key-1", "key-2")
        every { apiKeyManager.getApiKeys() } returns keys

        // Scenario: model-1/key-1 returns ModelNotFound -> should SKIP key-2 for model-1
        coEvery { geminiApiClient.tryGenerateContent("key-1", "model-1", any()) } returns GeminiApiClient.ApiResult.ModelNotFound
        coEvery { geminiApiClient.tryGenerateContent("key-1", "model-2", any()) } returns GeminiApiClient.ApiResult.Success("Win")

        val result = geminiApiClient.cleanTextWithGemini("Input")

        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("model-2", (result as GeminiApiClient.GeminiResult.Success).model)

        // Verify key-2/model-1 was NEVER called
        coVerify(exactly = 0) { geminiApiClient.tryGenerateContent("key-2", "model-1", any()) }
    }

    @Test
    fun `test IOException triggers retry`() = runBlocking {
        val keys = listOf("key-1")
        every { apiKeyManager.getApiKeys() } returns keys
        every { quotaManager.isAvailable(any()) } returns true

        // First call fails with IOException, second succeeds
        coEvery { 
            geminiApiClient.tryGenerateContent("key-1", "model-1", any()) 
        } throws IOException("Network error") andThen GeminiApiClient.ApiResult.Success("Recovered")

        val result = geminiApiClient.cleanTextWithGemini("Input")

        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Recovered", (result as GeminiApiClient.GeminiResult.Success).text)

        // Verify it was called twice
        coVerify(exactly = 2) { geminiApiClient.tryGenerateContent("key-1", "model-1", any()) }
    }
}
