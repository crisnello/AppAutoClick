package com.appautoclick.android

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class AutoClickAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var preferences: AutoClickPreferences
    private var clickPrimaryNext: Boolean = true

    private val clickRunnable = object : Runnable {
        override fun run() {
            if (!preferences.isRunning()) return
            val primaryPoint = preferences.getPrimaryClickPoint() ?: DEFAULT_POINT
            val secondaryPoint = preferences.getSecondaryClickPoint()
            val point = if (secondaryPoint == null) {
                primaryPoint
            } else if (clickPrimaryNext) {
                clickPrimaryNext = false
                primaryPoint
            } else {
                clickPrimaryNext = true
                secondaryPoint
            }
            performTap(point.first, point.second)
            handler.postDelayed(this, preferences.getIntervalMs())
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        preferences = AutoClickPreferences(this)
        if (preferences.isRunning()) {
            scheduleClicks()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() {
        handler.removeCallbacks(clickRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(clickRunnable)
        if (instance === this) {
            instance = null
        }
    }

    fun scheduleClicks() {
        handler.removeCallbacks(clickRunnable)
        clickPrimaryNext = true
        handler.post(clickRunnable)
    }

    fun stopClicks() {
        handler.removeCallbacks(clickRunnable)
    }

    private fun performTap(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 40)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }

    companion object {
        private val DEFAULT_POINT = 500f to 1000f
        var instance: AutoClickAccessibilityService? = null
            private set
    }
}
