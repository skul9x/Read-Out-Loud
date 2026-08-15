package com.skul9x.readoutloud.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.skul9x.readoutloud.databinding.ActivityFullscreenReaderBinding
import io.noties.markwon.Markwon

class FullScreenReaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenReaderBinding
    private var rawContent: String = ""

    companion object {
        const val EXTRA_CONTENT = "extra_content"
        const val EXTRA_TOPIC = "extra_topic"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fix System Insets Overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }

        setupOnBackPressed()
        setupUI()
        renderContent()
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.copyButton.setOnClickListener {
            copyContentToClipboard()
        }
    }

    private fun renderContent() {
        rawContent = intent.getStringExtra(EXTRA_CONTENT) ?: ""
        val topic = intent.getStringExtra(EXTRA_TOPIC)

        if (!topic.isNullOrBlank()) {
            binding.toolbar.title = topic
        } else {
            binding.toolbar.title = "Trình đọc toàn màn hình"
        }

        val markwon = Markwon.create(this)
        markwon.setMarkdown(binding.fullScreenTextView, rawContent)
    }

    private fun copyContentToClipboard() {
        val textToCopy = if (rawContent.isNotBlank()) {
            rawContent
        } else {
            binding.fullScreenTextView.text?.toString() ?: ""
        }

        if (textToCopy.isNotBlank()) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Markdown Content", textToCopy)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Đã sao chép vào clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
    }
}

