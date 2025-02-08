package com.tsfb.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsfb.app.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutoInteractionViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> = _isRunning

    fun startAutomation() {
        _isRunning.value = true
        viewModelScope.launch {
            repository.addLog("Bắt đầu tự động tương tác")
        }
    }

    fun stopAutomation() {
        _isRunning.value = false
        viewModelScope.launch {
            repository.addLog("Dừng tự động tương tác")
        }
    }
} 