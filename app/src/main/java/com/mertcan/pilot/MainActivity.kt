package com.mertcan.pilot

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.mertcan.pilot.core.ui.PilotTheme
import com.mertcan.pilot.core.voice.PilotVoiceService
import com.mertcan.pilot.feature.assistant.AssistantScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isListening by remember { mutableStateOf(false) }
            var isAwake by remember { mutableStateOf(false) }
            var voiceStatus by remember { mutableStateOf("Sesli asistan kapalı") }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val microphoneGranted =
                    permissions[Manifest.permission.RECORD_AUDIO] == true ||
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                if (microphoneGranted) {
                    startPilotVoiceService()
                    isListening = true
                    voiceStatus = "Hey Pilot komutunu dinliyor"
                } else {
                    voiceStatus = "Mikrofon izni gerekli"
                }
            }

            DisposableEffect(this) {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        when (intent?.action) {
                            PilotVoiceService.ACTION_WAKE_DETECTED -> {
                                isAwake = true
                                voiceStatus = "Pilot hazır"
                            }
                            PilotVoiceService.ACTION_VOICE_COMMAND -> {
                                voiceStatus = intent.getStringExtra(
                                    PilotVoiceService.EXTRA_COMMAND
                                ) ?: "Komut alındı"
                            }
                            PilotVoiceService.ACTION_SESSION_CLOSED -> {
                                isAwake = false
                                voiceStatus = if (isListening) {
                                    "Hey Pilot komutunu dinliyor"
                                } else {
                                    "Sesli asistan kapalı"
                                }
                                finishAndRemoveTask()
                            }
                            PilotVoiceService.ACTION_LISTENING_STOPPED -> {
                                isListening = false
                                isAwake = false
                                voiceStatus = "Sesli asistan kapalı"
                            }
                        }
                    }
                }

                val filter = IntentFilter().apply {
                    addAction(PilotVoiceService.ACTION_WAKE_DETECTED)
                    addAction(PilotVoiceService.ACTION_VOICE_COMMAND)
                    addAction(PilotVoiceService.ACTION_SESSION_CLOSED)
                    addAction(PilotVoiceService.ACTION_LISTENING_STOPPED)
                }
                ContextCompat.registerReceiver(
                    this@MainActivity,
                    receiver,
                    filter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )

                onDispose {
                    runCatching { unregisterReceiver(receiver) }
                }
            }

            PilotTheme {
                AssistantScreen(
                    isListening = isListening,
                    isAwake = isAwake,
                    voiceStatus = voiceStatus,
                    onStartListening = {
                        val requestedPermissions = buildList {
                            add(Manifest.permission.RECORD_AUDIO)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                add(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }.toTypedArray()
                        permissionLauncher.launch(requestedPermissions)
                    },
                    onStopListening = {
                        stopPilotVoiceService()
                        isListening = false
                        isAwake = false
                        voiceStatus = "Sesli asistan kapalı"
                    },
                    onClosePilot = {
                        stopPilotVoiceService()
                        finishAndRemoveTask()
                    }
                )
            }
        }
    }

    private fun startPilotVoiceService() {
        val intent = Intent(this, PilotVoiceService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopPilotVoiceService() {
        stopService(Intent(this, PilotVoiceService::class.java))
    }
}
