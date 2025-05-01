package edu.cit.sarismart.features.user.tabs.stores.data.models

data class ProductRequest (
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val description: String,
    val reorder_level: Int,
    val barcode: String
)