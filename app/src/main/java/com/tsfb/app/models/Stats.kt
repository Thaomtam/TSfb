package com.tsfb.app.models

data class Stats(
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) 