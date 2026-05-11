package com.skul9x.readoutloud.data

import android.content.Context
import android.content.SharedPreferences
import com.skul9x.readoutloud.utils.SecurityUtils

/**
 * Manager for tracking model and API key quota status (Cooldown/Exhausted).
 */
class ModelQuotaManager private constructor(context: Context) {

    companion object {
        private const val PREFS_NAME = "model_quota_prefs"
        private const val COOLDOWN_DURATION_MS = 5 * 60 * 1000L // 5 minutes
        private const val EXHAUSTED_DURATION_MS = 30 * 60 * 60 * 1000L // 30 hours

        @Volatile
        private var instance: ModelQuotaManager? = null

        fun getInstance(context: Context): ModelQuotaManager {
            return instance ?: synchronized(this) {
                instance ?: ModelQuotaManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // In-memory map for Cooldown status: PairHash -> ExpiryTimestamp
    private val cooldownMap = mutableMapOf<String, Long>()

    /**
     * Checks if a model/key pair is currently available.
     * Also performs cleanup of expired entries.
     */
    fun isAvailable(pairHash: String): Boolean {
        val now = System.currentTimeMillis()

        // Check Cooldown (In-memory)
        val cooldownExpiry = cooldownMap[pairHash] ?: 0L
        if (now < cooldownExpiry) return false
        
        // Cleanup expired cooldown if it exists
        if (cooldownExpiry > 0) cooldownMap.remove(pairHash)

        // Check Exhausted (Persistent)
        val exhaustedExpiry = prefs.getLong("exhausted_$pairHash", 0L)
        if (exhaustedExpiry > 0) {
            if (now < exhaustedExpiry) {
                return false
            } else {
                // Expired, clean it up
                prefs.edit().remove("exhausted_$pairHash").apply()
            }
        }

        return true
    }

    /**
     * Marks a model/key pair as being in Cooldown (e.g. Rate Limit 429 RPM).
     */
    fun markCooldown(pairHash: String) {
        val expiry = System.currentTimeMillis() + COOLDOWN_DURATION_MS
        cooldownMap[pairHash] = expiry
    }

    /**
     * Marks a model/key pair as Exhausted (e.g. Daily Quota 429).
     */
    fun markExhausted(pairHash: String) {
        val expiry = System.currentTimeMillis() + EXHAUSTED_DURATION_MS
        prefs.edit().putLong("exhausted_$pairHash", expiry).apply()
    }

    /**
     * Cleans up all expired entries from SharedPreferences.
     * This can be called on app start or periodically.
     */
    fun cleanupExpiredEntries() {
        val now = System.currentTimeMillis()
        val editor = prefs.edit()
        var changed = false
        
        prefs.all.forEach { (key, value) ->
            if (key.startsWith("exhausted_") && value is Long) {
                if (now >= value) {
                    editor.remove(key)
                    changed = true
                }
            }
        }
        
        if (changed) editor.apply()
        
        // Cleanup in-memory cooldowns
        val iterator = cooldownMap.entries.iterator()
        while (iterator.hasNext()) {
            if (now >= iterator.next().value) {
                iterator.remove()
            }
        }
    }

    /**
     * Clears all status (for debugging/testing).
     */
    fun clearStatus() {
        cooldownMap.clear()
        prefs.edit().clear().apply()
    }
}
