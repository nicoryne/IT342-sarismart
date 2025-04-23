package edu.cit.sarismart.features.user.tabs.stores.data.models

data class Product (
    val id: Long,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val description: String,
    val reorderLevel: Int,
    val store: Store,
)