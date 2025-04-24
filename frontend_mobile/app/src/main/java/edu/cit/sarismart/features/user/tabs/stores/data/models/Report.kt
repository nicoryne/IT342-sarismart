package edu.cit.sarismart.features.user.tabs.stores.data.models

data class Report (
    val reportType: String,
    val period: String,
    val totalSales: Double,
    val totalTransactions: Int
)