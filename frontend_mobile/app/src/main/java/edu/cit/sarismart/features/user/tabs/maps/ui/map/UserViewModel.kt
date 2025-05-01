package edu.cit.sarismart.features.user.tabs.maps.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.account.data.models.User
import edu.cit.sarismart.features.user.tabs.maps.data.MapRepository
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException
import javax.inject.Inject


data class StoreUiState(
    val store: Store,
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

object EmptyStore {
    operator fun invoke(id: Long) = Store(
        id = id,
        storeName = "Loading...",
        location = "...",
        latitude = 0.0,
        longitude = 0.0,
        owner = User(
            "", "",
            email = "",
            phone = "",
            fullName = ""
        ),
        workers = emptyList(),
        sales = emptyList()
    )
}

@HiltViewModel
class UserMapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _nearbyStores = MutableStateFlow<List<Store>>(emptyList())
    val nearbyStores: StateFlow<List<Store>> = _nearbyStores

    private val _filteredStores = MutableStateFlow<List<Store>>(emptyList())
    val filteredStores: StateFlow<List<Store>> = _filteredStores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _storeDetails = MutableStateFlow<Map<Long, StoreUiState>>(emptyMap())
    val storeDetails: StateFlow<Map<Long, StoreUiState>> = _storeDetails

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
                Log.e("UserMapViewModel", "Network error: ${e.message}")
                _errorMessage.value = "Network error. Please check your connection."
            } catch (e: HttpException) {
                Log.e("UserMapViewModel", "HTTP error: ${e.message}")
                _errorMessage.value = "Error fetching stores: ${e.message()}"
            } catch (e: Exception) {
                Log.e("UserMapViewModel", "Unexpected error: ${e.message}")
                _errorMessage.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchProductsForStore(storeId: Long) {
        _storeDetails.value = _storeDetails.value.toMutableMap().apply {
            this[storeId] = this[storeId]?.copy(isLoading = true, error = null) ?: StoreUiState(store = EmptyStore.invoke(storeId), isLoading = true)
        }

        viewModelScope.launch {
            try {
                val products = productRepository.getProductsForStore(storeId)
                _storeDetails.value = _storeDetails.value.toMutableMap().apply {
                    this[storeId] = this[storeId]?.copy(products = products, isLoading = false) ?: StoreUiState(store = EmptyStore.invoke(storeId), products = products)
                }
            } catch (e: Exception) {
                _storeDetails.value = _storeDetails.value.toMutableMap().apply {
                    this[storeId] = this[storeId]?.copy(isLoading = false, error = e.message) ?: StoreUiState(store = EmptyStore.invoke(storeId), error = e.message)
                }
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