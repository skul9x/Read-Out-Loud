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

@RunWith(RobolectricTestRunner::class)
class GeminiRotationTest {

    private lateinit var context: Context
    private lateinit var apiKeyManager: ApiKeyManager
    private lateinit var geminiApiClient: GeminiApiClient

    @Before
    fun setUp() {
        println("Setting up GeminiRotationTest...")
        context = mockk(relaxed = true)
        apiKeyManager = mockk()
        every { apiKeyManager.getApiKeys() } returns emptyList()

        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0

        // Initialize client with mocked ApiKeyManager (DI)
        geminiApiClient = spyk(GeminiApiClient(context, apiKeyManager))
    }

    @Test
    fun `test rotation when first key fails with QuotaExceeded`() = runBlocking {
        // 1. Setup two API keys
        val keys = listOf("key-1", "key-2")
        every { apiKeyManager.getApiKeys() } returns keys
        geminiApiClient.refreshApiKeys()

        // 2. Mock tryGenerateContent behavior
        // First key (key-1) returns QuotaExceeded
        coEvery { 
            geminiApiClient.tryGenerateContent("key-1", any(), any()) 
        } returns GeminiApiClient.ApiResult.QuotaExceeded

        // Second key (key-2) returns Success
        coEvery { 
            geminiApiClient.tryGenerateContent("key-2", any(), any()) 
        } returns GeminiApiClient.ApiResult.Success("Cleaned text from Key 2")

        // 3. Execute
        val result = geminiApiClient.cleanTextWithGemini("Input text")

        // 4. Verify
        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Cleaned text from Key 2", (result as GeminiApiClient.GeminiResult.Success).text)
        assertEquals("models/gemini-2.0-flash", (result as GeminiApiClient.GeminiResult.Success).model)
        
        // Verify current status shows Key 2 was the winner
        assertTrue(geminiApiClient.getCurrentStatus().contains("API 2/2"))
        
        // Verify key-1 was actually tried
        coVerify { geminiApiClient.tryGenerateContent("key-1", any(), any()) }
    }

    @Test
    fun `test model switching when model is not found`() = runBlocking {
        // 1. Setup one API key
        val keys = listOf("key-1")
        every { apiKeyManager.getApiKeys() } returns keys
        geminiApiClient.refreshApiKeys()

        // 2. Mock behavior: 
        // First model fails with ModelNotFound
        coEvery { 
            geminiApiClient.tryGenerateContent("key-1", "models/gemini-2.0-flash", any()) 
        } returns GeminiApiClient.ApiResult.ModelNotFound

        // Second model succeeds
        coEvery { 
            geminiApiClient.tryGenerateContent("key-1", "models/gemini-2.0-flash-lite-preview-02-05", any()) 
        } returns GeminiApiClient.ApiResult.Success("Success from second model")

        // 3. Execute
        val result = geminiApiClient.cleanTextWithGemini("Input text")

        // 4. Verify
        assertTrue(result is GeminiApiClient.GeminiResult.Success)
        assertEquals("Success from second model", (result as GeminiApiClient.GeminiResult.Success).text)
        assertEquals("models/gemini-2.0-flash-lite-preview-02-05", (result as GeminiApiClient.GeminiResult.Success).model)
    }
}

