package com.skul9x.readoutloud

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import java.util.Locale

class TtsService : Service(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isTtsInitialized = false
    private var isStoppedManually = false
    private var pendingStartIntent: Intent? = null

    companion object {
        const val CHANNEL_ID = "TtsServiceChannel"
        const val NOTIFICATION_ID = 1
        // Actions
        const val ACTION_START = "com.skul9x.readoutloud.ACTION_START"
        const val ACTION_STOP = "com.skul9x.readoutloud.ACTION_STOP"
        // Extras
        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_LANG = "EXTRA_LANG"
        const val EXTRA_VOICE_NAME = "EXTRA_VOICE_NAME"

        private const val UTTERANCE_ID_READING_DONE = "READING_DONE"
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (isTtsInitialized) {
                    handleStartCommand(intent)
                } else {
                    pendingStartIntent = intent
                }
            }
            ACTION_STOP -> stopReadingAndService()
        }
        return START_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == UTTERANCE_ID_READING_DONE && !isStoppedManually) {
                        stopReadingAndService()
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    stopReadingAndService()
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {}
            })

            pendingStartIntent?.let {
                handleStartCommand(it)
                pendingStartIntent = null
            }
        } else {
            stopSelf()
        }
    }

    private fun handleStartCommand(intent: Intent) {
        tts.stop()
        isStoppedManually = false
        val textToRead = intent.getStringExtra(EXTRA_TEXT) ?: ""

        if (textToRead.isBlank()) {
            stopReadingAndService()
            return
        }

        // Setup TTS parameters
        val language = intent.getStringExtra(EXTRA_LANG)
        val voiceName = intent.getStringExtra(EXTRA_VOICE_NAME)
        val locale = if (language == "en-US") Locale.US else Locale("vi", "VN")
        tts.language = locale
        if (language == "vi-VN" && voiceName != null) {
            val selectedVoice = tts.voices.find { it.name == voiceName }
            tts.voice = selectedVoice ?: tts.defaultVoice
        }

        startForegroundWithNotification()

        // Split text into chunks smaller than the TTS limit
        val chunks = splitTextForTts(textToRead)
        chunks.forEachIndexed { index, chunk ->
            val utteranceId = if (index == chunks.size - 1) UTTERANCE_ID_READING_DONE else "chunk_$index"
            // The TTS engine will use the system media volume, no need to pass it here
            tts.speak(chunk, TextToSpeech.QUEUE_ADD, null, utteranceId)
        }
    }

    private fun splitTextForTts(text: String): List<String> {
        val maxLength = TextToSpeech.getMaxSpeechInputLength() - 1
        if (text.length <= maxLength) {
            return listOf(text)
        }
        val chunks = mutableListOf<String>()
        var index = 0
        while (index < text.length) {
            val endIndex = (index + maxLength).coerceAtMost(text.length)
            chunks.add(text.substring(index, endIndex))
            index = endIndex
        }
        return chunks
    }

    private fun stopReadingAndService() {
        isStoppedManually = true
        if (this::tts.isInitialized) {
            tts.stop()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundWithNotification() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this, TtsService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Đang đọc văn bản")
            .setContentText("Bấm để quay lại ứng dụng...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Dừng", stopPendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Kênh dịch vụ đọc", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        if (this::tts.isInitialized) {
            tts.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

