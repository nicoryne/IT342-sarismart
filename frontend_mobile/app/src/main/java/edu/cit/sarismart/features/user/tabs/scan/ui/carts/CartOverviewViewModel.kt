package edu.cit.sarismart.features.user.tabs.scan.ui.carts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import edu.cit.sarismart.features.user.tabs.scan.data.models.CartItem
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOverviewViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut

    private val _checkoutComplete = MutableStateFlow(false)
    val checkoutComplete: StateFlow<Boolean> = _checkoutComplete

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private var currentStoreId: Long = -1
    private var currentCartId: Long = -1

    fun loadCartAndItems(storeId: Long, cartId: Long) {
        currentStoreId = storeId
        currentCartId = cartId

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Load cart
                val cart = cartRepository.getCartById(cartId, storeId)
                _cart.value = cart

                // Load cart items
                cartRepository.getCartItems(cartId).collect { items ->
                    _cartItems.value = items
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading cart: ${e.message ?: "Unknown error"}"
                _isLoading.value = false
            }
        }
    }

    fun updateItemQuantity(itemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            try {
                cartRepository.updateCartItemQuantity(currentCartId, itemId, newQuantity)
            } catch (e: Exception) {
                _errorMessage.value = "Error updating quantity: ${e.message ?: "Unknown error"}"
            }
        }
    }

    fun removeItem(itemId: Long) {
        viewModelScope.launch {
            try {
                cartRepository.removeCartItem(currentCartId, itemId)
            } catch (e: Exception) {
                _errorMessage.value = "Error removing item: ${e.message ?: "Unknown error"}"
            }
        }
    }

    fun checkout() {
        if (currentStoreId == -1L || currentCartId == -1L) {
            _errorMessage.value = "Invalid store or cart ID"
            return
        }

        _isCheckingOut.value = true

        viewModelScope.launch {
            try {
                val success = cartRepository.checkout(currentCartId, currentStoreId)

                if (success) {
                    _checkoutComplete.value = true
                } else {
                    _errorMessage.value = "Checkout failed. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error during checkout: ${e.message ?: "Unknown error"}"
            } finally {
                _isCheckingOut.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}