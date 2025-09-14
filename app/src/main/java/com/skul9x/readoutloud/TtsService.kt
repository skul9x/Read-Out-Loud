package com.skul9x.readoutloud

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
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

    companion object {
        const val CHANNEL_ID = "TtsServiceChannel"
        const val NOTIFICATION_ID = 1

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_UPDATE_SETTINGS = "ACTION_UPDATE_SETTINGS"

        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_VOICE_NAME = "EXTRA_VOICE_NAME"
        const val EXTRA_SPEED = "EXTRA_SPEED"
        const val EXTRA_PITCH = "EXTRA_PITCH"
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> stopReadingAndService()
            ACTION_UPDATE_SETTINGS -> handleUpdateSettings(intent)
        }
        return START_NOT_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    // Kiểm tra xem đây có phải là đoạn cuối cùng không
                    if (utteranceId?.startsWith("chunk_") == true) {
                        val parts = utteranceId.split("_")
                        if (parts.size == 3) {
                            val currentIndex = parts[1].toInt()
                            val totalChunks = parts[2].toInt()
                            if (currentIndex >= totalChunks - 1) {
                                stopReadingAndService()
                            }
                        }
                    }
                }
                override fun onError(utteranceId: String?) {
                    stopReadingAndService()
                }
            })
        } else {
            stopSelf()
        }
    }

    private fun handleStart(intent: Intent) {
        if (!isTtsInitialized) return

        val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
        if (text.isBlank()) {
            stopReadingAndService()
            return
        }

        val voiceName = intent.getStringExtra(EXTRA_VOICE_NAME)
        val speed = intent.getFloatExtra(EXTRA_SPEED, 1.0f)
        val pitch = intent.getFloatExtra(EXTRA_PITCH, 1.0f)

        tts.language = Locale("vi", "VN")
        tts.setSpeechRate(speed)
        tts.setPitch(pitch)

        voiceName?.let { name ->
            val voice = tts.voices.find { it.name == name }
            tts.voice = voice ?: tts.defaultVoice
        }

        startForeground(NOTIFICATION_ID, buildSimpleNotification())
        speakText(text)
    }

    private fun handleUpdateSettings(intent: Intent) {
        if (isTtsInitialized && tts.isSpeaking) {
            val speed = intent.getFloatExtra(EXTRA_SPEED, 1.0f)
            val pitch = intent.getFloatExtra(EXTRA_PITCH, 1.0f)
            tts.setSpeechRate(speed)
            tts.setPitch(pitch)
        }
    }

    private fun speakText(text: String) {
        val chunks = splitTextForTts(text)
        // Dừng lần đọc trước đó trước khi bắt đầu lần mới
        tts.stop()
        for ((index, chunk) in chunks.withIndex()) {
            val utteranceId = "chunk_${index}_${chunks.size}"
            tts.speak(chunk, TextToSpeech.QUEUE_ADD, null, utteranceId)
        }
    }

    private fun splitTextForTts(text: String): List<String> {
        val maxLength = TextToSpeech.getMaxSpeechInputLength() - 1
        if (text.length <= maxLength) return listOf(text)
        val chunks = mutableListOf<String>()
        var index = 0
        while (index < text.length) {
            var endIndex = (index + maxLength).coerceAtMost(text.length)
            // Cố gắng ngắt câu tại dấu chấm câu để tự nhiên hơn
            val sentenceEnd = text.lastIndexOfAny(charArrayOf('.', '!', '?'), endIndex)
            if (sentenceEnd > index) {
                endIndex = sentenceEnd + 1
            }
            chunks.add(text.substring(index, endIndex))
            index = endIndex
        }
        return chunks
    }

    private fun buildSimpleNotification(): Notification {
        createNotificationChannel()
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

        val openAppIntent = Intent(this, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, flag)

        val stopIntent = Intent(this, TtsService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, flag)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Đang đọc văn bản...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now) // Sử dụng icon mặc định của hệ thống
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .addAction(0, "Dừng", stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Kênh dịch vụ đọc", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(serviceChannel)
        }
    }

    private fun stopReadingAndService() {
        if (::tts.isInitialized) {
            tts.stop()
        }
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}