package com.skul9x.readoutloud

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.text.Spannable
import android.text.method.ScrollingMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import com.skul9x.readoutloud.ui.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var geminiApiClient: GeminiApiClient

    private var vietnameseVoices = listOf<Voice>()
    private var selectedVoiceName: String? = null
    private var currentVolumePercent = 80
    
    private lateinit var gestureDetector: GestureDetector
    private var isEditingMode = false
    private val currentHighlightSpans = mutableListOf<Any>()
    
    // Phase 04: Auto-scroll
    private var isUserScrolling = false
    private val scrollHandler = Handler(Looper.getMainLooper())
    private val resumeAutoScrollRunnable = Runnable {
        isUserScrolling = false
    }

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TtsService.ACTION_PROGRESS) {
                val percent = intent.getIntExtra(TtsService.EXTRA_PROGRESS_PERCENT, 0)
                val wordStart = intent.getIntExtra(TtsService.EXTRA_WORD_START, -1)
                val wordEnd = intent.getIntExtra(TtsService.EXTRA_WORD_END, -1)
                
                updateReadingProgress(percent)
                
                if (wordStart >= 0 && wordEnd > wordStart) {
                    highlightWord(wordStart, wordEnd)
                }
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "ReadOutLoudPrefs"
        private const val KEY_VOICE_NAME = "lastVoiceName"
        private const val KEY_GEMINI_ENABLED = "gemini_enabled"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startReading()
            } else {
                Toast.makeText(this, "Cần cấp quyền thông báo để chạy nền", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fix Status Bar Overlap (Window Insets)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, insets.top, 0, 0)
            
            val imeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())
            if (!imeVisible && isEditingMode) {
                exitEditMode()
            }
            windowInsets
        }
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        geminiApiClient = GeminiApiClient(this)

        setupUI()
        setupVolumeControl()
        initializeTtsForVoiceDiscovery()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(TtsService.ACTION_PROGRESS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(progressReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(progressReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(progressReceiver)
        } catch (e: Exception) {
            // Ignored
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload selected voice preference
        if (::sharedPreferences.isInitialized) {
            val savedVoice = sharedPreferences.getString(KEY_VOICE_NAME, null)
            if (savedVoice != null) {
                selectedVoiceName = savedVoice
            }
        }
    }

    private fun setupUI() {
        // UI Navigation
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // AI Text Action
        binding.aiTextButton.setOnClickListener {
            val textToPolish = binding.editText.text.toString()
            if (textToPolish.isBlank()) {
                Toast.makeText(this, "Không có nội dung để dọn dẹp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Tạm thời bật AI Read nếu cần cho logic hệ thống khác
            sharedPreferences.edit().putBoolean(KEY_GEMINI_ENABLED, true).apply()
            updateStatus("Gemini AI: ON")
            processWithAI(textToPolish)
        }

        // Action Cards (Modern UI)
        binding.pasteCard.setOnClickListener { pasteFromClipboard() }
        binding.readCard.setOnClickListener { checkPermissionsAndRead() }
        binding.stopCard.setOnClickListener { stopReading() }

        binding.editText.movementMethod = ScrollingMovementMethod.getInstance()
        
        // Phase 01: Read-Only & Gesture Handling
        binding.editText.isFocusable = false
        binding.editText.isFocusableInTouchMode = false
        binding.editText.isCursorVisible = false

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (!isEditingMode) {
                    enterEditMode()
                }
                return true
            }
        })

        binding.editText.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            
            // Phase 04: Detect User Interaction
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    isUserScrolling = true
                    scrollHandler.removeCallbacks(resumeAutoScrollRunnable)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Resume auto-scroll after 3 seconds of no interaction
                    scrollHandler.postDelayed(resumeAutoScrollRunnable, 3000)
                }
            }
            false
        }
        
        binding.volumeButton.setOnClickListener {
            cycleVolume()
        }
    }

    private fun enterEditMode() {
        isEditingMode = true
        clearHighlight()
        binding.editText.isFocusable = true
        binding.editText.isFocusableInTouchMode = true
        binding.editText.isCursorVisible = true
        binding.editText.requestFocus()
        
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
        updateStatus("Chế độ Edit")
    }

    private fun exitEditMode() {
        isEditingMode = false
        binding.editText.isFocusable = false
        binding.editText.isFocusableInTouchMode = false
        binding.editText.isCursorVisible = false
        binding.editText.clearFocus()
        
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        updateStatus("Đã lưu văn bản")
        
        // Re-calculate or format text properly to restore highlight state 
        // if TTS continues reading or when user hit play again.
        clearHighlight()
    }

    private fun setupVolumeControl() {
        // Initial setup to 80% as requested
        setDeviceVolume(80)
        binding.volumeButton.text = "80%"
    }

    private fun cycleVolume() {
        currentVolumePercent = when (currentVolumePercent) {
            80 -> 85
            85 -> 90
            else -> 80
        }
        setDeviceVolume(currentVolumePercent)
        binding.volumeButton.text = "$currentVolumePercent%"
    }

    private fun setDeviceVolume(percent: Int) {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val targetVolume = (maxVolume * (percent / 100.0)).toInt()
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                targetVolume,
                AudioManager.FLAG_SHOW_UI
            )
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting volume: ${e.message}")
        }
    }

    private fun initializeTtsForVoiceDiscovery() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                vietnameseVoices = tts.voices
                    .filter { it.locale == Locale("vi", "VN") && !it.isNetworkConnectionRequired }
                    .distinctBy { it.name }

                if (vietnameseVoices.isNotEmpty()) {
                    selectedVoiceName = sharedPreferences.getString(KEY_VOICE_NAME, vietnameseVoices[0].name)
                }
            }
        }
    }

    private fun pasteFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!clipboard.hasPrimaryClip()) {
            Toast.makeText(this, "Clipboard trống", Toast.LENGTH_SHORT).show()
            return
        }

        val rawText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        
        // Luôn lọc cơ bản khi dán (Local Regex) - Không tốn thời gian gọi AI tự động
        val plainText = rawText.replace(Regex("[*#_`~]"), "")
        binding.editText.setText(plainText)
        updateStatus("Đã dán (Lọc cơ bản)")
    }

    private fun processWithAI(text: String) {
        val apiKeys = ApiKeyManager.getInstance(this).getApiKeys()
        if (apiKeys.isEmpty()) {
            Toast.makeText(this, "Chưa cấu hình Gemini API Key", Toast.LENGTH_LONG).show()
            updateStatus("Gemini: Không có API Key")
            return
        }

        updateStatus("Gemini đang dọn dẹp văn bản...")
        setLoading(true)

        lifecycleScope.launch {
            try {
                geminiApiClient.refreshApiKeys()
                val result = geminiApiClient.cleanTextWithGemini(text)
                
                withContext(Dispatchers.Main) {
                    setLoading(false)
                    when (result) {
                        is GeminiApiClient.GeminiResult.Success -> {
                            binding.editText.setText(result.text)
                            updateStatus("Gemini: Done (${result.model.substringAfter("/")})")
                        }
                        else -> {
                            val msg = result.getFinalText()
                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                            updateStatus("Lỗi Gemini")
                            // Fallback to basic clean
                            binding.editText.setText(text.replace(Regex("[*#_`~]"), ""))
                        }
                    }
                }
            } catch (e: Exception) {
                setLoading(false)
                updateStatus("Lỗi hệ thống")
                binding.editText.setText(text.replace(Regex("[*#_`~]"), ""))
            }
        }
    }

    private fun updateStatus(status: String) {
        binding.statusText.text = status
    }

    private fun updateReadingProgress(percent: Int) {
        binding.readingProgressBar.progress = percent
        binding.readingPercentText.text = "$percent%"
        if (percent >= 100) {
            binding.readingStatusText.text = "Finished"
            clearHighlight() // Clear highlight when done
        } else if (percent > 0) {
            binding.readingStatusText.text = "Reading..."
        }
    }
    
    // Phase 03: Highlight Animation (Karaoke)
    private fun highlightWord(start: Int, end: Int) {
        if (isEditingMode) return
        val editable = binding.editText.text ?: return
        if (start < 0 || end > editable.length || start >= end) return
        
        clearHighlight() // Remove previous highlight
        
        // Orange background, White text, Bold
        val bgSpan = BackgroundColorSpan(Color.parseColor("#FF9800"))
        val fgSpan = ForegroundColorSpan(Color.WHITE)
        val boldSpan = StyleSpan(Typeface.BOLD)
        
        editable.setSpan(bgSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        editable.setSpan(fgSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        editable.setSpan(boldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        
        currentHighlightSpans.add(bgSpan)
        currentHighlightSpans.add(fgSpan)
        currentHighlightSpans.add(boldSpan)
        
        autoScrollToHighlight(start)
    }
    
    // Phase 04: Auto-Scroll with Smooth Animation
    private fun autoScrollToHighlight(start: Int) {
        if (isEditingMode || isUserScrolling) return
        
        val layout = binding.editText.layout ?: return
        val currentScrollY = binding.editText.scrollY
        val viewHeight = binding.editText.height
        val paddingTop = binding.editText.paddingTop
        val paddingBottom = binding.editText.paddingBottom
        
        val line = layout.getLineForOffset(start)
        val lineTop = layout.getLineTop(line)
        val lineBottom = layout.getLineBottom(line)
        
        val isOffScreenTop = lineTop < currentScrollY
        val isOffScreenBottom = lineBottom > (currentScrollY + viewHeight - paddingTop - paddingBottom)
        
        if (isOffScreenTop || isOffScreenBottom) {
            // Center the word
            val targetY = lineTop - (viewHeight / 2) + paddingTop
            val finalY = targetY.coerceAtLeast(0)
            
            ObjectAnimator.ofInt(binding.editText, "scrollY", finalY).apply {
                duration = 300 // smooth auto-giật effect
                start()
            }
        }
    }
    
    private fun clearHighlight() {
        val editable = binding.editText.text ?: return
        for (span in currentHighlightSpans) {
            editable.removeSpan(span)
        }
        currentHighlightSpans.clear()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.pasteCard.isEnabled = !isLoading
        binding.readCard.isEnabled = !isLoading
        binding.aiTextButton.isEnabled = !isLoading
        
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        if (isLoading) {
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.md_theme_dark_primary))
        } else {
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.md_theme_dark_onSurfaceVariant))
        }
    }

    private fun checkPermissionsAndRead() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        startReading()
    }

    private fun startReading() {
        val text = binding.editText.text.toString()
        if (text.isBlank()) {
            Toast.makeText(this, "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show()
            updateStatus("Văn bản trống")
            return
        }

        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_START
            putExtra(TtsService.EXTRA_TEXT, text)
            putExtra(TtsService.EXTRA_VOICE_NAME, selectedVoiceName)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        updateStatus("Đang đọc...")
    }

    private fun stopReading() {
        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_STOP
        }
        startService(intent)
        updateStatus("Đã dừng")
        updateReadingProgress(0)
        clearHighlight()
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.shutdown()
        }
        super.onDestroy()
    }
}
