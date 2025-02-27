package com.fbautomanager.app.util

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsUtil(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Lưu tất cả cài đặt
     */
    fun saveAllSettings(settings: Map<String, Any>) {
        val editor = prefs.edit()
        
        // Cài đặt tác vụ
        editor.putBoolean(KEY_LIKE_POSTS_ENABLED, settings["like_posts_enabled"] as? Boolean ?: false)
        editor.putString(KEY_LIKE_POSTS_KEYWORDS, settings["like_posts_keywords"] as? String ?: "")
        
        editor.putBoolean(KEY_COMMENT_POSTS_ENABLED, settings["comment_posts_enabled"] as? Boolean ?: false)
        editor.putString(KEY_COMMENT_POSTS_TEXT, settings["comment_posts_text"] as? String ?: "")
        
        editor.putBoolean(KEY_SHARE_POSTS_ENABLED, settings["share_posts_enabled"] as? Boolean ?: false)
        editor.putString(KEY_SHARE_POSTS_TYPE, settings["share_posts_type"] as? String ?: "wall")
        
        editor.putBoolean(KEY_POST_CONTENT_ENABLED, settings["post_content_enabled"] as? Boolean ?: false)
        editor.putString(KEY_POST_CONTENT_TEXT, settings["post_content_text"] as? String ?: "")
        
        editor.putBoolean(KEY_SEND_MESSAGES_ENABLED, settings["send_messages_enabled"] as? Boolean ?: false)
        editor.putString(KEY_SEND_MESSAGES_TEXT, settings["send_messages_text"] as? String ?: "")
        
        editor.putBoolean(KEY_ADD_FRIENDS_ENABLED, settings["add_friends_enabled"] as? Boolean ?: false)
        editor.putString(KEY_ADD_FRIENDS_KEYWORDS, settings["add_friends_keywords"] as? String ?: "")
        
        // Cài đặt độ trễ
        val minDelay = settings["min_delay"] as? Int ?: 2
        val maxDelay = settings["max_delay"] as? Int ?: 5
        editor.putInt(KEY_MIN_DELAY, minDelay)
        editor.putInt(KEY_MAX_DELAY, maxDelay)
        
        // Cài đặt giới hạn hành động
        val actionLimit = settings["action_limit"] as? Int ?: 50
        editor.putInt(KEY_ACTION_LIMIT, actionLimit)
        
        editor.apply()
    }
    
    /**
     * Lấy tất cả cài đặt
     */
    fun getAllSettings(): Map<String, Any> {
        val settings = mutableMapOf<String, Any>()
        
        // Cài đặt tác vụ
        settings["like_posts_enabled"] = prefs.getBoolean(KEY_LIKE_POSTS_ENABLED, false)
        settings["like_posts_keywords"] = prefs.getString(KEY_LIKE_POSTS_KEYWORDS, "") ?: ""
        
        settings["comment_posts_enabled"] = prefs.getBoolean(KEY_COMMENT_POSTS_ENABLED, false)
        settings["comment_posts_text"] = prefs.getString(KEY_COMMENT_POSTS_TEXT, "") ?: ""
        
        settings["share_posts_enabled"] = prefs.getBoolean(KEY_SHARE_POSTS_ENABLED, false)
        settings["share_posts_type"] = prefs.getString(KEY_SHARE_POSTS_TYPE, "wall") ?: "wall"
        
        settings["post_content_enabled"] = prefs.getBoolean(KEY_POST_CONTENT_ENABLED, false)
        settings["post_content_text"] = prefs.getString(KEY_POST_CONTENT_TEXT, "") ?: ""
        
        settings["send_messages_enabled"] = prefs.getBoolean(KEY_SEND_MESSAGES_ENABLED, false)
        settings["send_messages_text"] = prefs.getString(KEY_SEND_MESSAGES_TEXT, "") ?: ""
        
        settings["add_friends_enabled"] = prefs.getBoolean(KEY_ADD_FRIENDS_ENABLED, false)
        settings["add_friends_keywords"] = prefs.getString(KEY_ADD_FRIENDS_KEYWORDS, "") ?: ""
        
        // Cài đặt độ trễ
        settings["min_delay"] = prefs.getInt(KEY_MIN_DELAY, 2)
        settings["max_delay"] = prefs.getInt(KEY_MAX_DELAY, 5)
        
        // Cài đặt giới hạn hành động
        settings["action_limit"] = prefs.getInt(KEY_ACTION_LIMIT, 50)
        
        return settings
    }
    
    /**
     * Lưu cài đặt lịch trình
     */
    fun saveScheduleSettings(settings: Map<String, Any>) {
        val editor = prefs.edit()
        
        editor.putBoolean(KEY_SCHEDULE_ENABLED, settings["schedule_enabled"] as? Boolean ?: false)
        editor.putString(KEY_START_TIME, settings["start_time"] as? String ?: "08:00")
        editor.putString(KEY_END_TIME, settings["end_time"] as? String ?: "20:00")
        editor.putInt(KEY_INTERVAL_INDEX, settings["interval_index"] as? Int ?: 0)
        
        editor.putBoolean(KEY_MONDAY, settings["monday"] as? Boolean ?: false)
        editor.putBoolean(KEY_TUESDAY, settings["tuesday"] as? Boolean ?: false)
        editor.putBoolean(KEY_WEDNESDAY, settings["wednesday"] as? Boolean ?: false)
        editor.putBoolean(KEY_THURSDAY, settings["thursday"] as? Boolean ?: false)
        editor.putBoolean(KEY_FRIDAY, settings["friday"] as? Boolean ?: false)
        editor.putBoolean(KEY_SATURDAY, settings["saturday"] as? Boolean ?: false)
        editor.putBoolean(KEY_SUNDAY, settings["sunday"] as? Boolean ?: false)
        
        editor.apply()
    }
    
    /**
     * Lấy cài đặt lịch trình
     */
    fun getScheduleSettings(): Map<String, Any> {
        val settings = mutableMapOf<String, Any>()
        
        settings["schedule_enabled"] = prefs.getBoolean(KEY_SCHEDULE_ENABLED, false)
        settings["start_time"] = prefs.getString(KEY_START_TIME, "08:00") ?: "08:00"
        settings["end_time"] = prefs.getString(KEY_END_TIME, "20:00") ?: "20:00"
        settings["interval_index"] = prefs.getInt(KEY_INTERVAL_INDEX, 0)
        
        settings["monday"] = prefs.getBoolean(KEY_MONDAY, false)
        settings["tuesday"] = prefs.getBoolean(KEY_TUESDAY, false)
        settings["wednesday"] = prefs.getBoolean(KEY_WEDNESDAY, false)
        settings["thursday"] = prefs.getBoolean(KEY_THURSDAY, false)
        settings["friday"] = prefs.getBoolean(KEY_FRIDAY, false)
        settings["saturday"] = prefs.getBoolean(KEY_SATURDAY, false)
        settings["sunday"] = prefs.getBoolean(KEY_SUNDAY, false)
        
        return settings
    }
    
    companion object {
        private const val PREFS_NAME = "fb_auto_manager_prefs"
        
        // Cài đặt tác vụ
        private const val KEY_LIKE_POSTS_ENABLED = "like_posts_enabled"
        private const val KEY_LIKE_POSTS_KEYWORDS = "like_posts_keywords"
        
        private const val KEY_COMMENT_POSTS_ENABLED = "comment_posts_enabled"
        private const val KEY_COMMENT_POSTS_TEXT = "comment_posts_text"
        
        private const val KEY_SHARE_POSTS_ENABLED = "share_posts_enabled"
        private const val KEY_SHARE_POSTS_TYPE = "share_posts_type"
        
        private const val KEY_POST_CONTENT_ENABLED = "post_content_enabled"
        private const val KEY_POST_CONTENT_TEXT = "post_content_text"
        
        private const val KEY_SEND_MESSAGES_ENABLED = "send_messages_enabled"
        private const val KEY_SEND_MESSAGES_TEXT = "send_messages_text"
        
        private const val KEY_ADD_FRIENDS_ENABLED = "add_friends_enabled"
        private const val KEY_ADD_FRIENDS_KEYWORDS = "add_friends_keywords"
        
        // Cài đặt độ trễ
        private const val KEY_MIN_DELAY = "min_delay"
        private const val KEY_MAX_DELAY = "max_delay"
        
        // Cài đặt giới hạn hành động
        private const val KEY_ACTION_LIMIT = "action_limit"
        
        // Cài đặt lịch trình
        private const val KEY_SCHEDULE_ENABLED = "schedule_enabled"
        private const val KEY_START_TIME = "start_time"
        private const val KEY_END_TIME = "end_time"
        private const val KEY_INTERVAL_INDEX = "interval_index"
        
        private const val KEY_MONDAY = "monday"
        private const val KEY_TUESDAY = "tuesday"
        private const val KEY_WEDNESDAY = "wednesday"
        private const val KEY_THURSDAY = "thursday"
        private const val KEY_FRIDAY = "friday"
        private const val KEY_SATURDAY = "saturday"
        private const val KEY_SUNDAY = "sunday"
    }
}
