package com.tsfb.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tsfb.app.repository.AppRepository
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LogsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val logs = repository.getAllLogs().asLiveData()

    fun exportLogs(context: Context) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
            val fileName = "logs_${dateFormat.format(Date())}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)

            file.printWriter().use { out ->
                out.println("Thời gian,Hành động,Chi tiết")
                logs.value?.forEach { log ->
                    out.println("${log.timestamp},${log.action},${log.details}")
                }
            }
        }
    }
} 