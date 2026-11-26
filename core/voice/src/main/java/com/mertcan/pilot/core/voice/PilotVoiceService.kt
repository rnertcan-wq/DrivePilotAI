package com.mertcan.pilot.core.voice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import com.mertcan.pilot.core.voice.R
import java.util.Locale

/**
 * Development wake-phrase bridge.
 *
 * Android's general SpeechRecognizer is not a dedicated hotword engine. Replace this
 * adapter with an on-device wake-word model before shipping an always-listening product.
 */
class PilotVoiceService : Service() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false
    private var recognitionRunning = false
    private var speaking = false
    private var pilotAwake = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAsForeground()
        initializeTextToSpeech()
        initializeSpeechRecognizer()
        startRecognition()
        return START_NOT_STICKY
    }

    private fun startAsForeground() {
        createNotificationChannel()
        val notification = buildNotification("Hey Pilot komutunu dinliyor")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun initializeSpeechRecognizer() {
        if (speechRecognizer != null) return
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            updateNotification("Bu cihazda konuşma tanıma servisi bulunamadı")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: android.os.Bundle?) {
                    recognitionRunning = true
                }

                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() {
                    recognitionRunning = false
                }

                override fun onError(error: Int) {
                    recognitionRunning = false
                    if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                        stopSelf()
                    } else {
                        scheduleRecognition(RETRY_DELAY_MS)
                    }
                }

                override fun onResults(results: android.os.Bundle?) {
                    recognitionRunning = false
                    val spokenText = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                    if (!spokenText.isNullOrBlank()) {
                        processTranscript(spokenText)
                    }
                    scheduleRecognition(RESULT_RESTART_DELAY_MS)
                }

                override fun onPartialResults(partialResults: android.os.Bundle?) = Unit
                override fun onEvent(eventType: Int, params: android.os.Bundle?) = Unit
            })
        }
    }

    private fun initializeTextToSpeech() {
        if (textToSpeech != null) return
        textToSpeech = TextToSpeech(applicationContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) {
                textToSpeech?.language = Locale.forLanguageTag("tr-TR")
                textToSpeech?.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) = Unit

                        override fun onDone(utteranceId: String?) {
                            mainHandler.post {
                                speaking = false
                                startRecognition()
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            mainHandler.post {
                                speaking = false
                                startRecognition()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun startRecognition() {
        if (speaking || recognitionRunning || speechRecognizer == null) return
        val request = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }

        runCatching {
            speechRecognizer?.startListening(request)
            recognitionRunning = true
        }.onFailure {
            recognitionRunning = false
            scheduleRecognition(RETRY_DELAY_MS)
        }
    }

    private fun processTranscript(transcript: String) {
        val normalized = transcript
            .lowercase(Locale.forLanguageTag("tr-TR"))
            .replace("ı", "i")
            .replace("İ", "i")
            .trim()

        if (normalized.contains("pilot kapan") ||
            normalized.contains("pilot kapat") ||
            normalized.contains("hey pilot kapan")
        ) {
            closePilotSession()
            return
        }

        val wakeDetected = normalized.contains("hey pilot") ||
            normalized.contains("hey paylot") ||
            normalized.contains("hey pailot")

        if (!pilotAwake && wakeDetected) {
            pilotAwake = true
            updateNotification("Pilot uyandı • komutunu bekliyor")
            sendPilotBroadcast(ACTION_WAKE_DETECTED)
            speak("Buradayım. Nasıl yardımcı olayım?")
            return
        }

        if (pilotAwake && transcript.isNotBlank()) {
            sendPilotBroadcast(ACTION_VOICE_COMMAND) {
                putExtra(EXTRA_COMMAND, transcript)
            }
        }
    }

    private fun speak(message: String) {
        if (!ttsReady || textToSpeech == null) {
            updateNotification("Pilot uyandı • komutunu bekliyor")
            return
        }
        speaking = true
        runCatching {
            speechRecognizer?.cancel()
            recognitionRunning = false
            textToSpeech?.speak(
                message,
                TextToSpeech.QUEUE_FLUSH,
                null,
                TTS_UTTERANCE_ID
            )
        }.onFailure {
            speaking = false
            scheduleRecognition(RETRY_DELAY_MS)
        }
    }

    private fun closePilotSession() {
        sendPilotBroadcast(ACTION_SESSION_CLOSED)
        stopSelf()
    }

    private fun sendPilotBroadcast(action: String, extras: Intent.() -> Unit = {}) {
        val event = Intent(action).setPackage(packageName).apply(extras)
        sendBroadcast(event)
    }

    private fun scheduleRecognition(delayMillis: Long) {
        mainHandler.removeCallbacks(restartRecognition)
        mainHandler.postDelayed(restartRecognition, delayMillis)
    }

    private val restartRecognition = Runnable { startRecognition() }

    private fun updateNotification(message: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(message))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pilot sesli asistan",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Pilot'un sesli komut dinleme durumu"
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(message: String): Notification {
        val openAppIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = openAppIntent?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Pilot aktif")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacksAndMessages(null)
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        sendPilotBroadcast(ACTION_LISTENING_STOPPED)
        super.onDestroy()
    }

    companion object {
        const val ACTION_WAKE_DETECTED = "com.mertcan.pilot.action.WAKE_DETECTED"
        const val ACTION_VOICE_COMMAND = "com.mertcan.pilot.action.VOICE_COMMAND"
        const val ACTION_SESSION_CLOSED = "com.mertcan.pilot.action.SESSION_CLOSED"
        const val ACTION_LISTENING_STOPPED = "com.mertcan.pilot.action.LISTENING_STOPPED"
        const val EXTRA_COMMAND = "extra_command"

        private const val CHANNEL_ID = "pilot_voice"
        private const val NOTIFICATION_ID = 4101
        private const val TTS_UTTERANCE_ID = "pilot_wake_response"
        private const val RETRY_DELAY_MS = 900L
        private const val RESULT_RESTART_DELAY_MS = 350L
    }
}
