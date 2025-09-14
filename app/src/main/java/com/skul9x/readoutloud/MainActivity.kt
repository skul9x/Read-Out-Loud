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
import android.widget.SeekBar
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

    companion object {
        private const val PREFS_NAME = "ReadOutLoudPrefs"
        private const val KEY_VOICE_NAME = "lastVoiceName"
        private const val KEY_SPEED = "lastSpeed"
        private const val KEY_PITCH = "lastPitch"
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

        binding.editText.movementMethod = ScrollingMovementMethod()

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

        val settingsUpdateListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSpeedAndPitchLabels()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSettings()
                updateServiceSettings()
            }
        }
        binding.speedSlider.setOnSeekBarChangeListener(settingsUpdateListener)
        binding.pitchSlider.setOnSeekBarChangeListener(settingsUpdateListener)

        loadSettings()
    }

    private fun initializeTtsForVoiceDiscovery() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                vietnameseVoices = tts.voices.filter { it.locale == Locale("vi", "VN") }
                if (vietnameseVoices.isNotEmpty()) {
                    val voiceDisplayNames = vietnameseVoices.map { formatVoiceName(it.name) }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, voiceDisplayNames)
                    (binding.voiceSelectorLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

                    val lastVoiceName = sharedPreferences.getString(KEY_VOICE_NAME, null)
                    val lastVoiceIndex = vietnameseVoices.indexOfFirst { it.name == lastVoiceName }.coerceAtLeast(0)
                    selectedVoiceName = vietnameseVoices[lastVoiceIndex].name
                    (binding.voiceSelectorLayout.editText as? AutoCompleteTextView)?.setText(adapter.getItem(lastVoiceIndex), false)

                } else {
                    Toast.makeText(this, "Không tìm thấy giọng đọc Tiếng Việt", Toast.LENGTH_LONG).show()
                }
            }
        }

        (binding.voiceSelectorLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            selectedVoiceName = vietnameseVoices[position].name
            saveSettings()
        }
    }

    private fun formatVoiceName(voiceName: String): String {
        return voiceName.replace(Regex("vi-vn-x-v(.)[a-z]-local"), "Giọng \\1").split("#")[0]
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

    private fun pasteFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val textToPaste = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            binding.editText.setText(textToPaste)
            Toast.makeText(this, "Đã dán văn bản", Toast.LENGTH_SHORT).show()
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
            putExtra(TtsService.EXTRA_SPEED, binding.speedSlider.progress / 50f)
            putExtra(TtsService.EXTRA_PITCH, binding.pitchSlider.progress / 50f)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun updateServiceSettings() {
        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_UPDATE_SETTINGS
            putExtra(TtsService.EXTRA_SPEED, binding.speedSlider.progress / 50f)
            putExtra(TtsService.EXTRA_PITCH, binding.pitchSlider.progress / 50f)
        }
        startService(intent)
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
            putInt(KEY_SPEED, binding.speedSlider.progress)
            putInt(KEY_PITCH, binding.pitchSlider.progress)
            apply()
        }
    }

    private fun loadSettings() {
        binding.speedSlider.progress = sharedPreferences.getInt(KEY_SPEED, 50)
        binding.pitchSlider.progress = sharedPreferences.getInt(KEY_PITCH, 50)
        updateSpeedAndPitchLabels()
    }

    private fun updateSpeedAndPitchLabels() {
        binding.speedValueText.text = String.format("%.2fx", binding.speedSlider.progress / 50f)
        binding.pitchValueText.text = String.format("%.2fx", binding.pitchSlider.progress / 50f)
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.shutdown()
        }
        super.onDestroy()
    }
}