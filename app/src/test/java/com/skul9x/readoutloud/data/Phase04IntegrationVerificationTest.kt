package com.skul9x.readoutloud.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.skul9x.readoutloud.utils.SecurityUtils
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import android.util.Log

@RunWith(RobolectricTestRunner::class)
class Phase04IntegrationVerificationTest {

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

        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0

        // Active 2 models & 2 keys
        every { modelManager.getModels() } returns listOf("models/gemini-3.1-flash-lite", "models/gemini-2.5-flash-lite")
        every { apiKeyManager.getApiKeys() } returns listOf("key-1", "key-2")
        coEvery { quotaManager.isAvailable(any()) } returns true

        geminiApiClient = spyk(GeminiApiClient(context, apiKeyManager, modelManager, quotaManager))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testCompleteSuccessfulFlowWithModelFirstRotation() = runTest {
        // model 1/key 1 fails with daily quota 429
        coEvery { geminiApiClient.tryGenerateContent("key-1", "models/gemini-3.1-flash-lite", any()) } returns GeminiApiClient.ApiResult.QuotaExceeded
        // model 1/key 2 fails with 503
        coEvery { geminiApiClient.tryGenerateContent("key-2", "models/gemini-3.1-flash-lite", any()) } returns GeminiApiClient.ApiResult.ServiceUnavailable
        // model 2/key 1 succeeds
        coEvery { geminiApiClient.tryGenerateContent("key-1", "models/gemini-2.5-flash-lite", any()) } returns GeminiApiClient.ApiResult.Success("Clean text")

        val result = geminiApiClient.cleanTextWithGemini("Input")

        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Clean text", (result as GeminiApiClient.GeminiResult.Success).text)
        assertEquals("models/gemini-2.5-flash-lite", result.model)

        // Verify markExhausted and markCooldown were called on correct hashes
        val hashExhausted = SecurityUtils.getPairHash("models/gemini-3.1-flash-lite", "key-1")
        val hashCooldown = SecurityUtils.getPairHash("models/gemini-3.1-flash-lite", "key-2")
        
        coVerify { quotaManager.markExhausted(hashExhausted) }
        coVerify { quotaManager.markCooldown(hashCooldown) }
    }

    @Test
    fun testIntegrationWithRealQuotaManager() = runTest {
        val realContext = ApplicationProvider.getApplicationContext<Context>()
        val realQuotaManager = ModelQuotaManager.getInstance(realContext)
        realQuotaManager.clearStatus()

        // Mock apiKeyManager and modelManager
        val mockApiKeyManager = mockk<ApiKeyManager>()
        val mockModelManager = mockk<ModelManager>()
        
        every { mockModelManager.getModels() } returns listOf("models/gemini-3.1-flash-lite", "models/gemini-2.5-flash-lite")
        every { mockApiKeyManager.getApiKeys() } returns listOf("key-1", "key-2")

        val realClient = spyk(GeminiApiClient(realContext, mockApiKeyManager, mockModelManager, realQuotaManager))

        // model 1/key 1 fails with daily quota 429 (QuotaExceeded)
        coEvery { realClient.tryGenerateContent("key-1", "models/gemini-3.1-flash-lite", any()) } returns GeminiApiClient.ApiResult.QuotaExceeded
        // model 1/key 2 fails with 503 (ServiceUnavailable)
        coEvery { realClient.tryGenerateContent("key-2", "models/gemini-3.1-flash-lite", any()) } returns GeminiApiClient.ApiResult.ServiceUnavailable
        // model 2/key 1 succeeds
        coEvery { realClient.tryGenerateContent("key-1", "models/gemini-2.5-flash-lite", any()) } returns GeminiApiClient.ApiResult.Success("Clean text")

        val result = realClient.cleanTextWithGemini("Input")

        // Assert API client returns successful fallback model
        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Clean text", (result as GeminiApiClient.GeminiResult.Success).text)
        assertEquals("models/gemini-2.5-flash-lite", result.model)

        // Verify QuotaManager has recorded the status of errors properly
        val hashExhausted = SecurityUtils.getPairHash("models/gemini-3.1-flash-lite", "key-1")
        val hashCooldown = SecurityUtils.getPairHash("models/gemini-3.1-flash-lite", "key-2")
        val hashSuccess = SecurityUtils.getPairHash("models/gemini-2.5-flash-lite", "key-1")

        // Directly query the real quota manager to ensure states are active
        assertTrue(!realQuotaManager.isAvailable(hashExhausted)) // Should be exhausted (blocked)
        assertTrue(!realQuotaManager.isAvailable(hashCooldown))  // Should be in cooldown (blocked)
        assertTrue(realQuotaManager.isAvailable(hashSuccess))    // Should be available (success)

        // Verify the persistent aspect: read directly from SharedPreferences
        val prefs = realContext.getSharedPreferences("model_quota_prefs", Context.MODE_PRIVATE)
        val exhaustedExpiry = prefs.getLong("exhausted_$hashExhausted", 0L)
        assertTrue(exhaustedExpiry > System.currentTimeMillis())

        // The cooldown should NOT be written to SharedPreferences (in-memory only)
        val cooldownInPrefs = prefs.getLong("exhausted_$hashCooldown", 0L)
        assertEquals(0L, cooldownInPrefs)
    }
}

