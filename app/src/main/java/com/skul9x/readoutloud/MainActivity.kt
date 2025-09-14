package com.skul9x.readoutloud

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var ttsForDiscovery: TextToSpeech
    private var vietnameseVoices = listOf<Voice>()
    private var voiceDisplayNames = listOf<String>()
    private var selectedVoice: Voice? = null

    private lateinit var audioManager: AudioManager
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "ReadOutLoudPrefs"
        private const val KEY_LAST_VOICE_NAME = "last_used_voice_name"
    }

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

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        setupUI()
        setSystemVolume(0.80f)
        binding.volumeToggleGroup.check(R.id.volumeButton80)

        initializeTtsForVoiceDiscovery()
    }

    private fun setupUI() {
        // Main controls
        binding.pasteButton.setOnClickListener { pasteFromClipboard() }
        binding.readButton.setOnClickListener { checkPermissionsAndRead() }
        binding.stopButton.setOnClickListener { stopReading() }

        // System Volume controls
        binding.volumeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val percentage = when (checkedId) {
                    R.id.volumeButton80 -> 0.80f
                    R.id.volumeButton85 -> 0.85f
                    R.id.volumeButton90 -> 0.90f
                    else -> 0.80f
                }
                setSystemVolume(percentage)
            }
        }
    }

    private fun setSystemVolume(percentage: Float) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVolume = (maxVolume * percentage).roundToInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
    }

    private fun initializeTtsForVoiceDiscovery() {
        ttsForDiscovery = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                vietnameseVoices = ttsForDiscovery.voices
                    .filter { it.locale == Locale("vi", "VN") }
                    .sortedBy { it.name } // Sắp xếp để đảm bảo thứ tự nhất quán

                if (vietnameseVoices.isNotEmpty()) {
                    voiceDisplayNames = vietnameseVoices.map { formatVoiceName(it.name) }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, voiceDisplayNames)
                    binding.vietnameseVoiceAutoComplete.setAdapter(adapter)

                    // Khôi phục giọng đọc đã lưu
                    val lastVoiceName = prefs.getString(KEY_LAST_VOICE_NAME, null)
                    val lastVoiceIndex = vietnameseVoices.indexOfFirst { it.name == lastVoiceName }

                    val defaultIndex = if (lastVoiceIndex != -1) lastVoiceIndex else 0
                    selectedVoice = vietnameseVoices[defaultIndex]
                    binding.vietnameseVoiceAutoComplete.setText(voiceDisplayNames[defaultIndex], false)

                    // Bắt sự kiện chọn giọng đọc mới
                    binding.vietnameseVoiceAutoComplete.setOnItemClickListener { _, _, position, _ ->
                        selectedVoice = vietnameseVoices[position]
                        // Lưu lựa chọn mới
                        prefs.edit().putString(KEY_LAST_VOICE_NAME, selectedVoice?.name).apply()
                    }
                }
            }
        }
    }

    private fun formatVoiceName(voiceName: String): String {
        return voiceName.replace("vi-vn-", "").replace("-", " ").split("#")[0]
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            .let {
                val gender = voiceName.substringAfter("#").substringBefore("_")
                val type = if (voiceName.contains("network")) " (Mạng)" else ""
                "$it ($gender)$type"
            }
    }

    private fun cleanMarkdown(text: String): String {
        return text
            .replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            .replace(Regex("!\\[(.*?)\\]\\(.*?\\)"), "$1")
            .replace(Regex("(\\*\\*|\\*|__|_|~~)"), "")
            .replace(Regex("^>\\s?", RegexOption.MULTILINE), "")
            .replace(Regex("^\\s*([-*+]|\\d+\\.)\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^[\\-_*]{3,}\\s*$", RegexOption.MULTILINE), "")
            .replace(Regex("`"), "")
            .trim()
    }

    private fun pasteFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val textToPaste = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            binding.editText.setText(cleanMarkdown(textToPaste))
            Toast.makeText(this, "Đã dán và làm sạch văn bản", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionsAndRead() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)) {
                PackageManager.PERMISSION_GRANTED -> startReading()
                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startReading()
        }
    }

    private fun startReading() {
        val rawText = binding.editText.text.toString()
        if (rawText.isBlank()) {
            Toast.makeText(this, "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show()
            return
        }

        val cleanedText = cleanMarkdown(rawText)
        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_START
            putExtra(TtsService.EXTRA_TEXT, cleanedText)
            // Luôn gửi thông tin giọng đọc tiếng Việt
            putExtra(TtsService.EXTRA_LANG, "vi-VN")
            selectedVoice?.let { putExtra(TtsService.EXTRA_VOICE_NAME, it.name) }
        }
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
        if (this::ttsForDiscovery.isInitialized) {
            ttsForDiscovery.stop()
            ttsForDiscovery.shutdown()
        }
        super.onDestroy()
    }
}

