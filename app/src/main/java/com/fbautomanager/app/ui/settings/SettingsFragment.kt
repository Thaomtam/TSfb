package com.fbautomanager.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fbautomanager.app.databinding.FragmentSettingsBinding
import com.fbautomanager.app.ui.settings.adapter.PackageAdapter
import com.fbautomanager.app.ui.settings.dialog.ScheduleDialog
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: SettingsViewModel
    private lateinit var packageAdapter: PackageAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        
        // Thiết lập RecyclerView cho danh sách package
        setupPackageRecyclerView()
        
        // Thiết lập các listener cho checkbox
        setupCheckboxListeners()
        
        // Thiết lập các listener cho slider
        setupSliderListeners()
        
        // Thiết lập các listener cho button
        setupButtonListeners()
        
        // Quan sát dữ liệu từ ViewModel
        observeViewModel()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupPackageRecyclerView() {
        packageAdapter = PackageAdapter { packageName ->
            // Xử lý khi người dùng nhấn nút "Thêm" cho một package
            viewModel.addAccountFromPackage(packageName)
        }
        
        binding.recyclerPackages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = packageAdapter
        }
    }
    
    private fun setupCheckboxListeners() {
        // Checkbox Like Posts
        binding.cbLikePosts.setOnCheckedChangeListener { _, isChecked ->
            binding.tilLikeKeywords.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Checkbox Comment Posts
        binding.cbCommentPosts.setOnCheckedChangeListener { _, isChecked ->
            binding.tilCommentText.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Checkbox Share Posts
        binding.cbSharePosts.setOnCheckedChangeListener { _, isChecked ->
            binding.rgShareType.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Checkbox Post Content
        binding.cbPostContent.setOnCheckedChangeListener { _, isChecked ->
            binding.tilPostContent.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Checkbox Send Messages
        binding.cbSendMessages.setOnCheckedChangeListener { _, isChecked ->
            binding.tilMessageText.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Checkbox Add Friends
        binding.cbAddFriends.setOnCheckedChangeListener { _, isChecked ->
            binding.tilFriendKeywords.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }
    
    private fun setupSliderListeners() {
        // Range Slider cho độ trễ
        binding.sliderDelay.addOnChangeListener(RangeSlider.OnChangeListener { slider, _, _ ->
            val values = slider.values
            val minDelay = values[0].toInt()
            val maxDelay = values[1].toInt()
            
            binding.tvMinDelay.text = "$minDelay giây"
            binding.tvMaxDelay.text = "$maxDelay giây"
            
            viewModel.updateDelaySettings(minDelay, maxDelay)
        })
        
        // Slider cho giới hạn hành động
        binding.sliderActionLimit.addOnChangeListener(Slider.OnChangeListener { slider, value, _ ->
            val limit = value.toInt()
            binding.tvActionLimit.text = "$limit hành động/ngày"
            
            viewModel.updateActionLimit(limit)
        })
    }
    
    private fun setupButtonListeners() {
        // Button Scan Packages
        binding.btnScanPackages.setOnClickListener {
            viewModel.scanFacebookPackages()
        }
        
        // Button Set Schedule
        binding.btnSetSchedule.setOnClickListener {
            showScheduleDialog()
        }
        
        // Button Save Settings
        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }
    
    private fun observeViewModel() {
        // Quan sát danh sách package
        viewModel.facebookPackages.observe(viewLifecycleOwner) { packages ->
            packageAdapter.submitList(packages)
        }
        
        // Quan sát thông tin lịch trình
        viewModel.scheduleInfo.observe(viewLifecycleOwner) { info ->
            binding.tvScheduleInfo.text = info
        }
        
        // Quan sát cài đặt độ trễ
        viewModel.delaySettings.observe(viewLifecycleOwner) { (min, max) ->
            // Chỉ cập nhật UI nếu giá trị khác với giá trị hiện tại của slider
            val currentValues = binding.sliderDelay.values
            if (currentValues[0].toInt() != min || currentValues[1].toInt() != max) {
                binding.sliderDelay.values = listOf(min.toFloat(), max.toFloat())
                binding.tvMinDelay.text = "$min giây"
                binding.tvMaxDelay.text = "$max giây"
            }
        }
        
        // Quan sát giới hạn hành động
        viewModel.actionLimit.observe(viewLifecycleOwner) { limit ->
            // Chỉ cập nhật UI nếu giá trị khác với giá trị hiện tại của slider
            if (binding.sliderActionLimit.value.toInt() != limit) {
                binding.sliderActionLimit.value = limit.toFloat()
                binding.tvActionLimit.text = "$limit hành động/ngày"
            }
        }
    }
    
    private fun showScheduleDialog() {
        val dialog = ScheduleDialog(
            viewModel.getScheduleSettings(),
            onSave = { settings ->
                viewModel.updateScheduleSettings(settings)
            }
        )
        dialog.show(childFragmentManager, "ScheduleDialog")
    }
    
    private fun saveSettings() {
        // Thu thập dữ liệu từ UI
        val settings = collectSettingsFromUI()
        
        // Lưu cài đặt
        viewModel.saveAllSettings(settings)
        
        // Hiển thị thông báo
        Toast.makeText(context, "Đã lưu cài đặt", Toast.LENGTH_SHORT).show()
    }
    
    private fun collectSettingsFromUI(): Map<String, Any> {
        val settings = mutableMapOf<String, Any>()
        
        // Cài đặt tác vụ
        settings["like_posts_enabled"] = binding.cbLikePosts.isChecked
        settings["like_posts_keywords"] = binding.etLikeKeywords.text.toString()
        
        settings["comment_posts_enabled"] = binding.cbCommentPosts.isChecked
        settings["comment_posts_text"] = binding.etCommentText.text.toString()
        
        settings["share_posts_enabled"] = binding.cbSharePosts.isChecked
        settings["share_posts_type"] = when {
            binding.rbShareToWall.isChecked -> "wall"
            binding.rbShareToGroup.isChecked -> "group"
            binding.rbShareToFriend.isChecked -> "friend"
            else -> "wall"
        }
        
        settings["post_content_enabled"] = binding.cbPostContent.isChecked
        settings["post_content_text"] = binding.etPostContent.text.toString()
        
        settings["send_messages_enabled"] = binding.cbSendMessages.isChecked
        settings["send_messages_text"] = binding.etMessageText.text.toString()
        
        settings["add_friends_enabled"] = binding.cbAddFriends.isChecked
        settings["add_friends_keywords"] = binding.etFriendKeywords.text.toString()
        
        return settings
    }
} 