package com.fbautomanager.app.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fbautomanager.app.R
import com.fbautomanager.app.data.entity.Account
import com.fbautomanager.app.data.entity.Task
import com.fbautomanager.app.databinding.DialogTaskSelectionBinding
import com.fbautomanager.app.ui.home.HomeViewModel
import com.fbautomanager.app.ui.home.adapter.TaskAdapter

class TaskSelectionDialog(
    private val account: Account,
    private val onSave: (List<Task>) -> Unit
) : DialogFragment() {

    private var _binding: DialogTaskSelectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var taskAdapter: TaskAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTaskSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        
        // Thiết lập thông tin tài khoản
        setupAccountInfo()
        
        // Thiết lập RecyclerView
        setupRecyclerView()
        
        // Thiết lập các listener
        setupListeners()
        
        // Tải danh sách tác vụ
        loadTasks()
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
        return dialog
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupAccountInfo() {
        binding.tvAccountName.text = account.name
    }
    
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter()
        
        binding.recyclerTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }
    
    private fun setupListeners() {
        // Button Cancel
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        // Button Save
        binding.btnSave.setOnClickListener {
            saveTasks()
        }
    }
    
    private fun loadTasks() {
        viewModel.getTasksByAccount(account.id).observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }
    
    private fun saveTasks() {
        val selectedTasks = taskAdapter.getSelectedTasks()
        onSave(selectedTasks)
        dismiss()
    }
} 