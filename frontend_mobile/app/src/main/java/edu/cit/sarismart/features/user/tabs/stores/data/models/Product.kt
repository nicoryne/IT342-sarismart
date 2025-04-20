package edu.cit.sarismart.features.user.tabs.stores.data.models

data class Product (
    val id: Long,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Integer,
    val description: String,
    val reorderLevel: Integer,
    val store: Store,
)