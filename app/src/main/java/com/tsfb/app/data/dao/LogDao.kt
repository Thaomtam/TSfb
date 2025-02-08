package com.tsfb.app.data.dao

import androidx.room.*
import com.tsfb.app.data.entities.LogEntity

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    suspend fun getAll(): List<LogEntity>

    @Insert
    suspend fun insert(log: LogEntity)

    @Query("DELETE FROM logs")
    suspend fun deleteAll()
} 