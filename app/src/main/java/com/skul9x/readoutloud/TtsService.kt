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
import android.speech.tts.Voice
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale

class TtsService : Service(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var textToRead: String? = null
    private var language: String? = "vi-VN"
    private var voiceName: String? = null

    companion object {
        const val CHANNEL_ID = "TtsServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "com.skul9x.readoutloud.ACTION_START"
        const val ACTION_STOP = "com.skul9x.readoutloud.ACTION_STOP"
        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_LANG = "EXTRA_LANG"
        const val EXTRA_VOICE_NAME = "EXTRA_VOICE_NAME"
    }

    override fun onCreate() {
        super.onCreate()
        // Khởi tạo TextToSpeech khi dịch vụ được tạo
        tts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                textToRead = intent.getStringExtra(EXTRA_TEXT)
                language = intent.getStringExtra(EXTRA_LANG)
                voiceName = intent.getStringExtra(EXTRA_VOICE_NAME)

                if (!textToRead.isNullOrBlank()) {
                    startForegroundWithNotification()
                    // Việc đọc sẽ bắt đầu trong onInit sau khi TTS sẵn sàng
                }
            }
            ACTION_STOP -> {
                stopReadingAndService()
            }
        }
        return START_NOT_STICKY
    }

    // Callback khi TTS đã sẵn sàng
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Thiết lập ngôn ngữ và giọng đọc
            val locale = if (language == "en-US") Locale.US else Locale("vi", "VN")
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsService", "Ngôn ngữ không được hỗ trợ")
                stopSelf()
                return
            }

            // Nếu có giọng đọc cụ thể được chọn, hãy đặt nó
            if (language == "vi-VN" && voiceName != null) {
                val selectedVoice = tts.voices.find { it.name == voiceName }
                if (selectedVoice != null) {
                    tts.voice = selectedVoice
                } else {
                    Log.w("TtsService", "Không tìm thấy giọng đọc: $voiceName")
                }
            }

            // Theo dõi tiến trình đọc để tự động dừng dịch vụ khi đọc xong
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    // Tự động dừng dịch vụ khi đọc xong
                    stopReadingAndService()
                }
                override fun onError(utteranceId: String?) {
                    stopReadingAndService()
                }
            })

            // Bắt đầu đọc
            textToRead?.let { speak(it) }
        } else {
            Log.e("TtsService", "Không thể khởi tạo TextToSpeech")
            stopSelf()
        }
    }

    private fun speak(text: String) {
        // Chia văn bản thành các đoạn nhỏ hơn để tránh lỗi giới hạn ký tự của TTS
        val chunks = text.chunked(TextToSpeech.getMaxSpeechInputLength() - 1)
        for (chunk in chunks) {
            tts.speak(chunk, TextToSpeech.QUEUE_ADD, null, "UniqueID")
        }
    }

    private fun startForegroundWithNotification() {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Tạo action "Dừng" cho thông báo
        val stopIntent = Intent(this, TtsService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Đang đọc văn bản")
            .setContentText("Ứng dụng đang đọc văn bản của bạn...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Dừng", stopPendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Kênh dịch vụ đọc",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun stopReadingAndService() {
        if (this::tts.isInitialized && tts.isSpeaking) {
            tts.stop()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Không cần onBind cho dịch vụ loại này
    }
}
