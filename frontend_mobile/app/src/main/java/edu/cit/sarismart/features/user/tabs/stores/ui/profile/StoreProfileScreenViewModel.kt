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

    private val _isDeleteLoading = MutableStateFlow(false)
    val isDeleteLoading: StateFlow<Boolean> = _isDeleteLoading

    private val _isDeleteSuccess = MutableStateFlow<String?>(null)
    val isDeleteSuccess: StateFlow<String?> = _isDeleteSuccess

    private val _isDeleteError = MutableStateFlow<String?>(null)
    val isDeleteError: StateFlow<String?> = _isDeleteError

    private val _isEditLoading = MutableStateFlow(false)
    val isEditLoading: StateFlow<Boolean> = _isEditLoading

    private val _isEditSuccess = MutableStateFlow<String?>(null)
    val isEditSuccess: StateFlow<String?> = _isEditSuccess

    private val _isEditError = MutableStateFlow<String?>(null)
    val isEditError: StateFlow<String?> = _isEditError

    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> = _store

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog

    private val _showLoadingDialog = MutableStateFlow(false)
    val showLoadingDialog: StateFlow<Boolean> = _showLoadingDialog

    private val _showEditLoadingDialog = MutableStateFlow(false)
    val showEditLoadingDialog: StateFlow<Boolean> = _showEditLoadingDialog

    private val _showEditBottomSheet = MutableStateFlow(false)
    val showEditBottomSheet: StateFlow<Boolean> = _showEditBottomSheet

    // Store form data
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName

    private val _storeLocation = MutableStateFlow("")
    val storeLocation: StateFlow<String> = _storeLocation

    private val _storeLatitude = MutableStateFlow(0.0)
    val storeLatitude: StateFlow<Double> = _storeLatitude

    private val _storeLongitude = MutableStateFlow(0.0)
    val storeLongitude: StateFlow<Double> = _storeLongitude

    private val _storeNameError = MutableStateFlow<String?>(null)
    val storeNameError: StateFlow<String?> = _storeNameError

    private val _storeLocationError = MutableStateFlow<String?>(null)
    val storeLocationError: StateFlow<String?> = _storeLocationError

    fun updateStoreName(name: String) {
        _storeName.value = name
        _storeNameError.value = null
    }

    fun updateStoreLocation(location: String) {
        _storeLocation.value = location
        _storeLocationError.value = null
    }

    fun updateStoreLatitude(latitude: Double) {
        _storeLatitude.value = latitude
    }

    fun updateStoreLongitude(longitude: Double) {
        _storeLongitude.value = longitude
    }

    fun onShowEditBottomSheet() {
        _store.value?.let { store ->
            _storeName.value = store.storeName
            _storeLocation.value = store.location
            _storeLatitude.value = store.latitude
            _storeLongitude.value = store.longitude
        }
        _showEditBottomSheet.value = true
    }

    fun onDismissEditBottomSheet() {
        _showEditBottomSheet.value = false
    }

    fun onDismissConfirmationDialog() {
        _showConfirmationDialog.value = false
    }

    fun onShowConfirmationDialog() {
        _showConfirmationDialog.value = true
    }

    fun onDismissLoadingDialog() {
        _showLoadingDialog.value = false
    }

    fun onShowLoadingDialog() {
        _showLoadingDialog.value = true
    }

    fun onDismissEditLoadingDialog() {
        _showEditLoadingDialog.value = false
    }

    fun onShowEditLoadingDialog() {
        _showEditLoadingDialog.value = true
    }

    fun fetchStore(storeId: Long) {
        _isLoading.value = true

        viewModelScope.launch {
            _store.value = storeRepository.getStoreById(storeId)
            _isLoading.value = false
        }
    }

    fun onDeleteStore() {
        onDismissConfirmationDialog()
        onShowLoadingDialog()
        _isDeleteSuccess.value = null
        _isDeleteError.value = null

        if (_store.value == null) {
            _isDeleteLoading.value = false
            _isDeleteError.value = "Store not found"
            return
        }

        viewModelScope.launch {
            val res = storeRepository.deleteStore(_store.value!!.id)

            if (res) {
                _isDeleteLoading.value = false
                _isDeleteSuccess.value = "Store has been successfully deleted."
            } else {
                _isDeleteLoading.value = false
                _isDeleteError.value = "Failed to delete store"
            }
        }
    }

    fun onSubmitEdit() {
        var hasErrors = false

        if (_storeName.value.isBlank()) {
            _storeNameError.value = "Store name is required."
            hasErrors = true
        }

        if (_storeLocation.value.isBlank()) {
            _storeLocationError.value = "Store location is required."
            hasErrors = true
        }

        if (hasErrors) {
            return
        }

        onDismissEditBottomSheet()
        onShowEditLoadingDialog()
        _isEditSuccess.value = null
        _isEditError.value = null
        _isEditLoading.value = true

        if (_store.value == null) {
            _isEditLoading.value = false
            _isEditError.value = "Store not found"
            return
        }

        viewModelScope.launch {
            try {
                storeRepository.editStore(
                    _store.value!!,
                    _storeName.value,
                    _storeLocation.value,
                    _storeLatitude.value,
                    _storeLongitude.value
                )
                _isEditLoading.value = false
                _isEditSuccess.value = "Store has been successfully updated."
                fetchStore(_store.value!!.id)
            } catch (e: Exception) {
                _isEditLoading.value = false
                _isEditError.value = "Failed to update store: ${e.message}"
            }
        }
    }
}