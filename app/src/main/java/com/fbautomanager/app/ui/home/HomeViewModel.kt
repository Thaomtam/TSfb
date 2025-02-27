package com.fbautomanager.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbautomanager.app.FBAutoManagerApp
import com.fbautomanager.app.data.entity.Account
import com.fbautomanager.app.data.entity.Log
import com.fbautomanager.app.data.entity.LogStatus
import com.fbautomanager.app.data.entity.Task
import com.fbautomanager.app.data.repository.AccountRepository
import com.fbautomanager.app.data.repository.LogRepository
import com.fbautomanager.app.data.repository.TaskRepository
import com.fbautomanager.app.service.AutomationService
import com.fbautomanager.app.util.RootUtil
import com.fbautomanager.app.util.SharedPrefsUtil
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val accountRepository: AccountRepository
    private val taskRepository: TaskRepository
    private val logRepository: LogRepository
    private val sharedPrefsUtil: SharedPrefsUtil
    
    val allAccounts: LiveData<List<Account>>
    val activeAccounts: LiveData<List<Account>>
    
    private val _isAutomationRunning = MutableLiveData<Boolean>(false)
    val isAutomationRunning: LiveData<Boolean> = _isAutomationRunning
    
    init {
        val accountDao = FBAutoManagerApp.database.accountDao()
        val taskDao = FBAutoManagerApp.database.taskDao()
        val logDao = FBAutoManagerApp.database.logDao()
        
        accountRepository = AccountRepository(accountDao)
        taskRepository = TaskRepository(taskDao)
        logRepository = LogRepository(logDao)
        sharedPrefsUtil = SharedPrefsUtil(FBAutoManagerApp.instance)
        
        allAccounts = accountRepository.allAccounts
        activeAccounts = accountRepository.activeAccounts
        
        // Quan sát trạng thái của AutomationService
        observeAutomationService()
    }
    
    /**
     * Thêm tài khoản mới
     */
    fun addAccount(name: String, packageName: String) {
        viewModelScope.launch {
            // Kiểm tra xem package đã tồn tại chưa
            val existingAccount = accountRepository.getAccountByPackage(packageName)
            if (existingAccount != null) {
                // Package đã tồn tại, cập nhật thông tin
                val updatedAccount = existingAccount.copy(name = name)
                accountRepository.update(updatedAccount)
            } else {
                // Thêm tài khoản mới
                val newAccount = Account(
                    name = name,
                    packageName = packageName
                )
                accountRepository.insert(newAccount)
            }
        }
    }
    
    /**
     * Cập nhật trạng thái hoạt động của tài khoản
     */
    fun updateAccountActiveStatus(accountId: Long, isActive: Boolean) {
        viewModelScope.launch {
            accountRepository.updateActiveStatus(accountId, isActive)
        }
    }
    
    /**
     * Xóa tài khoản
     */
    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            // Xóa tất cả tác vụ liên quan đến tài khoản
            taskRepository.deleteByAccount(account.id)
            
            // Xóa tất cả nhật ký liên quan đến tài khoản
            logRepository.deleteByAccount(account.id)
            
            // Xóa tài khoản
            accountRepository.delete(account)
        }
    }
    
    /**
     * Quét các package Facebook đã cài đặt
     */
    fun scanFacebookPackages() {
        viewModelScope.launch {
            val packages = RootUtil.getAllFacebookPackages()
            for (packageName in packages) {
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
    }
    
    /**
     * Lấy tác vụ theo tài khoản
     */
    fun getTasksByAccount(accountId: Long): LiveData<List<Task>> {
        return taskRepository.getTasksByAccount(accountId)
    }
    
    /**
     * Tạo tác vụ mới cho tài khoản
     */
    fun createTasksForAccount(accountId: Long) {
        viewModelScope.launch {
            // Lấy cài đặt từ SharedPreferences
            val settings = sharedPrefsUtil.getAllSettings()
            
            // Tạo tác vụ từ cài đặt
            val taskIds = taskRepository.createTaskFromSettings(accountId, settings)
            
            // Ghi log
            if (taskIds.isNotEmpty()) {
                val log = Log(
                    accountId = accountId,
                    action = "Tạo tác vụ",
                    details = "Đã tạo ${taskIds.size} tác vụ",
                    status = LogStatus.INFO
                )
                logRepository.insert(log)
            }
        }
    }
    
    /**
     * Bắt đầu tự động hóa
     */
    fun startAutomation() {
        viewModelScope.launch {
            // Lấy danh sách tài khoản đang hoạt động
            val accounts = activeAccounts.value ?: return@launch
            if (accounts.isEmpty()) return@launch
            
            // Cập nhật trạng thái
            _isAutomationRunning.postValue(true)
            
            // Ghi log bắt đầu
            for (account in accounts) {
                val log = Log(
                    accountId = account.id,
                    action = "Bắt đầu tự động hóa",
                    details = "Tài khoản: ${account.name}",
                    status = LogStatus.INFO
                )
                logRepository.insert(log)
                
                // Lấy tác vụ đang hoạt động của tài khoản
                val tasks = taskRepository.getActiveTasksByAccount(account.id).value ?: continue
                if (tasks.isEmpty()) {
                    // Nếu chưa có tác vụ, tạo tác vụ mới từ cài đặt
                    createTasksForAccount(account.id)
                } else {
                    // Bắt đầu tự động hóa với tác vụ đầu tiên
                    val automationService = AutomationService.getInstance()
                    automationService?.startAutomation(account, tasks.first())
                }
            }
        }
    }
    
    /**
     * Dừng tự động hóa
     */
    fun stopAutomation() {
        viewModelScope.launch {
            // Cập nhật trạng thái
            _isAutomationRunning.postValue(false)
            
            // Dừng service
            val automationService = AutomationService.getInstance()
            automationService?.stopAutomation()
            
            // Ghi log dừng
            val accounts = activeAccounts.value ?: return@launch
            for (account in accounts) {
                val log = Log(
                    accountId = account.id,
                    action = "Dừng tự động hóa",
                    details = "Tài khoản: ${account.name}",
                    status = LogStatus.INFO
                )
                logRepository.insert(log)
            }
        }
    }
    
    /**
     * Quan sát trạng thái của AutomationService
     */
    private fun observeAutomationService() {
        AutomationService.isRunning.observeForever { isRunning ->
            _isAutomationRunning.value = isRunning
        }
    }
} 