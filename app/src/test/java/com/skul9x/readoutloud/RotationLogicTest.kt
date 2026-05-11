package com.skul9x.readoutloud

import android.content.Context
import android.util.Log
import com.skul9x.readoutloud.data.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RotationLogicTest {

    private lateinit var context: Context
    private lateinit var apiKeyManager: ApiKeyManager
    private lateinit var modelManager: ModelManager
    private lateinit var quotaManager: ModelQuotaManager
    private lateinit var apiClient: GeminiApiClient

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        apiKeyManager = mockk()
        modelManager = mockk()
        quotaManager = mockk()
        
        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        
        // Mock default behavior
        every { modelManager.getModels() } returns listOf("model1", "model2")
        every { apiKeyManager.getApiKeys() } returns listOf("key1", "key2")
        every { quotaManager.isAvailable(any()) } returns true
        
        apiClient = spyk(GeminiApiClient(context, apiKeyManager, modelManager, quotaManager))
    }

    @Test
    fun `test rotation - failover to second key`() = runBlocking {
        // First key fails with Rate Limit, second key succeeds
        coEvery { apiClient.tryGenerateContent("key1", "model1", any()) } returns GeminiApiClient.ApiResult.RateLimited
        coEvery { apiClient.tryGenerateContent("key2", "model1", any()) } returns GeminiApiClient.ApiResult.Success("Cleaned text")
        
        every { quotaManager.markCooldown(any()) } just Runs

        val result = apiClient.cleanTextWithGemini("Dirty text")
        
        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Cleaned text", (result as GeminiApiClient.GeminiResult.Success).text)
        assertEquals("model1", result.model)
        
        // Verify key1 was marked as cooldown
        verify { quotaManager.markCooldown(any()) }
    }

    @Test
    fun `test rotation - failover to second model when all keys fail for first model`() = runBlocking {
        // All keys for model1 fail with Quota Exceeded
        coEvery { apiClient.tryGenerateContent(any(), "model1", any()) } returns GeminiApiClient.ApiResult.QuotaExceeded
        // First key for model2 succeeds
        coEvery { apiClient.tryGenerateContent("key1", "model2", any()) } returns GeminiApiClient.ApiResult.Success("Cleaned text model 2")
        
        every { quotaManager.markExhausted(any()) } just Runs

        val result = apiClient.cleanTextWithGemini("Dirty text")
        
        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Cleaned text model 2", (result as GeminiApiClient.GeminiResult.Success).text)
        assertEquals("model2", result.model)
        
        // Verify keys for model1 were marked as exhausted
        verify(atLeast = 1) { quotaManager.markExhausted(any()) }
    }

    @Test
    fun `test rotation - model not found skips to next model immediately`() = runBlocking {
        // model1 returns ModelNotFound on first key
        coEvery { apiClient.tryGenerateContent("key1", "model1", any()) } returns GeminiApiClient.ApiResult.ModelNotFound
        // model2 succeeds
        coEvery { apiClient.tryGenerateContent("key1", "model2", any()) } returns GeminiApiClient.ApiResult.Success("Success model 2")
        
        val result = apiClient.cleanTextWithGemini("text")
        
        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("model2", (result as GeminiApiClient.GeminiResult.Success).model)
        
        // Verify key2 was NOT tried for model1 (it should break inner loop)
        coVerify(exactly = 0) { apiClient.tryGenerateContent("key2", "model1", any()) }
    }

    @Test
    fun `test rotation - all exhausted returns AllQuotaExhausted`() = runBlocking {
        coEvery { apiClient.tryGenerateContent(any(), any(), any()) } returns GeminiApiClient.ApiResult.QuotaExceeded
        every { quotaManager.markExhausted(any()) } just Runs
        
        val result = apiClient.cleanTextWithGemini("text")
        
        assertEquals(GeminiApiClient.GeminiResult.AllQuotaExhausted, result)
    }

    @Test(expected = java.io.IOException::class)
    fun `test streaming safety - no retry after streaming started`() = runBlocking {
        // Setup keys and models
        every { modelManager.getModels() } returns listOf("model1")
        every { apiKeyManager.getApiKeys() } returns listOf("key1")
        
        // Mock tryGenerateContent to throw IOException
        coEvery { apiClient.tryGenerateContent(any(), any(), any()) } throws java.io.IOException("Network error")
        
        // Simulate streaming started
        apiClient.hasStartedStreaming = true
        
        // This should throw IOException immediately without retrying (cleanTextWithGemini calls retryWithBackoff)
        apiClient.cleanTextWithGemini("text")
        
        // Verify it was only called once
        coVerify(exactly = 1) { apiClient.tryGenerateContent(any(), any(), any()) }
    }
}
