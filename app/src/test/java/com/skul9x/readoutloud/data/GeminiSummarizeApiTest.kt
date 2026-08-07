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
