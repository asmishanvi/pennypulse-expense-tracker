package com.example.expensetracker.ui

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {
    private val prefs = context.getSharedPreferences("smart_reminders", Context.MODE_PRIVATE)

    init {
        if (isEnabled()) {
            scheduleDaily()
        }
    }

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
        if (enabled) {
            scheduleDaily()
        } else {
            cancel()
        }
    }

    private fun scheduleDaily() {
        val now = ZonedDateTime.now()
        var next = now.withHour(20).withMinute(0).withSecond(0).withNano(0)
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        val delay = Duration.between(now, next).toMillis().coerceAtLeast(0)

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(ReminderWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(ReminderWorker.WORK_NAME)
    }

    private companion object {
        const val KEY_ENABLED = "enabled"
    }
}
