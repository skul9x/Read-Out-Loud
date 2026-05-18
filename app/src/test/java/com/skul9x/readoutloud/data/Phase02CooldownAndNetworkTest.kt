package com.skul9x.readoutloud.data

import android.content.Context
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import android.util.Log
import java.net.ConnectException
import java.net.UnknownHostException

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class Phase02CooldownAndNetworkTest {

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

        every { modelManager.getModels() } returns listOf("model-1", "model-2")
        every { apiKeyManager.getApiKeys() } returns listOf("key-1", "key-2")
        coEvery { quotaManager.isAvailable(any()) } returns true

        geminiApiClient = spyk(GeminiApiClient(context, apiKeyManager, modelManager, quotaManager))
    }

    @Test
    fun testCooldownDelayOn503() = runTest {
        coEvery { geminiApiClient.tryGenerateContent("key-1", "model-1", any()) } returns GeminiApiClient.ApiResult.ServiceUnavailable
        coEvery { geminiApiClient.tryGenerateContent("key-2", "model-1", any()) } returns GeminiApiClient.ApiResult.Success("Ok")

        val result = geminiApiClient.cleanTextWithGemini("Test input")

        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        coVerify { quotaManager.markCooldown(any()) }
    }

    @Test
    fun testPhysicalNetworkFailureTerminationOnUnknownHostException() = runTest {
        coEvery { 
            geminiApiClient.tryGenerateContent("key-1", "model-1", any()) 
        } throws UnknownHostException("No address associated with hostname")

        val result = geminiApiClient.cleanTextWithGemini("Test input")

        assertTrue(result is GeminiApiClient.GeminiResult.Error)
        assertTrue((result as GeminiApiClient.GeminiResult.Error).message.contains("mạng vật lý") || result.message.contains("kết nối"))
        
        coVerify(exactly = 1) { geminiApiClient.tryGenerateContent(any(), any(), any()) }
    }

    @Test
    fun testPhysicalNetworkFailureTerminationOnConnectException() = runTest {
        coEvery { 
            geminiApiClient.tryGenerateContent("key-1", "model-1", any()) 
        } throws ConnectException("Connection refused")

        val result = geminiApiClient.cleanTextWithGemini("Test input")

        assertTrue(result is GeminiApiClient.GeminiResult.Error)
        
        coVerify(exactly = 1) { geminiApiClient.tryGenerateContent(any(), any(), any()) }
    }
}
