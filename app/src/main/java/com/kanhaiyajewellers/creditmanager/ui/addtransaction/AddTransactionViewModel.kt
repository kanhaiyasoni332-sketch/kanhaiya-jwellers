package com.kanhaiyajewellers.creditmanager.ui.addtransaction

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanhaiyajewellers.creditmanager.data.db.dao.CustomerDao
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import com.kanhaiyajewellers.creditmanager.data.db.entity.Customer
import com.kanhaiyajewellers.creditmanager.data.db.entity.Transaction
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) : ViewModel() {

    val totalAmount = MutableLiveData("")
    val paidAmount = MutableLiveData("")

    /** Auto-calculated remaining = total − paid, always ≥ 0 */
    val remainingAmount: MediatorLiveData<Double> = MediatorLiveData<Double>(0.0).also { mediator ->
        fun recalculate() {
            val total = totalAmount.value?.toDoubleOrNull() ?: 0.0
            val paid  = paidAmount.value?.toDoubleOrNull()  ?: 0.0
            mediator.value = maxOf(0.0, total - paid)
        }
        mediator.addSource(totalAmount) { recalculate() }
        mediator.addSource(paidAmount)  { recalculate() }
    }

    /**
     * Persists a new transaction. If the customer doesn't exist by phone, creates one first.
     * Determines status from remaining amount.
     *
     * @param onSuccess  Callback on the main thread when save is complete.
     */
    fun saveTransaction(
        name: String,
        phone: String,
        itemName: String,
        itemWeight: String,
        promiseDate: Long?,
        total: Double,
        paid: Double,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Upsert customer
                var customer = customerDao.getByPhone(phone)
                if (customer == null) {
                    val id = customerDao.insert(Customer(name = name, phone = phone))
                    customer = Customer(id = id, name = name, phone = phone)
                }

                val remaining = maxOf(0.0, total - paid)
                val status = if (remaining > 0) "PENDING" else "COMPLETED"

                val transactionId = transactionDao.insert(
                    Transaction(
                        customerId = customer.id,
                        itemName = itemName,
                        itemWeight = itemWeight,
                        promiseDate = promiseDate,
                        totalAmount = total,
                        paidAmount = paid,
                        remainingAmount = remaining,
                        status = status
                    )
                )
                onSuccess(transactionId)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
