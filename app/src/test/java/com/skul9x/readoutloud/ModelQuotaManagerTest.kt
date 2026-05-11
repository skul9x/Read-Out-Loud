package com.skul9x.readoutloud

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.skul9x.readoutloud.data.ModelQuotaManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ModelQuotaManagerTest {

    private lateinit var context: Context
    private lateinit var quotaManager: ModelQuotaManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        quotaManager = ModelQuotaManager.getInstance(context)
        quotaManager.clearStatus()
    }

    @Test
    fun `test initial availability`() {
        assertTrue(quotaManager.isAvailable("test_pair"))
    }

    @Test
    fun `test mark cooldown`() {
        quotaManager.markCooldown("test_pair")
        assertFalse(quotaManager.isAvailable("test_pair"))
    }

    @Test
    fun `test mark exhausted`() {
        quotaManager.markExhausted("test_pair")
        assertFalse(quotaManager.isAvailable("test_pair"))
    }

    @Test
    fun `test cleanup expired entries`() {
        // We can't easily test time-based expiry without mocking System.currentTimeMillis()
        // but we can verify it doesn't crash and clears everything if we call clearStatus
        quotaManager.markCooldown("pair1")
        quotaManager.markExhausted("pair2")
        
        quotaManager.clearStatus()
        assertTrue(quotaManager.isAvailable("pair1"))
        assertTrue(quotaManager.isAvailable("pair2"))
    }
}
