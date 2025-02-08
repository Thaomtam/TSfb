package com.tsfb.app.data

data class Settings(
    val interactionSpeed: Int = 5,
    val apiKey: String = "",
    val autoLikeEnabled: Boolean = false,
    val autoCommentEnabled: Boolean = false,
    val autoShareEnabled: Boolean = false,
    val commentTemplates: List<String> = emptyList()
) 