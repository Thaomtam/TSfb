package com.fbautomanager.app.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fbautomanager.app.data.entity.Task
import com.fbautomanager.app.data.entity.TaskType
import com.fbautomanager.app.databinding.ItemTaskBinding

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    private val selectedTasks = mutableMapOf<Long, Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }
    
    fun getSelectedTasks(): List<Task> {
        return selectedTasks.values.toList()
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cbTask.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = getItem(position)
                    if (isChecked) {
                        selectedTasks[task.id] = task
                        showConfigField(task)
                    } else {
                        selectedTasks.remove(task.id)
                        binding.tilTaskConfig.visibility = View.GONE
                    }
                }
            }
        }

        fun bind(task: Task) {
            // Thiết lập checkbox
            binding.cbTask.text = getTaskTypeText(task.type)
            binding.cbTask.isChecked = task.isActive
            
            // Thiết lập trường cấu hình
            if (task.isActive) {
                selectedTasks[task.id] = task
                showConfigField(task)
            } else {
                binding.tilTaskConfig.visibility = View.GONE
            }
        }
        
        private fun showConfigField(task: Task) {
            binding.tilTaskConfig.visibility = View.VISIBLE
            
            // Thiết lập nội dung cấu hình dựa trên loại tác vụ
            val configText = when (task.type) {
                TaskType.LIKE -> {
                    if (task.target.isNotEmpty()) {
                        "Từ khóa: ${task.target}\n"
                    } else {
                        ""
                    } + "Số lượng: ${task.maxActions} lượt thích\n" +
                    "Độ trễ: ${task.minDelay / 1000}-${task.maxDelay / 1000} giây"
                }
                TaskType.COMMENT -> {
                    "Nội dung: ${task.content}\n" +
                    "Số lượng: ${task.maxActions} bình luận\n" +
                    "Độ trễ: ${task.minDelay / 1000}-${task.maxDelay / 1000} giây"
                }
                TaskType.SHARE -> {
                    "Chia sẻ đến: ${getShareTargetText(task.target)}\n" +
                    "Số lượng: ${task.maxActions} chia sẻ\n" +
                    "Độ trễ: ${task.minDelay / 1000}-${task.maxDelay / 1000} giây"
                }
                TaskType.POST -> {
                    "Nội dung: ${task.content}\n" +
                    "Số lượng: ${task.maxActions} bài đăng\n" +
                    "Độ trễ: ${task.minDelay / 1000}-${task.maxDelay / 1000} giây"
                }
                TaskType.MESSAGE -> {
                    "Nội dung: ${task.content}\n" +
                    "Số lượng: ${task.maxActions} tin nhắn\n" +
                    "Độ trễ: ${task.minDelay / 1000}-${task.maxDelay / 1000} giây"
                }
                TaskType.ADD_FRIEND -> {
                    if (task.target.isNotEmpty()) {
                        "Từ khóa: ${task.target}\n"
                    } else {
                        ""
                    } + "Số lượng: ${task.maxActions} lời mời\n" +
                    "Độ trễ: ${task.minDelay / 1000}-${task.maxDelay / 1000} giây"
                }
            }
            
            binding.etTaskConfig.setText(configText)
        }
        
        private fun getTaskTypeText(type: TaskType): String {
            return when (type) {
                TaskType.LIKE -> "Thích bài viết"
                TaskType.COMMENT -> "Bình luận bài viết"
                TaskType.SHARE -> "Chia sẻ bài viết"
                TaskType.POST -> "Đăng bài"
                TaskType.MESSAGE -> "Gửi tin nhắn"
                TaskType.ADD_FRIEND -> "Kết bạn"
            }
        }
        
        private fun getShareTargetText(target: String): String {
            return when (target) {
                "wall" -> "Tường cá nhân"
                "group" -> "Nhóm"
                "friend" -> "Bạn bè"
                else -> "Tường cá nhân"
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
} 