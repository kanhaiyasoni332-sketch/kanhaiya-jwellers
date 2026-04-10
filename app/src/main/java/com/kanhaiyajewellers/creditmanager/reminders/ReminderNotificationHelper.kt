package com.kanhaiyajewellers.creditmanager.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kanhaiyajewellers.creditmanager.R
import com.kanhaiyajewellers.creditmanager.ui.main.MainActivity
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

object ReminderNotificationHelper {

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            ReminderConstants.CHANNEL_ID,
            ReminderConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
    }

    fun showReminder(
        context: Context,
        transactionId: Long,
        customerId: Long,
        customerName: String,
        phone: String,
        pendingAmount: Double
    ) {
        val inr = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val amountText = inr.format(pendingAmount)
        val content = "$customerName will pay today. Collect $amountText"

        val openIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(ReminderConstants.EXTRA_OPEN_CUSTOMER_HISTORY, true)
            putExtra(ReminderConstants.EXTRA_TRANSACTION_ID, transactionId)
            putExtra(ReminderConstants.EXTRA_CUSTOMER_ID, customerId)
            putExtra(ReminderConstants.EXTRA_CUSTOMER_NAME, customerName)
            putExtra(ReminderConstants.EXTRA_CUSTOMER_PHONE, phone)
            putExtra(ReminderConstants.EXTRA_PENDING_AMOUNT, pendingAmount.toFloat())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            transactionId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = "Dear ${customerName} ji, as promised, today is your payment date. " +
            "Your pending amount is $amountText. Kindly pay today. - Kanhaiya Jewellers"
        val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://wa.me/91$phone?text=" +
                    URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            )
        }
        val whatsappPendingIntent = PendingIntent.getActivity(
            context,
            (transactionId + 100000L).toInt(),
            whatsappIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ReminderConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle("Payment Reminder")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(0, context.getString(R.string.send_whatsapp_reminder), whatsappPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(transactionId.toInt(), notification)
    }
}
