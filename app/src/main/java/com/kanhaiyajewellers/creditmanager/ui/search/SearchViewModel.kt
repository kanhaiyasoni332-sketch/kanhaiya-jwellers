package com.kanhaiyajewellers.creditmanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import com.kanhaiyajewellers.creditmanager.data.model.TransactionWithCustomer

class SearchViewModel(private val transactionDao: TransactionDao) : ViewModel() {

    private val _query = MutableLiveData<String>("")

    /**
     * Automatically re-queries whenever [_query] changes.
     * Returns all records when query is empty.
     */
    val searchResults: LiveData<List<com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals>> = _query.switchMap { q ->
        transactionDao.searchCustomers(q)
    }

    fun search(query: String) {
        _query.value = query.trim()
    }
}
