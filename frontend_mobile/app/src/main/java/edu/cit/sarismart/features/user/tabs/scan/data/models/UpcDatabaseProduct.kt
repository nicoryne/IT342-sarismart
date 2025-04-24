package edu.cit.sarismart.features.user.tabs.scan.data.models

data class UpcDatabaseProduct (
    val title: String?,
    val brand: String?,
    val description: String?,
    val images: List<String>?,
    val highest_recorded_price: Double?,
    val category: String?
)