package com.skul9x.readoutloud

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech
    private var vietnameseVoices = listOf<Voice>()
    private var selectedVoice: Voice? = null

    // Trình xử lý yêu cầu quyền thông báo (Android 13+)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startReading()
            } else {
                Toast.makeText(this, "Cần cấp quyền thông báo để đọc trong nền", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        // Khởi tạo TTS để lấy danh sách giọng đọc
        initializeTtsForVoiceDiscovery()
    }

    private fun setupUI() {
        binding.pasteButton.setOnClickListener { pasteFromClipboard() }
        binding.readButton.setOnClickListener { checkPermissionsAndRead() }
        binding.stopButton.setOnClickListener { stopReading() }

        binding.languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Ẩn/hiện tùy chọn giọng đọc tiếng Việt
            binding.vietnameseOptionsLabel.visibility = if (isChecked) View.GONE else View.VISIBLE
            binding.voiceSpinner.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
    }

    private fun initializeTtsForVoiceDiscovery() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Lọc và hiển thị các giọng đọc tiếng Việt
                vietnameseVoices = tts.voices.filter { it.locale == Locale("vi", "VN") }
                if (vietnameseVoices.isNotEmpty()) {
                    // Tạo danh sách tên giọng đọc để hiển thị
                    val voiceNames = vietnameseVoices.map { formatVoiceName(it.name) }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, voiceNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.voiceSpinner.adapter = adapter
                    selectedVoice = vietnameseVoices[0] // Chọn giọng mặc định
                } else {
                    Toast.makeText(this, "Không tìm thấy giọng đọc tiếng Việt", Toast.LENGTH_LONG).show()
                    binding.vietnameseOptionsLabel.visibility = View.GONE
                    binding.voiceSpinner.visibility = View.GONE
                }

                binding.voiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedVoice = vietnameseVoices[position]
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            } else {
                Log.e("MainActivity", "Không thể khởi tạo TextToSpeech")
            }
        }
    }

    // Định dạng tên giọng đọc cho dễ nhìn hơn
    private fun formatVoiceName(voiceName: String): String {
        return voiceName.replace("vi-vn-", "").replace("-", " ").split("#")[0]
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            .let {
                val gender = voiceName.substringAfter("#").substringBefore("_")
                "$it ($gender)"
            }
    }

    // *** HÀM MỚI: Dọn dẹp các ký tự Markdown ***
    private fun cleanMarkdown(text: String): String {
        return text
            // Loại bỏ tiêu đề (vd: # Heading)
            .replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
            // Loại bỏ link và giữ lại text (vd: [text](url) -> text)
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            // Loại bỏ ảnh và giữ lại alt text (vd: ![alt](url) -> alt)
            .replace(Regex("!\\[(.*?)\\]\\(.*?\\)"), "$1")
            // Loại bỏ các ký tự in đậm, in nghiêng, gạch ngang (**, *, __, _, ~~)
            .replace(Regex("(\\*\\*|\\*|__|_|~~)"), "")
            // Loại bỏ blockquote (vd: > quote)
            .replace(Regex("^>\\s?", RegexOption.MULTILINE), "")
            // Loại bỏ các mục danh sách (vd: * item, - item, 1. item)
            .replace(Regex("^\\s*([-*+]|\\d+\\.)\\s+", RegexOption.MULTILINE), "")
            // Loại bỏ các đường kẻ ngang (---, ***, ___ )
            .replace(Regex("^[\\-_*]{3,}\\s*$", RegexOption.MULTILINE), "")
            // Loại bỏ code inline (`) và code block (```)
            .replace(Regex("`"), "")
            // Xóa các dòng trống thừa
            .trim()
    }


    private fun pasteFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val clip: ClipData? = clipboard.primaryClip
            val textToPaste = clip?.getItemAt(0)?.text.toString()

            // *** THAY ĐỔI: Dọn dẹp văn bản trước khi dán ***
            val cleanedText = cleanMarkdown(textToPaste)
            binding.editText.setText(cleanedText)

            Toast.makeText(this, "Đã dán và làm sạch văn bản", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Không có gì trong clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionsAndRead() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startReading()
                }
                else -> {
                    // Yêu cầu quyền
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Không cần quyền cho các phiên bản cũ hơn
            startReading()
        }
    }

    private fun startReading() {
        val rawText = binding.editText.text.toString()
        if (rawText.isBlank()) {
            Toast.makeText(this, "Vui lòng nhập hoặc dán văn bản", Toast.LENGTH_SHORT).show()
            return
        }

        // *** THAY ĐỔI: Dọn dẹp văn bản trước khi đọc ***
        val cleanedText = cleanMarkdown(rawText)

        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_START
            putExtra(TtsService.EXTRA_TEXT, cleanedText)
            // Kiểm tra xem có đọc tiếng Anh không
            if (binding.languageSwitch.isChecked) {
                putExtra(TtsService.EXTRA_LANG, "en-US")
            } else {
                putExtra(TtsService.EXTRA_LANG, "vi-VN")
                // Gửi tên giọng đọc đã chọn
                selectedVoice?.let {
                    putExtra(TtsService.EXTRA_VOICE_NAME, it.name)
                }
            }
        }
        // Bắt đầu dịch vụ nền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopReading() {
        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_STOP
        }
        startService(intent)
    }

    override fun onDestroy() {
        // Giải phóng tài nguyên TTS khi Activity bị hủy
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
