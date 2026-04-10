package com.kanhaiyajewellers.creditmanager.ui.transactiondetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import com.kanhaiyajewellers.creditmanager.data.model.TransactionWithCustomer
import kotlinx.coroutines.launch

import com.kanhaiyajewellers.creditmanager.data.db.dao.PaymentDao
import com.kanhaiyajewellers.creditmanager.data.db.entity.Payment
import java.util.Calendar

class TransactionDetailViewModel(
    private val transactionDao: TransactionDao,
    private val paymentDao: PaymentDao
) : ViewModel() {

    private val _transactionId = MutableLiveData<Long>()

    /** Observed by the detail fragment; updates automatically after a payment. */
    val transaction: LiveData<TransactionWithCustomer> = _transactionId.switchMap { id ->
        transactionDao.getTransactionById(id)
    }

    /** Observed by the detail fragment; updates automatically when a payment is added. */
    val payments: LiveData<List<Payment>> = _transactionId.switchMap { id ->
        paymentDao.getPaymentsForTransaction(id)
    }

    fun loadTransaction(id: Long) {
        _transactionId.value = id
    }

    /**
     * Adds a partial payment, updates remaining amount and status automatically,
     * and logs the payment history.
     *
     * @param amount    The new payment being made now.
     * @param onSuccess Callback on the main thread after DB update.
     * @param onError   Callback with error message if something goes wrong.
     */
    fun addPayment(
        amount: Double,
        onSuccess: (PaymentCompletionInfo?) -> Unit,
        onError: (String) -> Unit
    ) {
        val current = transaction.value ?: return
        if (amount <= 0) {
            onError("Amount must be greater than 0")
            return
        }
        viewModelScope.launch {
            try {
                val newPaid      = current.paidAmount + amount
                val newRemaining = maxOf(0.0, current.remainingAmount - amount)
                val newStatus    = if (newRemaining <= 0.0) "COMPLETED" else "PENDING"
                val justCompleted = current.remainingAmount > 0.0 && newRemaining <= 0.0

                transactionDao.updatePayment(
                    id            = current.transactionId,
                    paidAmount    = newPaid,
                    remainingAmount = newRemaining,
                    status        = newStatus
                )

                paymentDao.insertPayment(
                    Payment(
                        transactionId = current.transactionId,
                        amount = amount
                    )
                )

                val loyalty = transactionDao.getLoyaltyStatus(
                    customerId = current.customerId,
                    startOfToday = startOfToday()
                )
                onSuccess(
                    if (justCompleted) {
                        PaymentCompletionInfo(
                            customerName = current.customerName,
                            phone = current.phone,
                            isLoyalCustomer = loyalty.isLoyalCustomer
                        )
                    } else null
                )
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun startOfToday(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
