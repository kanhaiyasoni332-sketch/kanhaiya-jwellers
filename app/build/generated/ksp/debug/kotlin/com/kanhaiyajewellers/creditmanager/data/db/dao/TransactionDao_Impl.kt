package com.kanhaiyajewellers.creditmanager.`data`.db.dao

import androidx.lifecycle.LiveData
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.kanhaiyajewellers.creditmanager.`data`.db.entity.Transaction
import com.kanhaiyajewellers.creditmanager.`data`.model.CustomerWithTotals
import com.kanhaiyajewellers.creditmanager.`data`.model.ReminderPayload
import com.kanhaiyajewellers.creditmanager.`data`.model.TransactionWithCustomer
import com.kanhaiyajewellers.creditmanager.`data`.model.UpcomingPaymentItem
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TransactionDao_Impl(
  __db: RoomDatabase,
) : TransactionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTransaction: EntityInsertAdapter<Transaction>
  init {
    this.__db = __db
    this.__insertAdapterOfTransaction = object : EntityInsertAdapter<Transaction>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `transactions` (`id`,`customer_id`,`item_name`,`item_weight`,`promise_date`,`total_amount`,`paid_amount`,`remaining_amount`,`status`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.customerId)
        statement.bindText(3, entity.itemName)
        statement.bindText(4, entity.itemWeight)
        val _tmpPromiseDate: Long? = entity.promiseDate
        if (_tmpPromiseDate == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpPromiseDate)
        }
        statement.bindDouble(6, entity.totalAmount)
        statement.bindDouble(7, entity.paidAmount)
        statement.bindDouble(8, entity.remainingAmount)
        statement.bindText(9, entity.status)
        statement.bindLong(10, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(transaction: Transaction): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfTransaction.insertAndReturnId(_connection, transaction)
    _result
  }

  public override fun getRecentTransactions(): LiveData<List<TransactionWithCustomer>> {
    val _sql: String = """
        |
        |        SELECT t.id           AS transactionId,
        |               c.name         AS customerName,
        |               c.phone        AS phone,
        |               t.item_name    AS itemName,
        |               t.item_weight  AS itemWeight,
        |               t.promise_date AS promiseDate,
        |               t.total_amount AS totalAmount,
        |               t.paid_amount  AS paidAmount,
        |               t.remaining_amount AS remainingAmount,
        |               t.status       AS status,
        |               t.created_at   AS createdAt
        |        FROM   transactions t
        |        INNER JOIN customers c ON t.customer_id = c.id
        |        ORDER  BY t.created_at DESC
        |        LIMIT  50
        |    
        """.trimMargin()
    return __db.invalidationTracker.createLiveData(arrayOf("transactions", "customers"), false) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfTransactionId: Int = 0
        val _cursorIndexOfCustomerName: Int = 1
        val _cursorIndexOfPhone: Int = 2
        val _cursorIndexOfItemName: Int = 3
        val _cursorIndexOfItemWeight: Int = 4
        val _cursorIndexOfPromiseDate: Int = 5
        val _cursorIndexOfTotalAmount: Int = 6
        val _cursorIndexOfPaidAmount: Int = 7
        val _cursorIndexOfRemainingAmount: Int = 8
        val _cursorIndexOfStatus: Int = 9
        val _cursorIndexOfCreatedAt: Int = 10
        val _result: MutableList<TransactionWithCustomer> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionWithCustomer
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpItemName: String
          _tmpItemName = _stmt.getText(_cursorIndexOfItemName)
          val _tmpItemWeight: String
          _tmpItemWeight = _stmt.getText(_cursorIndexOfItemWeight)
          val _tmpPromiseDate: Long?
          if (_stmt.isNull(_cursorIndexOfPromiseDate)) {
            _tmpPromiseDate = null
          } else {
            _tmpPromiseDate = _stmt.getLong(_cursorIndexOfPromiseDate)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_cursorIndexOfTotalAmount)
          val _tmpPaidAmount: Double
          _tmpPaidAmount = _stmt.getDouble(_cursorIndexOfPaidAmount)
          val _tmpRemainingAmount: Double
          _tmpRemainingAmount = _stmt.getDouble(_cursorIndexOfRemainingAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_cursorIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item =
              TransactionWithCustomer(_tmpTransactionId,_tmpCustomerName,_tmpPhone,_tmpItemName,_tmpItemWeight,_tmpPromiseDate,_tmpTotalAmount,_tmpPaidAmount,_tmpRemainingAmount,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchTransactions(query: String): LiveData<List<TransactionWithCustomer>> {
    val _sql: String = """
        |
        |        SELECT t.id           AS transactionId,
        |               c.name         AS customerName,
        |               c.phone        AS phone,
        |               t.item_name    AS itemName,
        |               t.item_weight  AS itemWeight,
        |               t.promise_date AS promiseDate,
        |               t.total_amount AS totalAmount,
        |               t.paid_amount  AS paidAmount,
        |               t.remaining_amount AS remainingAmount,
        |               t.status       AS status,
        |               t.created_at   AS createdAt
        |        FROM   transactions t
        |        INNER JOIN customers c ON t.customer_id = c.id
        |        WHERE  c.phone LIKE '%' || ? || '%'
        |            OR c.name  LIKE '%' || ? || '%'
        |        ORDER  BY t.created_at DESC
        |    
        """.trimMargin()
    return __db.invalidationTracker.createLiveData(arrayOf("transactions", "customers"), false) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        val _cursorIndexOfTransactionId: Int = 0
        val _cursorIndexOfCustomerName: Int = 1
        val _cursorIndexOfPhone: Int = 2
        val _cursorIndexOfItemName: Int = 3
        val _cursorIndexOfItemWeight: Int = 4
        val _cursorIndexOfPromiseDate: Int = 5
        val _cursorIndexOfTotalAmount: Int = 6
        val _cursorIndexOfPaidAmount: Int = 7
        val _cursorIndexOfRemainingAmount: Int = 8
        val _cursorIndexOfStatus: Int = 9
        val _cursorIndexOfCreatedAt: Int = 10
        val _result: MutableList<TransactionWithCustomer> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionWithCustomer
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpItemName: String
          _tmpItemName = _stmt.getText(_cursorIndexOfItemName)
          val _tmpItemWeight: String
          _tmpItemWeight = _stmt.getText(_cursorIndexOfItemWeight)
          val _tmpPromiseDate: Long?
          if (_stmt.isNull(_cursorIndexOfPromiseDate)) {
            _tmpPromiseDate = null
          } else {
            _tmpPromiseDate = _stmt.getLong(_cursorIndexOfPromiseDate)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_cursorIndexOfTotalAmount)
          val _tmpPaidAmount: Double
          _tmpPaidAmount = _stmt.getDouble(_cursorIndexOfPaidAmount)
          val _tmpRemainingAmount: Double
          _tmpRemainingAmount = _stmt.getDouble(_cursorIndexOfRemainingAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_cursorIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item =
              TransactionWithCustomer(_tmpTransactionId,_tmpCustomerName,_tmpPhone,_tmpItemName,_tmpItemWeight,_tmpPromiseDate,_tmpTotalAmount,_tmpPaidAmount,_tmpRemainingAmount,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransactionById(id: Long): LiveData<TransactionWithCustomer> {
    val _sql: String = """
        |
        |        SELECT t.id           AS transactionId,
        |               c.name         AS customerName,
        |               c.phone        AS phone,
        |               t.item_name    AS itemName,
        |               t.item_weight  AS itemWeight,
        |               t.promise_date AS promiseDate,
        |               t.total_amount AS totalAmount,
        |               t.paid_amount  AS paidAmount,
        |               t.remaining_amount AS remainingAmount,
        |               t.status       AS status,
        |               t.created_at   AS createdAt
        |        FROM   transactions t
        |        INNER JOIN customers c ON t.customer_id = c.id
        |        WHERE  t.id = ?
        |    
        """.trimMargin()
    return __db.invalidationTracker.createLiveData(arrayOf("transactions", "customers"), false) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _cursorIndexOfTransactionId: Int = 0
        val _cursorIndexOfCustomerName: Int = 1
        val _cursorIndexOfPhone: Int = 2
        val _cursorIndexOfItemName: Int = 3
        val _cursorIndexOfItemWeight: Int = 4
        val _cursorIndexOfPromiseDate: Int = 5
        val _cursorIndexOfTotalAmount: Int = 6
        val _cursorIndexOfPaidAmount: Int = 7
        val _cursorIndexOfRemainingAmount: Int = 8
        val _cursorIndexOfStatus: Int = 9
        val _cursorIndexOfCreatedAt: Int = 10
        val _result: TransactionWithCustomer?
        if (_stmt.step()) {
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpItemName: String
          _tmpItemName = _stmt.getText(_cursorIndexOfItemName)
          val _tmpItemWeight: String
          _tmpItemWeight = _stmt.getText(_cursorIndexOfItemWeight)
          val _tmpPromiseDate: Long?
          if (_stmt.isNull(_cursorIndexOfPromiseDate)) {
            _tmpPromiseDate = null
          } else {
            _tmpPromiseDate = _stmt.getLong(_cursorIndexOfPromiseDate)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_cursorIndexOfTotalAmount)
          val _tmpPaidAmount: Double
          _tmpPaidAmount = _stmt.getDouble(_cursorIndexOfPaidAmount)
          val _tmpRemainingAmount: Double
          _tmpRemainingAmount = _stmt.getDouble(_cursorIndexOfRemainingAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_cursorIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _result =
              TransactionWithCustomer(_tmpTransactionId,_tmpCustomerName,_tmpPhone,_tmpItemName,_tmpItemWeight,_tmpPromiseDate,_tmpTotalAmount,_tmpPaidAmount,_tmpRemainingAmount,_tmpStatus,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRecentCustomers(): LiveData<List<CustomerWithTotals>> {
    val _sql: String = """
        |
        |        SELECT c.id AS customerId,
        |               c.name AS customerName,
        |               c.phone AS phone,
        |               SUM(CASE WHEN t.status = 'PENDING' THEN t.remaining_amount ELSE 0 END) AS totalPending,
        |               SUM(t.paid_amount) AS totalPaid,
        |               MIN(CASE WHEN t.status = 'PENDING' AND t.promise_date IS NOT NULL THEN t.promise_date END) AS nextPromiseDate
        |        FROM customers c
        |        LEFT JOIN transactions t ON c.id = t.customer_id
        |        GROUP BY c.id
        |        ORDER BY MAX(t.created_at) DESC
        |        LIMIT 50
        |    
        """.trimMargin()
    return __db.invalidationTracker.createLiveData(arrayOf("customers", "transactions"), false) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfCustomerId: Int = 0
        val _cursorIndexOfCustomerName: Int = 1
        val _cursorIndexOfPhone: Int = 2
        val _cursorIndexOfTotalPending: Int = 3
        val _cursorIndexOfTotalPaid: Int = 4
        val _cursorIndexOfNextPromiseDate: Int = 5
        val _result: MutableList<CustomerWithTotals> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomerWithTotals
          val _tmpCustomerId: Long
          _tmpCustomerId = _stmt.getLong(_cursorIndexOfCustomerId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpTotalPending: Double
          _tmpTotalPending = _stmt.getDouble(_cursorIndexOfTotalPending)
          val _tmpTotalPaid: Double
          _tmpTotalPaid = _stmt.getDouble(_cursorIndexOfTotalPaid)
          val _tmpNextPromiseDate: Long?
          if (_stmt.isNull(_cursorIndexOfNextPromiseDate)) {
            _tmpNextPromiseDate = null
          } else {
            _tmpNextPromiseDate = _stmt.getLong(_cursorIndexOfNextPromiseDate)
          }
          _item =
              CustomerWithTotals(_tmpCustomerId,_tmpCustomerName,_tmpPhone,_tmpTotalPending,_tmpTotalPaid,_tmpNextPromiseDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchCustomers(query: String): LiveData<List<CustomerWithTotals>> {
    val _sql: String = """
        |
        |        SELECT c.id AS customerId,
        |               c.name AS customerName,
        |               c.phone AS phone,
        |               SUM(CASE WHEN t.status = 'PENDING' THEN t.remaining_amount ELSE 0 END) AS totalPending,
        |               SUM(t.paid_amount) AS totalPaid,
        |               MIN(CASE WHEN t.status = 'PENDING' AND t.promise_date IS NOT NULL THEN t.promise_date END) AS nextPromiseDate
        |        FROM customers c
        |        LEFT JOIN transactions t ON c.id = t.customer_id
        |        WHERE c.name LIKE '%' || ? || '%' OR c.phone LIKE '%' || ? || '%'
        |        GROUP BY c.id
        |        ORDER BY c.name ASC
        |    
        """.trimMargin()
    return __db.invalidationTracker.createLiveData(arrayOf("customers", "transactions"), false) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        val _cursorIndexOfCustomerId: Int = 0
        val _cursorIndexOfCustomerName: Int = 1
        val _cursorIndexOfPhone: Int = 2
        val _cursorIndexOfTotalPending: Int = 3
        val _cursorIndexOfTotalPaid: Int = 4
        val _cursorIndexOfNextPromiseDate: Int = 5
        val _result: MutableList<CustomerWithTotals> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomerWithTotals
          val _tmpCustomerId: Long
          _tmpCustomerId = _stmt.getLong(_cursorIndexOfCustomerId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpTotalPending: Double
          _tmpTotalPending = _stmt.getDouble(_cursorIndexOfTotalPending)
          val _tmpTotalPaid: Double
          _tmpTotalPaid = _stmt.getDouble(_cursorIndexOfTotalPaid)
          val _tmpNextPromiseDate: Long?
          if (_stmt.isNull(_cursorIndexOfNextPromiseDate)) {
            _tmpNextPromiseDate = null
          } else {
            _tmpNextPromiseDate = _stmt.getLong(_cursorIndexOfNextPromiseDate)
          }
          _item =
              CustomerWithTotals(_tmpCustomerId,_tmpCustomerName,_tmpPhone,_tmpTotalPending,_tmpTotalPaid,_tmpNextPromiseDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransactionsByCustomerId(customerId: Long):
      LiveData<List<TransactionWithCustomer>> {
    val _sql: String = """
        |
        |        SELECT t.id           AS transactionId,
        |               c.name         AS customerName,
        |               c.phone        AS phone,
        |               t.item_name    AS itemName,
        |               t.item_weight  AS itemWeight,
        |               t.promise_date AS promiseDate,
        |               t.total_amount AS totalAmount,
        |               t.paid_amount  AS paidAmount,
        |               t.remaining_amount AS remainingAmount,
        |               t.status       AS status,
        |               t.created_at   AS createdAt
        |        FROM   transactions t
        |        INNER JOIN customers c ON t.customer_id = c.id
        |        WHERE  t.customer_id = ?
        |        ORDER  BY t.created_at DESC
        |    
        """.trimMargin()
    return __db.invalidationTracker.createLiveData(arrayOf("transactions", "customers"), false) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, customerId)
        val _cursorIndexOfTransactionId: Int = 0
        val _cursorIndexOfCustomerName: Int = 1
        val _cursorIndexOfPhone: Int = 2
        val _cursorIndexOfItemName: Int = 3
        val _cursorIndexOfItemWeight: Int = 4
        val _cursorIndexOfPromiseDate: Int = 5
        val _cursorIndexOfTotalAmount: Int = 6
        val _cursorIndexOfPaidAmount: Int = 7
        val _cursorIndexOfRemainingAmount: Int = 8
        val _cursorIndexOfStatus: Int = 9
        val _cursorIndexOfCreatedAt: Int = 10
        val _result: MutableList<TransactionWithCustomer> = mutableListOf()
        while (_stmt.step()) {
          val _item: TransactionWithCustomer
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpItemName: String
          _tmpItemName = _stmt.getText(_cursorIndexOfItemName)
          val _tmpItemWeight: String
          _tmpItemWeight = _stmt.getText(_cursorIndexOfItemWeight)
          val _tmpPromiseDate: Long?
          if (_stmt.isNull(_cursorIndexOfPromiseDate)) {
            _tmpPromiseDate = null
          } else {
            _tmpPromiseDate = _stmt.getLong(_cursorIndexOfPromiseDate)
          }
          val _tmpTotalAmount: Double
          _tmpTotalAmount = _stmt.getDouble(_cursorIndexOfTotalAmount)
          val _tmpPaidAmount: Double
          _tmpPaidAmount = _stmt.getDouble(_cursorIndexOfPaidAmount)
          val _tmpRemainingAmount: Double
          _tmpRemainingAmount = _stmt.getDouble(_cursorIndexOfRemainingAmount)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_cursorIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item =
              TransactionWithCustomer(_tmpTransactionId,_tmpCustomerName,_tmpPhone,_tmpItemName,_tmpItemWeight,_tmpPromiseDate,_tmpTotalAmount,_tmpPaidAmount,_tmpRemainingAmount,_tmpStatus,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUpcomingPayments(startOfToday: Long): LiveData<List<UpcomingPaymentItem>> {
    val _sql: String = """
        |
        |        SELECT t.id AS transactionId,
        |               t.customer_id AS customerId,
        |               c.name AS customerName,
        |               c.phone AS phone,
        |               t.remaining_amount AS pendingAmount,
        |               t.promise_date AS promiseDate
        |        FROM transactions t
        |        INNER JOIN customers c ON t.customer_id = c.id
        |        WHERE t.status = 'PENDING'
        |          AND t.promise_date IS NOT NULL
        |          AND t.promise_date >= ?
        |        ORDER BY t.promise_date ASC
        |        LIMIT 5
        |    
        """.trimMargin()
    return __db.invalidationTracker.createLiveData(arrayOf("transactions", "customers"), false) {
        _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startOfToday)
        val _cursorIndexOfTransactionId: Int = 0
        val _cursorIndexOfCustomerId: Int = 1
        val _cursorIndexOfCustomerName: Int = 2
        val _cursorIndexOfPhone: Int = 3
        val _cursorIndexOfPendingAmount: Int = 4
        val _cursorIndexOfPromiseDate: Int = 5
        val _result: MutableList<UpcomingPaymentItem> = mutableListOf()
        while (_stmt.step()) {
          val _item: UpcomingPaymentItem
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpCustomerId: Long
          _tmpCustomerId = _stmt.getLong(_cursorIndexOfCustomerId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpPendingAmount: Double
          _tmpPendingAmount = _stmt.getDouble(_cursorIndexOfPendingAmount)
          val _tmpPromiseDate: Long
          _tmpPromiseDate = _stmt.getLong(_cursorIndexOfPromiseDate)
          _item =
              UpcomingPaymentItem(_tmpTransactionId,_tmpCustomerId,_tmpCustomerName,_tmpPhone,_tmpPendingAmount,_tmpPromiseDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getReminderPayload(transactionId: Long): ReminderPayload? {
    val _sql: String = """
        |
        |        SELECT t.id AS transactionId,
        |               t.customer_id AS customerId,
        |               c.name AS customerName,
        |               c.phone AS phone,
        |               t.remaining_amount AS pendingAmount
        |        FROM transactions t
        |        INNER JOIN customers c ON t.customer_id = c.id
        |        WHERE t.id = ?
        |        LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, transactionId)
        val _cursorIndexOfTransactionId: Int = 0
        val _cursorIndexOfCustomerId: Int = 1
        val _cursorIndexOfCustomerName: Int = 2
        val _cursorIndexOfPhone: Int = 3
        val _cursorIndexOfPendingAmount: Int = 4
        val _result: ReminderPayload?
        if (_stmt.step()) {
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpCustomerId: Long
          _tmpCustomerId = _stmt.getLong(_cursorIndexOfCustomerId)
          val _tmpCustomerName: String
          _tmpCustomerName = _stmt.getText(_cursorIndexOfCustomerName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpPendingAmount: Double
          _tmpPendingAmount = _stmt.getDouble(_cursorIndexOfPendingAmount)
          _result =
              ReminderPayload(_tmpTransactionId,_tmpCustomerId,_tmpCustomerName,_tmpPhone,_tmpPendingAmount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTotalPendingAmount(): LiveData<Double> {
    val _sql: String =
        "SELECT COALESCE(SUM(remaining_amount), 0.0) FROM transactions WHERE status = 'PENDING'"
    return __db.invalidationTracker.createLiveData(arrayOf("transactions"), false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPendingCustomersCount(): LiveData<Int> {
    val _sql: String =
        "SELECT COUNT(DISTINCT customer_id) FROM transactions WHERE status = 'PENDING'"
    return __db.invalidationTracker.createLiveData(arrayOf("transactions"), false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCompletedTransactionsCount(): LiveData<Int> {
    val _sql: String = "SELECT COUNT(*) FROM transactions WHERE status = 'COMPLETED'"
    return __db.invalidationTracker.createLiveData(arrayOf("transactions"), false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updatePayment(
    id: Long,
    paidAmount: Double,
    remainingAmount: Double,
    status: String,
  ) {
    val _sql: String = """
        |
        |        UPDATE transactions
        |        SET paid_amount = ?,
        |            remaining_amount = ?,
        |            status = ?
        |        WHERE id = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, paidAmount)
        _argIndex = 2
        _stmt.bindDouble(_argIndex, remainingAmount)
        _argIndex = 3
        _stmt.bindText(_argIndex, status)
        _argIndex = 4
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
