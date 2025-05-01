package edu.cit.sarismart.features.user.tabs.stores.ui.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val storeId: Long = savedStateHandle.get<Long>("storeId") ?: 0
    private val productId: Long = savedStateHandle.get<Long>("productId") ?: 0

    init {
        if (storeId > 0 && productId > 0) {
            loadProduct()
        }
    }

    private fun loadProduct() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val product = productRepository.getProductById(productId, storeId)
                _product.value = product
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStock(newStock: Int, onSuccess: () -> Unit) {
        if (newStock < 0) {
            _errorMessage.value = "Stock cannot be negative"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val success = productRepository.updateProductStock(productId, storeId, newStock)
                if (success) {
                    // Update the local product
                    _product.value = _product.value?.copy(stock = newStock)
                    onSuccess()
                } else {
                    _errorMessage.value = "Failed to update stock"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating stock: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}