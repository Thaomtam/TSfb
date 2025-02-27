package com.fbautomanager.app.ui.logs.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fbautomanager.app.R
import com.fbautomanager.app.data.entity.Log
import com.fbautomanager.app.data.entity.LogStatus
import com.fbautomanager.app.databinding.ItemLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter : ListAdapter<Log, LogAdapter.LogViewHolder>(LogDiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = getItem(position)
        holder.bind(log)
    }

    inner class LogViewHolder(private val binding: ItemLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(log: Log) {
            // Thiết lập thời gian
            val timestamp = Date(log.timestamp)
            binding.tvTimestamp.text = dateFormat.format(timestamp)
            
            // Thiết lập tên tài khoản
            binding.tvAccountName.text = log.details
            
            // Thiết lập nội dung log
            binding.tvLogMessage.text = log.action
            
            // Thiết lập trạng thái log
            binding.tvLogLevel.text = log.status.name
            
            // Thiết lập màu cho trạng thái
            val colorResId = when (log.status) {
                LogStatus.SUCCESS -> R.color.success
                LogStatus.FAILURE -> R.color.error
                LogStatus.WARNING -> R.color.accent
                LogStatus.INFO -> R.color.primary
            }
            
            val color = ContextCompat.getColor(binding.root.context, colorResId)
            binding.tvLogLevel.setBackgroundColor(color)
        }
    }

    class LogDiffCallback : DiffUtil.ItemCallback<Log>() {
        override fun areItemsTheSame(oldItem: Log, newItem: Log): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Log, newItem: Log): Boolean {
            return oldItem == newItem
        }
    }
} 