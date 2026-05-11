package com.skul9x.readoutloud.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resumeWithException
import kotlin.math.pow

/**
 * Gemini API client with automatic failover between API keys and models.
 * Re-uses exact logic from RSS-Reader as requested.
 */
class GeminiApiClient(
    context: Context,
    private val apiKeyManager: ApiKeyManager = ApiKeyManager.getInstance(context),
    private val modelManager: ModelManager = ModelManager.getInstance(context),
    private val quotaManager: ModelQuotaManager = ModelQuotaManager.getInstance(context)
) {
    companion object {
        private const val TAG = "GeminiApiClient"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

        // Models to fallback between
        private val MODELS = listOf(
            "models/gemini-3-flash-preview",
            "models/gemini-2.5-flash",
            "models/gemini-2.5-flash-lite",
            "models/gemini-flash-latest",
            "models/gemini-flash-lite-latest"
        )
    }
    private val stateMutex = Mutex()
    
    @Volatile
    private var apiKeys: List<String> = apiKeyManager.getApiKeys().toList()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var currentApiKeyIndex = 0
    @Volatile
    private var currentModelIndex = 0

    internal var hasStartedStreaming = false // Requirement 17

    suspend fun refreshApiKeys() {
        stateMutex.withLock {
            apiKeys = apiKeyManager.getApiKeys().toList()
            if (apiKeys.isEmpty() || currentApiKeyIndex >= apiKeys.size) {
                currentApiKeyIndex = 0
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun cleanTextWithGemini(content: String): GeminiResult {
        return withContext(Dispatchers.IO) {
            val keys = apiKeyManager.getApiKeys().toList()
            val models = modelManager.getModels()

            if (keys.isEmpty()) return@withContext GeminiResult.NoApiKeys
            if (models.isEmpty()) return@withContext GeminiResult.Error("No models configured")

            hasStartedStreaming = false

            // Requirement 10: Outer (Models), Inner (Keys)
            for (mIndex in models.indices) {
                val model = models[mIndex]
                
                for (kIndex in keys.indices) {
                    val apiKey = keys[kIndex]
                    val pairHash = com.skul9x.readoutloud.utils.SecurityUtils.getPairHash(model, apiKey)

                    // Requirement 11: Check isAvailable before each call
                    if (!quotaManager.isAvailable(pairHash)) {
                        Log.d(TAG, "Skipping $model with key ${kIndex + 1}: Not available")
                        continue
                    }

                    Log.d(TAG, "Trying Model: $model | API key ${kIndex + 1}/${keys.size}")

                    // Requirement 18: retryWithBackoff only for IOException before streaming starts
                    val result = retryWithBackoff {
                        tryGenerateContent(apiKey, model, content)
                    }

                    when (result) {
                        is ApiResult.Success -> {
                            stateMutex.withLock {
                                currentApiKeyIndex = kIndex
                                currentModelIndex = mIndex
                            }
                            return@withContext GeminiResult.Success(result.text, model)
                        }
                        is ApiResult.QuotaExceeded -> {
                            // Requirement 14: 429 daily/quota -> markExhausted
                            quotaManager.markExhausted(pairHash)
                            continue 
                        }
                        is ApiResult.RateLimited -> {
                            // Requirement 13: 429 RPM/Rate limit -> markCooldown
                            quotaManager.markCooldown(pairHash)
                            continue
                        }
                        is ApiResult.ServiceUnavailable -> {
                            // Requirement 15: 503 -> markCooldown
                            quotaManager.markCooldown(pairHash)
                            continue
                        }
                        is ApiResult.ModelNotFound -> {
                            // Requirement 16: 400/404 -> Next Model (break inner loop)
                            Log.w(TAG, "Model $model not found or bad request, skipping model")
                            break
                        }
                        is ApiResult.Error -> {
                            Log.e(TAG, "API error: ${result.message}")
                            continue
                        }
                    }
                }
            }

            return@withContext GeminiResult.AllQuotaExhausted
        }
    }

    private suspend fun <T> retryWithBackoff(
        maxRetries: Int = 2,
        initialDelay: Long = 1000,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(maxRetries) { attempt ->
            try {
                return block()
            } catch (e: IOException) {
                if (hasStartedStreaming || attempt == maxRetries - 1) throw e
                Log.w(TAG, "IOException on attempt ${attempt + 1}, retrying in $currentDelay ms...")
                delay(currentDelay)
                currentDelay *= 2
            }
        }
        return block() // Should not reach here normally
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    internal suspend fun tryGenerateContent(apiKey: String, model: String, content: String): ApiResult {
        return try {
            coroutineContext.ensureActive()
            
            val prompt = buildCleanPrompt(content)
            val requestBody = buildRequestBody(prompt)
            
            val request = Request.Builder()
                .url("$BASE_URL/$model:generateContent?key=$apiKey")
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = suspendCancellableCoroutine<Response> { continuation ->
                val call = client.newCall(request)
                
                continuation.invokeOnCancellation {
                    call.cancel()
                }
                
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (!continuation.isCompleted) {
                            continuation.resumeWithException(e)
                        }
                    }
                    
                    override fun onResponse(call: Call, response: Response) {
                        if (!continuation.isCompleted) {
                            continuation.resume(response) { _ ->
                                response.close()
                            }
                        } else {
                            response.close()
                        }
                    }
                })
            }
            
            response.use { resp ->
                val responseBody = resp.body?.string() ?: ""
                
                when (resp.code) {
                    200 -> {
                        val rawText = extractText(responseBody)
                        if (rawText.isNotBlank()) {
                            ApiResult.Success(rawText.trim())
                        } else {
                            ApiResult.Error("Empty response from API")
                        }
                    }
                    429 -> {
                        if (responseBody.contains("quota", ignoreCase = true)) {
                            ApiResult.QuotaExceeded
                        } else {
                            ApiResult.RateLimited
                        }
                    }
                    503 -> ApiResult.ServiceUnavailable
                    400, 404 -> ApiResult.ModelNotFound
                    else -> ApiResult.Error("HTTP ${resp.code}: $responseBody")
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    private fun buildCleanPrompt(content: String): String {
        return """
Bạn là trợ lý AI chuyên dọn dẹp văn bản để phục vụ việc đọc bằng giọng nói (Text-to-Speech).
Nhiệm vụ của bạn là viết lại đoạn văn bản sau để nó chỉ còn là văn bản thuần túy và các dấu câu cần thiết.

YÊU CẦU BẮT BUỘC:
1. TUYỆT ĐỐI KHÔNG dùng bất kỳ ký tự định dạng Markdown nào (*, #, _, [, ], `, etc.).
2. TUYỆT ĐỐI KHÔNG để lại bất kỳ đường dẫn URL (Link) nào.
3. TUYỆT ĐỐI KHÔNG sử dụng bảng biểu (Tables). Nếu văn bản gốc có bảng, hãy diễn đạt lại thông tin trong bảng thành các câu văn xuôi hoặc danh sách liệt kê đơn giản (1. 2. 3...).
4. Loại bỏ các ký tự lạ, ký tự kỹ thuật không có nghĩa trong văn bản đọc.
5. Giữ nguyên ý chính và ngôn ngữ của văn bản.
6. Chỉ trả về kết quả đã được làm sạch, không thêm bất kỳ câu dẫn dắt hay chào hỏi nào.

Nội dung cần xử lý:
$content
""".trim()
    }

    private fun buildRequestBody(prompt: String): String {
        val json = buildJsonObject {
            putJsonArray("contents") {
                addJsonObject {
                    putJsonArray("parts") {
                        addJsonObject { put("text", prompt) }
                    }
                }
            }
            putJsonObject("generationConfig") {
                put("temperature", 0.5)
                put("topP", 0.95)
            }
            // Add safety settings same as RSS-Reader for maximum availability
            putJsonArray("safetySettings") {
                listOf(
                    "HARM_CATEGORY_HARASSMENT",
                    "HARM_CATEGORY_HATE_SPEECH",
                    "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                    "HARM_CATEGORY_DANGEROUS_CONTENT"
                ).forEach { category ->
                    addJsonObject {
                        put("category", category)
                        put("threshold", "BLOCK_NONE")
                    }
                }
            }
        }
        return json.toString()
    }

    private fun extractText(responseBody: String): String {
        return try {
            val json = Json.parseToJsonElement(responseBody).jsonObject
            val candidates = json["candidates"]?.jsonArray ?: return ""
            if (candidates.isEmpty()) return ""
            
            val firstCandidate = candidates[0].jsonObject
            val content = firstCandidate["content"]?.jsonObject ?: return ""
            val parts = content["parts"]?.jsonArray ?: return ""
            if (parts.isEmpty()) return ""
            
            parts[0].jsonObject["text"]?.jsonPrimitive?.content ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getCurrentStatus(): String {
        val keys = apiKeys
        if (keys.isEmpty()) return "Chưa có API key"
        
        val safeKeyIndex = if (currentApiKeyIndex < keys.size) currentApiKeyIndex else 0
        val safeModelIndex = if (currentModelIndex < MODELS.size) currentModelIndex else 0
        return "API ${safeKeyIndex + 1}/${keys.size}, Model: ${MODELS[safeModelIndex].substringAfter("/")}"
    }

    sealed class ApiResult {
        data class Success(val text: String) : ApiResult()
        object QuotaExceeded : ApiResult()
        object RateLimited : ApiResult()
        object ServiceUnavailable : ApiResult()
        object ModelNotFound : ApiResult()
        data class Error(val message: String) : ApiResult()
    }

    sealed class GeminiResult {
        data class Success(val text: String, val model: String) : GeminiResult()
        object AllQuotaExhausted : GeminiResult()
        object NoApiKeys : GeminiResult()
        data class Error(val message: String) : GeminiResult()
        
        fun getFinalText(): String {
            return when (this) {
                is Success -> text
                is AllQuotaExhausted -> "Hệ thống đang quá tải (hết dung lượng miễn phí). Anh hãy thử lại sau hoặc thêm API Key khác nhé."
                is NoApiKeys -> "Vui lòng nhập API Key Gemini trong phần Cài đặt."
                is Error -> "Lỗi call API: $message"
            }
        }
    }
}
