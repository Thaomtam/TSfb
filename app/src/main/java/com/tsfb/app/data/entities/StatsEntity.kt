package com.tsfb.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatsEntity(
    @PrimaryKey
    val id: Long = 1,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) 