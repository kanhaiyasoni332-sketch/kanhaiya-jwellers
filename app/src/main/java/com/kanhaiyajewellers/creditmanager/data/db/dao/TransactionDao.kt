package com.kanhaiyajewellers.creditmanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kanhaiyajewellers.creditmanager.data.db.entity.Transaction
import com.kanhaiyajewellers.creditmanager.data.model.ReminderPayload
import com.kanhaiyajewellers.creditmanager.data.model.TransactionWithCustomer
import com.kanhaiyajewellers.creditmanager.data.model.UpcomingPaymentItem

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Query("""
        UPDATE transactions
        SET paid_amount = :paidAmount,
            remaining_amount = :remainingAmount,
            status = :status
        WHERE id = :id
    """)
    suspend fun updatePayment(id: Long, paidAmount: Double, remainingAmount: Double, status: String)

    // ─── Joined queries ────────────────────────────────────────────────────────

    @Query("""
        SELECT t.id           AS transactionId,
               c.name         AS customerName,
               c.phone        AS phone,
               t.item_name    AS itemName,
               t.item_weight  AS itemWeight,
               t.promise_date AS promiseDate,
               t.total_amount AS totalAmount,
               t.paid_amount  AS paidAmount,
               t.remaining_amount AS remainingAmount,
               t.status       AS status,
               t.created_at   AS createdAt
        FROM   transactions t
        INNER JOIN customers c ON t.customer_id = c.id
        ORDER  BY t.created_at DESC
        LIMIT  50
    """)
    fun getRecentTransactions(): LiveData<List<TransactionWithCustomer>>

    @Query("""
        SELECT t.id           AS transactionId,
               c.name         AS customerName,
               c.phone        AS phone,
               t.item_name    AS itemName,
               t.item_weight  AS itemWeight,
               t.promise_date AS promiseDate,
               t.total_amount AS totalAmount,
               t.paid_amount  AS paidAmount,
               t.remaining_amount AS remainingAmount,
               t.status       AS status,
               t.created_at   AS createdAt
        FROM   transactions t
        INNER JOIN customers c ON t.customer_id = c.id
        WHERE  c.phone LIKE '%' || :query || '%'
            OR c.name  LIKE '%' || :query || '%'
        ORDER  BY t.created_at DESC
    """)
    fun searchTransactions(query: String): LiveData<List<TransactionWithCustomer>>

    @Query("""
        SELECT t.id           AS transactionId,
               c.name         AS customerName,
               c.phone        AS phone,
               t.item_name    AS itemName,
               t.item_weight  AS itemWeight,
               t.promise_date AS promiseDate,
               t.total_amount AS totalAmount,
               t.paid_amount  AS paidAmount,
               t.remaining_amount AS remainingAmount,
               t.status       AS status,
               t.created_at   AS createdAt
        FROM   transactions t
        INNER JOIN customers c ON t.customer_id = c.id
        WHERE  t.id = :id
    """)
    fun getTransactionById(id: Long): LiveData<TransactionWithCustomer>

    @Query("""
        SELECT c.id AS customerId,
               c.name AS customerName,
               c.phone AS phone,
               SUM(CASE WHEN t.status = 'PENDING' THEN t.remaining_amount ELSE 0 END) AS totalPending,
               SUM(t.paid_amount) AS totalPaid,
               MIN(CASE WHEN t.status = 'PENDING' AND t.promise_date IS NOT NULL THEN t.promise_date END) AS nextPromiseDate
        FROM customers c
        LEFT JOIN transactions t ON c.id = t.customer_id
        GROUP BY c.id
        ORDER BY MAX(t.created_at) DESC
        LIMIT 50
    """)
    fun getRecentCustomers(): LiveData<List<com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals>>

    @Query("""
        SELECT c.id AS customerId,
               c.name AS customerName,
               c.phone AS phone,
               SUM(CASE WHEN t.status = 'PENDING' THEN t.remaining_amount ELSE 0 END) AS totalPending,
               SUM(t.paid_amount) AS totalPaid,
               MIN(CASE WHEN t.status = 'PENDING' AND t.promise_date IS NOT NULL THEN t.promise_date END) AS nextPromiseDate
        FROM customers c
        LEFT JOIN transactions t ON c.id = t.customer_id
        WHERE c.name LIKE '%' || :query || '%' OR c.phone LIKE '%' || :query || '%'
        GROUP BY c.id
        ORDER BY c.name ASC
    """)
    fun searchCustomers(query: String): LiveData<List<com.kanhaiyajewellers.creditmanager.data.model.CustomerWithTotals>>

    @Query("""
        SELECT t.id           AS transactionId,
               c.name         AS customerName,
               c.phone        AS phone,
               t.item_name    AS itemName,
               t.item_weight  AS itemWeight,
               t.promise_date AS promiseDate,
               t.total_amount AS totalAmount,
               t.paid_amount  AS paidAmount,
               t.remaining_amount AS remainingAmount,
               t.status       AS status,
               t.created_at   AS createdAt
        FROM   transactions t
        INNER JOIN customers c ON t.customer_id = c.id
        WHERE  t.customer_id = :customerId
        ORDER  BY t.created_at DESC
    """)
    fun getTransactionsByCustomerId(customerId: Long): LiveData<List<TransactionWithCustomer>>

    @Query("""
        SELECT t.id AS transactionId,
               t.customer_id AS customerId,
               c.name AS customerName,
               c.phone AS phone,
               t.remaining_amount AS pendingAmount,
               t.promise_date AS promiseDate
        FROM transactions t
        INNER JOIN customers c ON t.customer_id = c.id
        WHERE t.status = 'PENDING'
          AND t.promise_date IS NOT NULL
          AND t.promise_date >= :startOfToday
        ORDER BY t.promise_date ASC
        LIMIT 5
    """)
    fun getUpcomingPayments(startOfToday: Long): LiveData<List<UpcomingPaymentItem>>

    @Query("""
        SELECT t.id AS transactionId,
               t.customer_id AS customerId,
               c.name AS customerName,
               c.phone AS phone,
               t.remaining_amount AS pendingAmount
        FROM transactions t
        INNER JOIN customers c ON t.customer_id = c.id
        WHERE t.id = :transactionId
        LIMIT 1
    """)
    suspend fun getReminderPayload(transactionId: Long): ReminderPayload?

    // ─── Aggregate stats ───────────────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(remaining_amount), 0.0) FROM transactions WHERE status = 'PENDING'")
    fun getTotalPendingAmount(): LiveData<Double>

    @Query("SELECT COUNT(DISTINCT customer_id) FROM transactions WHERE status = 'PENDING'")
    fun getPendingCustomersCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM transactions WHERE status = 'COMPLETED'")
    fun getCompletedTransactionsCount(): LiveData<Int>
}
