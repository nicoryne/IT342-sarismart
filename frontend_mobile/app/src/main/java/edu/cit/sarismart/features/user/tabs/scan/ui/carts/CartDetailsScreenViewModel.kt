package edu.cit.sarismart.features.user.tabs.scan.ui.carts

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.cartDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_carts")

@HiltViewModel
class CartDetailsScreenViewModel @Inject constructor(
    private val applicationContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cartId: Long? = savedStateHandle["cartId"]
    private val storeId: Long? = savedStateHandle["storeId"]

    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart

    init {
        loadCartDetails()
    }

    private fun loadCartDetails() {
        viewModelScope.launch {
            if (cartId != null && storeId != null) {
                applicationContext.cartDataStore.data
                    .map { preferences ->
                        val cartPreferenceKey = longPreferencesKey("cart_${storeId}_${cartId}")
                        val cartData = preferences[cartPreferenceKey]
                        if (cartData != null) {
                            val name = preferences[stringPreferencesKey("name")] ?: "Unnamed Cart"
                            val itemCount = preferences[intPreferencesKey("itemCount")] ?: 0
                            Cart(id = cartId, storeId = storeId, name = name, itemCount = itemCount)
                        } else {
                            null
                        }
                    }.firstOrNull()?.let { fetchedCart ->
                        _cart.value = fetchedCart
                    }
            } else {
                _cart.value = null
            }
        }
    }

}