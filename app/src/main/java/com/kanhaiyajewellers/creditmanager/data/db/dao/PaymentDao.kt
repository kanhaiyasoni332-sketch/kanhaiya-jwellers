package com.kanhaiyajewellers.creditmanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kanhaiyajewellers.creditmanager.data.db.entity.Payment

@Dao
interface PaymentDao {

    @Insert
    suspend fun insertPayment(payment: Payment): Long

    @Query("SELECT * FROM payments WHERE transaction_id = :transactionId ORDER BY created_at DESC")
    fun getPaymentsForTransaction(transactionId: Long): LiveData<List<Payment>>
}
