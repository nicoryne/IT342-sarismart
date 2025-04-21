package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StoreFormBottomSheetViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName

    private val _storeLocation = MutableStateFlow("")
    val storeLocation: StateFlow<String> = _storeLocation

    private val _storeLongitude = MutableStateFlow<Double>(0.0)
    val storeLongitude: StateFlow<Double> = _storeLongitude

    private val _storeLatitude = MutableStateFlow<Double>(0.0)
    val storeLatitude: StateFlow<Double> = _storeLatitude

    fun onStoreNameChanged(name: String) {
        _storeName.value = name
    }

    fun onStoreLocationChanged(location: String) {
        _storeLocation.value = location
    }

    fun onStoreLongitudeChanged(longitude: Double) {
        _storeLongitude.value = longitude
    }

    fun onStoreLatitudeChanged(latitude: Double) {
        _storeLatitude.value = latitude
    }

    fun onCancel() {
        _storeName.value = ""
        _storeLocation.value = ""
        _storeLongitude.value = 0.0
        _storeLatitude.value = 0.0
    }

    suspend fun onSubmit() {
        Log.d("StoreFormBottomSheetViewModel", "Store Name: ${_storeName.value}")
        Log.d("StoreFormBottomSheetViewModel", "Store Location: ${_storeLocation.value}")
        Log.d("StoreFormBottomSheetViewModel", "Store Longitude: ${_storeLongitude.value}")
        Log.d("StoreFormBottomSheetViewModel", "Store Latitude: ${_storeLatitude.value}")

        storeRepository.createStore(storeName = _storeName.value, storeLocation = _storeLocation.value, storeLatitude = _storeLatitude.value, storeLongitude = _storeLongitude.value)

    }
}