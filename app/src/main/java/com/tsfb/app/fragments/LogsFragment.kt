package com.tsfb.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsfb.app.R
import com.tsfb.app.adapters.LogAdapter
import com.tsfb.app.viewmodels.LogsViewModel
import kotlinx.android.synthetic.main.fragment_logs.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogsFragment : Fragment() {
    private val viewModel: LogsViewModel by viewModels()
    private val logAdapter = LogAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        logs_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
        }

        viewModel.logs.observe(viewLifecycleOwner) { logs ->
            logAdapter.updateLogs(logs)
        }

        export_button.setOnClickListener {
            viewModel.exportLogs(requireContext())
        }
    }
} 