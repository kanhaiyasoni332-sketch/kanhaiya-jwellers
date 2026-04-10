package com.kanhaiyajewellers.creditmanager.ui.customerhistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kanhaiyajewellers.creditmanager.data.db.dao.PaymentDao
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import com.kanhaiyajewellers.creditmanager.data.model.TransactionWithCustomer

class CustomerHistoryViewModel(
    private val transactionDao: TransactionDao,
    private val paymentDao: PaymentDao
) : ViewModel() {

    fun getTransactionsForCustomer(customerId: Long): LiveData<List<TransactionWithCustomer>> {
        return transactionDao.getTransactionsByCustomerId(customerId)
    }

    suspend fun getLedgerExportData(customerId: Long, fallbackName: String, fallbackPhone: String): LedgerExportData {
        val transactions = transactionDao.getTransactionsByCustomerIdSync(customerId)
        val payments = paymentDao.getPaymentsForCustomer(customerId)

        val creditEntries = transactions.map {
            LedgerEntry(
                dateMillis = it.createdAt,
                type = "Credit",
                amount = it.totalAmount
            )
        }
        val paymentEntries = payments.map {
            LedgerEntry(
                dateMillis = it.createdAt,
                type = "Payment",
                amount = it.amount
            )
        }
        val allEntries = (creditEntries + paymentEntries).sortedBy { it.dateMillis }

        val totalCredit = transactions.sumOf { it.totalAmount }
        val totalPaid = transactions.sumOf { it.paidAmount }
        val remaining = transactions.sumOf { it.remainingAmount }
        val first = transactions.firstOrNull()

        return LedgerExportData(
            customerName = first?.customerName ?: fallbackName,
            phone = first?.phone ?: fallbackPhone,
            entries = allEntries,
            totalCredit = totalCredit,
            totalPaid = totalPaid,
            remainingBalance = remaining
        )
    }
}
