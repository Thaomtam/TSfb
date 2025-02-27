package com.fbautomanager.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "logs",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Log(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val taskId: Long? = null,
    val action: String,
    val details: String = "",
    val status: LogStatus,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LogStatus {
    SUCCESS,
    FAILURE,
    WARNING,
    INFO
} 