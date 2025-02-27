package com.fbautomanager.app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbautomanager.app.FBAutoManagerApp
import com.fbautomanager.app.data.entity.Account
import com.fbautomanager.app.data.repository.AccountRepository
import com.fbautomanager.app.util.RootUtil
import com.fbautomanager.app.util.SharedPrefsUtil
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SettingsViewModel : ViewModel() {
    
    private val _facebookPackages = MutableLiveData<List<String>>()
    val facebookPackages: LiveData<List<String>> = _facebookPackages
    
    private val _scheduleInfo = MutableLiveData<String>()
    val scheduleInfo: LiveData<String> = _scheduleInfo
    
    private val _delaySettings = MutableLiveData<Pair<Int, Int>>()
    val delaySettings: LiveData<Pair<Int, Int>> = _delaySettings
    
    private val _actionLimit = MutableLiveData<Int>()
    val actionLimit: LiveData<Int> = _actionLimit
    
    private val accountRepository: AccountRepository
    private val sharedPrefsUtil: SharedPrefsUtil
    
    init {
        val accountDao = FBAutoManagerApp.database.accountDao()
        accountRepository = AccountRepository(accountDao)
        sharedPrefsUtil = SharedPrefsUtil(FBAutoManagerApp.instance)
        
        // Khởi tạo giá trị mặc định
        _delaySettings.value = Pair(2, 5)
        _actionLimit.value = 50
        _scheduleInfo.value = "Chưa thiết lập lịch trình"
        
        // Tải cài đặt từ SharedPreferences
        loadSettings()
    }
    
    /**
     * Quét các package Facebook đã cài đặt
     */
    fun scanFacebookPackages() {
        viewModelScope.launch {
            val packages = RootUtil.getAllFacebookPackages()
            _facebookPackages.postValue(packages)
        }
    }
    
    /**
     * Thêm tài khoản từ package
     */
    fun addAccountFromPackage(packageName: String) {
        viewModelScope.launch {
            // Kiểm tra xem package đã tồn tại trong database chưa
            val existingAccount = accountRepository.getAccountByPackage(packageName)
            if (existingAccount == null) {
                // Thêm tài khoản mới
                val newAccount = Account(
                    name = "Facebook (${packageName.substringAfterLast('.')})",
                    packageName = packageName
                )
                accountRepository.insert(newAccount)
            }
        }
    }
    
    /**
     * Cập nhật cài đặt độ trễ
     */
    fun updateDelaySettings(minDelay: Int, maxDelay: Int) {
        _delaySettings.value = Pair(minDelay, maxDelay)
    }
    
    /**
     * Cập nhật giới hạn hành động
     */
    fun updateActionLimit(limit: Int) {
        _actionLimit.value = limit
    }
    
    /**
     * Lấy cài đặt lịch trình hiện tại
     */
    fun getScheduleSettings(): Map<String, Any> {
        return sharedPrefsUtil.getScheduleSettings()
    }
    
    /**
     * Cập nhật cài đặt lịch trình
     */
    fun updateScheduleSettings(settings: Map<String, Any>) {
        sharedPrefsUtil.saveScheduleSettings(settings)
        
        // Cập nhật thông tin lịch trình hiển thị
        updateScheduleInfo(settings)
    }
    
    /**
     * Lưu tất cả cài đặt
     */
    fun saveAllSettings(settings: Map<String, Any>) {
        viewModelScope.launch {
            // Lưu cài đặt vào SharedPreferences
            sharedPrefsUtil.saveAllSettings(settings)
            
            // Cập nhật các giá trị LiveData
            val minDelay = settings["min_delay"] as? Int ?: 2
            val maxDelay = settings["max_delay"] as? Int ?: 5
            _delaySettings.postValue(Pair(minDelay, maxDelay))
            
            val limit = settings["action_limit"] as? Int ?: 50
            _actionLimit.postValue(limit)
        }
    }
    
    /**
     * Tải cài đặt từ SharedPreferences
     */
    private fun loadSettings() {
        viewModelScope.launch {
            // Tải cài đặt từ SharedPreferences
            val settings = sharedPrefsUtil.getAllSettings()
            
            // Cập nhật các giá trị LiveData
            val minDelay = settings["min_delay"] as? Int ?: 2
            val maxDelay = settings["max_delay"] as? Int ?: 5
            _delaySettings.postValue(Pair(minDelay, maxDelay))
            
            val limit = settings["action_limit"] as? Int ?: 50
            _actionLimit.postValue(limit)
            
            // Cập nhật thông tin lịch trình
            updateScheduleInfo(sharedPrefsUtil.getScheduleSettings())
        }
    }
    
    /**
     * Cập nhật thông tin lịch trình hiển thị
     */
    private fun updateScheduleInfo(settings: Map<String, Any>) {
        val enabled = settings["schedule_enabled"] as? Boolean ?: false
        
        if (!enabled) {
            _scheduleInfo.postValue("Chưa thiết lập lịch trình")
            return
        }
        
        val startTime = settings["start_time"] as? String ?: "08:00"
        val endTime = settings["end_time"] as? String ?: "20:00"
        val intervalIndex = settings["interval_index"] as? Int ?: 0
        
        val intervals = arrayOf("Mỗi giờ", "Mỗi 2 giờ", "Mỗi 4 giờ", "Mỗi 6 giờ", "Mỗi 12 giờ", "Mỗi ngày")
        val interval = if (intervalIndex < intervals.size) intervals[intervalIndex] else intervals[0]
        
        val days = mutableListOf<String>()
        if (settings["monday"] as? Boolean == true) days.add("T2")
        if (settings["tuesday"] as? Boolean == true) days.add("T3")
        if (settings["wednesday"] as? Boolean == true) days.add("T4")
        if (settings["thursday"] as? Boolean == true) days.add("T5")
        if (settings["friday"] as? Boolean == true) days.add("T6")
        if (settings["saturday"] as? Boolean == true) days.add("T7")
        if (settings["sunday"] as? Boolean == true) days.add("CN")
        
        val daysText = if (days.isEmpty()) "Không có ngày nào" else days.joinToString(", ")
        
        val info = "Chạy từ $startTime đến $endTime, $interval\nCác ngày: $daysText"
        _scheduleInfo.postValue(info)
    }
} 