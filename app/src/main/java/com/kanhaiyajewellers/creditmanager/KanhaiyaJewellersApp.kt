package com.kanhaiyajewellers.creditmanager

import android.app.Application
import com.kanhaiyajewellers.creditmanager.data.db.AppDatabase
import com.kanhaiyajewellers.creditmanager.reminders.ReminderNotificationHelper

class KanhaiyaJewellersApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        ReminderNotificationHelper.ensureChannel(this)
    }
}
