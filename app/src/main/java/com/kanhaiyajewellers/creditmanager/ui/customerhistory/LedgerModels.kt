package com.kanhaiyajewellers.creditmanager.ui.customerhistory

data class LedgerEntry(
    val dateMillis: Long,
    val type: String,
    val amount: Double
)

data class LedgerExportData(
    val customerName: String,
    val phone: String,
    val entries: List<LedgerEntry>,
    val totalCredit: Double,
    val totalPaid: Double,
    val remainingBalance: Double
)
