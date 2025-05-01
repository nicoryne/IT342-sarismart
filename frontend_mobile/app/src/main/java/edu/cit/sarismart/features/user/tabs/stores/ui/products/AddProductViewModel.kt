package edu.cit.sarismart.features.user.tabs.stores.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.ProductRequest
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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

    private val _productBarcode = MutableStateFlow("")
    val productBarcode: StateFlow<String> = _productBarcode

    // Form validation errors
    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _priceError = MutableStateFlow<String?>(null)
    val priceError: StateFlow<String?> = _priceError

    private val _stockError = MutableStateFlow<String?>(null)
    val stockError: StateFlow<String?> = _stockError

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

    fun updateProductBarcode(barcode: String) {
        _productBarcode.value = barcode
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

        return isValid
    }

    fun createProduct(storeId: Long) {
        if (!validateForm()) {
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val store = storeRepository.getStoreById(storeId)
                if (store == null) {
                    _errorMessage.value = "Store not found"
                    _isLoading.value = false
                    return@launch
                }

                val newProduct = ProductRequest(
                    name = _productName.value,
                    category = _productCategory.value.takeIf { it.isNotBlank() }.toString(),
                    price = _productPrice.value.toDouble(),
                    stock = _productStock.value.toInt(),
                    description = _productDescription.value.takeIf { it.isNotBlank() }.toString(),
                    reorder_level = 5,
                    barcode = _productBarcode.value.takeIf { it.isNotBlank() }.toString()
                )

                val success = createProductInStore(storeId, newProduct)
                if (success) {
                    _isSuccess.value = true
                } else {
                    _errorMessage.value = "Failed to create product"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error creating product: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun createProductInStore(storeId: Long, product: ProductRequest): Boolean {
        return try {
            productRepository.createProduct(storeId, product) != null
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