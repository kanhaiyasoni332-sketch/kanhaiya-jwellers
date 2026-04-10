package com.kanhaiyajewellers.creditmanager.data.model

/**
 * Flat projection for Room JOIN queries between transactions and customers.
 * Used throughout the UI to avoid dealing with nested relations.
 */
data class TransactionWithCustomer(
    val transactionId: Long,
    val customerName: String,
    val phone: String,
    val itemName: String,
    val itemWeight: String,
    val promiseDate: Long?,
    val totalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double,
    val status: String,
    val createdAt: Long
)
