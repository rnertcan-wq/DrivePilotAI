package com.mertcan.pilot.core.ai

import kotlinx.coroutines.flow.Flow

interface AiAssistant {
    val events: Flow<AssistantEvent>
    suspend fun connect()
    suspend fun sendText(text: String)
    suspend fun disconnect()
}

sealed interface AssistantEvent {
    data object Connected : AssistantEvent
    data object Disconnected : AssistantEvent
    data class Transcript(val text: String, val isFinal: Boolean) : AssistantEvent
    data class AudioChunk(val bytes: ByteArray) : AssistantEvent
    data class Failure(val message: String) : AssistantEvent
}
