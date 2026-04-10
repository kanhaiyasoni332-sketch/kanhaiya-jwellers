package com.kanhaiyajewellers.creditmanager.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.kanhaiyajewellers.creditmanager.`data`.db.entity.Customer
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CustomerDao_Impl(
  __db: RoomDatabase,
) : CustomerDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCustomer: EntityInsertAdapter<Customer>
  init {
    this.__db = __db
    this.__insertAdapterOfCustomer = object : EntityInsertAdapter<Customer>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `customers` (`id`,`name`,`phone`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Customer) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.phone)
      }
    }
  }

  public override suspend fun insert(customer: Customer): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfCustomer.insertAndReturnId(_connection, customer)
    _result
  }

  public override suspend fun getByPhone(phone: String): Customer? {
    val _sql: String = "SELECT * FROM customers WHERE phone = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, phone)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _result: Customer?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          _result = Customer(_tmpId,_tmpName,_tmpPhone)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): Customer? {
    val _sql: String = "SELECT * FROM customers WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _result: Customer?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          _result = Customer(_tmpId,_tmpName,_tmpPhone)
        } else {
          _result = null
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
