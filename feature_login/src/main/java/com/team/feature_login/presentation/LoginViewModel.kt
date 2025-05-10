package com.team.feature_login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team.common.PreferencesManager
import com.team.feature_login.domain.repository.LoginRepository
import com.team.feature_login.presentation.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChange -> {
                state = state.copy(
                    username = event.value,
                    error = null
                )
            }
            is LoginEvent.OnPasswordChange -> {
                state = state.copy(
                    password = event.value,
                    error = null
                )
            }

            is LoginEvent.OnWarningClose -> {
                state = state.copy(
                    error = null
                )
            }

            is LoginEvent.OnLoginClick -> login()
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
                    preferencesManager.saveAuthToken(result.token)
                    preferencesManager.saveAuthTokenExpires(result.expires)

                    state = state.copy(
                        isLoading = false,
                        isSuccess = true,
                        tokenInfo = result
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