package com.skul9x.readoutloud.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
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
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.R
import com.skul9x.readoutloud.TtsService
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.databinding.FragmentReadBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ReadFragment : Fragment() {

    private var _binding: FragmentReadBinding? = null
    val binding get() = _binding!!

    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private var tts: TextToSpeech? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var geminiApiClient: GeminiApiClient

    private var vietnameseVoices = listOf<Voice>()
    private var selectedVoiceName: String? = null

    private lateinit var gestureDetector: GestureDetector
    private var isEditingMode = false
    private val currentHighlightSpans = mutableListOf<Any>()

    var isUserScrolling = false
        set(value) {
            field = value
            (activity as? MainActivity)?.isUserScrolling = value
        }

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
                context?.let {
                    Toast.makeText(it, "Cần cấp quyền thông báo để chạy nền", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        geminiApiClient = GeminiApiClient(context)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val imeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())
            if (!imeVisible && isEditingMode) {
                exitEditMode()
            }
            windowInsets
        }

        setupUI()
        initializeTtsForVoiceDiscovery()

        sharedViewModel.summarizeEvent.observe(viewLifecycleOwner) { text ->
            if (!text.isNullOrBlank()) {
                binding.editText.setText(text)
                binding.editText.scrollTo(0, 0)

                Snackbar.make(binding.root, "📄 Đang tóm tắt kết quả tìm kiếm...", Snackbar.LENGTH_SHORT).show()

                processSummarizeWithAI(text)
                sharedViewModel.clearSummarizeEvent()
            }
        }

        sharedViewModel.readAloudEvent.observe(viewLifecycleOwner) { text ->
            if (!text.isNullOrBlank()) {
                binding.editText.setText(text)
                binding.editText.scrollTo(0, 0)

                Snackbar.make(binding.root, "🔊 Đang đọc kết quả tìm kiếm...", Snackbar.LENGTH_SHORT).show()

                checkPermissionsAndRead()
                sharedViewModel.clearReadAloudEvent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val context = context ?: return
        val filter = IntentFilter(TtsService.ACTION_PROGRESS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(progressReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(progressReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            context?.unregisterReceiver(progressReceiver)
        } catch (e: Exception) {
            // Ignored
        }
    }

    override fun onResume() {
        super.onResume()
        if (::sharedPreferences.isInitialized) {
            val savedVoice = sharedPreferences.getString(KEY_VOICE_NAME, null)
            if (savedVoice != null) {
                selectedVoiceName = savedVoice
            }
        }
    }

    private fun setupUI() {
        // AI Text Action
        binding.aiTextButton.setOnClickListener {
            val textToPolish = binding.editText.text?.toString() ?: ""
            if (textToPolish.isBlank()) {
                Toast.makeText(requireContext(), "Không có nội dung để dọn dẹp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sharedPreferences.edit().putBoolean(KEY_GEMINI_ENABLED, true).apply()
            updateStatus("Gemini AI: ON")
            processWithAI(textToPolish)
        }

        // Summarize Action
        binding.summarizeButton.setOnClickListener {
            val textToSummarize = binding.editText.text?.toString() ?: ""
            if (textToSummarize.isBlank()) {
                Toast.makeText(requireContext(), "Không có nội dung để tóm tắt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            processSummarizeWithAI(textToSummarize)
        }

        // Action Cards
        binding.pasteCard.setOnClickListener { pasteFromClipboard() }
        binding.readCard.setOnClickListener { checkPermissionsAndRead() }
        binding.stopCard.setOnClickListener { stopReading() }
        binding.copyTextButton.setOnClickListener { copyToClipboard() }

        binding.editText.movementMethod = ScrollingMovementMethod.getInstance()

        // Read-Only & Gesture Handling
        binding.editText.isFocusable = false
        binding.editText.isFocusableInTouchMode = false
        binding.editText.isCursorVisible = false

        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (!isEditingMode) {
                    enterEditMode()
                }
                return true
            }
        })

        binding.editText.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    isUserScrolling = true
                    scrollHandler.removeCallbacks(resumeAutoScrollRunnable)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    scrollHandler.postDelayed(resumeAutoScrollRunnable, 3000)
                }
            }
            false
        }
    }

    private fun enterEditMode() {
        isEditingMode = true
        clearHighlight()
        binding.editText.isFocusable = true
        binding.editText.isFocusableInTouchMode = true
        binding.editText.isCursorVisible = true
        binding.editText.requestFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
        updateStatus("Chế độ Edit")
    }

    private fun exitEditMode() {
        isEditingMode = false
        binding.editText.isFocusable = false
        binding.editText.isFocusableInTouchMode = false
        binding.editText.isCursorVisible = false
        binding.editText.clearFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        updateStatus("Đã lưu văn bản")

        clearHighlight()
    }

    private fun initializeTtsForVoiceDiscovery() {
        val context = context ?: return
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                vietnameseVoices = tts?.voices
                    ?.filter { it.locale == Locale("vi", "VN") && !it.isNetworkConnectionRequired }
                    ?.distinctBy { it.name } ?: emptyList()

                if (vietnameseVoices.isNotEmpty()) {
                    selectedVoiceName = sharedPreferences.getString(KEY_VOICE_NAME, vietnameseVoices[0].name)
                }
            }
        }
    }

    private fun pasteFromClipboard() {
        val context = context ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!clipboard.hasPrimaryClip()) {
            Toast.makeText(context, "Clipboard trống", Toast.LENGTH_SHORT).show()
            return
        }

        val rawText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
        val plainText = rawText.replace("---", "").replace(Regex("[*#_`~]"), "")
        binding.editText.setText(plainText)
        updateStatus("Đã dán (Lọc cơ bản)")
    }

    private fun copyToClipboard() {
        val context = context ?: return
        val text = binding.editText.text?.toString() ?: ""
        if (text.isBlank()) {
            Toast.makeText(context, "Không có văn bản để sao chép", Toast.LENGTH_SHORT).show()
            return
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Đã sao chép văn bản vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()
    }

    private fun processWithAI(text: String) {
        val context = context ?: return
        val apiKeys = ApiKeyManager.getInstance(context).getApiKeys()
        if (apiKeys.isEmpty()) {
            Toast.makeText(context, "Chưa cấu hình Gemini API Key", Toast.LENGTH_LONG).show()
            updateStatus("Gemini: Không có API Key")
            return
        }

        updateStatus("Gemini đang dọn dẹp văn bản...")
        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
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
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            updateStatus("Lỗi Gemini")
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

    private fun processSummarizeWithAI(text: String) {
        val context = context ?: return
        val apiKeys = ApiKeyManager.getInstance(context).getApiKeys()
        if (apiKeys.isEmpty()) {
            Toast.makeText(context, "Chưa cấu hình Gemini API Key", Toast.LENGTH_LONG).show()
            updateStatus("Gemini: Không có API Key")
            return
        }

        updateStatus("Gemini đang tóm tắt...")
        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                geminiApiClient.refreshApiKeys()
                val result = geminiApiClient.summarizeTextWithGemini(text)

                withContext(Dispatchers.Main) {
                    setLoading(false)
                    when (result) {
                        is GeminiApiClient.GeminiResult.Success -> {
                            binding.editText.setText(result.text)
                            updateStatus("Gemini: Tóm tắt xong (${result.model.substringAfter("/")})")
                        }
                        else -> {
                            val msg = result.getFinalText()
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            updateStatus("Lỗi Gemini")
                        }
                    }
                }
            } catch (e: Exception) {
                setLoading(false)
                updateStatus("Lỗi hệ thống")
            }
        }
    }

    private fun updateStatus(status: String) {
        _binding?.statusText?.text = status
    }

    private fun updateReadingProgress(percent: Int) {
        if (_binding == null) return
        binding.readingProgressBar.progress = percent
        binding.readingPercentText.text = "$percent%"
        if (percent >= 100) {
            binding.readingStatusText.text = "Finished"
            clearHighlight()
        } else if (percent > 0) {
            binding.readingStatusText.text = "Reading..."
        }
    }

    private fun highlightWord(start: Int, end: Int) {
        if (isEditingMode || _binding == null) return
        val editable = binding.editText.text ?: return
        if (start < 0 || end > editable.length || start >= end) return

        clearHighlight()

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

    private fun autoScrollToHighlight(start: Int) {
        if (isEditingMode || isUserScrolling || _binding == null) return

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
            val targetY = lineTop - (viewHeight / 2) + paddingTop
            val finalY = targetY.coerceAtLeast(0)

            ObjectAnimator.ofInt(binding.editText, "scrollY", finalY).apply {
                duration = 300
                start()
            }
        }
    }

    private fun clearHighlight() {
        if (_binding == null) return
        val editable = binding.editText.text ?: return
        for (span in currentHighlightSpans) {
            editable.removeSpan(span)
        }
        currentHighlightSpans.clear()
    }

    fun setLoading(isLoading: Boolean) {
        if (_binding == null) return
        binding.pasteCard.isEnabled = !isLoading
        binding.readCard.isEnabled = !isLoading
        binding.aiTextButton.isEnabled = !isLoading
        binding.summarizeButton.isEnabled = !isLoading

        (activity as? MainActivity)?.setLoading(isLoading)

        val ctx = context ?: return
        if (isLoading) {
            binding.statusText.setTextColor(ContextCompat.getColor(ctx, R.color.md_theme_dark_primary))
        } else {
            binding.statusText.setTextColor(ContextCompat.getColor(ctx, R.color.md_theme_dark_onSurfaceVariant))
        }
    }

    private fun checkPermissionsAndRead() {
        val context = context ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        startReading()
    }

    private fun startReading() {
        val context = context ?: return
        val text = binding.editText.text?.toString() ?: ""
        if (text.isBlank()) {
            Toast.makeText(context, "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show()
            updateStatus("Văn bản trống")
            return
        }

        val intent = Intent(context, TtsService::class.java).apply {
            action = TtsService.ACTION_START
            putExtra(TtsService.EXTRA_TEXT, text)
            putExtra(TtsService.EXTRA_VOICE_NAME, selectedVoiceName)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        updateStatus("Đang đọc...")
    }

    private fun stopReading() {
        val context = context ?: return
        val intent = Intent(context, TtsService::class.java).apply {
            action = TtsService.ACTION_STOP
        }
        context.startService(intent)
        updateStatus("Đã dừng")
        updateReadingProgress(0)
        clearHighlight()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}
