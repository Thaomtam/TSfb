package com.tsfb.app.models

data class Settings(
    val interactionSpeed: Int = 1,
    val autoLike: Boolean = false,
    val autoComment: Boolean = false,
    val autoShare: Boolean = false,
    val commentTemplates: List<String> = emptyList()
) 