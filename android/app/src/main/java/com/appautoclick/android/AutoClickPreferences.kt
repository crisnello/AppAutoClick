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

    fun setPrimaryClickPoint(x: Float, y: Float) = prefs.edit()
        .putFloat(KEY_PRIMARY_CLICK_X, x)
        .putFloat(KEY_PRIMARY_CLICK_Y, y)
        .putBoolean(KEY_HAS_PRIMARY_POINT, true)
        .apply()

    fun setSecondaryClickPoint(x: Float, y: Float) = prefs.edit()
        .putFloat(KEY_SECONDARY_CLICK_X, x)
        .putFloat(KEY_SECONDARY_CLICK_Y, y)
        .putBoolean(KEY_HAS_SECONDARY_POINT, true)
        .apply()

    fun clearClickPoints() = prefs.edit()
        .putBoolean(KEY_HAS_PRIMARY_POINT, false)
        .putBoolean(KEY_HAS_SECONDARY_POINT, false)
        .apply()

    fun getPrimaryClickPoint(): Pair<Float, Float>? {
        if (!prefs.getBoolean(KEY_HAS_PRIMARY_POINT, false)) return null
        val x = prefs.getFloat(KEY_PRIMARY_CLICK_X, 0f)
        val y = prefs.getFloat(KEY_PRIMARY_CLICK_Y, 0f)
        return x to y
    }

    fun getSecondaryClickPoint(): Pair<Float, Float>? {
        if (!prefs.getBoolean(KEY_HAS_SECONDARY_POINT, false)) return null
        val x = prefs.getFloat(KEY_SECONDARY_CLICK_X, 0f)
        val y = prefs.getFloat(KEY_SECONDARY_CLICK_Y, 0f)
        return x to y
    }

    companion object {
        private const val PREFS_NAME = "autoclick_prefs"
        private const val KEY_RUNNING = "running"
        private const val KEY_INTERVAL_MS = "interval_ms"
        private const val KEY_INTERVAL_OPTION_ID = "interval_option_id"
        private const val KEY_HAS_PRIMARY_POINT = "has_primary_point"
        private const val KEY_HAS_SECONDARY_POINT = "has_secondary_point"
        private const val KEY_PRIMARY_CLICK_X = "primary_click_x"
        private const val KEY_PRIMARY_CLICK_Y = "primary_click_y"
        private const val KEY_SECONDARY_CLICK_X = "secondary_click_x"
        private const val KEY_SECONDARY_CLICK_Y = "secondary_click_y"
    }
}
