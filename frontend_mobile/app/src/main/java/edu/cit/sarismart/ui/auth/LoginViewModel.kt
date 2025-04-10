package edu.cit.sarismart.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val success = authRepository.login(_email.value, _password.value)
            _isLoading.value = false

            // TODO: after success navigation
        }
    }

    fun onContinueAsGuestClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            // TODO: implement continue as guest click
        }
    }

    fun onLoginWithFacebookClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            // TODO: login with facebook
        }
    }

    fun onLoginWithGoogleClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            // TODO: login with google
        }
    }
}
