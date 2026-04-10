package com.kanhaiyajewellers.creditmanager.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kanhaiyajewellers.creditmanager.KanhaiyaJewellersApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val transactionId = intent.getLongExtra(ReminderConstants.EXTRA_TRANSACTION_ID, -1L)
        if (transactionId <= 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = (context.applicationContext as KanhaiyaJewellersApp).database
                val payload = db.transactionDao().getReminderPayload(transactionId) ?: return@launch
                ReminderNotificationHelper.showReminder(
                    context = context,
                    transactionId = payload.transactionId,
                    customerId = payload.customerId,
                    customerName = payload.customerName,
                    phone = payload.phone,
                    pendingAmount = payload.pendingAmount
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
