package com.fbautomanager.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val packageName: String,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) 