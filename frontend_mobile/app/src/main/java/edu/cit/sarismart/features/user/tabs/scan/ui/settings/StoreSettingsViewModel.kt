package edu.cit.sarismart.features.user.tabs.scan.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreSettingsViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> = _store

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadStore(storeId: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val store = storeRepository.getStoreById(storeId)
                _store.value = store
            } catch (e: Exception) {
                _errorMessage.value = "Error loading store: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStore(storeId: Long, name: String, location: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Get the current store
                val currentStore = _store.value ?: throw IllegalStateException("Store not loaded")

                // Use the existing latitude and longitude if available, or default to 0.0
                val latitude = currentStore.latitude ?: 0.0
                val longitude = currentStore.longitude ?: 0.0

                // Call the editStore method with the correct parameters
                storeRepository.editStore(
                    store = currentStore,
                    storeName = name,
                    storeLocation = location,
                    storeLatitude = latitude,
                    storeLongitude = longitude
                )

                // Reload the store to get the updated data
                loadStore(storeId)

                _errorMessage.value = "Store updated successfully"
            } catch (e: Exception) {
                _errorMessage.value = "Error updating store: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}