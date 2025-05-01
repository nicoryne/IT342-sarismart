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
class EditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val storeId: Long = savedStateHandle.get<Long>("storeId") ?: 0
    private val productId: Long = savedStateHandle.get<Long>("productId") ?: 0

    // Form fields
    private val _productName = MutableStateFlow("")
    val productName: StateFlow<String> = _productName

    private val _productCategory = MutableStateFlow("")
    val productCategory: StateFlow<String> = _productCategory

    private val _productPrice = MutableStateFlow("")
    val productPrice: StateFlow<String> = _productPrice

    private val _productStock = MutableStateFlow("")
    val productStock: StateFlow<String> = _productStock

    private val _productDescription = MutableStateFlow("")
    val productDescription: StateFlow<String> = _productDescription

    private val _productReorderLevel = MutableStateFlow("")
    val productReorderLevel: StateFlow<String> = _productReorderLevel

    // Form validation errors
    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _priceError = MutableStateFlow<String?>(null)
    val priceError: StateFlow<String?> = _priceError

    private val _stockError = MutableStateFlow<String?>(null)
    val stockError: StateFlow<String?> = _stockError

    private val _reorderLevelError = MutableStateFlow<String?>(null)
    val reorderLevelError: StateFlow<String?> = _reorderLevelError

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
                if (product != null) {
                    _productName.value = product.name
                    _productCategory.value = product.category ?: ""
                    _productPrice.value = product.price.toString()
                    _productStock.value = product.stock.toString()
                    _productDescription.value = product.description ?: ""
                    _productReorderLevel.value = product.reorderLevel.toString()
                } else {
                    _errorMessage.value = "Product not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProductName(name: String) {
        _productName.value = name
        _nameError.value = null
    }

    fun updateProductCategory(category: String) {
        _productCategory.value = category
    }

    fun updateProductPrice(price: String) {
        _productPrice.value = price
        _priceError.value = null
    }

    fun updateProductStock(stock: String) {
        _productStock.value = stock
        _stockError.value = null
    }

    fun updateProductDescription(description: String) {
        _productDescription.value = description
    }

    fun updateProductReorderLevel(level: String) {
        _productReorderLevel.value = level
        _reorderLevelError.value = null
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (_productName.value.isBlank()) {
            _nameError.value = "Product name is required"
            isValid = false
        }

        if (_productPrice.value.isBlank()) {
            _priceError.value = "Price is required"
            isValid = false
        } else {
            try {
                val price = _productPrice.value.toDouble()
                if (price <= 0) {
                    _priceError.value = "Price must be greater than zero"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                _priceError.value = "Invalid price format"
                isValid = false
            }
        }

        if (_productStock.value.isBlank()) {
            _stockError.value = "Stock quantity is required"
            isValid = false
        } else {
            try {
                val stock = _productStock.value.toInt()
                if (stock < 0) {
                    _stockError.value = "Stock cannot be negative"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                _stockError.value = "Invalid stock format"
                isValid = false
            }
        }

        if (_productReorderLevel.value.isNotBlank()) {
            try {
                val level = _productReorderLevel.value.toInt()
                if (level < 0) {
                    _reorderLevelError.value = "Reorder level cannot be negative"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                _reorderLevelError.value = "Invalid reorder level format"
                isValid = false
            }
        }

        return isValid
    }

    fun updateProduct() {
        if (!validateForm()) {
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val product = productRepository.getProductById(productId, storeId)
                if (product == null) {
                    _errorMessage.value = "Product not found"
                    _isLoading.value = false
                    return@launch
                }

                val updatedProduct = product.copy(
                    name = _productName.value,
                    category = _productCategory.value.takeIf { it.isNotBlank() }.toString(),
                    price = _productPrice.value.toDouble(),
                    stock = _productStock.value.toInt(),
                    description = _productDescription.value.takeIf { it.isNotBlank() }.toString(),
                    reorderLevel = _productReorderLevel.value.toIntOrNull() ?: product.reorderLevel
                )

                // Assuming we need to add this method to the repository
                val success = updateProductInStore(storeId, productId, updatedProduct)
                if (success) {
                    _isSuccess.value = true
                } else {
                    _errorMessage.value = "Failed to update product"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // This is a placeholder since the actual method isn't in the repository yet
    private suspend fun updateProductInStore(storeId: Long, productId: Long, product: Product): Boolean {
        return try {
            productRepository.updateProduct(storeId, productId, product)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun resetState() {
        _isSuccess.value = false
        _errorMessage.value = null
    }
}