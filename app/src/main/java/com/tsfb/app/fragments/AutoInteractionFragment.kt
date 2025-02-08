package com.tsfb.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tsfb.app.databinding.FragmentAutoInteractionBinding
import com.tsfb.app.viewmodels.AutoInteractionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoInteractionFragment : Fragment() {
    private var _binding: FragmentAutoInteractionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AutoInteractionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAutoInteractionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            viewModel.startAutomation()
        }

        binding.stopButton.setOnClickListener {
            viewModel.stopAutomation()
        }

        viewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.startButton.isEnabled = !isRunning
            binding.stopButton.isEnabled = isRunning
            binding.statusText.text = if (isRunning) "Đang chạy" else "Đã dừng"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 