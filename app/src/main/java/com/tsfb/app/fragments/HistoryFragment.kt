package com.tsfb.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tsfb.app.databinding.FragmentHistoryBinding
import com.tsfb.app.viewmodels.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.logs.observe(viewLifecycleOwner) { logs ->
            // Update RecyclerView adapter with logs
        }

        binding.clearButton.setOnClickListener {
            viewModel.clearLogs()
        }

        binding.exportButton.setOnClickListener {
            viewModel.exportLogs()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 