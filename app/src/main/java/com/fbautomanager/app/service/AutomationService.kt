package com.fbautomanager.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fbautomanager.app.FBAutoManagerApp
import com.fbautomanager.app.data.entity.Account
import com.fbautomanager.app.data.entity.Log
import com.fbautomanager.app.data.entity.LogStatus
import com.fbautomanager.app.data.entity.Task
import com.fbautomanager.app.data.entity.TaskType
import com.fbautomanager.app.util.RootUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.max
import kotlin.math.min

class AutomationService : AccessibilityService() {

    companion object {
        private val _isRunning = MutableLiveData<Boolean>(false)
        val isRunning: LiveData<Boolean> = _isRunning
        
        private var instance: AutomationService? = null
        
        fun getInstance(): AutomationService? {
            return instance
        }
    }
    
    private val handler = Handler(Looper.getMainLooper())
    private val random = Random()
    private var currentAccount: Account? = null
    private var currentTask: Task? = null
    private var actionsPerformed = 0
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isRunning.value = true
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Xử lý các sự kiện accessibility
        if (!FBAutoManagerApp.isRooted) return
        
        val packageName = event.packageName?.toString() ?: return
        
        // Chỉ xử lý các sự kiện từ ứng dụng Facebook
        if (!packageName.contains("facebook") && !packageName.contains("fb")) return
        
