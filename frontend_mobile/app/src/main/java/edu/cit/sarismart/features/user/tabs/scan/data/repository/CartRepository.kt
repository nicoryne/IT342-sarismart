package edu.cit.sarismart.features.user.tabs.scan.data.repository

import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import edu.cit.sarismart.features.user.tabs.scan.data.models.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    // Cart operations
    suspend fun createCart(storeId: Long, name: String = "Cart ${System.currentTimeMillis().toString().takeLast(5)}"): Cart
    fun getCartsForStore(storeId: Long): Flow<List<Cart>>
    suspend fun getCartById(cartId: Long, storeId: Long): Cart?
    suspend fun deleteCart(cartId: Long, storeId: Long)

    // Cart items operations
    suspend fun addItemToCart(cartId: Long, storeId: Long, productId: Long, quantity: Int = 1)
    suspend fun updateCartItemQuantity(cartId: Long, itemId: Long, quantity: Int)
    suspend fun removeCartItem(cartId: Long, itemId: Long)
    fun getCartItems(cartId: Long): Flow<List<CartItem>>
    suspend fun checkout(cartId: Long, storeId: Long): Boolean
}