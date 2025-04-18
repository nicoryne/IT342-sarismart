package edu.cit.sarismart.features.user.tabs.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigationEvent = MutableSharedFlow<AccountNavigationEvent>()
    val navigationEvent: SharedFlow<AccountNavigationEvent> = _navigationEvent

    fun onLogoutClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // simulate network delay

            val res = authRepository.logout()
            _isLoading.value = false

            if (res.success) {
                _navigationEvent.emit(AccountNavigationEvent.NavigateToLogin)
            }

        }
    }
}

sealed class AccountNavigationEvent {
    object NavigateToLogin : AccountNavigationEvent()
}
