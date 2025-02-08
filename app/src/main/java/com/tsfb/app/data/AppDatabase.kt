package com.tsfb.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tsfb.app.data.dao.LogDao
import com.tsfb.app.data.dao.StatsDao
import com.tsfb.app.data.entities.LogEntity
import com.tsfb.app.data.entities.StatsEntity

@Database(entities = [LogEntity::class, StatsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun statsDao(): StatsDao
} 