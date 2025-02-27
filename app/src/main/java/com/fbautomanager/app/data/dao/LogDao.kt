package com.fbautomanager.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fbautomanager.app.data.entity.Log
import com.fbautomanager.app.data.entity.LogStatus

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    fun getAllLogs(): LiveData<List<Log>>
    
    @Query("SELECT * FROM logs WHERE accountId = :accountId ORDER BY timestamp DESC")
    fun getLogsByAccount(accountId: Long): LiveData<List<Log>>
    
    @Query("SELECT * FROM logs WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getLogsByTask(taskId: Long): LiveData<List<Log>>
    
    @Query("SELECT * FROM logs WHERE status = :status ORDER BY timestamp DESC")
    fun getLogsByStatus(status: LogStatus): LiveData<List<Log>>
    
    @Query("SELECT * FROM logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getLogsByTimeRange(startTime: Long, endTime: Long): LiveData<List<Log>>
    
    @Insert
    suspend fun insert(log: Log): Long
    
    @Insert
    suspend fun insertAll(logs: List<Log>)
    
    @Delete
    suspend fun delete(log: Log)
    
    @Query("DELETE FROM logs WHERE accountId = :accountId")
    suspend fun deleteByAccount(accountId: Long)
    
    @Query("DELETE FROM logs")
    suspend fun deleteAll()
} 