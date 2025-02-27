package com.fbautomanager.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fbautomanager.app.data.entity.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): LiveData<List<Account>>
    
    @Query("SELECT * FROM accounts WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveAccounts(): LiveData<List<Account>>
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Long): Account?
    
    @Query("SELECT * FROM accounts WHERE packageName = :packageName LIMIT 1")
    suspend fun getAccountByPackage(packageName: String): Account?
    
    @Insert
    suspend fun insert(account: Account): Long
    
    @Update
    suspend fun update(account: Account)
    
    @Delete
    suspend fun delete(account: Account)
    
    @Query("UPDATE accounts SET isActive = :isActive WHERE id = :id")
    suspend fun updateActiveStatus(id: Long, isActive: Boolean)
} 