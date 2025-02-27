package com.fbautomanager.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fbautomanager.app.FBAutoManagerApp
import com.fbautomanager.app.R
import com.fbautomanager.app.data.entity.Account
import com.fbautomanager.app.data.entity.Task
import com.fbautomanager.app.databinding.FragmentHomeBinding
import com.fbautomanager.app.service.AutomationService
import com.fbautomanager.app.ui.home.adapter.AccountAdapter
import com.fbautomanager.app.ui.home.dialog.AddAccountDialog
import com.fbautomanager.app.ui.home.dialog.TaskSelectionDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var accountAdapter: AccountAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        
        // Thiết lập RecyclerView
        setupRecyclerView()
        
        // Thiết lập các listener
        setupListeners()
        
        // Quan sát dữ liệu từ ViewModel
        observeViewModel()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupRecyclerView() {
        accountAdapter = AccountAdapter(
            onActiveStatusChanged = { account, isActive ->
                viewModel.updateAccountActiveStatus(account.id, isActive)
            },
            onAccountSelected = { account ->
                showTaskSelectionDialog(account)
            },
            onDeleteAccount = { account ->
                viewModel.deleteAccount(account)
            }
        )
        
        binding.recyclerAccounts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = accountAdapter
        }
    }
    
    private fun setupListeners() {
        // Button Add Account
        binding.btnAddAccount.setOnClickListener {
            showAddAccountDialog()
        }
        
        // Button Start Automation
        binding.btnStartAutomation.setOnClickListener {
            startAutomation()
        }
        
        // Button Stop Automation
        binding.btnStopAutomation.setOnClickListener {
            stopAutomation()
        }
    }
    
    private fun observeViewModel() {
        // Quan sát danh sách tài khoản
        viewModel.allAccounts.observe(viewLifecycleOwner) { accounts ->
            accountAdapter.submitList(accounts)
            
            // Hiển thị/ẩn thông báo trống
            binding.emptyView.visibility = if (accounts.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerAccounts.visibility = if (accounts.isEmpty()) View.GONE else View.VISIBLE
        }
        
        // Quan sát trạng thái đang chạy của tự động hóa
        viewModel.isAutomationRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.btnStartAutomation.isEnabled = !isRunning
            binding.btnStopAutomation.isEnabled = isRunning
        }
        
        // Quan sát trạng thái đang chạy của service
        AutomationService.isRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.btnStartAutomation.isEnabled = !isRunning
            binding.btnStopAutomation.isEnabled = isRunning
        }
    }
    
    /**
     * Hiển thị dialog thêm tài khoản
     */
    private fun showAddAccountDialog() {
        val dialog = AddAccountDialog { name, packageName ->
            viewModel.addAccount(name, packageName)
        }
        dialog.show(childFragmentManager, "AddAccountDialog")
    }
    
    /**
     * Hiển thị dialog chọn tác vụ
     */
    private fun showTaskSelectionDialog(account: Account) {
        val dialog = TaskSelectionDialog(
            account = account,
            onSave = { tasks ->
                handleSelectedTasks(account, tasks)
            }
        )
        dialog.show(childFragmentManager, "TaskSelectionDialog")
    }
    
    /**
     * Xử lý các tác vụ đã chọn
     */
    private fun handleSelectedTasks(account: Account, tasks: List<Task>) {
        if (tasks.isEmpty()) {
            // Nếu không có tác vụ nào được chọn, tạo tác vụ mới từ cài đặt
            viewModel.createTasksForAccount(account.id)
            Toast.makeText(context, "Đã tạo tác vụ mới cho tài khoản ${account.name}", Toast.LENGTH_SHORT).show()
        } else {
            // Cập nhật trạng thái hoạt động của các tác vụ
            Toast.makeText(context, "Đã cập nhật ${tasks.size} tác vụ cho tài khoản ${account.name}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Bắt đầu tự động hóa
     */
    private fun startAutomation() {
        if (!FBAutoManagerApp.isRooted) {
            Toast.makeText(context, R.string.root_required_message, Toast.LENGTH_SHORT).show()
            return
        }
        
        val activeAccounts = accountAdapter.currentList.filter { it.isActive }
        if (activeAccounts.isEmpty()) {
            Toast.makeText(context, "Vui lòng chọn ít nhất một tài khoản để tự động hóa", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Bắt đầu tự động hóa
        viewModel.startAutomation()
        Toast.makeText(context, "Bắt đầu tự động hóa ${activeAccounts.size} tài khoản", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Dừng tự động hóa
     */
    private fun stopAutomation() {
        viewModel.stopAutomation()
        Toast.makeText(context, "Đã dừng tự động hóa", Toast.LENGTH_SHORT).show()
    }
} 