package edu.cit.sarismart.features.user.tabs.scan.ui.carts

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.cartDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_carts")

@HiltViewModel
class PickCartViewModel @Inject constructor(
    private val applicationContext: Context
) : ViewModel() {

    private val _carts = MutableStateFlow<List<Cart>>(emptyList())
    val carts: StateFlow<List<Cart>> = _carts

    private var currentStoreId: Long = -1

    fun setStoreId(storeId: Long) {
        currentStoreId = storeId
        loadCarts()
    }

    private fun loadCarts() {
        viewModelScope.launch {
            if (currentStoreId != -1L) {
                applicationContext.cartDataStore.data
                    .map { preferences ->
                        preferences.asMap().filter { it.key.name.startsWith("cart_${currentStoreId}") }
                            .mapNotNull { (key, value) ->
                                if (value is Preferences) {
                                    val cartId = key.name.substringAfter("cart_${currentStoreId}").toLongOrNull()
                                    val name = value[stringPreferencesKey("name")]
                                    val itemCount = value[intPreferencesKey("itemCount")] ?: 0
                                    if (cartId != null && name != null) {
                                        Cart(id = cartId, storeId = currentStoreId, name = name, itemCount = itemCount)
                                    } else {
                                        null
                                    }
                                } else {
                                    null
                                }
                            }.toList()
                    }.collect { fetchedCarts ->
                        _carts.value = fetchedCarts
                    }
            } else {
                _carts.value = emptyList()
            }
        }
    }

    fun createNewCart() {
        viewModelScope.launch {
            if (currentStoreId != -1L) {
                val cartId = System.currentTimeMillis()
                val cartNameKey = stringPreferencesKey("name")
                val cartItemCountKey = intPreferencesKey("itemCount")

                applicationContext.cartDataStore.edit { preferences ->
                    preferences[longPreferencesKey("cart_${currentStoreId}_$cartId")] = cartId
                    preferences[cartNameKey] = "Cart ${cartId.toString().takeLast(5)}"
                    preferences[cartItemCountKey] = 0
                }
                loadCarts()
            }
        }
    }
}