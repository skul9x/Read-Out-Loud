# Phase 02: Gemini Summarize API Logic & Prompt Integration

Status: ✅ Completed
Dependencies: Phase 01

## Objective
Implement `summarizeTextWithGemini` in `GeminiApiClient.kt` using the exact user-specified prompt designed for drivers listening to news summaries.

## Prompt Specification
The exact prompt template to use:
```
Bạn là trợ lý AI chuyên tóm tắt tin tức cho người lái xe. Nhiệm vụ của bạn là tóm tắt nội dung sau thành các ý chính quan trọng nhất, ngắn gọn, súc tích.

YÊU CẦU BẮT BUỘC:
1. Chỉ trả về nội dung tóm tắt dưới dạng danh sách đánh số (1. 2. 3...).
2. TUYỆT ĐỐI KHÔNG có bất kỳ câu dẫn dắt, chào hỏi, rào đón hay kết thúc nào (Ví dụ: KHÔNG viết "Dưới đây là tóm tắt...", "Thưa giám đốc...", "Chào bạn...", "Tuyệt vời...").
3. Vào thẳng nội dung chính ngay lập tức.
4. Ngôn ngữ tự nhiên, dễ nghe khi đọc bằng giọng nói.

Nội dung cần tóm tắt:
{content}.
```

## Requirements
### Functional
- [x] Add `buildSummarizePrompt(content: String): String` in `GeminiApiClient.kt` (or dedicated prompt builder helper).
- [x] Implement `suspend fun summarizeTextWithGemini(content: String): GeminiResult` in `GeminiApiClient.kt`.
- [x] Maintain full support for key rotation, model fallback, rate limit cooldowns, and error mapping identically to `cleanTextWithGemini`.

### Non-Functional
- [x] Thread safety with Coroutines (`Dispatchers.IO`).
- [x] No regression on existing text polishing feature (`cleanTextWithGemini`).

## Implementation Steps
1. [x] Define prompt construction method `buildSummarizePrompt(content: String)` in `GeminiApiClient.kt`.
2. [x] Add `summarizeTextWithGemini(content: String)` to `GeminiApiClient.kt` reusing internal `tryGenerateContentWithPrompt` helper or parameterized `tryGenerateContent`.
3. [x] Add unit test `GeminiSummarizeApiTest.kt` to verify prompt construction and mock API responses.

## Files to Create/Modify
- `app/src/main/java/com/skul9x/readoutloud/data/GeminiApiClient.kt` - [MODIFY] Add prompt builder and `summarizeTextWithGemini` function
- `app/src/test/java/com/skul9x/readoutloud/data/GeminiSummarizeApiTest.kt` - [NEW] Unit test verifying prompt structure and summarization call flow

## Verification Test (File-Based)
Create unit test file `app/src/test/java/com/skul9x/readoutloud/data/GeminiSummarizeApiTest.kt`:

```kotlin
package com.skul9x.readoutloud.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GeminiSummarizeApiTest {

    private lateinit var context: Context
    private lateinit var client: GeminiApiClient

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        client = GeminiApiClient(context)
    }

    @Test
    fun testBuildSummarizePromptContainsRequiredRules() {
        val sampleContent = "Hà Nội hôm nay có mưa rào rải rác và dông ở một số khu vực."
        
        // Use reflection to verify internal prompt construction method
        val method = GeminiApiClient::class.java.getDeclaredMethod("buildSummarizePrompt", String::class.java)
        method.isAccessible = true
        val prompt = method.invoke(client, sampleContent) as String

        assertTrue("Prompt must mention driver persona", prompt.contains("trợ lý AI chuyên tóm tắt tin tức cho người lái xe"))
        assertTrue("Prompt must mandate numbered list", prompt.contains("danh sách đánh số"))
        assertTrue("Prompt must forbid conversational greetings", prompt.contains("TUYỆT ĐỐI KHÔNG"))
        assertTrue("Prompt must embed target content", prompt.contains(sampleContent))
    }
}
```

### Automated Test Command
```bash
./gradlew testDebugUnitTest --tests "com.skul9x.readoutloud.data.GeminiSummarizeApiTest"
```
