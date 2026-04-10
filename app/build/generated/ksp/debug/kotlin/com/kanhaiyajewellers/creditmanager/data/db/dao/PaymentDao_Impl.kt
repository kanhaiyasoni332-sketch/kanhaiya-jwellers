package com.kanhaiyajewellers.creditmanager.`data`.db.dao

import androidx.lifecycle.LiveData
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.kanhaiyajewellers.creditmanager.`data`.db.entity.Payment
import com.kanhaiyajewellers.creditmanager.`data`.model.PaymentLedgerItem
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
public class PaymentDao_Impl(
  __db: RoomDatabase,
) : PaymentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPayment: EntityInsertAdapter<Payment>
  init {
    this.__db = __db
    this.__insertAdapterOfPayment = object : EntityInsertAdapter<Payment>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `payments` (`id`,`transaction_id`,`amount`,`created_at`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Payment) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.transactionId)
        statement.bindDouble(3, entity.amount)
        statement.bindLong(4, entity.createdAt)
      }
    }
  }

  public override suspend fun insertPayment(payment: Payment): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfPayment.insertAndReturnId(_connection, payment)
    _result
  }

  public override fun getPaymentsForTransaction(transactionId: Long): LiveData<List<Payment>> {
    val _sql: String = "SELECT * FROM payments WHERE transaction_id = ? ORDER BY created_at DESC"
    return __db.invalidationTracker.createLiveData(arrayOf("payments"), false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, transactionId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_stmt, "transaction_id")
        val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<Payment> = mutableListOf()
        while (_stmt.step()) {
          val _item: Payment
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_cursorIndexOfAmount)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item = Payment(_tmpId,_tmpTransactionId,_tmpAmount,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPaymentsForCustomer(customerId: Long): List<PaymentLedgerItem> {
    val _sql: String = """
        |
        |        SELECT p.transaction_id AS transactionId,
        |               p.amount AS amount,
        |               p.created_at AS createdAt
        |        FROM payments p
        |        INNER JOIN transactions t ON p.transaction_id = t.id
        |        WHERE t.customer_id = ?
        |        ORDER BY p.created_at ASC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, customerId)
        val _cursorIndexOfTransactionId: Int = 0
        val _cursorIndexOfAmount: Int = 1
        val _cursorIndexOfCreatedAt: Int = 2
        val _result: MutableList<PaymentLedgerItem> = mutableListOf()
        while (_stmt.step()) {
          val _item: PaymentLedgerItem
          val _tmpTransactionId: Long
          _tmpTransactionId = _stmt.getLong(_cursorIndexOfTransactionId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_cursorIndexOfAmount)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_cursorIndexOfCreatedAt)
          _item = PaymentLedgerItem(_tmpTransactionId,_tmpAmount,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
