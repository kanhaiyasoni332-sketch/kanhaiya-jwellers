package com.kanhaiyajewellers.creditmanager.data.model

data class PaymentLedgerItem(
    val transactionId: Long,
    val amount: Double,
    val createdAt: Long
)
