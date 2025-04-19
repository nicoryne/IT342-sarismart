package edu.cit.sarismart.features.auth.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import edu.cit.sarismart.core.util.BiometricUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isBiometricAvailable = MutableStateFlow(false)
    val isBiometricAvailable: StateFlow<Boolean> = _isBiometricAvailable

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent: SharedFlow<LoginNavigationEvent> = _navigationEvent

    internal val _loginError = MutableStateFlow("")
    val loginError: StateFlow<String> = _loginError


    init {
        checkBiometricAvailability()
    }

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    private fun checkBiometricAvailability() {
        _isBiometricAvailable.value = BiometricUtil.canAuthenticate(context) &&
                authRepository.isBiometricEnabled()
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val res = authRepository.login(_email.value, _password.value)
            _isLoading.value = false

            if (res.success) {
                _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
            } else {
                _loginError.value = res.message
            }
        }
    }

    fun onBiometricLoginClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.ShowBiometricPrompt)
        }
    }

    fun onBiometricAuthSuccess() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val res = authRepository.loginWithBiometric()
            _isLoading.value = false

            if (res.success) {
                _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
            }
        }
    }

    fun onContinueAsGuestClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            _isLoading.value = false

            _navigationEvent.emit(LoginNavigationEvent.NavigateToGuestMap)
        }
    }

    fun onLoginWithFacebookClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            _isLoading.value = false

            // TODO: Implement Facebook login
        }
    }

    fun onLoginWithGoogleClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            _isLoading.value = false

            // TODO: Implement Google login
        }
    }

}

sealed class LoginNavigationEvent {
    object NavigateToHome : LoginNavigationEvent()
    object NavigateToGuestMap : LoginNavigationEvent()
    object ShowBiometricPrompt : LoginNavigationEvent()
}
