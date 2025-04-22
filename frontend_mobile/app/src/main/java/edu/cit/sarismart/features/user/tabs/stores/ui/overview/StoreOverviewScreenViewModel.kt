package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreOverviewScreenViewModel @Inject constructor() : ViewModel() {

    private val _isSubmitLoading = MutableStateFlow(false)
    val isSubmitLoading: StateFlow<Boolean> = _isSubmitLoading

    private val _isSubmitError = MutableStateFlow<String?>(null)
    val isSubmitError: StateFlow<String?> = _isSubmitError

    private val _isSubmitSuccess = MutableStateFlow<String?>(null)
    val isSubmitSuccess: StateFlow<String?> = _isSubmitSuccess

    private val _showSubmitDialog = MutableStateFlow(false)
    val showSubmitDialog: StateFlow<Boolean> = _showSubmitDialog

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


}