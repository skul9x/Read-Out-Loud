package com.skul9x.readoutloud

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.skul9x.readoutloud.databinding.ActivityMainTabsBinding
import com.skul9x.readoutloud.ui.MainPagerAdapter
import com.skul9x.readoutloud.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainTabsBinding
    private var currentVolumePercent = 80
    var isUserScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTabsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fix Status Bar Overlap (Window Insets)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, insets.top, 0, 0)
            windowInsets
        }

        setupToolbar()
        setupTabs()
        setupVolumeControl()
    }

    private fun setupToolbar() {
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.volumeButton.setOnClickListener {
            cycleVolume()
        }
    }

    private fun setupTabs() {
        val pagerAdapter = MainPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Read"
                    tab.setIcon(R.drawable.ic_tab_read)
                }
                1 -> {
                    tab.text = "Prompt"
                    tab.setIcon(R.drawable.ic_tab_prompt)
                }
            }
        }.attach()
    }

    private fun setupVolumeControl() {
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

    fun setLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE

        findViewById<View>(R.id.pasteCard)?.isEnabled = !isLoading
        findViewById<View>(R.id.readCard)?.isEnabled = !isLoading
        findViewById<View>(R.id.aiTextButton)?.isEnabled = !isLoading
        findViewById<View>(R.id.summarizeButton)?.isEnabled = !isLoading

        val statusText = findViewById<TextView>(R.id.statusText)
        if (isLoading) {
            statusText?.setTextColor(ContextCompat.getColor(this, R.color.md_theme_dark_primary))
        } else {
            statusText?.setTextColor(ContextCompat.getColor(this, R.color.md_theme_dark_onSurfaceVariant))
        }
    }

    fun switchToTab(index: Int) {
        binding.viewPager.setCurrentItem(index, true)
    }
}

