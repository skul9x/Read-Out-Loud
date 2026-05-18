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
import com.skul9x.readoutloud.data.ModelManager
import com.skul9x.readoutloud.data.ModelQuotaManager
import com.skul9x.readoutloud.databinding.ActivitySettingsBinding
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var apiKeyManager: ApiKeyManager
    private lateinit var modelManager: ModelManager
    private lateinit var quotaManager: ModelQuotaManager
    private lateinit var modelAdapter: ModelAdapter
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
        modelManager = ModelManager.getInstance(this)
        quotaManager = ModelQuotaManager.getInstance(this)
        
        setupUI()
        setupModelsList()
        loadCurrentKeys()
        initializeTts()
    }

    private fun setupModelsList() {
        modelAdapter = ModelAdapter(
            models = modelManager.getModelItems(),
            quotaManager = quotaManager,
            apiKeyManager = apiKeyManager,
            onToggle = { index ->
                modelManager.toggleModel(index)
                refreshModelsList()
            },
            onMoveUp = { index ->
                modelManager.moveUp(index)
                refreshModelsList()
            },
            onMoveDown = { index ->
                modelManager.moveDown(index)
                refreshModelsList()
            },
            onDelete = { index ->
                modelManager.removeModel(index)
                refreshModelsList()
            },
            onEdit = { index ->
                showEditModelDialog(index)
            }
        )

        binding.modelsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SettingsActivity)
            adapter = modelAdapter
        }
    }

    private fun refreshModelsList() {
        modelAdapter.updateModels(modelManager.getModelItems())
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.settingsPasteButton.setOnClickListener {
            pasteKeysFromClipboard()
        }

        binding.addModelButton.setOnClickListener {
            showAddModelDialog()
        }

        binding.resetModelsButton.setOnClickListener {
            modelManager.resetToDefault()
            refreshModelsList()
            Toast.makeText(this, "Đã khôi phục danh sách mặc định", Toast.LENGTH_SHORT).show()
        }

        binding.saveButton.setOnClickListener {
            saveKeys()
        }
    }

    private fun showAddModelDialog() {
        val paddingDp = 20
        val density = resources.displayMetrics.density
        val paddingPx = (paddingDp * density).toInt()
        
        val container = android.widget.FrameLayout(this)
        val editText = com.google.android.material.textfield.TextInputEditText(this).apply {
            hint = "Ví dụ: models/gemini-2.5-pro"
            setSingleLine(true)
        }
        val layoutParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = paddingPx
            rightMargin = paddingPx
            topMargin = paddingPx / 2
            bottomMargin = paddingPx / 2
        }
        container.addView(editText, layoutParams)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Thêm mô hình mới")
            .setView(container)
            .setPositiveButton("Thêm") { dialog, _ ->
                val modelName = editText.text.toString().trim()
                if (modelName.isNotEmpty()) {
                    modelManager.addModel(modelName)
                    refreshModelsList()
                    Toast.makeText(this, "Đã thêm mô hình: $modelName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tên mô hình không được để trống", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showEditModelDialog(index: Int) {
        val currentItems = modelManager.getModelItems()
        if (index !in currentItems.indices) return
        val currentItem = currentItems[index]

        val paddingDp = 20
        val density = resources.displayMetrics.density
        val paddingPx = (paddingDp * density).toInt()
        
        val container = android.widget.FrameLayout(this)
        val editText = com.google.android.material.textfield.TextInputEditText(this).apply {
            setText(currentItem.name)
            setSelection(currentItem.name.length)
            setSingleLine(true)
        }
        val layoutParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = paddingPx
            rightMargin = paddingPx
            topMargin = paddingPx / 2
            bottomMargin = paddingPx / 2
        }
        container.addView(editText, layoutParams)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Chỉnh sửa mô hình")
            .setView(container)
            .setPositiveButton("Lưu") { dialog, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val updatedItems = currentItems.toMutableList()
                    updatedItems[index] = currentItem.copy(name = newName)
                    modelManager.saveModelItems(updatedItems)
                    refreshModelsList()
                    Toast.makeText(this, "Đã cập nhật mô hình", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tên mô hình không được để trống", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
