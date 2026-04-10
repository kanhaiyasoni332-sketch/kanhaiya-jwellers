package com.kanhaiyajewellers.creditmanager.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customer_id")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "customer_id")
    val customerId: Long,

    @ColumnInfo(name = "item_name")
    val itemName: String,

    @ColumnInfo(name = "item_weight")
    val itemWeight: String = "",

    @ColumnInfo(name = "promise_date")
    val promiseDate: Long? = null,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Double,

    @ColumnInfo(name = "paid_amount")
    val paidAmount: Double,

    @ColumnInfo(name = "remaining_amount")
    val remainingAmount: Double,

    val status: String = "PENDING",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
