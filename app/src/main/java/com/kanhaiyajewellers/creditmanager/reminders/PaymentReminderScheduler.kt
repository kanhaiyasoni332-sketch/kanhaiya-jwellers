package com.kanhaiyajewellers.creditmanager.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object PaymentReminderScheduler {

    fun schedulePromiseReminder(
        context: Context,
        transactionId: Long,
        triggerAtMillis: Long
    ) {
        val normalized = Calendar.getInstance().apply {
            timeInMillis = triggerAtMillis
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (normalized < System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            transactionId.toInt(),
            Intent(context, PaymentReminderReceiver::class.java).apply {
                putExtra(ReminderConstants.EXTRA_TRANSACTION_ID, transactionId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, normalized, pendingIntent)
    }
}
