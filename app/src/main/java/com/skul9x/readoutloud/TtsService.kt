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

    // Biến để lưu yêu cầu đọc trong khi chờ TTS khởi tạo
    private var pendingTextToSpeak: String? = null
    private var pendingVoiceNameToUse: String? = null

    private var lastUtteranceId: String? = null

    companion object {
        const val CHANNEL_ID = "TtsServiceChannel"
        const val NOTIFICATION_ID = 1

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_VOICE_NAME = "EXTRA_VOICE_NAME"
    }

    override fun onCreate() {
        super.onCreate()
        // Khởi tạo TTS, callback sẽ được gọi trong onInit
        tts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> stopReadingAndService()
        }
        return START_NOT_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == lastUtteranceId) {
                        stopReadingAndService()
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    stopReadingAndService()
                }
            })

            // Sau khi khởi tạo thành công, kiểm tra xem có yêu cầu nào đang chờ không
            pendingTextToSpeak?.let { text ->
                handlePendingRequest(text, pendingVoiceNameToUse)
            }
        } else {
            stopSelf()
        }
    }

    private fun handleStart(intent: Intent) {
        val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
        val voiceName = intent.getStringExtra(EXTRA_VOICE_NAME)

        if (text.isBlank()) {
            stopReadingAndService()
            return
        }

        if (isTtsInitialized) {
            // Nếu TTS đã sẵn sàng, thực hiện đọc ngay lập tức
            executeReading(text, voiceName)
        } else {
            // Nếu TTS chưa sẵn sàng, lưu lại yêu cầu để xử lý sau trong onInit
            pendingTextToSpeak = text
            pendingVoiceNameToUse = voiceName
        }
    }

    private fun handlePendingRequest(text: String, voiceName: String?) {
        executeReading(text, voiceName)
        // Xóa yêu cầu đã lưu sau khi thực hiện
        pendingTextToSpeak = null
        pendingVoiceNameToUse = null
    }

    private fun executeReading(text: String, voiceName: String?) {
        tts.language = Locale("vi", "VN")
        voiceName?.let { name ->
            val voice = tts.voices.find { it.name == name }
            tts.voice = voice ?: tts.defaultVoice
        }

        startForeground(NOTIFICATION_ID, buildSimpleNotification())
        speakText(text)
    }

    private fun speakText(text: String) {
        val chunks = splitTextForTts(text)
        if (chunks.isEmpty()) {
            stopReadingAndService()
            return
        }

        lastUtteranceId = "chunk_${chunks.size - 1}"
        tts.stop()

        for ((index, chunk) in chunks.withIndex()) {
            val utteranceId = "chunk_$index"
            tts.speak(chunk, TextToSpeech.QUEUE_ADD, null, utteranceId)
        }
    }

    private fun splitTextForTts(text: String): List<String> {
        val maxLength = 3900
        if (text.length <= maxLength) return listOf(text)

        val chunks = mutableListOf<String>()
        var index = 0
        while (index < text.length) {
            var endIndex = (index + maxLength).coerceAtMost(text.length)
            if (endIndex < text.length) {
                val lastPunctuation = text.lastIndexOfAny(charArrayOf('.', '!', '?'), endIndex)
                if (lastPunctuation > index) {
                    endIndex = lastPunctuation + 1
                }
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
            .setSmallIcon(R.drawable.ic_play_arrow)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "Dừng", stopPendingIntent)
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

