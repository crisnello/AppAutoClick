package com.appautoclick.android

import android.content.Context

class AutoClickPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setRunning(running: Boolean) = prefs.edit().putBoolean(KEY_RUNNING, running).apply()
    fun isRunning(): Boolean = prefs.getBoolean(KEY_RUNNING, false)

    fun setIntervalMs(intervalMs: Long) = prefs.edit().putLong(KEY_INTERVAL_MS, intervalMs).apply()
    fun getIntervalMs(): Long = prefs.getLong(KEY_INTERVAL_MS, 5000L)
    fun setIntervalOptionId(optionId: Int) = prefs.edit().putInt(KEY_INTERVAL_OPTION_ID, optionId).apply()
    fun getIntervalOptionId(): Int = prefs.getInt(KEY_INTERVAL_OPTION_ID, 0)

    fun setClickPoint(x: Float, y: Float) = prefs.edit()
        .putFloat(KEY_CLICK_X, x)
        .putFloat(KEY_CLICK_Y, y)
        .putBoolean(KEY_HAS_FIXED_POINT, true)
        .apply()

    fun clearClickPoint() = prefs.edit().putBoolean(KEY_HAS_FIXED_POINT, false).apply()

    fun hasFixedPoint(): Boolean = prefs.getBoolean(KEY_HAS_FIXED_POINT, false)

    fun getClickPoint(): Pair<Float, Float>? {
        if (!hasFixedPoint()) return null
        val x = prefs.getFloat(KEY_CLICK_X, 0f)
        val y = prefs.getFloat(KEY_CLICK_Y, 0f)
        return x to y
    }

    companion object {
        private const val PREFS_NAME = "autoclick_prefs"
        private const val KEY_RUNNING = "running"
        private const val KEY_INTERVAL_MS = "interval_ms"
        private const val KEY_INTERVAL_OPTION_ID = "interval_option_id"
        private const val KEY_HAS_FIXED_POINT = "has_fixed_point"
        private const val KEY_CLICK_X = "click_x"
        private const val KEY_CLICK_Y = "click_y"
    }
}
