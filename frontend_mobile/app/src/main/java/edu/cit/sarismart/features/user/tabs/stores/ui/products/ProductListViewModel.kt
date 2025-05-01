package edu.cit.sarismart.features.user.tabs.stores.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting

    private val _deleteSuccess = MutableStateFlow<Product?>(null)
    val deleteSuccess: StateFlow<Product?> = _deleteSuccess

    fun loadProducts(storeId: Long) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val productList = productRepository.getProductsForStore(storeId)
                _products.value = productList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load products: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteProduct(storeId: Long, productId: Long) {
        _isDeleting.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val success = productRepository.deleteProduct(storeId, productId)
                if (success) {
                    val deletedProduct = _products.value.find { it.id == productId }

                    _products.value = _products.value.filter { it.id != productId }

                    _deleteSuccess.value = deletedProduct
                } else {
                    _errorMessage.value = "Failed to delete product"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting product: ${e.message}"
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun resetDeleteState() {
        _deleteSuccess.value = null
    }

    fun getFilteredProducts(): List<Product> {
        val query = _searchQuery.value.lowercase()
        return if (query.isBlank()) {
            _products.value
        } else {
            _products.value.filter {
                it.name.lowercase().contains(query) ||
                        it.category?.lowercase()?.contains(query) == true ||
                        it.description?.lowercase()?.contains(query) == true
            }
        }
    }
}