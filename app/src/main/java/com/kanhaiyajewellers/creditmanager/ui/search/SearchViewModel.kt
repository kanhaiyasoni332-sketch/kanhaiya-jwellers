package com.kanhaiyajewellers.creditmanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.kanhaiyajewellers.creditmanager.data.db.dao.TransactionDao
import java.util.Calendar

class SearchViewModel(private val transactionDao: TransactionDao) : ViewModel() {

    private val _query = MutableLiveData<String>("")

    /**
     * Automatically re-queries whenever [_query] changes.
     * Returns all records when query is empty.
     */
    val searchResults: LiveData<List<com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals>> = _query.switchMap { q ->
        transactionDao.searchCustomers(q, startOfToday())
    }

    fun search(query: String) {
        _query.value = query.trim()
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
