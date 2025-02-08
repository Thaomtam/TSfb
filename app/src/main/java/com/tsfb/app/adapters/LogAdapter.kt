package com.tsfb.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tsfb.app.databinding.ItemLogBinding
import com.tsfb.app.models.LogEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    private var logs = listOf<LogEntry>()

    class LogViewHolder(private val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        fun bind(log: LogEntry) {
            binding.actionText.text = log.action
            binding.timestamp.text = dateFormat.format(Date(log.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount() = logs.size

    fun submitList(newLogs: List<LogEntry>) {
        logs = newLogs
        notifyDataSetChanged()
    }
} 