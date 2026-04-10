package com.kanhaiyajewellers.creditmanager.data.model

data class CustomerWithTotals(
    val customerId: Long,
    val customerName: String,
    val phone: String,
    val totalPending: Double,
    val totalPaid: Double,
    val nextPromiseDate: Long?
)
