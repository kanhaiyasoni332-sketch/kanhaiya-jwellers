package com.kanhaiyajewellers.creditmanager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kanhaiyajewellers.creditmanager.data.db.AppDatabase
import com.kanhaiyajewellers.creditmanager.ui.addtransaction.AddTransactionViewModel
import com.kanhaiyajewellers.creditmanager.ui.dashboard.DashboardViewModel
import com.kanhaiyajewellers.creditmanager.ui.search.SearchViewModel
import com.kanhaiyajewellers.creditmanager.ui.transactiondetail.TransactionDetailViewModel

/**
 * Unified factory that creates all ViewModels with their DAO dependencies.
 */
class ViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(db.transactionDao()) as T

            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(db.transactionDao()) as T

            modelClass.isAssignableFrom(AddTransactionViewModel::class.java) ->
                AddTransactionViewModel(db.customerDao(), db.transactionDao()) as T

            modelClass.isAssignableFrom(TransactionDetailViewModel::class.java) ->
                TransactionDetailViewModel(db.transactionDao(), db.paymentDao()) as T

            modelClass.isAssignableFrom(com.kanhaiyajewellers.creditmanager.ui.customerhistory.CustomerHistoryViewModel::class.java) ->
                com.kanhaiyajewellers.creditmanager.ui.customerhistory.CustomerHistoryViewModel(db.transactionDao()) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
