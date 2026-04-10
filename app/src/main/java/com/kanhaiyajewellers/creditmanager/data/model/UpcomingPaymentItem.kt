package com.kanhaiyajewellers.creditmanager.data.model

data class UpcomingPaymentItem(
    val transactionId: Long,
    val customerId: Long,
    val customerName: String,
    val phone: String,
    val pendingAmount: Double,
    val promiseDate: Long
)
