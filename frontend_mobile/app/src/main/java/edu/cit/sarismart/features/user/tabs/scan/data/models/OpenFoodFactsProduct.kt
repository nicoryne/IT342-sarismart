package edu.cit.sarismart.features.user.tabs.scan.data.models

data class OpenFoodFactsProduct(
    val product_name: String?,
    val brands: String?,
    val image_url: String?,
    val quantity: String?,
    val nutriments: Map<String, Any>?
)