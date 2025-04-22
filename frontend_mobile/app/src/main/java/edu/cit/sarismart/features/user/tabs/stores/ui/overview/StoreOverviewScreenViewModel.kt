package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import android.content.Context
import android.location.Geocoder
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
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.isNotEmpty

@HiltViewModel
class StoreOverviewScreenViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
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
                onSubmitSuccess()
            }.onFailure {
                onSubmitError("Server denied your request.")
            }

            onDismissRequest()
            clearValues()
        }
    }

    suspend fun getStores() {
        val stores = storeRepository.getOwnedStores()
        Log.i("StoreOverviewScreenViewModel", "Stores: $stores")
        _stores.value = stores
    }


}