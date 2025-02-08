import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.tsfb.app.R
import com.tsfb.app.automation.FacebookAutomation
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FloatingBubbleService : Service() {

    @Inject
    lateinit var automation: FacebookAutomation

    private lateinit var windowManager: WindowManager
    private lateinit var floatingBubble: View
    private var isRunning = false

    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        x = 0
        y = 100
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createFloatingBubble()
    }

    private fun createFloatingBubble() {
        floatingBubble = LayoutInflater.from(this).inflate(R.layout.layout_floating_bubble, null)
        
        // Xử lý di chuyển bubble
        var initialX: Int = 0
        var initialY: Int = 0
        var initialTouchX: Float = 0f
        var initialTouchY: Float = 0f

        floatingBubble.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingBubble, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (Math.abs(event.rawX - initialTouchX) < 10 && 
                        Math.abs(event.rawY - initialTouchY) < 10) {
                        toggleAutomation()
                    }
                    true
                }
                else -> false
            }
        }

        windowManager.addView(floatingBubble, params)
    }

    private fun toggleAutomation() {
        isRunning = !isRunning
        val bubbleImage = floatingBubble.findViewById<ImageView>(R.id.bubbleImage)
        
        if (isRunning) {
            automation.startAutomation()
            bubbleImage.setImageResource(R.drawable.ic_bubble_active)
        } else {
            automation.stopAutomation()
            bubbleImage.setImageResource(R.drawable.ic_bubble_inactive)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        automation.stopAutomation()
        windowManager.removeView(floatingBubble)
    }
}