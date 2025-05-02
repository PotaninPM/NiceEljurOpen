package com.team.feature_login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.feature_login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChange -> {
                state = state.copy(username = event.value)
            }
            is LoginEvent.OnPasswordChange -> {
                state = state.copy(password = event.value)
            }
            LoginEvent.OnLoginClick -> {
                login()
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            repository.login(state.username, state.password)
                .onSuccess { result ->
                    state = state.copy(
                        isLoading = false,
                        isSuccess = true,
                        token = result.token
                    )
                }
                .onFailure { throwable ->
                    state = state.copy(
                        isLoading = false,
                        error = throwable.message
                    )
                }
        }
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val token: String? = null
)

sealed class LoginEvent {
    data class OnUsernameChange(val value: String) : LoginEvent()
    data class OnPasswordChange(val value: String) : LoginEvent()
    data object OnLoginClick : LoginEvent()
} 