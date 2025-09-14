package com.skul9x.readoutloud

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.Locale
import kotlin.math.roundToInt
import android.content.ClipboardManager
import android.service.quicksettings.TileService

class TtsService : Service(), TextToSpeech.OnInitListener, AudioManager.OnAudioFocusChangeListener {

    private lateinit var tts: TextToSpeech
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    private var isTtsInitialized = false
    private var wasPlayingBeforeLoss = false

    private var textToRead: String = ""
    private var chunks: List<String> = emptyList()
    private var currentChunkIndex = 0
    private var currentVoiceName: String? = null
    private var currentSpeed: Float = 1.0f
    private var currentPitch: Float = 1.0f

    companion object {
        enum class State { IDLE, PLAYING, PAUSED }
        var currentState: State = State.IDLE
            private set(value) {
                field = value
                requestTileUpdate()
            }

        private fun requestTileUpdate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TileService.requestListeningState(
                    TtsApplication.appContext,
                    ComponentName(TtsApplication.appContext, QuickSettingsTileService::class.java)
                )
            }
        }

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_READ_CLIPBOARD = "ACTION_READ_CLIPBOARD"
        const val ACTION_UPDATE_SETTINGS = "ACTION_UPDATE_SETTINGS"
        const val ACTION_SET_VOLUME = "ACTION_SET_VOLUME"

        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_VOICE_NAME = "EXTRA_VOICE_NAME"
        const val EXTRA_SPEED = "EXTRA_SPEED"
        const val EXTRA_PITCH = "EXTRA_PITCH"
        const val EXTRA_VOLUME_PERCENTAGE = "EXTRA_VOLUME_PERCENTAGE"

        const val CHANNEL_ID = "TtsServiceChannel"
        const val NOTIFICATION_ID = 1
        private const val UTTERANCE_ID_PREFIX = "reading_chunk_"
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_READ_CLIPBOARD -> handleReadClipboard()
            ACTION_PAUSE -> pauseReading()
            ACTION_RESUME -> resumeReading()
            ACTION_STOP -> stopReadingAndService()
            ACTION_UPDATE_SETTINGS -> handleUpdateSettings(intent)
            ACTION_SET_VOLUME -> handleSetVolume(intent)
        }
        return START_STICKY
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    updateNotification()
                }

                override fun onDone(utteranceId: String?) {
                    if (currentState == State.PLAYING) {
                        currentChunkIndex++
                        if (currentChunkIndex >= chunks.size) {
                            stopReadingAndService()
                        } else {
                            speakChunk(currentChunkIndex)
                        }
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    stopReadingAndService()
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {}
            })
        } else {
            stopSelf()
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> if (wasPlayingBeforeLoss) resumeReading()
            AudioManager.AUDIOFOCUS_LOSS -> if (currentState == State.PLAYING) {
                wasPlayingBeforeLoss = true
                pauseReading()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (currentState == State.PLAYING) {
                wasPlayingBeforeLoss = true
                pauseReading()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun requestAudioFocus(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this)
                .build()
            audioManager.requestAudioFocus(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (::focusRequest.isInitialized) audioManager.abandonAudioFocusRequest(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(this)
        }
    }

    private fun handleStart(intent: Intent) {
        if (!isTtsInitialized || !requestAudioFocus()) return

        textToRead = intent.getStringExtra(EXTRA_TEXT) ?: ""
        currentVoiceName = intent.getStringExtra(EXTRA_VOICE_NAME)
        currentSpeed = intent.getFloatExtra(EXTRA_SPEED, 1.0f)
        currentPitch = intent.getFloatExtra(EXTRA_PITCH, 1.0f)

        if (textToRead.isBlank()) {
            stopReadingAndService()
            return
        }
        startReading()
    }

    private fun handleReadClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipboardText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""

        if (clipboardText.isBlank()) {
            Toast.makeText(this, "Clipboard rỗng", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isTtsInitialized || !requestAudioFocus()) return

        textToRead = clipboardText
        val prefs = getSharedPreferences("ReadOutLoudPrefs", Context.MODE_PRIVATE)
        currentVoiceName = prefs.getString("lastVoiceName", null)
        currentSpeed = prefs.getInt("lastSpeed", 50) / 50f
        currentPitch = prefs.getInt("lastPitch", 50) / 50f
        startReading()
    }

    private fun handleUpdateSettings(intent: Intent) {
        if (currentState == State.PLAYING) {
            currentSpeed = intent.getFloatExtra(EXTRA_SPEED, 1.0f)
            currentPitch = intent.getFloatExtra(EXTRA_PITCH, 1.0f)
            tts.setSpeechRate(currentSpeed)
            tts.setPitch(currentPitch)
        }
    }

    private fun handleSetVolume(intent: Intent) {
        val percentage = intent.getFloatExtra(EXTRA_VOLUME_PERCENTAGE, 0.8f)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVolume = (maxVolume * percentage).roundToInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
    }

    private fun startReading() {
        tts.stop()
        currentState = State.PLAYING
        wasPlayingBeforeLoss = false
        currentChunkIndex = 0

        tts.language = Locale("vi", "VN")
        tts.setSpeechRate(currentSpeed)
        tts.setPitch(currentPitch)

        currentVoiceName?.let { name ->
            val voice = tts.voices.find { it.name == name }
            tts.voice = voice ?: tts.defaultVoice
        }

        chunks = splitTextForTts(textToRead)
        startForeground(NOTIFICATION_ID, buildNotification())
        speakChunk(currentChunkIndex)
    }

    private fun pauseReading() {
        if (currentState != State.PLAYING) return
        tts.stop()
        currentState = State.PAUSED
        updateNotification()
    }

    private fun resumeReading() {
        if (currentState != State.PAUSED) return
        if (!requestAudioFocus()) return
        currentState = State.PLAYING
        updateNotification()
        speakChunk(currentChunkIndex)
    }

    private fun speakChunk(index: Int) {
        if (index < chunks.size) {
            val chunk = chunks[index]
            tts.speak(chunk, TextToSpeech.QUEUE_FLUSH, null, "$UTTERANCE_ID_PREFIX$index")
        }
    }

    private fun stopReadingAndService() {
        tts.stop()
        currentState = State.IDLE
        abandonAudioFocus()
        stopForeground(true)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        createNotificationChannel()
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

        val openAppIntent = Intent(this, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, flag)

        val stopIntent = Intent(this, TtsService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, flag)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_qs_read_clipboard)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_notification_stop, "Dừng", stopPendingIntent)

        if (currentState == State.PLAYING) {
            val pauseIntent = Intent(this, TtsService::class.java).apply { action = ACTION_PAUSE }
            val pausePendingIntent = PendingIntent.getService(this, 2, pauseIntent, flag)
            builder.addAction(R.drawable.ic_notification_pause, "Tạm dừng", pausePendingIntent)
            builder.setContentText("Đang đọc văn bản...")
        } else if (currentState == State.PAUSED) {
            val resumeIntent = Intent(this, TtsService::class.java).apply { action = ACTION_RESUME }
            val resumePendingIntent = PendingIntent.getService(this, 3, resumeIntent, flag)
            builder.addAction(R.drawable.ic_notification_resume, "Tiếp tục", resumePendingIntent)
            builder.setContentText("Đã tạm dừng.")
        }

        if (chunks.isNotEmpty()) builder.setProgress(chunks.size, currentChunkIndex + 1, false)

        val vol80Intent = Intent(this, TtsService::class.java).apply { action = ACTION_SET_VOLUME; putExtra(EXTRA_VOLUME_PERCENTAGE, 0.80f) }
        val vol85Intent = Intent(this, TtsService::class.java).apply { action = ACTION_SET_VOLUME; putExtra(EXTRA_VOLUME_PERCENTAGE, 0.85f) }
        val vol90Intent = Intent(this, TtsService::class.java).apply { action = ACTION_SET_VOLUME; putExtra(EXTRA_VOLUME_PERCENTAGE, 0.90f) }

        builder.addAction(0, "80%", PendingIntent.getService(this, 4, vol80Intent, flag))
        builder.addAction(0, "85%", PendingIntent.getService(this, 5, vol85Intent, flag))
        builder.addAction(0, "90%", PendingIntent.getService(this, 6, vol90Intent, flag))

        builder.setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))

        return builder.build()
    }

    private fun updateNotification() {
        if (currentState != State.IDLE) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, buildNotification())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Kênh dịch vụ đọc", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(serviceChannel)
        }
    }

    private fun splitTextForTts(text: String): List<String> {
        val maxLength = TextToSpeech.getMaxSpeechInputLength() - 1
        if (text.length <= maxLength) return listOf(text)
        val chunks = mutableListOf<String>()
        var index = 0
        while (index < text.length) {
            var endIndex = (index + maxLength).coerceAtMost(text.length)
            val sentenceEnd = text.lastIndexOfAny(charArrayOf('.', '!', '?'), endIndex)
            if (sentenceEnd > index) {
                endIndex = sentenceEnd + 1
            }
            chunks.add(text.substring(index, endIndex))
            index = endIndex
        }
        return chunks
    }

    override fun onDestroy() {
        tts.shutdown()
        abandonAudioFocus()
        currentState = State.IDLE
        super.onDestroy()
    }
}

