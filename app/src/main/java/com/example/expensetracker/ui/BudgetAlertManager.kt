package com.example.expensetracker.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.expensetracker.R
import java.time.YearMonth

class BudgetAlertManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("budget_alerts", Context.MODE_PRIVATE)
    private val channelId = "budget_alerts"

    init {
        ensureChannel()
    }

    fun checkAndNotify(month: YearMonth, total: Double, budget: Double?) {
        if (budget == null || budget <= 0.0) return
        val ratio = total / budget
        val level = when {
            ratio >= 1.0 -> 100
            ratio >= 0.8 -> 80
            else -> 0
        }
        if (level == 0) return

        val key = "alert_${month}"
        val lastLevel = prefs.getInt(key, 0)
        if (level <= lastLevel) return

        if (!canPostNotifications()) return

        val (title, message) = if (level >= 100) {
            "Budget exceeded" to "You have passed your monthly budget for ${monthLabel(month)}."
        } else {
            "Budget alert" to "You have used 80% of your monthly budget for ${monthLabel(month)}."
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_pulse)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(level + month.hashCode(), notification)
        prefs.edit().putInt(key, level).apply()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Budget alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when you approach or exceed your budget"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun monthLabel(month: YearMonth): String {
        return formatMonth(month)
    }
}
