package com.tsfb.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsfb.app.models.LogEntry
import com.tsfb.app.models.Stats

class DashboardViewModel : ViewModel() {
    private val _stats = MutableLiveData<Stats>()
    val stats: LiveData<Stats> = _stats

    private val _recentLogs = MutableLiveData<List<LogEntry>>()
    val recentLogs: LiveData<List<LogEntry>> = _recentLogs

    private val _isModuleActive = MutableLiveData<Boolean>()
    val isModuleActive: LiveData<Boolean> = _isModuleActive

    fun toggleModule(active: Boolean) {
        _isModuleActive.value = active
        // Thêm logic xử lý kích hoạt/vô hiệu hóa module
    }

    fun refreshStats() {
        // Cập nhật thống kê từ database
    }

    fun refreshLogs() {
        // Cập nhật logs từ database
    }
} 