package com.tsfb.app.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun addLog(message: String) {
        database.logDao().insert(LogEntity(message = message))
    }

    suspend fun getLogs() = database.logDao().getAll()

    suspend fun clearLogs() = database.logDao().deleteAll()

    suspend fun updateStats(
        likes: Int = 0,
        comments: Int = 0,
        shares: Int = 0
    ) {
        database.statsDao().insert(
            StatsEntity(
                likesCount = likes,
                commentsCount = comments,
                sharesCount = shares
            )
        )
    }

    suspend fun getStats() = database.statsDao().get()
} 