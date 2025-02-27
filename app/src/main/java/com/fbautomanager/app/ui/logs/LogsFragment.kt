package com.fbautomanager.app.ui.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fbautomanager.app.databinding.FragmentLogsBinding
import com.fbautomanager.app.ui.logs.adapter.LogAdapter

class LogsFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: LogsViewModel
    private lateinit var logAdapter: LogAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this).get(LogsViewModel::class.java)
        
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
        logAdapter = LogAdapter()
        
        binding.recyclerLogs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
        }
    }
    
    private fun setupListeners() {
        // Button Clear Logs
        binding.btnClearLogs.setOnClickListener {
            viewModel.clearAllLogs()
            Toast.makeText(context, "Đã xóa tất cả nhật ký", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        // Quan sát danh sách log
        viewModel.logs.observe(viewLifecycleOwner) { logs ->
            logAdapter.submitList(logs)
            
            // Hiển thị/ẩn thông báo trống
            binding.tvEmptyLogs.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerLogs.visibility = if (logs.isEmpty()) View.GONE else View.VISIBLE
        }
    }
} 