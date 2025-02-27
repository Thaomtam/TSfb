package com.fbautomanager.app.data.repository

import androidx.lifecycle.LiveData
import com.fbautomanager.app.data.dao.LogDao
import com.fbautomanager.app.data.entity.Log
import com.fbautomanager.app.data.entity.LogStatus

class LogRepository(private val logDao: LogDao) {
    
    /**
     * Lấy tất cả nhật ký
     */
    fun getAllLogs(): LiveData<List<Log>> {
        return logDao.getAllLogs()
    }
    
    /**
     * Lấy nhật ký theo tài khoản
     */
    fun getLogsByAccount(accountId: Long): LiveData<List<Log>> {
        return logDao.getLogsByAccount(accountId)
    }
    
    /**
     * Lấy nhật ký theo tác vụ
     */
    fun getLogsByTask(taskId: Long): LiveData<List<Log>> {
        return logDao.getLogsByTask(taskId)
    }
    
    /**
     * Lấy nhật ký theo trạng thái
     */
    fun getLogsByStatus(status: LogStatus): LiveData<List<Log>> {
        return logDao.getLogsByStatus(status)
    }
    
    /**
     * Lấy nhật ký theo khoảng thời gian
     */
    fun getLogsByTimeRange(startTime: Long, endTime: Long): LiveData<List<Log>> {
        return logDao.getLogsByTimeRange(startTime, endTime)
    }
    
    /**
     * Thêm nhật ký mới
     */
    suspend fun insert(log: Log): Long {
        return logDao.insert(log)
    }
    
    /**
     * Thêm nhiều nhật ký
     */
    suspend fun insertAll(logs: List<Log>) {
        logDao.insertAll(logs)
    }
    
    /**
     * Xóa nhật ký
     */
    suspend fun delete(log: Log) {
        logDao.delete(log)
    }
    
    /**
     * Xóa nhật ký theo tài khoản
     */
    suspend fun deleteByAccount(accountId: Long) {
        logDao.deleteByAccount(accountId)
    }
    
    /**
     * Xóa tất cả nhật ký
     */
    suspend fun deleteAll() {
        logDao.deleteAll()
    }
} 