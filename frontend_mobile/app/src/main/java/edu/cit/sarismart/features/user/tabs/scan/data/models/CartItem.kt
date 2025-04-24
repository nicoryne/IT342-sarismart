package edu.cit.sarismart.features.user.tabs.scan.data.models

import edu.cit.sarismart.features.user.tabs.stores.data.models.Product

data class CartItem(
    val id: Long = System.currentTimeMillis(),
    val product: Product,
    val quantity: Int = 1,
    val cartId: Long
) {
    val subtotal: Double
        get() = product.price * quantity
}