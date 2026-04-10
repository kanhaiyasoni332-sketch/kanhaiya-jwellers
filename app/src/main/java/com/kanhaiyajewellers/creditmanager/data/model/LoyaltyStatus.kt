package com.kanhaiyajewellers.creditmanager.data.model

data class LoyaltyStatus(
    val completedPayments: Int,
    val overduePendingCount: Int,
    val totalTransactionValue: Double,
    val isLoyalCustomer: Boolean
)
