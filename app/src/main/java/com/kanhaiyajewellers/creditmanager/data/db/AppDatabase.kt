package com.kanhaiyajewellers.creditmanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kanhaiyajewellers.creditmanager.data.db.dao.CustomerDao
import com.kanhaiyajewellers.creditmanager.data.db.dao.PaymentDao
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import com.kanhaiyajewellers.creditmanager.data.db.entity.Customer
import com.kanhaiyajewellers.creditmanager.data.db.entity.Payment
import com.kanhaiyajewellers.creditmanager.data.db.entity.Transaction

@Database(
    entities = [Customer::class, Transaction::class, Payment::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kanhaiya_jewellers_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
