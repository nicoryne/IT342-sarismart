package edu.cit.sarismart.features.user.tabs.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import edu.cit.sarismart.features.user.tabs.account.data.repository.AccountRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigationEvent = MutableSharedFlow<AccountNavigationEvent>()
    val navigationEvent: SharedFlow<AccountNavigationEvent> = _navigationEvent

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone

    fun onLogoutClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val res = authRepository.logout()
            _isLoading.value = false

            if (res.success) {
                _navigationEvent.emit(AccountNavigationEvent.NavigateToLogin)
                _navigationEvent.emit(AccountNavigationEvent.ClearBackStack)
            }

        }
    }

    fun getUserDetails() {
        viewModelScope.launch {
            _userEmail.value = accountRepository.getUserEmail()
            _userName.value = accountRepository.getUserFullName()
            _userPhone.value = accountRepository.getUserPhone()
        }
    }
}

sealed class AccountNavigationEvent {
    object NavigateToLogin : AccountNavigationEvent()
    object ClearBackStack : AccountNavigationEvent()
}
