package com.mertcan.pilot.core.notification

data class NotificationItem(
    val packageName: String,
    val title: String?,
    val message: String?,
    val receivedAtMillis: Long
)
