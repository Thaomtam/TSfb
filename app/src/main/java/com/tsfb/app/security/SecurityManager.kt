package com.tsfb.app.security

import android.content.Context
import com.github.kyuubiran.ezxhelper.EzXHelper
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

class SecurityManager(private val context: Context) {
    private val interactionCounter = AtomicInteger(0)
    private val lastInteractionTime = AtomicLong(0)
    
    fun canPerformInteraction(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastInteraction = currentTime - lastInteractionTime.get()
        
        // Kiểm tra tần suất tương tác
        if (timeSinceLastInteraction < getMinimumDelay()) {
            return false
        }
        
        // Kiểm tra số lượng tương tác trong một phiên
        if (interactionCounter.get() >= getMaxInteractionsPerSession()) {
            return false
        }
        
        // Thêm delay ngẫu nhiên
        Thread.sleep(getRandomDelay())
        
        interactionCounter.incrementAndGet()
        lastInteractionTime.set(currentTime)
        return true
    }
    
    fun resetSession() {
        interactionCounter.set(0)
    }
    
    private fun getMinimumDelay(): Long {
        val settings = getSettings()
        return (settings.interactionSpeed * 1000).toLong()
    }
    
    private fun getMaxInteractionsPerSession(): Int {
        // Giới hạn số lượng tương tác mỗi phiên để tránh phát hiện
        return Random.nextInt(30, 50)
    }
    
    private fun getRandomDelay(): Long {
        // Thêm delay ngẫu nhiên để tương tác tự nhiên hơn
        return Random.nextLong(1000, 3000)
    }
    
    private fun getSettings(): Settings {
        val prefs = context.getSharedPreferences("tsfb_settings", Context.MODE_PRIVATE)
        return Settings(
            interactionSpeed = prefs.getInt("interaction_speed", 5)
        )
    }
} 