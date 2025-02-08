package com.tsfb.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsfb.app.data.AppRepository
import com.tsfb.app.data.entities.StatsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _stats = MutableLiveData<StatsEntity>()
    val stats: LiveData<StatsEntity> = _stats

    fun loadStats() {
        viewModelScope.launch {
            repository.getStats()?.let {
                _stats.value = it
            }
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            repository.updateStats(0, 0, 0)
            loadStats()
        }
    }
} 