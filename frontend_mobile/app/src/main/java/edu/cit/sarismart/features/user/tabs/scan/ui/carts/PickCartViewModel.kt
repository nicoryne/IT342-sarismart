package edu.cit.sarismart.features.user.tabs.scan.ui.carts

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PickCartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get storeId from navigation arguments
    private val storeId: Long = savedStateHandle.get<Long>("storeId") ?: -1L

    private val _carts = MutableStateFlow<List<Cart>>(emptyList())
    val carts: StateFlow<List<Cart>> = _carts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _navigateToScanner = MutableStateFlow<Pair<Long, Long>?>(null)
    val navigateToScanner: StateFlow<Pair<Long, Long>?> = _navigateToScanner

    init {
        // Log the storeId to verify it's being set correctly
        Log.d("PickCartViewModel", "Initialized with storeId: $storeId")
        loadCarts()
    }

    private fun loadCarts() {
        if (storeId == -1L) {
            _errorMessage.value = "Invalid store ID"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            cartRepository.getCartsForStore(storeId)
                .catch { e ->
                    _errorMessage.value = "Error loading carts: ${e.message}"
                    _isLoading.value = false
                }
                .collect { cartsList ->
                    _carts.value = cartsList
                    _isLoading.value = false
                }
        }
    }

    fun createNewCart(name: String = "Cart ${System.currentTimeMillis().toString().takeLast(5)}") {
        if (storeId == -1L) {
            _errorMessage.value = "Store ID not set"
            throw IllegalStateException("Store ID not set")
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newCart = cartRepository.createCart(storeId, name)
                // Refresh the cart list
                loadCarts()
                // Navigate to scanner with the new cart
                _navigateToScanner.value = Pair(storeId, newCart.id)
            } catch (e: Exception) {
                _errorMessage.value = "Error creating cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCart(cartId: Long) {
        if (storeId == -1L) {
            _errorMessage.value = "Store ID not set"
            return
        }

        _navigateToScanner.value = Pair(storeId, cartId)
    }

    fun clearNavigationEvent() {
        _navigateToScanner.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}