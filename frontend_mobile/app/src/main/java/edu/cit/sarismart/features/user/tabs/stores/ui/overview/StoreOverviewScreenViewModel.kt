package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import edu.cit.sarismart.features.user.tabs.stores.ui.util.StoreStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.isNotEmpty

@HiltViewModel
class StoreOverviewScreenViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository,
    private val applicationContext: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_STORE_NAME = "store_name"
        private const val KEY_STORE_LOCATION = "store_location"
        private const val KEY_STORE_LONGITUDE = "store_longitude"
        private const val KEY_STORE_LATITUDE = "store_latitude"
    }

    val storeName = savedStateHandle.getStateFlow(KEY_STORE_NAME, "")
    val storeLocation = savedStateHandle.getStateFlow(KEY_STORE_LOCATION, "")
    val storeLongitude = savedStateHandle.getStateFlow(KEY_STORE_LONGITUDE, 0.0)
    val storeLatitude = savedStateHandle.getStateFlow(KEY_STORE_LATITUDE, 0.0)

    private val _isSubmitLoading = MutableStateFlow(false)
    val isSubmitLoading: StateFlow<Boolean> = _isSubmitLoading

    private val _isSubmitError = MutableStateFlow<String?>(null)
    val isSubmitError: StateFlow<String?> = _isSubmitError

    private val _isSubmitSuccess = MutableStateFlow<String?>(null)
    val isSubmitSuccess: StateFlow<String?> = _isSubmitSuccess

    private val _showSubmitDialog = MutableStateFlow(false)
    val showSubmitDialog: StateFlow<Boolean> = _showSubmitDialog

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet

    private val _storeNameError = MutableStateFlow<String?>(null)
    val storeNameError: StateFlow<String?> = _storeNameError

    private val _storeLocationError = MutableStateFlow<String?>(null)
    val storeLocationError: StateFlow<String?> = _storeLocationError

    // Store statuses
    private val _storeStatuses = MutableStateFlow<Map<Long, StoreStatus>>(emptyMap())
    val storeStatuses: StateFlow<Map<Long, StoreStatus>> = _storeStatuses

    // Restocking information
    private val _restockingStore = MutableStateFlow<Store?>(null)
    val restockingStore: StateFlow<Store?> = _restockingStore

    private val _restockingDays = MutableStateFlow(0)
    val restockingDays: StateFlow<Int> = _restockingDays

    // Join store as worker
    private val _showJoinDialog = MutableStateFlow(false)
    val showJoinDialog: StateFlow<Boolean> = _showJoinDialog

    private val _joinCode = MutableStateFlow("")
    val joinCode: StateFlow<String> = _joinCode

    private val _isJoining = MutableStateFlow(false)
    val isJoining: StateFlow<Boolean> = _isJoining

    private val _joinError = MutableStateFlow<String?>(null)
    val joinError: StateFlow<String?> = _joinError

    private val _joinSuccess = MutableStateFlow<String?>(null)
    val joinSuccess: StateFlow<String?> = _joinSuccess

    fun onShowBottomSheetChanged(show: Boolean) {
        _showBottomSheet.value = show
    }

    fun onSubmitLoading() {
        _isSubmitLoading.value = true
        _isSubmitSuccess.value = null
        _isSubmitError.value = null
        _showSubmitDialog.value = true
    }

    fun onSubmitSuccess() {
        _isSubmitLoading.value = false
        _isSubmitSuccess.value = "Store created successfully!"
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _showSubmitDialog.value = false
        }
    }

    fun onSubmitError(error: String?) {
        _isSubmitLoading.value = false
        _isSubmitError.value = error ?: "Failed to create store."
        _showSubmitDialog.value = true
    }

    fun onDismissSubmitDialog() {
        _showSubmitDialog.value = false
        _isSubmitSuccess.value = null
        _isSubmitError.value = null
    }

    fun updateStoreName(name: String) {
        savedStateHandle[KEY_STORE_NAME] = name
    }

    fun updateStoreLocation(location: String) {
        savedStateHandle[KEY_STORE_LOCATION] = location
    }

    fun updateStoreLongitude(longitude: Double) {
        savedStateHandle[KEY_STORE_LONGITUDE] = longitude
    }

    fun updateStoreLatitude(latitude: Double) {
        savedStateHandle[KEY_STORE_LATITUDE] = latitude
    }

    fun clearValues() {
        updateStoreName("")
        updateStoreLocation("")
        updateStoreLatitude(0.0)
        updateStoreLongitude(0.0)
        _storeNameError.value = null
        _storeLocationError.value = null
    }

    fun onCancel() {
        clearValues()
    }

    suspend fun onSubmit(
        onSubmitting: () -> Unit,
        onSubmitSuccess: () -> Unit,
        onSubmitError: (it: String) -> Unit,
        onDismissRequest: () -> Unit
    ) {
        var hasErrors = false

        if (storeName.value.isBlank()) {
            _storeNameError.value = "Store name is required."
            hasErrors = true
        } else {
            _storeNameError.value = null
        }

        if (storeLocation.value.isBlank()) {
            _storeLocationError.value = "Store location is required."
            hasErrors = true
        } else {
            _storeLocationError.value = null
        }

        if (hasErrors) {
            return
        }

        var latitude = storeLatitude.value
        var longitude = storeLongitude.value
        var geocodingSuccessful = true

        if (latitude == 0.0 || longitude == 0.0) {
            val geocoder = Geocoder(applicationContext)
            try {
                val addresses = geocoder.getFromLocationName(storeLocation.value, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    latitude = addresses[0].latitude
                    longitude = addresses[0].longitude
                    updateStoreLatitude(latitude)
                    updateStoreLongitude(longitude)
                    updateStoreLocation(addresses[0].getAddressLine(0))
                } else {
                    onSubmitError("Could not find coordinates for the provided location.")
                    geocodingSuccessful = false
                    onDismissRequest()
                    clearValues()
                    return
                }
            } catch (e: IOException) {
                Log.e("StoreFormVM", "Geocoding failed: ${e.localizedMessage}")
                onSubmitError("Failed to retrieve coordinates for the location.")
                geocodingSuccessful = false
                onDismissRequest()
                clearValues()
                return
            }
        }

        if (geocodingSuccessful) {
            onSubmitting()

            storeRepository.createStore(
                storeName = storeName.value,
                storeLocation = storeLocation.value,
                storeLatitude = storeLatitude.value,
                storeLongitude = storeLongitude.value
            ).onSuccess {
                getStores()
                onSubmitSuccess()
            }.onFailure {
                onSubmitError("Server denied your request.")
            }

            onDismissRequest()
            clearValues()
        }
    }

    fun getStores() {
        viewModelScope.launch {
            val stores = storeRepository.getOwnedStores()
            _stores.value = stores
            checkStoreStockLevels()
        }
    }

    private fun checkStoreStockLevels() {
        viewModelScope.launch {
            val statuses = mutableMapOf<Long, StoreStatus>()
            var closestRestockStore: Store? = null
            var minDaysToRestock = Int.MAX_VALUE

            for (store in stores.value) {
                try {
                    // Get products for this store
                    val products = productRepository.getProductsForStore(store.id)

                    // Calculate stock status
                    val status = calculateStoreStatus(products)
                    statuses[store.id] = status

                    // Calculate days until restock needed
                    if (status != StoreStatus.GOOD) {
                        val daysToRestock = calculateDaysToRestock(products)
                        if (daysToRestock < minDaysToRestock) {
                            minDaysToRestock = daysToRestock
                            closestRestockStore = store
                        }
                    }
                } catch (e: Exception) {
                    Log.e("StoreViewModel", "Error checking stock for store ${store.id}: ${e.message}")
                    statuses[store.id] = StoreStatus.GOOD // Default to GOOD if there's an error
                }
            }

            _storeStatuses.value = statuses
            _restockingStore.value = closestRestockStore
            _restockingDays.value = if (minDaysToRestock == Int.MAX_VALUE) 0 else minDaysToRestock
        }
    }

    private fun calculateStoreStatus(products: List<Product>): StoreStatus {
        if (products.isEmpty()) return StoreStatus.GOOD // Default to GOOD if no products

        val outOfStockCount = products.count { it.stock <= 0 }
        val lowStockCount = products.count { it.stock > 0 && it.stock <= 5 }

        return when {
            outOfStockCount > 0 -> StoreStatus.OUT_OF_STOCK
            lowStockCount > 0 -> StoreStatus.LOW_STOCK
            else -> StoreStatus.GOOD
        }
    }

    private fun calculateDaysToRestock(products: List<Product>): Int {
        // Simple algorithm: assume products with quantity <= 5 need restocking
        // and each product is consumed at a rate of 1 unit per day
        val productsNeedingRestock = products.filter { it.stock <= 5 }
        if (productsNeedingRestock.isEmpty()) return Int.MAX_VALUE

        // Find the product that will run out first
        return productsNeedingRestock.minOfOrNull { Math.max(it.stock, 1) } ?: 5
    }

    // Join store as worker functions
    fun showJoinDialog() {
        _showJoinDialog.value = true
    }

    fun dismissJoinDialog() {
        _showJoinDialog.value = false
        _joinCode.value = ""
        _joinError.value = null
        _joinSuccess.value = null
    }

    fun updateJoinCode(code: String) {
        _joinCode.value = code.uppercase()
        _joinError.value = null
    }

    fun joinStoreAsWorker() {
        if (_joinCode.value.isBlank()) {
            _joinError.value = "Please enter an invitation code"
            return
        }

        _isJoining.value = true
        _joinError.value = null
        _joinSuccess.value = null

        viewModelScope.launch {
            try {
                // In a real app, you would call an API to join the store
                // For now, we'll simulate a successful join after a delay
                kotlinx.coroutines.delay(1500)

                // Simulate success
                _isJoining.value = false
                _joinSuccess.value = "Successfully joined store!"

                // In a real app, you would refresh the stores list
                getStores()

                // Auto-dismiss after success
                kotlinx.coroutines.delay(2000)
                dismissJoinDialog()
            } catch (e: Exception) {
                _isJoining.value = false
                _joinError.value = "Failed to join store: ${e.message}"
            }
        }
    }
}
