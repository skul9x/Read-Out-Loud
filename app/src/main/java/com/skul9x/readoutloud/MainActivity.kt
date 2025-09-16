package com.skul9x.readoutloud

import android.Manifest
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
import android.text.method.ScrollingMovementMethod
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.skul9x.readoutloud.databinding.ActivityMainBinding
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech
    private lateinit var audioManager: AudioManager
    private lateinit var sharedPreferences: SharedPreferences

    private var vietnameseVoices = listOf<Voice>()
    private var selectedVoiceName: String? = null
    private val voiceDisplayNames = mutableListOf<String>()
    private val voiceMap = mutableMapOf<String, String>()

    companion object {
        private const val PREFS_NAME = "ReadOutLoudPrefs"
        private const val KEY_VOICE_NAME = "lastVoiceName"
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
        setSupportActionBar(binding.toolbar)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setupUI()
        initializeTtsForVoiceDiscovery()
        setInitialSystemVolume()
    }

    private fun setupUI() {
        binding.pasteButton.setOnClickListener { pasteFromClipboard() }
        binding.readButton.setOnClickListener { checkPermissionsAndRead() }
        binding.stopButton.setOnClickListener { stopReading() }

        binding.editText.movementMethod = ScrollingMovementMethod.getInstance()

        binding.volumeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val percentage = when (checkedId) {
                    R.id.volumeButton80 -> 0.80f
                    R.id.volumeButton85 -> 0.85f
                    R.id.volumeButton90 -> 0.90f
                    else -> 0.80f // Mặc định
                }
                setSystemVolume(percentage)
            }
        }
    }

    private fun initializeTtsForVoiceDiscovery() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                vietnameseVoices = tts.voices
                    .filter { it.locale == Locale("vi", "VN") && !it.isNetworkConnectionRequired }
                    .distinctBy { it.name }

                if (vietnameseVoices.isNotEmpty()) {
                    populateVoiceSelector()
                } else {
                    Toast.makeText(this, "Không tìm thấy giọng đọc Tiếng Việt", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Không thể khởi tạo Text-to-Speech", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateVoiceSelector() {
        // Clear previous data to avoid duplication
        voiceDisplayNames.clear()
        voiceMap.clear()

        vietnameseVoices.forEachIndexed { index, voice ->
            val displayName = "Giọng đọc ${index + 1}"
            voiceDisplayNames.add(displayName)
            voiceMap[displayName] = voice.name
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, voiceDisplayNames)
        val autoCompleteTextView = (binding.voiceSelectorLayout.editText as? AutoCompleteTextView)
        autoCompleteTextView?.setAdapter(adapter)

        val lastVoiceName = sharedPreferences.getString(KEY_VOICE_NAME, null)
        val lastDisplayName = voiceMap.entries.find { it.value == lastVoiceName }?.key

        if (lastDisplayName != null) {
            autoCompleteTextView?.setText(lastDisplayName, false)
            selectedVoiceName = lastVoiceName
        } else if (voiceDisplayNames.isNotEmpty()) {
            autoCompleteTextView?.setText(voiceDisplayNames[0], false)
            selectedVoiceName = voiceMap[voiceDisplayNames[0]]
        }

        autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
            val displayName = adapter.getItem(position) ?: return@setOnItemClickListener
            selectedVoiceName = voiceMap[displayName]
            saveSettings()
        }
    }

    private fun pasteFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val textToPaste = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            val plainText = textToPaste.replace(Regex("[*#_`~]"), "")
            binding.editText.setText(plainText)
            Toast.makeText(this, "Đã dán và lọc văn bản", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Không có gì trong clipboard", Toast.LENGTH_SHORT).show()
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
    }

    private fun stopReading() {
        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_STOP
        }
        startService(intent)
    }

    private fun setInitialSystemVolume() {
        binding.volumeToggleGroup.check(R.id.volumeButton80)
        setSystemVolume(0.80f)
    }

    private fun setSystemVolume(percentage: Float) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVolume = (maxVolume * percentage).roundToInt()
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
        } catch (e: SecurityException) {
            Toast.makeText(this, "Không có quyền thay đổi âm lượng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSettings() {
        with(sharedPreferences.edit()) {
            putString(KEY_VOICE_NAME, selectedVoiceName)
            apply()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.shutdown()
        }
        super.onDestroy()
    }
}

