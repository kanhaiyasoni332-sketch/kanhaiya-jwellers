package com.kanhaiyajewellers.creditmanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kanhaiyajewellers.creditmanager.data.db.entity.Payment
import com.kanhaiyajewellers.creditmanager.data.model.PaymentLedgerItem

@Dao
interface PaymentDao {

    @Insert
    suspend fun insertPayment(payment: Payment): Long

    @Query("SELECT * FROM payments WHERE transaction_id = :transactionId ORDER BY created_at DESC")
    fun getPaymentsForTransaction(transactionId: Long): LiveData<List<Payment>>

    @Query("""
        SELECT p.transaction_id AS transactionId,
               p.amount AS amount,
               p.created_at AS createdAt
        FROM payments p
        INNER JOIN transactions t ON p.transaction_id = t.id
        WHERE t.customer_id = :customerId
        ORDER BY p.created_at ASC
    """)
    suspend fun getPaymentsForCustomer(customerId: Long): List<PaymentLedgerItem>
}
