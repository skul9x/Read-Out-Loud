package com.skul9x.readoutloud.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.skul9x.readoutloud.MainActivity
import com.skul9x.readoutloud.data.GeminiApiClient
import com.skul9x.readoutloud.databinding.FragmentPromptBinding
import com.skul9x.readoutloud.utils.PromptTemplateHelper
import io.noties.markwon.Markwon
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PromptFragment : Fragment() {

    private var _binding: FragmentPromptBinding? = null
    val binding get() = _binding!!

    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    private lateinit var geminiApiClient: GeminiApiClient
    private lateinit var markwon: Markwon
    private var searchJob: Job? = null
    private var currentResultText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geminiApiClient = GeminiApiClient(requireContext())
        markwon = Markwon.create(requireContext())
        setupInputAndButtons()
        setupListeners()
    }

    private fun setupInputAndButtons() {
        val topicText = binding.promptTopicInput.text?.toString()?.trim() ?: ""
        updateButtonStates(topicText.isNotEmpty())

        binding.promptTopicInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = s?.toString()?.trim()?.isNotEmpty() == true
                updateButtonStates(hasText)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.promptTopicInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
    }

    private fun updateButtonStates(enabled: Boolean) {
        binding.makePromptButton.isEnabled = enabled
        binding.searchNowButton.isEnabled = enabled
    }

    private fun setupListeners() {
        binding.makePromptButton.setOnClickListener {
            handleMakePrompt()
        }

        binding.searchNowButton.setOnClickListener {
            triggerHapticFeedback()
            handleSearchNow()
        }

        binding.retryButton.setOnClickListener {
            triggerHapticFeedback()
            handleSearchNow()
        }

        binding.summarizeResultButton.setOnClickListener {
            triggerHapticFeedback()
            val resultText = binding.resultTextView.text?.toString() ?: ""
            if (resultText.isNotBlank()) {
                // Scale animation for feedback
                it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(75).withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(75).start()
                }.start()

                // Disable to prevent double-tap
                it.isEnabled = false

                // Post to shared ViewModel
                sharedViewModel.requestSummarize(resultText)

                // Smooth tab switch
                (requireActivity() as? MainActivity)?.switchToTab(0)
            }
        }

        binding.readResultButton.setOnClickListener {
            triggerHapticFeedback()
            val resultText = binding.resultTextView.text?.toString() ?: ""
            if (resultText.isNotBlank()) {
                // Scale animation for feedback
                it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(75).withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(75).start()
                }.start()

                // Disable to prevent double-tap
                it.isEnabled = false

                // Post to shared ViewModel
                sharedViewModel.requestReadAloud(resultText)

                // Smooth tab switch
                (requireActivity() as? MainActivity)?.switchToTab(0)
            }
        }

        binding.showResultButton.setOnClickListener {
            triggerHapticFeedback()
            val resultText = if (currentResultText.isNotBlank()) currentResultText else (binding.resultTextView.text?.toString() ?: "")
            val topic = binding.promptTopicInput.text?.toString()?.trim() ?: ""
            if (resultText.isNotBlank()) {
                it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(75).withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(75).start()
                }.start()

                val intent = Intent(requireContext(), FullScreenReaderActivity::class.java).apply {
                    putExtra(FullScreenReaderActivity.EXTRA_CONTENT, resultText)
                    putExtra(FullScreenReaderActivity.EXTRA_TOPIC, topic)
                }
                startActivity(intent)
            }
        }
    }

    private fun handleMakePrompt() {
        hideKeyboard()
        val topic = binding.promptTopicInput.text?.toString()?.trim() ?: ""
        if (topic.isEmpty()) {
            Snackbar.make(binding.root, "⚠️ Vui lòng nhập chủ đề", Snackbar.LENGTH_SHORT).show()
            return
        }

        val template = PromptTemplateHelper.loadTemplate(requireContext())
        val builtPrompt = PromptTemplateHelper.buildPrompt(template, topic)

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Prompt", builtPrompt)
        clipboard.setPrimaryClip(clip)

        Snackbar.make(binding.root, "✅ Đã copy prompt vào clipboard!", Snackbar.LENGTH_SHORT).show()
        binding.promptStatusText.text = "Prompt đã sẵn sàng trong clipboard"
    }

    private fun handleSearchNow() {
        hideKeyboard()
        val topic = binding.promptTopicInput.text?.toString()?.trim() ?: ""
        if (topic.isEmpty()) {
            Snackbar.make(binding.root, "⚠️ Vui lòng nhập chủ đề", Snackbar.LENGTH_SHORT).show()
            return
        }

        showLoading()

        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            val template = PromptTemplateHelper.loadTemplate(requireContext())
            val prompt = PromptTemplateHelper.buildPrompt(template, topic)
            val result = geminiApiClient.searchWithPrompt(prompt)

            if (_binding == null) return@launch

            when (result) {
                is GeminiApiClient.GeminiResult.Success -> {
                    showResult(result.text, result.model)
                }
                is GeminiApiClient.GeminiResult.AllQuotaExhausted -> {
                    showError("⚠️ Hết quota API", result.getFinalText())
                }
                is GeminiApiClient.GeminiResult.NoApiKeys -> {
                    showError("⚠️ Chưa có API Key", result.getFinalText())
                }
                is GeminiApiClient.GeminiResult.Error -> {
                    showError("⚠️ Lỗi kết nối", result.getFinalText())
                }
            }
        }
    }

    fun showLoading() {
        currentResultText = ""
        binding.emptyStateGroup.visibility = View.GONE
        binding.resultCard.visibility = View.GONE
        binding.errorCard.visibility = View.GONE
        binding.resultActionsLayout.visibility = View.GONE
        fadeIn(binding.loadingCard, 200L)

        binding.promptTopicInput.isEnabled = false
        binding.makePromptButton.isEnabled = false
        binding.searchNowButton.isEnabled = false
        binding.summarizeResultButton.isEnabled = false
        binding.readResultButton.isEnabled = false
        binding.showResultButton.isEnabled = false
        binding.promptStatusText.text = "Đang tìm kiếm..."
    }

    fun showResult(text: String, model: String) {
        currentResultText = text.trim()
        binding.loadingCard.visibility = View.GONE
        binding.errorCard.visibility = View.GONE
        binding.emptyStateGroup.visibility = View.GONE

        markwon.setMarkdown(binding.resultTextView, text.trim())
        fadeIn(binding.resultCard, 300L)

        binding.summarizeResultButton.isEnabled = true
        binding.readResultButton.isEnabled = true
        binding.showResultButton.isEnabled = true
        binding.resultActionsLayout.postDelayed({
            if (_binding != null) {
                slideDown(binding.resultActionsLayout, 200L)
            }
        }, 100L)

        val modelShortName = model.substringAfter("/")
        binding.promptStatusText.text = "Gemini: Done ($modelShortName)"

        binding.promptTopicInput.isEnabled = true
        updateButtonStates(binding.promptTopicInput.text?.toString()?.trim()?.isNotEmpty() == true)
    }

    fun showError(title: String, message: String) {
        currentResultText = ""
        binding.loadingCard.visibility = View.GONE
        binding.resultCard.visibility = View.GONE
        binding.errorCard.visibility = View.GONE
        binding.resultActionsLayout.visibility = View.GONE

        binding.errorTitle.text = title
        binding.errorMessage.text = message
        fadeIn(binding.errorCard, 200L)

        binding.promptStatusText.text = "Lỗi Gemini"

        binding.promptTopicInput.isEnabled = true
        updateButtonStates(binding.promptTopicInput.text?.toString()?.trim()?.isNotEmpty() == true)
    }

    fun fadeIn(view: View, duration: Long = 300L) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun slideDown(view: View, duration: Long = 200L) {
        view.alpha = 0f
        view.translationY = -20f * resources.displayMetrics.density
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun triggerHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(10)
            }
        } catch (_: Exception) {}
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val currentFocus = activity?.currentFocus ?: view
        currentFocus?.let {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.summarizeResultButton?.isEnabled = true
        _binding?.readResultButton?.isEnabled = true
        _binding?.showResultButton?.isEnabled = true
    }

    override fun onDestroyView() {
        searchJob?.cancel()
        _binding?.let { b ->
            b.loadingCard.animate().cancel()
            b.resultCard.animate().cancel()
            b.errorCard.animate().cancel()
            b.resultActionsLayout.animate().cancel()
            b.summarizeResultButton.animate().cancel()
            b.readResultButton.animate().cancel()
            b.showResultButton.animate().cancel()
        }
        super.onDestroyView()
        _binding = null
    }
}
