package edu.cit.sarismart.features.user.tabs.sasa.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.sasa.data.models.Message
import edu.cit.sarismart.features.user.tabs.sasa.data.repository.ChatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.plus


@HiltViewModel
class SasaChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow<String>("")
    val inputText: StateFlow<String> = _inputText

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onInputTextChanged(text: String) {
        _inputText.update { text }
    }

    fun sendMessage(text: String) {
        _messages.update { currentMessages ->
            currentMessages + Message(text, true)
        }

        _inputText.update { "" }

        viewModelScope.launch {
            _isLoading.update { true }

            try {
                val res = chatRepository.sendMessage(text)

                if(res.isNotBlank()) {
                    _messages.update { currentMessages ->
                        currentMessages + Message(res, false)
                    }
                } else {
                    _messages.update { currentMessages ->
                        currentMessages + Message("An error has occurred", false)
                    }
                }

            } catch (e: Exception) {
                _messages.update { currentMessages ->
                    currentMessages + Message("An error has occurred", false)
                }
            }

            _isLoading.update { false }
        }
    }
}