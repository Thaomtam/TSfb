package com.fbautomanager.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fbautomanager.app.data.converter.DateConverter
import com.fbautomanager.app.data.dao.AccountDao
import com.fbautomanager.app.data.dao.LogDao
import com.fbautomanager.app.data.dao.TaskDao
import com.fbautomanager.app.data.entity.Account
import com.fbautomanager.app.data.entity.Log
import com.fbautomanager.app.data.entity.Task

@Database(
    entities = [Account::class, Task::class, Log::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun taskDao(): TaskDao
    abstract fun logDao(): LogDao
} 