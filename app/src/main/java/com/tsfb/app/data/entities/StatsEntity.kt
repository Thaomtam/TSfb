package com.tsfb.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) 