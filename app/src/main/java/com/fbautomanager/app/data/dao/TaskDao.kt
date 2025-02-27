package com.fbautomanager.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fbautomanager.app.data.entity.Task
import com.fbautomanager.app.data.entity.TaskType

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): LiveData<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE accountId = :accountId ORDER BY createdAt DESC")
    fun getTasksByAccount(accountId: Long): LiveData<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE type = :type ORDER BY createdAt DESC")
    fun getTasksByType(type: TaskType): LiveData<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveTasks(): LiveData<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE accountId = :accountId AND isActive = 1 ORDER BY createdAt DESC")
    fun getActiveTasksByAccount(accountId: Long): LiveData<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
    
    @Insert
    suspend fun insert(task: Task): Long
    
    @Update
    suspend fun update(task: Task)
    
    @Delete
    suspend fun delete(task: Task)
    
    @Query("DELETE FROM tasks WHERE accountId = :accountId")
    suspend fun deleteByAccount(accountId: Long)
    
    @Query("UPDATE tasks SET isActive = :isActive WHERE id = :id")
    suspend fun updateActiveStatus(id: Long, isActive: Boolean)
    
    @Query("UPDATE tasks SET isActive = :isActive WHERE accountId = :accountId")
    suspend fun updateActiveStatusByAccount(accountId: Long, isActive: Boolean)
} 