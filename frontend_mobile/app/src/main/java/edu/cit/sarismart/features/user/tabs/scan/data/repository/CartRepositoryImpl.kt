package edu.cit.sarismart.features.user.tabs.scan.data.repository

import edu.cit.sarismart.core.data.CartManager
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import edu.cit.sarismart.features.user.tabs.scan.data.models.CartItem
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartManager: CartManager,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository
) : CartRepository {
    // Cart operations
    override suspend fun createCart(storeId: Long, name: String): Cart {
        return cartManager.createCart(storeId, name)
    }

    override fun getCartsForStore(storeId: Long): Flow<List<Cart>> {
        return cartManager.getCartsForStore(storeId)
    }

    override suspend fun getCartById(cartId: Long, storeId: Long): Cart? {
        return cartManager.getCartById(cartId, storeId)
    }

    override suspend fun deleteCart(cartId: Long, storeId: Long) {
        cartManager.deleteCart(cartId, storeId)
    }

    // Cart items operations
    override suspend fun addItemToCart(cartId: Long, storeId: Long, productId: Long, quantity: Int) {
        val product = productRepository.getProductById(productId, storeId) ?: return

        // Check if item already exists in cart
        val existingItems = getCartItems(cartId).firstOrNull() ?: emptyList()
        val existingItem = existingItems.find { it.product.id == productId }

        if (existingItem != null) {
            // Update quantity
            updateCartItemQuantity(cartId, existingItem.id, existingItem.quantity + quantity)
        } else {
            // Add new item
            cartManager.addItemToCart(cartId, productId, quantity)

            // Update cart item count
            cartManager.updateCartItemCount(cartId, storeId, existingItems.size + 1)
        }
    }

    override suspend fun updateCartItemQuantity(cartId: Long, itemId: Long, quantity: Int) {
        cartManager.updateCartItemQuantity(cartId, itemId, quantity)
    }

    override suspend fun removeCartItem(cartId: Long, itemId: Long) {
        val cart = getCartItems(cartId).firstOrNull()?.firstOrNull()?.let {
            getCartById(cartId, it.product.store?.id ?: 0)
        }

        cartManager.removeCartItem(cartId, itemId)

        // Update cart item count
        if (cart != null) {
            val items = getCartItems(cartId).firstOrNull() ?: emptyList()
            cartManager.updateCartItemCount(cartId, cart.storeId, items.size)
        }
    }

    override fun getCartItems(cartId: Long): Flow<List<CartItem>> {
        return cartManager.getCartItemIds(cartId).map { itemPairs ->
            itemPairs.mapNotNull { (itemId, productId) ->
                val cart = getCartById(cartId, 0)
                val storeId = cart?.storeId ?: return@mapNotNull null
                val store = storeRepository.getStoreById(storeId)
                val product = productRepository.getProductById(productId, storeId) ?: return@mapNotNull null
                val quantity = cartManager.getCartItemQuantity(cartId, itemId) ?: 1

                CartItem(id = itemId, product = product, quantity = quantity, cartId = cartId)
            }
        }
    }

    override suspend fun checkout(cartId: Long, storeId: Long): Boolean {
        val items = getCartItems(cartId).firstOrNull() ?: return false

        // Update product stock
        items.forEach { cartItem ->
            productRepository.updateProductStock(
                cartItem.product.id,
                storeId,
                cartItem.product.stock - cartItem.quantity
            )
        }

        // Create sale record
        val totalAmount = items.sumOf { it.subtotal }
        val sale = productRepository.createSale(storeId, totalAmount, items)

        // Clear cart
        deleteCart(cartId, storeId)

        return sale != null
    }
}