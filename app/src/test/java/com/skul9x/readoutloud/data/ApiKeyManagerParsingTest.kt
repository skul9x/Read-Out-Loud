package com.skul9x.readoutloud.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiKeyManagerParsingTest {

    @Test
    fun `test parsing keys with multiple separators`() {
        // String with newlines, spaces, and commas
        val rawInput = """
<<<<<<< HEAD
            AIzaSyD1234567890123456789012345678,
            AIzaSyE9876543210987654321098765432
            AIzaSyF_INVALID_KEY_BUT_STARTS_WITH_AIZA_AND_LENGTH_OK_30_LENGTH
=======
            REDACTED_API_KEY_1,
            REDACTED_API_KEY_2
            REDACTED_API_KEY_3
>>>>>>> ca11023 (Cập nhật README tiếng Việt và ẩn API keys trong tests)
            SomeOtherRandomString
            AIzaSyShort
        """.trimIndent()

        val parsedKeys = ApiKeyManager.parseApiKeysFromRaw(rawInput)

        // Should have 3 valid keys (starts with AIza and length >= 30)
        assertEquals(3, parsedKeys.size)
        assertTrue(parsedKeys.contains("AIzaSyD1234567890123456789012345678"))
        assertTrue(parsedKeys.contains("AIzaSyE9876543210987654321098765432"))
        assertTrue(parsedKeys.contains("AIzaSyF_INVALID_KEY_BUT_STARTS_WITH_AIZA_AND_LENGTH_OK_30_LENGTH"))
    }

    @Test
    fun `test parsing single key with spaces`() {
<<<<<<< HEAD
        val rawInput = "  AIzaSyD1234567890123456789012345678  "
=======
        val rawInput = "  REDACTED_API_KEY_1  "
>>>>>>> ca11023 (Cập nhật README tiếng Việt và ẩn API keys trong tests)
        val result = ApiKeyManager.parseApiKeysFromRaw(rawInput)
        assertEquals(1, result.size)
        assertEquals("AIzaSyD1234567890123456789012345678", result[0])
    }

    @Test
    fun `test parsing with commas and spaces`() {
<<<<<<< HEAD
        val rawInput = "AIzaSy_1234567890123456789012345678, AIzaSy_09876543210987654321098765432    AIzaSy_ABCDEFGHIJKLMN1234567890"
=======
        val rawInput = "REDACTED_API_KEY_1, REDACTED_API_KEY_2    REDACTED_API_KEY_3"
>>>>>>> ca11023 (Cập nhật README tiếng Việt và ẩn API keys trong tests)
        val result = ApiKeyManager.parseApiKeysFromRaw(rawInput)
        assertEquals(3, result.size)
    }
}
