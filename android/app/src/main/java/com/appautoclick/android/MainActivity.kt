package com.appautoclick.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: AutoClickPreferences
    private var awaitingPointSelection: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = AutoClickPreferences(this)

        val statusText = findViewById<TextView>(R.id.statusText)
        val fixedPointText = findViewById<TextView>(R.id.fixedPointText)
        val runningSwitch = findViewById<Switch>(R.id.runningSwitch)
        val intervalGroup = findViewById<RadioGroup>(R.id.intervalGroup)
        val savePointButton = findViewById<Button>(R.id.savePointButton)
        val clearPointButton = findViewById<Button>(R.id.clearPointButton)
        val openAccessibilityButton = findViewById<Button>(R.id.openAccessibilityButton)

        runningSwitch.isChecked = preferences.isRunning()
        when (val optionId = preferences.getIntervalOptionId()) {
            R.id.interval5s,
            R.id.interval10s,
            R.id.interval30s,
            R.id.interval60s,
            R.id.interval1m,
            R.id.interval1m30s -> intervalGroup.check(optionId)
            else -> {
                when (preferences.getIntervalMs()) {
                    5000L -> intervalGroup.check(R.id.interval5s)
                    10000L -> intervalGroup.check(R.id.interval10s)
                    30000L -> intervalGroup.check(R.id.interval30s)
                    60000L -> intervalGroup.check(R.id.interval60s)
                    90000L -> intervalGroup.check(R.id.interval1m30s)
                    else -> intervalGroup.check(R.id.interval5s)
                }
            }
        }

        fun refreshUi() {
            val running = preferences.isRunning()
            statusText.text = if (running) getString(R.string.status_running) else getString(R.string.status_stopped)
            val fixedPoint = preferences.getClickPoint()
            fixedPointText.text = fixedPoint?.let { "Ponto fixo: (${it.first.toInt()}, ${it.second.toInt()})" }
                ?: getString(R.string.point_dynamic)
            runningSwitch.isChecked = running
        }

        runningSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferences.setRunning(isChecked)
            AutoClickAccessibilityService.instance?.let { service ->
                if (isChecked) service.scheduleClicks() else service.stopClicks()
            }
            refreshUi()
        }

        intervalGroup.setOnCheckedChangeListener { _, checkedId ->
            val interval = when (checkedId) {
                R.id.interval5s -> 5000L
                R.id.interval10s -> 10000L
                R.id.interval30s -> 30000L
                R.id.interval60s -> 60000L
                R.id.interval1m -> 60000L
                R.id.interval1m30s -> 90000L
                else -> 5000L
            }
            preferences.setIntervalMs(interval)
            preferences.setIntervalOptionId(checkedId)
        }

        savePointButton.setOnClickListener {
            awaitingPointSelection = true
            statusText.text = getString(R.string.tap_instruction)
        }

        clearPointButton.setOnClickListener {
            preferences.clearClickPoint()
            refreshUi()
        }

        openAccessibilityButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        val root = findViewById<View>(R.id.rootLayout)
        root.setOnTouchListener { _, event ->
            if (awaitingPointSelection && event.action == MotionEvent.ACTION_DOWN) {
                preferences.setClickPoint(event.rawX, event.rawY)
                awaitingPointSelection = false
                refreshUi()
                true
            } else {
                false
            }
        }

        refreshUi()
    }

    private fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedComponent = ComponentName(context, AutoClickAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.split(':').any { ComponentName.unflattenFromString(it) == expectedComponent }
    }

    override fun onResume() {
        super.onResume()
        val enabled = isAccessibilityServiceEnabled(this)
        val statusText = findViewById<TextView>(R.id.statusText)
        if (!enabled) {
            statusText.text = getString(R.string.enable_accessibility_hint)
        }
    }
}
