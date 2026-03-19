package com.skul9x.readoutloud.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var apiKeyManager: ApiKeyManager
    private lateinit var tts: TextToSpeech
    
    private var vietnameseVoices = listOf<Voice>()
    
    companion object {
        private const val PREFS_NAME = "ReadOutLoudPrefs"
        private const val KEY_VOICE_NAME = "lastVoiceName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Fix Status Bar Overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, insets.top, 0, 0)
            windowInsets
        }
        
        apiKeyManager = ApiKeyManager.getInstance(this)
        
        setupUI()
        loadCurrentKeys()
        initializeTts()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.settingsPasteButton.setOnClickListener {
            pasteKeysFromClipboard()
        }

        binding.saveCard.setOnClickListener {
            saveKeys()
        }
    }

    private fun pasteKeysFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!clipboard.hasPrimaryClip()) {
            Toast.makeText(this, "Clipboard trống", Toast.LENGTH_SHORT).show()
            return
        }

        val rawText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        val foundKeys = ApiKeyManager.parseApiKeysFromRaw(rawText)

        if (foundKeys.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy API Key (AIza...) trong Clipboard", Toast.LENGTH_LONG).show()
        } else {
            val currentText = binding.apiKeyEditText.text.toString().trim()
            val newText = if (currentText.isEmpty()) {
                foundKeys.joinToString("\n")
            } else {
                currentText + "\n" + foundKeys.joinToString("\n")
            }
            binding.apiKeyEditText.setText(newText)
            Toast.makeText(this, "Đã dán thêm ${foundKeys.size} keys", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCurrentKeys() {
        val keys = apiKeyManager.getApiKeys()
        if (keys.isNotEmpty()) {
            val keysString = keys.joinToString("\n")
            binding.apiKeyEditText.setText(keysString)
        }
    }

    private fun saveKeys() {
        val rawInput = binding.apiKeyEditText.text.toString().trim()
        
        if (rawInput.isEmpty()) {
            apiKeyManager.clearAllKeys()
            Toast.makeText(this, "Đã xóa toàn bộ API Keys", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val keys = ApiKeyManager.parseApiKeysFromRaw(rawInput)
        if (keys.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy API Key hợp lệ (phải bắt đầu bằng AIza)", Toast.LENGTH_LONG).show()
            return
        }

        apiKeyManager.setApiKeys(keys)
        
        // Refresh UI with cleanly parsed keys
        val keysString = keys.joinToString("\n")
        binding.apiKeyEditText.setText(keysString)

        Toast.makeText(this, "Đã lưu bản cấu hình (${keys.size} keys)", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initializeTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                vietnameseVoices = tts.voices
                    .filter { it.locale == Locale("vi", "VN") && !it.isNetworkConnectionRequired }
                    .distinctBy { it.name }
                    .sortedBy { it.name }

                setupVoiceDropdown()
            }
        }
    }

    private fun setupVoiceDropdown() {
        if (vietnameseVoices.isEmpty()) {
            binding.voiceAutoComplete.setText("Không tìm thấy giọng đọc VN")
            binding.voiceSelectorLayout.isEnabled = false
            return
        }

        // Friendly names: Giọng đọc 1, Giọng đọc 2...
        val voiceDisplayNames = vietnameseVoices.mapIndexed { index, _ -> "Giọng đọc ${index + 1}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, voiceDisplayNames)
        binding.voiceAutoComplete.setAdapter(adapter)

        // Load current selection
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedVoiceName = sharedPrefs.getString(KEY_VOICE_NAME, null)
        
        val currentIndex = vietnameseVoices.indexOfFirst { it.name == savedVoiceName }
        if (currentIndex != -1) {
            binding.voiceAutoComplete.setText(voiceDisplayNames[currentIndex], false)
        } else {
            binding.voiceAutoComplete.setText(voiceDisplayNames[0], false)
        }

        binding.voiceAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedVoice = vietnameseVoices[position]
            sharedPrefs.edit().putString(KEY_VOICE_NAME, selectedVoice.name).apply()
            Toast.makeText(this, "Đã chọn: ${voiceDisplayNames[position]}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.shutdown()
        }
        super.onDestroy()
    }
}
