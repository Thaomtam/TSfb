package com.tsfb.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsfb.app.R
import com.tsfb.app.models.LogEntry

class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    private var logs = listOf<LogEntry>()

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val actionText: TextView = view.findViewById(R.id.action_text)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        holder.actionText.text = log.action
        holder.timestamp.text = log.timestamp
    }

    override fun getItemCount() = logs.size

    fun updateLogs(newLogs: List<LogEntry>) {
        logs = newLogs
        notifyDataSetChanged()
    }
} 