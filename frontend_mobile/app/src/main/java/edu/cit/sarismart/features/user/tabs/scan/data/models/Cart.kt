package edu.cit.sarismart.features.user.tabs.scan.data.models

data class Cart(
    val id: Long = System.currentTimeMillis(),
    val storeId: Long,
    val name: String = "Cart ${System.currentTimeMillis()}",
    val itemCount: Int = 0
)
