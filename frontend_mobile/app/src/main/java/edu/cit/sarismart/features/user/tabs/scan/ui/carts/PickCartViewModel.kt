package edu.cit.sarismart.features.user.tabs.scan.ui.carts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PickCartViewModel @Inject constructor(
    private val cartRepository: CartRepository
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
                cartRepository.getCartsForStore(currentStoreId).collect { fetchedCarts ->
                    _carts.value = fetchedCarts
                }
            } else {
                _carts.value = emptyList()
            }
        }
    }

    suspend fun createNewCart(): Cart {
        if (currentStoreId == -1L) {
            throw IllegalStateException("Store ID not set")
        }

        val cart = cartRepository.createCart(currentStoreId)
        loadCarts()
        return cart
    }
}