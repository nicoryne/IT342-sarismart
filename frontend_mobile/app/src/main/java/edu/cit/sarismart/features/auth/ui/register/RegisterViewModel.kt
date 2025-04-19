package edu.cit.sarismart.features.auth.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import edu.cit.sarismart.features.auth.ui.login.LoginNavigationEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _verifyPassword = MutableStateFlow("")
    val verifyPassword: StateFlow<String> = _verifyPassword

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _isVerifyPasswordVisible = MutableStateFlow(false)
    val isVerifyPasswordVisible: StateFlow<Boolean> = _isVerifyPasswordVisible

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Validation states
    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _verifyPasswordError = MutableStateFlow<String?>(null)
    val verifyPasswordError: StateFlow<String?> = _verifyPasswordError

    private val _navigationEvent = MutableSharedFlow<RegisterNavigationEvent>()
    val navigationEvent: SharedFlow<RegisterNavigationEvent> = _navigationEvent

    internal val _registerError = MutableStateFlow("")
    val registerError: StateFlow<String> = _registerError

    fun onNameChanged(value: String) {
        _name.value = value
        validateName()
    }

    fun onEmailChanged(value: String) {
        _email.value = value
        validateEmail()
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        validatePassword()
        validateVerifyPassword() // also validate verify password when password changes
    }

    fun onVerifyPasswordChanged(value: String) {
        _verifyPassword.value = value
        validateVerifyPassword()
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun toggleVerifyPasswordVisibility() {
        _isVerifyPasswordVisible.value = !_isVerifyPasswordVisible.value
    }

    private fun validateName() {
        _nameError.value = when {
            _name.value.isBlank() -> "Name is required"
            _name.value.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    private fun validateEmail() {
        _emailError.value = when {
            _email.value.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> "Invalid email format"
            else -> null
        }
    }

    private fun validatePassword() {
        _passwordError.value = when {
            _password.value.isBlank() -> "Password is required"
            _password.value.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    private fun validateVerifyPassword() {
        _verifyPasswordError.value = when {
            _verifyPassword.value.isBlank() -> "Please confirm your password"
            _verifyPassword.value != _password.value -> "Passwords do not match"
            else -> null
        }
    }

    private fun validateAll(): Boolean {
        validateName()
        validateEmail()
        validatePassword()
        validateVerifyPassword()

        return _nameError.value == null &&
                _emailError.value == null &&
                _passwordError.value == null &&
                _verifyPasswordError.value == null
    }

    fun onRegisterClicked() {
        if (!validateAll()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // simulate network delay

            val res = authRepository.register(
                _name.value,
                _email.value,
                _password.value,
                _verifyPassword.value
            )
            _isLoading.value = false

            if (res.success) {
                _navigationEvent.emit(RegisterNavigationEvent.SuccessfulRegistration)
            } else {
                _registerError.value = res.message
            }
        }
    }

    fun onRegisterWithGoogleClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // simulate network delay

            // TODO: Implement Google registration

            _isLoading.value = false
        }
    }

    fun onRegisterWithFacebookClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // simulate network delay

            // TODO: Implement Facebook registration

            _isLoading.value = false
        }
    }
}

sealed class RegisterNavigationEvent {
    object SuccessfulRegistration : RegisterNavigationEvent()
}
