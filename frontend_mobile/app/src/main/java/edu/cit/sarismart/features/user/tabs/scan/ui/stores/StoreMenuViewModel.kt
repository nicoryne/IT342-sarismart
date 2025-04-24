package edu.cit.sarismart.features.user.tabs.scan.ui.stores

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
class StoreMenuViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Fix: Get storeId as String first, then convert to Long
    private val storeIdString: String? = savedStateHandle.get<String>("storeId")
    private val storeId: Long = storeIdString?.toLongOrNull() ?: -1L

    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> = _store

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        Log.d("StoreMenuViewModel", "Store ID String: $storeIdString")
        Log.d("StoreMenuViewModel", "Store ID Long: $storeId")

        if (storeId != -1L) {
            loadStore()
        } else {
            _errorMessage.value = "Invalid store ID: $storeIdString"
        }
    }

    private fun loadStore() {
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

    fun clearError() {
        _errorMessage.value = null
    }
}