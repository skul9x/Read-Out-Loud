package com.skul9x.readoutloud.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.skul9x.readoutloud.utils.SecurityUtils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manager for storing and retrieving Gemini API keys.
 * Uses EncryptedSharedPreferences for secure storage.
 */
class ApiKeyManager private constructor(context: Context) {

    companion object {
        private const val PREFS_NAME = "api_keys_secure"
        private const val KEY_API_KEYS = "gemini_api_keys"
        
        @Volatile
        private var instance: ApiKeyManager? = null
        
        fun getInstance(context: Context): ApiKeyManager {
            return instance ?: synchronized(this) {
                instance ?: ApiKeyManager(context.applicationContext).also { instance = it }
            }
        }

        /**
         * Logic for parsing API keys from a raw input string.
         */
        fun parseApiKeysFromRaw(rawInput: String): List<String> {
            return rawInput.split(Regex("""[\n,\s]+"""))
                .map { it.trim() }
                .filter { it.startsWith("AIza") && it.length >= 30 }
        }

        /**
         * Computes SHA-256 hash of an API key.
         */
        fun getSha256(apiKey: String): String {
            return SecurityUtils.sha256(apiKey)
        }
    }

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            try {
                context.deleteSharedPreferences(PREFS_NAME)
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                    
                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e2: Exception) {
                // Ultimate fallback for environments without KeyStore support (e.g. Robolectric)
                // or where EncryptedSharedPreferences is hopelessly broken.
                context.getSharedPreferences(PREFS_NAME + "_unencrypted_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    /**
     * Get all stored API keys.
     */
    fun getApiKeys(): List<String> {
        val json = prefs.getString(KEY_API_KEYS, null) ?: return emptyList()
        return try {
            Json.decodeFromString<List<String>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Set the entire list of API keys.
     */
    fun setApiKeys(keys: List<String>) {
        val json = Json.encodeToString(keys)
        prefs.edit().putString(KEY_API_KEYS, json).apply()
    }

    /**
     * Add multiple API keys from a raw string.
     * Separates by newline, comma, or space.
     */
    fun addApiKeysFromRawString(rawInput: String): Int {
        val newKeys = parseApiKeysFromRaw(rawInput)
        
        if (newKeys.isEmpty()) return 0
        
        val currentKeys = getApiKeys().toMutableList()
        var addedCount = 0
        
        for (key in newKeys) {
            if (!currentKeys.contains(key)) {
                currentKeys.add(key)
                addedCount++
            }
        }
        
        if (addedCount > 0) {
            setApiKeys(currentKeys)
        }
        return addedCount
    }

    /**
     * Clear all API keys.
     */
    fun clearAllKeys() {
        prefs.edit().remove(KEY_API_KEYS).apply()
    }

    /**
     * Mask an API key for display.
     */
    fun maskApiKey(apiKey: String): String {
        if (apiKey.length <= 12) return "****"
        return "${apiKey.take(8)}...${apiKey.takeLast(4)}"
    }
}
