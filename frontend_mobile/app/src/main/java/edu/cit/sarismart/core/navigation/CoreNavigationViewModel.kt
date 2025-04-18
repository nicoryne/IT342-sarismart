package edu.cit.sarismart.core.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.core.data.PreferencesManager
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import edu.cit.sarismart.features.auth.ui.login.LoginNavigationEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoreNavigationViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    val onboardingCompleted: Flow<Boolean> = preferencesManager.onboardingCompletedFlow

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _logoutSuccess = MutableStateFlow<Boolean?>(null)
    val logoutSuccess: StateFlow<Boolean?> = _logoutSuccess


    fun checkOnboardingStatus() {
        viewModelScope.launch {
            preferencesManager.onboardingCompletedFlow.collect { completed ->
                _isLoading.value = false
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted(true)
        }
    }

}