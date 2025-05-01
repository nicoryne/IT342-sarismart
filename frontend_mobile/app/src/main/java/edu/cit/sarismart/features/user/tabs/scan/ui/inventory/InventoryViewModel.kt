package edu.cit.sarismart.features.user.tabs.scan.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor() : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Temporary mock data - replace with actual API calls
    private val mockProducts = mutableListOf(
        Product(1, "Rice", "123456789", 50.0, 100),
        Product(2, "Canned Goods", "987654321", 25.0, 50),
        Product(3, "Soap", "456789123", 15.0, 30),
        Product(4, "Shampoo", "789123456", 35.0, 20),
        Product(5, "Toothpaste", "321654987", 20.0, 40)
    )

    fun loadProducts(storeId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1000)

                // In a real app, you would call your API here
                // val products = productRepository.getProductsForStore(storeId)

                _products.value = mockProducts
            } catch (e: Exception) {
                _errorMessage.value = "Error loading products: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProduct(storeId: Long, name: String, barcode: String, price: Double, quantity: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1000)

                // In a real app, you would call your API here
                // val newProduct = productRepository.addProduct(storeId, name, barcode, price, quantity)

                // For now, just add to our mock list
                val newId = mockProducts.maxOfOrNull { it.id }?.plus(1) ?: 1
                val newProduct = Product(newId, name, barcode, price, quantity)
                mockProducts.add(newProduct)

                // Refresh the product list
                _products.value = mockProducts.toList()

                _errorMessage.value = "Product added successfully"
            } catch (e: Exception) {
                _errorMessage.value = "Error adding product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProduct(product: Product) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1000)

                // In a real app, you would call your API here
                // productRepository.updateProduct(product)

                // For now, just update our mock list
                val index = mockProducts.indexOfFirst { it.id == product.id }
                if (index != -1) {
                    mockProducts[index] = product
                    _products.value = mockProducts.toList()
                    _errorMessage.value = "Product updated successfully"
                } else {
                    _errorMessage.value = "Product not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProduct(productId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1000)

                // In a real app, you would call your API here
                // productRepository.deleteProduct(productId)

                // For now, just remove from our mock list
                mockProducts.removeIf { it.id == productId }
                _products.value = mockProducts.toList()

                _errorMessage.value = "Product deleted successfully"
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}