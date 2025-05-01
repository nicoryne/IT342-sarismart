package edu.cit.sarismart.features.guest.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.maps.data.MapRepository
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GuestMapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _nearbyStores = MutableStateFlow<List<Store>>(emptyList())
    val nearbyStores: StateFlow<List<Store>> = _nearbyStores

    private val _filteredStores = MutableStateFlow<List<Store>>(emptyList())
    val filteredStores: StateFlow<List<Store>> = _filteredStores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchNearbyStores(location: LatLng, radius: Double = 1.0) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val stores = mapRepository.getNearbyStores(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    radius = radius
                )
                _nearbyStores.value = stores
                _filteredStores.value = stores
                _errorMessage.value = null
            } catch (e: IOException) {
                Log.e("GuestMapViewModel", "Network error: ${e.message}")
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: HttpException) {
                Log.e("GuestMapViewModel", "HTTP error: ${e.message}")
                _errorMessage.value = "Error fetching stores: ${e.message()}"
            } catch (e: Exception) {
                Log.e("GuestMapViewModel", "Unexpected error: ${e.message}")
                _errorMessage.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterStores(query: String) {
        if (query.isBlank()) {
            _filteredStores.value = _nearbyStores.value
            return
        }

        _filteredStores.value = _nearbyStores.value.filter { store ->
            store.storeName.contains(query, ignoreCase = true) ||
                    store.location?.contains(query, ignoreCase = true) == true
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}