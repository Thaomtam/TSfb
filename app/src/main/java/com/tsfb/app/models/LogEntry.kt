package com.tsfb.app.models

data class LogEntry(
    val id: Long = 0,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
) 