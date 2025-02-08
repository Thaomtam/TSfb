package com.tsfb.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsfb.app.data.Settings
import com.tsfb.app.repository.AppRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val _settings = MutableLiveData<Settings>()
    val settings: LiveData<Settings> = _settings

    init {
        loadSettings()
    }

    fun saveSettings(
        interactionSpeed: Int,
        autoLikeEnabled: Boolean,
        autoCommentEnabled: Boolean,
        autoShareEnabled: Boolean,
        commentTemplates: List<String>
    ) {
        viewModelScope.launch {
            repository.saveSettings(Settings(
                interactionSpeed = interactionSpeed,
                autoLikeEnabled = autoLikeEnabled,
                autoCommentEnabled = autoCommentEnabled,
                autoShareEnabled = autoShareEnabled,
                commentTemplates = commentTemplates
            ))
            repository.addLog("Đã cập nhật cài đặt")
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _settings.value = repository.getSettings()
        }
    }
} 