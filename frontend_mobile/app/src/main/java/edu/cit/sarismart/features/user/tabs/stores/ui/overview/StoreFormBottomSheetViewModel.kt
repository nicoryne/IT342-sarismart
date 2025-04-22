package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class StoreFormBottomSheetViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName

    private val _storeLocation = MutableStateFlow("")
    val storeLocation: StateFlow<String> = _storeLocation

    private val _storeLongitude = MutableStateFlow<Double>(0.0)
    val storeLongitude: StateFlow<Double> = _storeLongitude

    private val _storeLatitude = MutableStateFlow<Double>(0.0)
    val storeLatitude: StateFlow<Double> = _storeLatitude

    private val _storeNameError = MutableStateFlow<String?>(null)
    val storeNameError: StateFlow<String?> = _storeNameError

    private val _storeLocationError = MutableStateFlow<String?>(null)
    val storeLocationError: StateFlow<String?> = _storeLocationError




    fun onStoreNameChanged(name: String) {
        _storeName.value = name
        _storeNameError.value = null
    }

    fun onStoreLocationChanged(location: String) {
        _storeLocation.value = location
        _storeLocationError.value = null
    }

    fun onStoreLongitudeChanged(longitude: Double) {
        _storeLongitude.value = longitude
    }

    fun onStoreLatitudeChanged(latitude: Double) {
        _storeLatitude.value = latitude
    }

    fun clearValues() {
        _storeName.value = ""
        _storeLocation.value = ""
        _storeLongitude.value = 0.0
        _storeLatitude.value = 0.0
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

        if (_storeName.value.isBlank()) {
            _storeNameError.value = "Store name is required."
            hasErrors = true
        } else {
            _storeNameError.value = null
        }

        if (_storeLocation.value.isBlank()) {
            _storeLocationError.value = "Store location is required."
            hasErrors = true
        } else {
            _storeLocationError.value = null
        }

        if (hasErrors) {
            return
        }

        var latitude = _storeLatitude.value
        var longitude = _storeLongitude.value
        var geocodingSuccessful = true

        if (latitude == 0.0 || longitude == 0.0) {
            val geocoder = Geocoder(applicationContext)
            try {
                val addresses = geocoder.getFromLocationName(_storeLocation.value, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    latitude = addresses[0].latitude
                    longitude = addresses[0].longitude
                    _storeLatitude.value = latitude
                    _storeLongitude.value = longitude
                    _storeLocation.value = addresses[0].getAddressLine(0)
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
                storeName = _storeName.value,
                storeLocation = _storeLocation.value,
                storeLatitude = latitude,
                storeLongitude = longitude
            ).onSuccess {
                onSubmitSuccess()
            }.onFailure {
                onSubmitError("Server denied your request.")
            }

            onDismissRequest()
            clearValues()
        }
    }
}