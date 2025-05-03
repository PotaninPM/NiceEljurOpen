package com.team.feature_login.presentation.state

import com.team.feature_login.data.model.LoginResponse

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val tokenInfo: LoginResponse.Response.TokenResult? = null
)