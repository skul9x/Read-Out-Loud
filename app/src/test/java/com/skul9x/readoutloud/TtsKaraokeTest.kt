package com.skul9x.readoutloud

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.view.MotionEvent
import kotlin.time.toJavaDuration
import androidx.test.core.app.ApplicationProvider
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TtsKaraokeTest {

    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .start()
            .resume()
            .get()
    }

    @Test
    fun testHighlightSpanAppliedOnProgressBroadcast() {
        val testText = "Hello World Karaoke"
        activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText).setText(testText)

        // Simulate broadcast from TtsService
        val intent = Intent(TtsService.ACTION_PROGRESS).apply {
            putExtra(TtsService.EXTRA_PROGRESS_PERCENT, 50)
            putExtra(TtsService.EXTRA_WORD_START, 6) // "World"
            putExtra(TtsService.EXTRA_WORD_END, 11)
            setPackage(activity.packageName)
        }
        
        // Send broadcast
        ApplicationProvider.getApplicationContext<Context>().sendBroadcast(intent)
        
        // Trigger Robolectric to process the broadcast
        Shadows.shadowOf(activity.mainLooper).idle()

        val editText = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText)
        val spannable = editText.text as Spannable
        val spans = spannable.getSpans(6, 11, BackgroundColorSpan::class.java)

        assertNotNull("Highlight span should be applied", spans)
        assertTrue("Should have at least one BackgroundColorSpan", spans.isNotEmpty())
        assertEquals("Span color should be Orange (#FF9800)", Color.parseColor("#FF9800"), spans[0].backgroundColor)
    }

    @Test
    fun testClearHighlightOnStop() {
        val testText = "Hello World"
        val editText = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText)
        editText.setText(testText)

        // 1. Apply highlight
        activity.runOnUiThread {
            // We can call highlightWord via reflection or just trigger broadcast
            val intent = Intent(TtsService.ACTION_PROGRESS).apply {
                putExtra(TtsService.EXTRA_WORD_START, 0)
                putExtra(TtsService.EXTRA_WORD_END, 5)
                setPackage(activity.packageName)
            }
            activity.sendBroadcast(intent)
        }
        Shadows.shadowOf(activity.mainLooper).idle()
        
        assertTrue("Should have spans", (editText.text as android.text.Spannable).getSpans(0, 11, BackgroundColorSpan::class.java).isNotEmpty())

        // 2. Click Stop (which calls clearHighlight)
        activity.findViewById<com.google.android.material.card.MaterialCardView>(R.id.stopCard).performClick()
        Shadows.shadowOf(activity.mainLooper).idle()

        assertTrue("Spans should be cleared after Stop", (editText.text as android.text.Spannable).getSpans(0, 11, BackgroundColorSpan::class.java).isEmpty())
    }

    @Test
    fun testUserInteractionPausesAutoScroll() {
        val editText = activity.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editText)
        
        // 1. Simulate Touch Down
        val downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0)
        editText.dispatchTouchEvent(downEvent)
        
        // Use reflection to check private field isUserScrolling
        val field = MainActivity::class.java.getDeclaredField("isUserScrolling")
        field.isAccessible = true
        var isUserScrolling = field.get(activity) as Boolean
        assertTrue("isUserScrolling should be TRUE on ACTION_DOWN", isUserScrolling)

        // 2. Simulate Touch Up
        val upEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0)
        editText.dispatchTouchEvent(upEvent)
        
        // Immediately after UP, it should still be TRUE (waiting for 3s delay)
        isUserScrolling = field.get(activity) as Boolean
        assertTrue("isUserScrolling should STAY TRUE immediately after ACTION_UP", isUserScrolling)

        // 3. Advance time by 3001ms
        Shadows.shadowOf(activity.mainLooper).idleFor(kotlin.time.Duration.parse("3001ms").toJavaDuration())
        
        isUserScrolling = field.get(activity) as Boolean
        assertTrue("isUserScrolling should return to FALSE after 3s", !isUserScrolling)
    }
}
