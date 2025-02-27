package com.fbautomanager.app.ui.logs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbautomanager.app.FBAutoManagerApp
import com.fbautomanager.app.data.entity.Log
import com.fbautomanager.app.data.repository.LogRepository
import kotlinx.coroutines.launch

class LogsViewModel : ViewModel() {
    
    private val logRepository: LogRepository
    val logs: LiveData<List<Log>>
    
    init {
        val logDao = FBAutoManagerApp.database.logDao()
        logRepository = LogRepository(logDao)
        logs = logRepository.getAllLogs()
    }
    
    /**
     * Xóa tất cả nhật ký
     */
    fun clearAllLogs() {
        viewModelScope.launch {
            logRepository.deleteAll()
        }
    }
    
    /**
     * Lọc nhật ký theo tài khoản
     */
    fun filterByAccount(accountId: Long) {
        viewModelScope.launch {
            // Không cần thực hiện gì ở đây vì chúng ta đã có LiveData từ repository
            // Chỉ cần thay đổi LiveData đang được quan sát
        }
    }
    
    /**
     * Lọc nhật ký theo trạng thái
     */
    fun filterByStatus(status: String) {
        viewModelScope.launch {
            // Không cần thực hiện gì ở đây vì chúng ta đã có LiveData từ repository
            // Chỉ cần thay đổi LiveData đang được quan sát
        }
    }
    
    /**
     * Lọc nhật ký theo khoảng thời gian
     */
    fun filterByTimeRange(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            // Không cần thực hiện gì ở đây vì chúng ta đã có LiveData từ repository
            // Chỉ cần thay đổi LiveData đang được quan sát
        }
    }
} 