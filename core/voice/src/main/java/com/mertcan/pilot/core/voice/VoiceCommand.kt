package com.mertcan.pilot.core.voice

sealed interface VoiceCommand {
    data object WakePilot : VoiceCommand
    data object ClosePilot : VoiceCommand
    data class Unknown(val transcript: String) : VoiceCommand
}

class VoiceCommandParser {
    fun parse(transcript: String): VoiceCommand {
        val normalized = transcript
            .lowercase()
            .replace("ı", "i")
            .trim()

        return when {
            "pilot kapan" in normalized || "pilot kapat" in normalized ->
                VoiceCommand.ClosePilot
            "hey pilot" in normalized || "hey paylot" in normalized ->
                VoiceCommand.WakePilot
            else -> VoiceCommand.Unknown(transcript)
        }
    }
}
