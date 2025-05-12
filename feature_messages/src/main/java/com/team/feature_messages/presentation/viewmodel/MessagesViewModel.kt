package com.team.feature_messages.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.common.PreferencesManager
import com.team.feature_messages.data.model.Message
import com.team.feature_messages.data.model.MessageFolder
import com.team.feature_messages.domain.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FolderState(
    val messages: List<Message> = emptyList(),
    val currentPage: Int = 1,
    val totalMessages: Int = 0,
    val searchQuery: String = "",
    val unreadOnly: Boolean = false
)

data class MessagesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentFolder: MessageFolder = MessageFolder.INBOX,
    val personName: String = "",
    val personRole: String = "",
    val inboxState: FolderState = FolderState(),
    val sentState: FolderState = FolderState()
)

@OptIn(FlowPreview::class)
@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repository: MessagesRepository,
    preferencesManager: PreferencesManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    private var initialLoadDone = false

    private val authToken = preferencesManager.getAuthToken()
    private val personName = preferencesManager.getPersonName()
    private val personRole = preferencesManager.getPersonRole()

    init {
        _uiState.update {
            it.copy(
                personName = personName,
                personRole = personRole
            )
        }

        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .collect { query ->
                    updateCurrentFolderState {
                        it.copy(
                            searchQuery = query,
                            currentPage = 1,
                            messages = emptyList(),
                            totalMessages = 0
                        )
                    }
                    loadMessages(authToken)
                }
        }
    }

    fun loadInitialMessages() {
        if (!initialLoadDone) {
            loadMessages(authToken)
            initialLoadDone = true
        }
    }

    fun onFolderSelected(folder: MessageFolder) {
        if (_uiState.value.currentFolder == folder) return

        _uiState.update { it.copy(currentFolder = folder) }
        if (getCurrentFolderState().messages.isEmpty()) {
            loadMessages(authToken)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        updateCurrentFolderState { it.copy(searchQuery = query) }
    }

    fun onUnreadOnlyChanged(unreadOnly: Boolean) {
        updateCurrentFolderState {
            it.copy(
                unreadOnly = unreadOnly,
                currentPage = 1,
                messages = emptyList(),
                totalMessages = 0
            )
        }
        loadMessages(authToken)
    }

    fun loadNextPage() {
        val currentState = getCurrentFolderState()
        if (!_uiState.value.isLoading && currentState.messages.size < currentState.totalMessages) {
            updateCurrentFolderState { it.copy(currentPage = it.currentPage + 1) }
            loadMessages(authToken, append = true)
        }
    }

    private fun getCurrentFolderState(): FolderState {
        return when (_uiState.value.currentFolder) {
            MessageFolder.INBOX -> _uiState.value.inboxState
            MessageFolder.SENT -> _uiState.value.sentState
        }
    }

    private fun updateCurrentFolderState(update: (FolderState) -> FolderState) {
        _uiState.update { state ->
            when (state.currentFolder) {
                MessageFolder.INBOX -> state.copy(inboxState = update(state.inboxState))
                MessageFolder.SENT -> state.copy(sentState = update(state.sentState))
            }
        }
    }

    private fun loadMessages(authToken: String, append: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val currentState = getCurrentFolderState()
                val response = repository.getMessages(
                    authToken = authToken,
                    folder = _uiState.value.currentFolder,
                    page = currentState.currentPage,
                    searchQuery = currentState.searchQuery.takeIf { it.isNotBlank() },
                    unreadOnly = currentState.unreadOnly
                )

                if (response.response.state == 200 && response.response.result != null) {
                    updateCurrentFolderState { folderState ->
                        folderState.copy(
                            messages = if (append) folderState.messages + response.response.result.messages
                            else response.response.result.messages,
                            totalMessages = response.response.result.total?.toIntOrNull() ?: 0
                        )
                    }
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.response.error ?: "Failed to load messages"
                        )
                    }
                }
            } catch (e: Exception) {
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