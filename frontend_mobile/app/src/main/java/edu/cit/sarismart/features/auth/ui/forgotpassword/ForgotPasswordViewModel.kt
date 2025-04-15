package edu.cit.sarismart.features.auth.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _resetSent = MutableStateFlow(false)
    val resetSent: StateFlow<Boolean> = _resetSent

    private val _resetError = MutableStateFlow<String?>(null)
    val resetError: StateFlow<String?> = _resetError

    private val _navigationEvent = MutableSharedFlow<ForgotPasswordNavigationEvent>()
    val navigationEvent: SharedFlow<ForgotPasswordNavigationEvent> = _navigationEvent

    fun onEmailChanged(value: String) {
        _email.value = value
        validateEmail()
        _resetError.value = null
    }

    private fun validateEmail() {
        _emailError.value = when {
            _email.value.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun onResetPasswordClicked() {
        validateEmail()

        if (_emailError.value != null) {
            return
        }


    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ForgotPasswordNavigationEvent.NavigateBack)
        }
    }
}

sealed class ForgotPasswordNavigationEvent {
    object NavigateBack : ForgotPasswordNavigationEvent()
}
