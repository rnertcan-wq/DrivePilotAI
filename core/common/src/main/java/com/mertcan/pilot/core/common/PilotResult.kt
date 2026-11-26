package com.mertcan.pilot.core.common

sealed interface PilotResult<out T> {
    data class Success<T>(val value: T) : PilotResult<T>
    data class Failure(val cause: Throwable) : PilotResult<Nothing>
}
