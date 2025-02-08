package com.tsfb.app.data.dao

import androidx.room.*
import com.tsfb.app.data.entities.StatsEntity

@Dao
interface StatsDao {
    @Query("SELECT * FROM stats WHERE id = 1")
    suspend fun get(): StatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: StatsEntity)
} 