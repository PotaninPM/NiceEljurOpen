package com.team.feature_login.presentation

sealed class LoginEvent {
    data class OnUsernameChange(val value: String) : LoginEvent()
    data class OnPasswordChange(val value: String) : LoginEvent()
    data object OnLoginClick : LoginEvent()
    data object OnWarningClose : LoginEvent()
}