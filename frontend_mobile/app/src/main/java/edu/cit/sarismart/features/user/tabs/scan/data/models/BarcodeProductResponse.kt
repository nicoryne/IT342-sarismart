package edu.cit.sarismart.features.user.tabs.scan.data.models

data class BarcodeProductResponse(
    val name: String,
    val brand: String?,
    val description: String?,
    val imageUrl: String?,
    val price: Double?,
    val barcode: String
)