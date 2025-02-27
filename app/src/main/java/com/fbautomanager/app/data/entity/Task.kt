package com.fbautomanager.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val type: TaskType,
    val target: String = "", // URL, ID, hoặc từ khóa tìm kiếm
    val content: String = "", // Nội dung bình luận, tin nhắn, bài đăng
    val maxActions: Int = 50, // Giới hạn số lượng hành động
    val minDelay: Int = 2000, // Độ trễ tối thiểu (ms)
    val maxDelay: Int = 5000, // Độ trễ tối đa (ms)
    val startTime: Long = 0, // Thời gian bắt đầu (0 = ngay lập tức)
    val endTime: Long = 0, // Thời gian kết thúc (0 = không giới hạn)
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskType {
    LIKE, // Thích bài viết
    COMMENT, // Bình luận
    SHARE, // Chia sẻ
    POST, // Đăng bài
    MESSAGE, // Gửi tin nhắn
    ADD_FRIEND // Kết bạn
} 