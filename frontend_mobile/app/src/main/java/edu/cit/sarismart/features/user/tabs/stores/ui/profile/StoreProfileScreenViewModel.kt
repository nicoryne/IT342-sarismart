package edu.cit.sarismart.features.user.tabs.stores.ui.profile

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
class StoreProfileScreenViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> = _store

    fun fetchStore(storeId: Long) {
        _isLoading.value = true

        viewModelScope.launch {
            _store.value = storeRepository.getStoreById(storeId)
            _isLoading.value = false
        }
    }
}