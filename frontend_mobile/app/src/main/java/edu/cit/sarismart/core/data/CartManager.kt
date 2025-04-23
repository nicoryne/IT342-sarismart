package edu.cit.sarismart.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.cartDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_carts")
    private val Context.cartItemsDataStore: DataStore<Preferences> by preferencesDataStore(name = "cart_items")

    private val cartDataStore = context.cartDataStore
    private val cartItemsDataStore = context.cartItemsDataStore

    companion object {
        // Cart keys
        private fun getCartKey(storeId: Long, cartId: Long) = longPreferencesKey("cart_${storeId}_$cartId")
        private fun getCartNameKey(storeId: Long, cartId: Long) = stringPreferencesKey("cart_${storeId}_${cartId}_name")
        private fun getCartItemCountKey(storeId: Long, cartId: Long) = intPreferencesKey("cart_${storeId}_${cartId}_itemCount")

        // Cart item keys
        private fun getItemKey(cartId: Long, itemId: Long) = longPreferencesKey("item_${cartId}_${itemId}")
        private fun getItemProductKey(cartId: Long, itemId: Long) = longPreferencesKey("item_${cartId}_${itemId}_product")
        private fun getItemQuantityKey(cartId: Long, itemId: Long) = intPreferencesKey("item_${cartId}_${itemId}_quantity")
    }

    // Cart operations
    suspend fun createCart(storeId: Long, name: String = "Cart ${System.currentTimeMillis().toString().takeLast(5)}"): Cart {
        val cartId = System.currentTimeMillis()
        val cart = Cart(id = cartId, storeId = storeId, name = name)

        cartDataStore.edit { preferences ->
            preferences[getCartKey(storeId, cartId)] = cartId
            preferences[getCartNameKey(storeId, cartId)] = name
            preferences[getCartItemCountKey(storeId, cartId)] = 0
        }

        return cart
    }

    fun getCartsForStore(storeId: Long): Flow<List<Cart>> {
        return cartDataStore.data.map { preferences ->
            preferences.asMap().entries
                .filter {
                    it.key.name.startsWith("cart_${storeId}_") &&
                            !it.key.name.contains("_name") &&
                            !it.key.name.contains("_itemCount")
                }
                .mapNotNull { entry ->
                    val cartId = entry.value as? Long ?: return@mapNotNull null
                    val nameKey = getCartNameKey(storeId, cartId)
                    val itemCountKey = getCartItemCountKey(storeId, cartId)

                    val name = preferences[nameKey] ?: "Unnamed Cart"
                    val itemCount = preferences[itemCountKey] ?: 0

                    Cart(id = cartId, storeId = storeId, name = name, itemCount = itemCount)
                }
        }
    }

    suspend fun getCartById(cartId: Long, storeId: Long): Cart? {
        val preferences = cartDataStore.data.map { it }.firstOrNull() ?: return null

        val cartKey = getCartKey(storeId, cartId)
        val nameKey = getCartNameKey(storeId, cartId)
        val itemCountKey = getCartItemCountKey(storeId, cartId)

        val cartExists = preferences[cartKey] != null
        if (!cartExists) return null

        val name = preferences[nameKey] ?: "Unnamed Cart"
        val itemCount = preferences[itemCountKey] ?: 0

        return Cart(id = cartId, storeId = storeId, name = name, itemCount = itemCount)
    }

    suspend fun deleteCart(cartId: Long, storeId: Long) {
        cartDataStore.edit { preferences ->
            val cartKey = getCartKey(storeId, cartId)
            val nameKey = getCartNameKey(storeId, cartId)
            val itemCountKey = getCartItemCountKey(storeId, cartId)

            preferences.remove(cartKey)
            preferences.remove(nameKey)
            preferences.remove(itemCountKey)
        }

        // Also delete all cart items
        cartItemsDataStore.edit { preferences ->
            preferences.asMap().keys
                .filter { it.name.startsWith("item_${cartId}_") }
                .forEach { preferences.remove(it) }
        }
    }

    suspend fun updateCartItemCount(cartId: Long, storeId: Long, count: Int) {
        cartDataStore.edit { preferences ->
            preferences[getCartItemCountKey(storeId, cartId)] = count
        }
    }

    // Cart items operations
    suspend fun addItemToCart(cartId: Long, productId: Long, quantity: Int = 1) {
        // Add new item
        val itemId = System.currentTimeMillis()

        cartItemsDataStore.edit { preferences ->
            preferences[getItemKey(cartId, itemId)] = itemId
            preferences[getItemProductKey(cartId, itemId)] = productId
            preferences[getItemQuantityKey(cartId, itemId)] = quantity
        }
    }

    suspend fun updateCartItemQuantity(cartId: Long, itemId: Long, quantity: Int) {
        if (quantity <= 0) {
            removeCartItem(cartId, itemId)
            return
        }

        cartItemsDataStore.edit { preferences ->
            preferences[getItemQuantityKey(cartId, itemId)] = quantity
        }
    }

    suspend fun removeCartItem(cartId: Long, itemId: Long) {
        cartItemsDataStore.edit { preferences ->
            val itemKey = getItemKey(cartId, itemId)
            val productKey = getItemProductKey(cartId, itemId)
            val quantityKey = getItemQuantityKey(cartId, itemId)

            preferences.remove(itemKey)
            preferences.remove(productKey)
            preferences.remove(quantityKey)
        }
    }

    fun getCartItemIds(cartId: Long): Flow<List<Pair<Long, Long>>> {
        return cartItemsDataStore.data.map { preferences ->
            preferences.asMap().entries
                .filter {
                    it.key.name.startsWith("item_${cartId}_") &&
                            !it.key.name.contains("_product") &&
                            !it.key.name.contains("_quantity")
                }
                .mapNotNull { entry ->
                    val itemId = entry.value as? Long ?: return@mapNotNull null
                    val productIdKey = getItemProductKey(cartId, itemId)
                    val productId = preferences[productIdKey] as? Long ?: return@mapNotNull null

                    Pair(itemId, productId)
                }
        }
    }

    suspend fun getCartItemQuantity(cartId: Long, itemId: Long): Int? {
        val preferences = cartItemsDataStore.data.map { it }.firstOrNull() ?: return null
        return preferences[getItemQuantityKey(cartId, itemId)]
    }

    suspend fun clearAll() {
        cartDataStore.edit { preferences ->
            preferences.clear()
        }

        cartItemsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}