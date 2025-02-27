package com.fbautomanager.app.data.repository

import androidx.lifecycle.LiveData
import com.fbautomanager.app.data.dao.TaskDao
import com.fbautomanager.app.data.entity.Task
import com.fbautomanager.app.data.entity.TaskType

class TaskRepository(private val taskDao: TaskDao) {
    
    /**
     * Lấy tất cả tác vụ
     */
    fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks()
    }
    
    /**
     * Lấy tác vụ theo tài khoản
     */
    fun getTasksByAccount(accountId: Long): LiveData<List<Task>> {
        return taskDao.getTasksByAccount(accountId)
    }
    
    /**
     * Lấy tác vụ theo loại
     */
    fun getTasksByType(type: TaskType): LiveData<List<Task>> {
        return taskDao.getTasksByType(type)
    }
    
    /**
     * Lấy tác vụ đang hoạt động
     */
    fun getActiveTasks(): LiveData<List<Task>> {
        return taskDao.getActiveTasks()
    }
    
    /**
     * Lấy tác vụ đang hoạt động theo tài khoản
     */
    fun getActiveTasksByAccount(accountId: Long): LiveData<List<Task>> {
        return taskDao.getActiveTasksByAccount(accountId)
    }
    
    /**
     * Lấy tác vụ theo ID
     */
    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }
    
    /**
     * Thêm tác vụ mới
     */
    suspend fun insert(task: Task): Long {
        return taskDao.insert(task)
    }
    
    /**
     * Cập nhật tác vụ
     */
    suspend fun update(task: Task) {
        taskDao.update(task)
    }
    
    /**
     * Xóa tác vụ
     */
    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
    
    /**
     * Xóa tác vụ theo tài khoản
     */
    suspend fun deleteByAccount(accountId: Long) {
        taskDao.deleteByAccount(accountId)
    }
    
    /**
     * Cập nhật trạng thái hoạt động của tác vụ
     */
    suspend fun updateActiveStatus(id: Long, isActive: Boolean) {
        taskDao.updateActiveStatus(id, isActive)
    }
    
    /**
     * Cập nhật trạng thái hoạt động của tác vụ theo tài khoản
     */
    suspend fun updateActiveStatusByAccount(accountId: Long, isActive: Boolean) {
        taskDao.updateActiveStatusByAccount(accountId, isActive)
    }
    
    /**
     * Tạo tác vụ mới từ cài đặt
     */
    suspend fun createTaskFromSettings(accountId: Long, settings: Map<String, Any>): List<Long> {
        val taskIds = mutableListOf<Long>()
        
        // Tạo tác vụ Like Posts
        if (settings["like_posts_enabled"] as? Boolean == true) {
            val keywords = settings["like_posts_keywords"] as? String ?: ""
            val task = Task(
                accountId = accountId,
                type = TaskType.LIKE,
                target = keywords,
                content = "",
                maxActions = settings["action_limit"] as? Int ?: 50,
                minDelay = (settings["min_delay"] as? Int ?: 2) * 1000,
                maxDelay = (settings["max_delay"] as? Int ?: 5) * 1000,
                isActive = true
            )
            taskIds.add(taskDao.insert(task))
        }
        
        // Tạo tác vụ Comment Posts
        if (settings["comment_posts_enabled"] as? Boolean == true) {
            val commentText = settings["comment_posts_text"] as? String ?: ""
            val task = Task(
                accountId = accountId,
                type = TaskType.COMMENT,
                target = "",
                content = commentText,
                maxActions = (settings["action_limit"] as? Int ?: 50) / 2, // Giảm số lượng bình luận
                minDelay = (settings["min_delay"] as? Int ?: 2) * 1000,
                maxDelay = (settings["max_delay"] as? Int ?: 5) * 1000,
                isActive = true
            )
            taskIds.add(taskDao.insert(task))
        }
        
        // Tạo tác vụ Share Posts
        if (settings["share_posts_enabled"] as? Boolean == true) {
            val shareType = settings["share_posts_type"] as? String ?: "wall"
            val task = Task(
                accountId = accountId,
                type = TaskType.SHARE,
                target = shareType,
                content = "",
                maxActions = (settings["action_limit"] as? Int ?: 50) / 3, // Giảm số lượng chia sẻ
                minDelay = (settings["min_delay"] as? Int ?: 2) * 1000,
                maxDelay = (settings["max_delay"] as? Int ?: 5) * 1000,
                isActive = true
            )
            taskIds.add(taskDao.insert(task))
        }
        
        // Tạo tác vụ Post Content
        if (settings["post_content_enabled"] as? Boolean == true) {
            val postContent = settings["post_content_text"] as? String ?: ""
            val task = Task(
                accountId = accountId,
                type = TaskType.POST,
                target = "",
                content = postContent,
                maxActions = (settings["action_limit"] as? Int ?: 50) / 5, // Giảm số lượng đăng bài
                minDelay = (settings["min_delay"] as? Int ?: 2) * 1000,
                maxDelay = (settings["max_delay"] as? Int ?: 5) * 1000,
                isActive = true
            )
            taskIds.add(taskDao.insert(task))
        }
        
        // Tạo tác vụ Send Messages
        if (settings["send_messages_enabled"] as? Boolean == true) {
            val messageText = settings["send_messages_text"] as? String ?: ""
            val task = Task(
                accountId = accountId,
                type = TaskType.MESSAGE,
                target = "",
                content = messageText,
                maxActions = (settings["action_limit"] as? Int ?: 50) / 2, // Giảm số lượng tin nhắn
                minDelay = (settings["min_delay"] as? Int ?: 2) * 1000,
                maxDelay = (settings["max_delay"] as? Int ?: 5) * 1000,
                isActive = true
            )
            taskIds.add(taskDao.insert(task))
        }
        
        // Tạo tác vụ Add Friends
        if (settings["add_friends_enabled"] as? Boolean == true) {
            val friendKeywords = settings["add_friends_keywords"] as? String ?: ""
            val task = Task(
                accountId = accountId,
                type = TaskType.ADD_FRIEND,
                target = friendKeywords,
                content = "",
                maxActions = (settings["action_limit"] as? Int ?: 50) / 3, // Giảm số lượng kết bạn
                minDelay = (settings["min_delay"] as? Int ?: 2) * 1000,
                maxDelay = (settings["max_delay"] as? Int ?: 5) * 1000,
                isActive = true
            )
            taskIds.add(taskDao.insert(task))
        }
        
        return taskIds
    }
} 