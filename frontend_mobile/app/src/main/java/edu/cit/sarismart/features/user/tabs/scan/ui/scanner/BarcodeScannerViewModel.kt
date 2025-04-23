package edu.cit.sarismart.features.user.tabs.scan.ui.scanner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _lastScannedBarcode = MutableStateFlow("")
    val lastScannedBarcode: StateFlow<String> = _lastScannedBarcode

    private val _isProductFound = MutableStateFlow(false)
    val isProductFound: StateFlow<Boolean> = _isProductFound

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private var storeId: Long = -1
    private var cartId: Long = -1

    fun setStoreAndCartId(storeId: Long, cartId: Long) {
        this.storeId = storeId
        this.cartId = cartId
    }

    fun onBarcodeDetected(barcode: String) {
        if (_isScanning.value || storeId == -1L || cartId == -1L) return

        // Prevent duplicate scans
        if (barcode == _lastScannedBarcode.value && _lastScannedBarcode.value.isNotEmpty()) {
            return
        }

        _isScanning.value = true
        _lastScannedBarcode.value = barcode
        _isLoading.value = true

        Log.d("BarcodeScannerViewModel", "Processing barcode: $barcode")

        viewModelScope.launch {
            try {
                val product = productRepository.getProductByBarcode(barcode, storeId)

                if (product != null) {
                    Log.d("BarcodeScannerViewModel", "Product found: ${product.name}")
                    // Add product to cart
                    cartRepository.addItemToCart(cartId, storeId, product.id)
                    _isProductFound.value = true
                } else {
                    Log.e("BarcodeScannerViewModel", "Product not found for barcode: $barcode")
                    _errorMessage.value = "Product not found for barcode: $barcode"
                }
            } catch (e: Exception) {
                Log.e("BarcodeScannerViewModel", "Error processing barcode", e)
                _errorMessage.value = "Error processing barcode: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
                _isScanning.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = ""
    }

    fun resetProductFound() {
        _isProductFound.value = false
    }
}