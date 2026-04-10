package com.kanhaiyajewellers.creditmanager.ui.customerhistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import com.kanhaiyajewellers.creditmanager.data.model.TransactionWithCustomer

class CustomerHistoryViewModel(private val transactionDao: TransactionDao) : ViewModel() {

    fun getTransactionsForCustomer(customerId: Long): LiveData<List<TransactionWithCustomer>> {
        return transactionDao.getTransactionsByCustomerId(customerId)
    }
}
