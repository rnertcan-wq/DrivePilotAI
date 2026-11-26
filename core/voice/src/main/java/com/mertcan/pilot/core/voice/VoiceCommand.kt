package com.mertcan.pilot.core.voice

sealed interface VoiceCommand {
    data object WakePilot : VoiceCommand
    data object ClosePilotSession : VoiceCommand
    data object StopWakeListener : VoiceCommand
    data class Unknown(val transcript: String) : VoiceCommand
}

class VoiceCommandParser {
    fun parse(transcript: String): VoiceCommand {
        val normalized = transcript
            .lowercase()
            .replace("ı", "i")
            .trim()

        return when {
            "pilot tamamen kapan" in normalized ||
                "pilot tamamen kapat" in normalized ||
                "pilot dinlemeyi birak" in normalized ->
                VoiceCommand.StopWakeListener

            "pilot kapan" in normalized || "pilot kapat" in normalized ->
                VoiceCommand.ClosePilotSession

            "hey pilot" in normalized || "hey paylot" in normalized ->
                VoiceCommand.WakePilot

            else -> VoiceCommand.Unknown(transcript)
        }
    }
}
