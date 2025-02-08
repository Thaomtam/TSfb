package com.tsfb.app.hook

import android.content.Context
import com.github.kyuubiran.ezxhelper.EzXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import de.robv.android.xposed.XC_LoadPackage
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FacebookHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
    private val automation: FacebookAutomation,
    private val repository: AppRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val classLoader = lpparam.classLoader
    
    fun init() {
        hookFeedLoading()
        hookLikeAction()
        hookCommentAction()
        hookShareAction()
    }

    private fun hookFeedLoading() {
        val feedClass = lpparam.classLoader.loadClass("com.facebook.feed.ui.FeedFragment")
        feedClass.declaredMethods.find { it.name == "onCreateView" }?.createHook {
            after { param ->
                val view = param.result as View
                val recyclerView = view.findViewById<RecyclerView>(
                    view.resources.getIdentifier("feed_stream", "id", "com.facebook.katana")
                )
                setupFeedObserver(recyclerView)
            }
        }
    }

    private fun hookLikeAction() {
        val likeClass = lpparam.classLoader.loadClass("com.facebook.feed.protocol.LikeStoryAction")
        likeClass.declaredMethods.find { it.name == "execute" }?.createHook {
            after {
                updateLikeStats()
            }
        }
    }

    private fun hookCommentAction() {
        val commentClass = lpparam.classLoader.loadClass("com.facebook.feed.protocol.CommentStoryAction")
        commentClass.declaredMethods.find { it.name == "execute" }?.createHook {
            after {
                updateCommentStats()
            }
        }
    }

    private fun hookShareAction() {
        val shareClass = lpparam.classLoader.loadClass("com.facebook.feed.protocol.ShareStoryAction")
        shareClass.declaredMethods.find { it.name == "execute" }?.createHook {
            after {
                updateShareStats()
            }
        }
    }

    private fun setupFeedObserver(recyclerView: Any?) {
        if (recyclerView == null) return
        
        try {
            val onScrollListener = Class.forName("androidx.recyclerview.widget.RecyclerView\$OnScrollListener")
            val proxy = java.lang.reflect.Proxy.newProxyInstance(
                classLoader,
                arrayOf(onScrollListener)
            ) { _, method, args ->
                when (method.name) {
                    "onScrolled" -> {
                        // Kiểm tra và tự động tương tác khi cuộn
                        checkForInteraction()
                    }
                }
                null
            }
            
            val addOnScrollListenerMethod = recyclerView.javaClass.getMethod("addOnScrollListener", onScrollListener)
            addOnScrollListenerMethod.invoke(recyclerView, proxy)
            
        } catch (e: Exception) {
            EzXHelper.log("Lỗi setup feed observer: ${e.message}")
        }
    }

    private fun checkForInteraction() {
        if (!isAutoInteractionEnabled()) return
        
        try {
            val settings = getSettings()
            if (settings.autoLikeEnabled) {
                automation.startAutoLike()
            }
            if (settings.autoCommentEnabled) {
                automation.startAutoComment(settings.commentTemplates)
            }
            if (settings.autoShareEnabled) {
                automation.startAutoShare()
            }
        } catch (e: Exception) {
            EzXHelper.log("Lỗi tự động tương tác: ${e.message}")
        }
    }

    private fun updatePostStats() {
        scope.launch {
            repository.incrementStats(StatsType.POSTS)
            repository.addLog("Đăng bài thành công")
        }
    }

    private fun updateLikeStats() {
        scope.launch {
            repository.incrementStats(StatsType.LIKES)
        }
    }

    private fun updateCommentStats() {
        scope.launch {
            repository.incrementStats(StatsType.COMMENTS)
        }
    }

    private fun updateShareStats() {
        scope.launch {
            repository.incrementStats(StatsType.SHARES)
        }
    }

    private fun isAutoInteractionEnabled(): Boolean {
        return try {
            val settings = getSettings()
            settings.autoLikeEnabled || settings.autoCommentEnabled || settings.autoShareEnabled
        } catch (e: Exception) {
            false
        }
    }

    private fun getSettings(): Settings {
        // Đọc cài đặt từ SharedPreferences
        val context = getSystemContext()
        val prefs = context.getSharedPreferences("tsfb_settings", Context.MODE_PRIVATE)
        
        return Settings(
            interactionSpeed = prefs.getInt("interaction_speed", 5),
            autoLikeEnabled = prefs.getBoolean("auto_like_enabled", false),
            autoCommentEnabled = prefs.getBoolean("auto_comment_enabled", false),
            autoShareEnabled = prefs.getBoolean("auto_share_enabled", false),
            commentTemplates = prefs.getStringSet("comment_templates", setOf())?.toList() ?: emptyList()
        )
    }

    private fun getSystemContext(): Context {
        val activityThread = Class.forName("android.app.ActivityThread")
        val currentActivityThread = activityThread.getMethod("currentActivityThread").invoke(null)
        return activityThread.getMethod("getSystemContext").invoke(currentActivityThread) as Context
    }
} 