        // Xử lý tác vụ tự động dựa trên loại task hiện tại
        currentTask?.let { task ->
            when (task.type) {
                TaskType.LIKE -> handleLikeTask(event)
                TaskType.COMMENT -> handleCommentTask(event)
                TaskType.SHARE -> handleShareTask(event)
                TaskType.POST -> handlePostTask(event)
                TaskType.MESSAGE -> handleMessageTask(event)
                TaskType.ADD_FRIEND -> handleAddFriendTask(event)
            }
        }
    }
    
    override fun onInterrupt() {
        // Xử lý khi service bị gián đoạn
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        _isRunning.value = false
    }
    
    /**
     * Bắt đầu tự động hóa với tài khoản và tác vụ cụ thể
     */
    fun startAutomation(account: Account, task: Task) {
        if (!FBAutoManagerApp.isRooted) return
        
        currentAccount = account
        currentTask = task
        actionsPerformed = 0
        
        CoroutineScope(Dispatchers.IO).launch {
            // Mở ứng dụng Facebook với package tương ứng
            val opened = RootUtil.openFacebookApp(account.packageName)
            
            if (opened) {
                // Ghi log bắt đầu tự động hóa
                val log = Log(
                    accountId = account.id,
                    taskId = task.id,
                    action = "Bắt đầu tự động hóa",
                    details = "Loại: ${task.type.name}",
                    status = LogStatus.INFO
                )
                FBAutoManagerApp.database.logDao().insert(log)
            } else {
                // Ghi log lỗi
                val log = Log(
                    accountId = account.id,
                    taskId = task.id,
                    action = "Lỗi mở ứng dụng",
                    details = "Không thể mở package: ${account.packageName}",
                    status = LogStatus.FAILURE
                )
                FBAutoManagerApp.database.logDao().insert(log)
            }
        }
    }
    
    /**
     * Dừng tự động hóa
     */
    fun stopAutomation() {
        currentAccount = null
        currentTask = null
        actionsPerformed = 0
    }
    
    /**
     * Xử lý tác vụ thích bài viết
     */
    private fun handleLikeTask(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        val task = currentTask ?: return
        
        // Tìm nút "Thích" hoặc "Like"
        val likeButtons = findNodesByText(rootNode, listOf("Thích", "Like", "Yêu thích", "Love"))
        
        if (likeButtons.isNotEmpty() && actionsPerformed < task.maxActions) {
            // Chọn ngẫu nhiên một nút để nhấn
            val randomIndex = random.nextInt(likeButtons.size)
            val likeButton = likeButtons[randomIndex]
            
            // Thực hiện nhấn nút
            likeButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            
            // Tăng số lượng hành động đã thực hiện
            actionsPerformed++
            
            // Ghi log
            CoroutineScope(Dispatchers.IO).launch {
                val log = Log(
                    accountId = currentAccount?.id ?: 0,
                    taskId = task.id,
                    action = "Thích bài viết",
                    details = "Đã thích bài viết #$actionsPerformed",
                    status = LogStatus.SUCCESS
                )
                FBAutoManagerApp.database.logDao().insert(log)
                
                // Tạo độ trễ ngẫu nhiên trước khi thực hiện hành động tiếp theo
                val randomDelay = random.nextInt(task.maxDelay - task.minDelay) + task.minDelay
                delay(randomDelay.toLong())
                
                // Cuộn trang để tìm thêm bài viết
                if (actionsPerformed < task.maxActions) {
                    scrollDown()
                }
            }
        } else {
            // Cuộn trang để tìm thêm bài viết
            scrollDown()
        }
    }
    
    /**
     * Xử lý tác vụ bình luận
     */
    private fun handleCommentTask(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        val task = currentTask ?: return
        
        // Tìm nút "Bình luận" hoặc "Comment"
        val commentButtons = findNodesByText(rootNode, listOf("Bình luận", "Comment"))
        
        if (commentButtons.isNotEmpty() && actionsPerformed < task.maxActions) {
            // Chọn ngẫu nhiên một nút để nhấn
            val randomIndex = random.nextInt(commentButtons.size)
            val commentButton = commentButtons[randomIndex]
            
            // Thực hiện nhấn nút
            commentButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            
            // Đợi một chút để hộp bình luận hiện lên
            handler.postDelayed({
                // Tìm hộp nhập bình luận
                val rootNode = rootInActiveWindow ?: return@postDelayed
                val editTexts = findNodesByClassName(rootNode, "android.widget.EditText")
                
                if (editTexts.isNotEmpty()) {
                    val commentBox = editTexts[0]
                    
                    // Nhập nội dung bình luận
                    val arguments = Bundle()
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, task.content)
                    commentBox.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    
                    // Đợi một chút rồi nhấn nút gửi
                    handler.postDelayed({
                        val rootNode = rootInActiveWindow ?: return@postDelayed
                        val sendButtons = findNodesByText(rootNode, listOf("Gửi", "Send", "Đăng", "Post"))
                        
                        if (sendButtons.isNotEmpty()) {
                            sendButtons[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            
                            // Tăng số lượng hành động đã thực hiện
                            actionsPerformed++
                            
                            // Ghi log
                            CoroutineScope(Dispatchers.IO).launch {
                                val log = Log(
                                    accountId = currentAccount?.id ?: 0,
                                    taskId = task.id,
                                    action = "Bình luận",
                                    details = "Đã bình luận: \"${task.content}\" (#$actionsPerformed)",
                                    status = LogStatus.SUCCESS
                                )
                                FBAutoManagerApp.database.logDao().insert(log)
                                
                                // Tạo độ trễ ngẫu nhiên trước khi thực hiện hành động tiếp theo
                                val randomDelay = random.nextInt(task.maxDelay - task.minDelay) + task.minDelay
                                delay(randomDelay.toLong())
                                
                                // Cuộn trang để tìm thêm bài viết
                                if (actionsPerformed < task.maxActions) {
                                    scrollDown()
                                }
                            }
                        }
                    }, 1000)
                }
            }, 1000)
        } else {
            // Cuộn trang để tìm thêm bài viết
            scrollDown()
        }
    }
    
    /**
     * Xử lý tác vụ chia sẻ
     */
    private fun handleShareTask(event: AccessibilityEvent) {
        // Tương tự như các hàm xử lý khác
    }
    
    /**
     * Xử lý tác vụ đăng bài
     */
    private fun handlePostTask(event: AccessibilityEvent) {
        // Tương tự như các hàm xử lý khác
    }
    
    /**
     * Xử lý tác vụ gửi tin nhắn
     */
    private fun handleMessageTask(event: AccessibilityEvent) {
        // Tương tự như các hàm xử lý khác
    }
    
    /**
     * Xử lý tác vụ kết bạn
     */
    private fun handleAddFriendTask(event: AccessibilityEvent) {
        // Tương tự như các hàm xử lý khác
    }
    
    /**
     * Tìm các node theo text
     */
    private fun findNodesByText(node: AccessibilityNodeInfo, textList: List<String>): List<AccessibilityNodeInfo> {
        val result = mutableListOf<AccessibilityNodeInfo>()
        
        for (text in textList) {
            val nodes = node.findAccessibilityNodeInfosByText(text)
            result.addAll(nodes)
        }
        
        return result
    }
    
    /**
     * Tìm các node theo class name
     */
    private fun findNodesByClassName(node: AccessibilityNodeInfo, className: String): List<AccessibilityNodeInfo> {
        val result = mutableListOf<AccessibilityNodeInfo>()
        
        if (node.className?.toString() == className) {
            result.add(node)
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            result.addAll(findNodesByClassName(child, className))
        }
        
        return result
    }
    
    /**
     * Cuộn trang xuống
     */
    private fun scrollDown() {
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        
        val path = Path()
        path.moveTo(width / 2f, height * 0.7f)
        path.lineTo(width / 2f, height * 0.3f)
        
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 500))
            .build()
        
        dispatchGesture(gesture, null, null)
    }
} 