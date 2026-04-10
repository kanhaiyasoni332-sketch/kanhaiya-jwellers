package com.kanhaiyajewellers.creditmanager.ui.dashboard

import androidx.lifecycle.ViewModel
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import java.util.Calendar

class DashboardViewModel(private val transactionDao: TransactionDao) : ViewModel() {

    /** Recent 50 customers joined with transaction stats. */
    val recentCustomers = transactionDao.getRecentCustomers()

    /** Sum of remaining_amount for all PENDING transactions. */
    val totalPendingAmount = transactionDao.getTotalPendingAmount()

    /** Count of unique customers with at least one PENDING transaction. */
    val pendingCustomersCount = transactionDao.getPendingCustomersCount()

    /** Count of all COMPLETED transactions. */
    val completedTransactionsCount = transactionDao.getCompletedTransactionsCount()

    /** Upcoming promise-date payments from today onward. */
    val upcomingPayments = transactionDao.getUpcomingPayments(startOfToday())

    private fun startOfToday(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
