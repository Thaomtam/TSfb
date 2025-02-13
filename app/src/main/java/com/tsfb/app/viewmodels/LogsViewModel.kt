package com.tsfb.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsfb.app.data.AppRepository
import com.tsfb.app.data.entities.LogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _logs = MutableLiveData<List<LogEntity>>()
    val logs: LiveData<List<LogEntity>> = _logs

    fun loadLogs() {
        viewModelScope.launch {
            _logs.value = repository.getLogs()
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
            loadLogs()
        }
    }

    fun exportLogs() {
        viewModelScope.launch {
            // TODO: Implement export functionality
        }
    }
} 