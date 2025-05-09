package com.team.feature_messages.presentation.viewmodel

import com.team.feature_messages.data.model.Message
import com.team.feature_messages.data.model.MessageFolder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.feature_messages.domain.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val currentFolder: MessageFolder = MessageFolder.INBOX,
    val currentPage: Int = 1,
    val totalMessages: Int = 0,
    val searchQuery: String = "",
    val unreadOnly: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repository: MessagesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState

    fun loadInitialMessages(authToken: String) {
        loadMessages(authToken)
    }

    fun onFolderSelected(authToken: String, folder: MessageFolder) {
        _uiState.update { it.copy(currentFolder = folder, currentPage = 1, messages = emptyList()) }
        loadMessages(authToken)
    }

    fun onSearchQueryChanged(authToken: String, query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 1, messages = emptyList()) }
        loadMessages(authToken)
    }

    fun onUnreadOnlyChanged(authToken: String, unreadOnly: Boolean) {
        _uiState.update { it.copy(unreadOnly = unreadOnly, currentPage = 1, messages = emptyList()) }
        loadMessages(authToken)
    }

    fun loadNextPage(authToken: String) {
        _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        loadMessages(authToken, append = true)
    }

    private fun loadMessages(authToken: String, append: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = repository.getMessages(
                    authToken = authToken,
                    folder = _uiState.value.currentFolder,
                    page = _uiState.value.currentPage,
                    searchQuery = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                    unreadOnly = _uiState.value.unreadOnly
                )

                Log.d("MessagesViewModel", "Response: $response")

                if (response.response.state == 200 && response.response.result != null) {
                    _uiState.update { state ->
                        state.copy(
                            messages = if (append) state.messages + response.response.result.messages else response.response.result.messages,
                            totalMessages = response.response.result.total?.toIntOrNull() ?: 0,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.response.error ?: "Failed to load messages"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error loading messages", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load messages"
                    )
                }
            }
        }
    }
} 