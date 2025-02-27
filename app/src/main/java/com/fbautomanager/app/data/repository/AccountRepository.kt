package com.fbautomanager.app.data.repository

import androidx.lifecycle.LiveData
import com.fbautomanager.app.data.dao.AccountDao
import com.fbautomanager.app.data.entity.Account

class AccountRepository(private val accountDao: AccountDao) {
    val allAccounts: LiveData<List<Account>> = accountDao.getAllAccounts()
    val activeAccounts: LiveData<List<Account>> = accountDao.getActiveAccounts()
    
    suspend fun insert(account: Account): Long {
        return accountDao.insert(account)
    }
    
    suspend fun update(account: Account) {
        accountDao.update(account)
    }
    
    suspend fun delete(account: Account) {
        accountDao.delete(account)
    }
    
    suspend fun getAccountById(id: Long): Account? {
        return accountDao.getAccountById(id)
    }
    
    suspend fun getAccountByPackage(packageName: String): Account? {
        return accountDao.getAccountByPackage(packageName)
    }
    
    suspend fun updateActiveStatus(id: Long, isActive: Boolean) {
        accountDao.updateActiveStatus(id, isActive)
    }
} 