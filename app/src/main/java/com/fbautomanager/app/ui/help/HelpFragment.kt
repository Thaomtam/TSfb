package com.fbautomanager.app.ui.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fbautomanager.app.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Thiết lập các listener
        setupListeners()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupListeners() {
        // Button Contact Support
        binding.btnContactSupport.setOnClickListener {
            contactSupport()
        }
    }
    
    /**
     * Mở email để liên hệ hỗ trợ
     */
    private fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@fbautomanager.com")
            putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ FB Multi-Auto Manager")
            putExtra(Intent.EXTRA_TEXT, "Xin chào,\n\nTôi cần hỗ trợ về ứng dụng FB Multi-Auto Manager.\n\n")
        }
        
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }
} 